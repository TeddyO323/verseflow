package com.example.verseflow.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.absoluteValue

enum class DesktopLyricsLoadState {
    Idle,
    Loading,
    Ready,
    Unavailable,
}

data class DesktopLyricsPayload(
    val syncedLyrics: List<DesktopLyricLine>,
    val plainLyrics: List<String>,
    val attribution: String,
)

fun desktopLyricsPayloadFromRawText(rawText: String, attribution: String): DesktopLyricsPayload? {
    val trimmed = rawText.trim()
    if (trimmed.isBlank()) return null

    val syncedLyrics = parseDesktopSyncedLyrics(trimmed)
    val plainLyrics = parseDesktopPlainLyrics(trimmed, stripTimestamps = true)
    if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) return null

    return DesktopLyricsPayload(
        syncedLyrics = syncedLyrics,
        plainLyrics = if (plainLyrics.isNotEmpty()) plainLyrics else syncedLyrics.map(DesktopLyricLine::text),
        attribution = attribution,
    )
}

data class DesktopLyricsSearchCandidate(
    val title: String,
    val artist: String,
    val album: String?,
    val source: String,
    val payload: DesktopLyricsPayload,
    val score: Double,
) {
    val typeLabel: String
        get() = if (payload.syncedLyrics.isNotEmpty()) "SYNCED" else "PLAIN"
}

class DesktopLyricsCacheStore(
    private val cacheDir: Path = Path.of(System.getProperty("user.home"), ".verseflow-desktop", "lyrics-cache"),
) {
    suspend fun load(trackPath: String): DesktopLyricsPayload? = withContext(Dispatchers.IO) {
        val target = cacheFile(trackPath)
        if (!Files.exists(target)) return@withContext null
        runCatching {
            val payload = JSONObject(Files.readString(target))
            DesktopLyricsPayload(
                syncedLyrics = (payload.optJSONArray("syncedLyrics") ?: JSONArray()).toDesktopLyricLines(),
                plainLyrics = (payload.optJSONArray("plainLyrics") ?: JSONArray()).toStringList(),
                attribution = payload.optString("attribution").ifBlank { "Lyrics cache" },
            )
        }.getOrNull()
    }

    suspend fun save(trackPath: String, payload: DesktopLyricsPayload) = withContext(Dispatchers.IO) {
        runCatching {
            Files.createDirectories(cacheDir)
            val target = cacheFile(trackPath)
            val json = JSONObject()
                .put("attribution", payload.attribution)
                .put(
                    "syncedLyrics",
                    JSONArray(
                        payload.syncedLyrics.map { line ->
                            JSONObject()
                                .put("timestampMs", line.timestampMs)
                                .put("text", line.text)
                        }
                    ),
                )
                .put("plainLyrics", JSONArray(payload.plainLyrics))
            Files.writeString(target, json.toString())
        }
    }

    private fun cacheFile(trackPath: String): Path =
        cacheDir.resolve("${trackPath.hashCode().absoluteValue}.json")
}

class DesktopLyricsRepository(
    private val apiKeyProvider: () -> String = {
        System.getProperty("verseflow.musixmatch.apiKey")
            ?.trim()
            .orEmpty()
            .ifBlank { System.getenv("VERSEFLOW_MUSIXMATCH_API_KEY")?.trim().orEmpty() }
    },
) {
    private val lyricsOvhFallbackRepository = DesktopLyricsOvhFallbackRepository()

    suspend fun lookup(track: DesktopTrack): DesktopLyricsPayload? = withContext(Dispatchers.IO) {
        val artistSearchInputs = buildDesktopLyricsArtistInputs(track)
        val lrcResults = artistSearchInputs.flatMap { artistInput ->
            lookupLrcLibCandidates(
                title = track.title,
                artistName = artistInput,
                albumTitle = track.album,
                durationMs = track.durationMs,
            )
        }
        lrcResults.bestResult()?.payload?.let { return@withContext it }

        val musixmatchResults = searchMusixmatchCandidates(
            title = track.title,
            artistName = track.artist,
            albumTitle = track.album,
            durationMs = track.durationMs,
            trackIsrc = track.isrc,
        )
        musixmatchResults
            .sortedWith(
                compareBy<DesktopLyricsSearchCandidate> { if (it.payload.syncedLyrics.isNotEmpty()) 0 else 1 }
                    .thenBy { it.score }
                    .thenByDescending { it.payload.syncedLyrics.size }
                    .thenByDescending { it.payload.plainLyrics.size },
            )
            .firstOrNull()
            ?.payload
            ?.let { return@withContext it }

        artistSearchInputs.asSequence().flatMap { artistInput ->
            lyricsOvhFallbackRepository.searchCandidates(
                title = track.title,
                artistName = artistInput,
                albumTitle = track.album,
                durationMs = track.durationMs,
            ).asSequence()
        }.distinctBy { "${normalizeLookupValue(it.title)}|${normalizeLookupValue(it.artist)}|${it.typeLabel}|${it.source}" }
            .sortedWith(
                compareBy<DesktopLyricsSearchCandidate> { if (it.payload.syncedLyrics.isNotEmpty()) 0 else 1 }
                    .thenBy { it.score }
                    .thenByDescending { it.payload.syncedLyrics.size }
                    .thenByDescending { it.payload.plainLyrics.size },
            )
            .firstOrNull()
            ?.payload
    }

    suspend fun searchCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): List<DesktopLyricsSearchCandidate> = withContext(Dispatchers.IO) {
        val artistSearchInputs = buildDesktopLyricsArtistInputs(
            title = title,
            primaryArtist = artistName,
        )
        val lrcResults = artistSearchInputs.flatMap { artistInput ->
            lookupLrcLibCandidates(
                title = title,
                artistName = artistInput,
                albumTitle = albumTitle,
                durationMs = durationMs,
            ).map { match ->
                DesktopLyricsSearchCandidate(
                    title = match.title.ifBlank { title },
                    artist = match.artist.ifBlank { artistInput },
                    album = match.album ?: albumTitle,
                    source = "LRCLIB",
                    payload = match.payload,
                    score = match.score,
                )
            }
        }

        val musixmatchResults = searchMusixmatchCandidates(
            title = title,
            artistName = artistName,
            albumTitle = albumTitle,
            durationMs = durationMs,
        )

        val ovhResults = artistSearchInputs.flatMap { artistInput ->
            lyricsOvhFallbackRepository.searchCandidates(
                title = title,
                artistName = artistInput,
                albumTitle = albumTitle,
                durationMs = durationMs,
            )
        }

        (lrcResults + musixmatchResults + ovhResults)
            .distinctBy { "${normalizeLookupValue(it.title)}|${normalizeLookupValue(it.artist)}|${it.typeLabel}|${it.source}" }
            .sortedWith(
                compareBy<DesktopLyricsSearchCandidate> { if (it.payload.syncedLyrics.isNotEmpty()) 0 else 1 }
                    .thenBy { it.score }
                    .thenByDescending { it.payload.syncedLyrics.size }
                    .thenByDescending { it.payload.plainLyrics.size },
            )
            .take(12)
    }

    private fun lookupLrcLibCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): List<ScoredDesktopLyricsMatch> {
        val titleCandidates = titleCandidateStrings(title)
        val artistCandidates = artistCandidateStrings(artistName)
        val albumCandidates = albumTitle?.let(::albumCandidateStrings).orEmpty()

        val exactMatches = buildList {
            titleCandidates.forEach { titleCandidate ->
                artistCandidates.forEach { artistCandidate ->
                    val albumOptions = if (albumCandidates.isEmpty()) listOf<String?>(null) else albumCandidates.map { it as String? }
                    albumOptions.forEach { albumCandidate ->
                        requestObject(
                            path = "get",
                            params = buildMap {
                                put("track_name", titleCandidate)
                                put("artist_name", artistCandidate)
                                albumCandidate?.takeIf(String::isNotBlank)?.let { put("album_name", it) }
                                if (durationMs > 0L) {
                                    put("duration", (durationMs / 1_000L).toString())
                                }
                            },
                            notFoundCode = HttpURLConnection.HTTP_NOT_FOUND,
                        )?.toScoredMatch(
                            requestedTitle = title,
                            requestedArtist = artistName,
                            requestedAlbum = albumTitle,
                            durationMs = durationMs,
                            attribution = "Lyrics via LRCLIB",
                        )?.let(::add)
                    }
                }
            }
        }

        val searchQueries = buildList {
            add(listOf(title, artistName, albumTitle).filterNotNull().joinToString(" "))
            titleCandidates.forEach { titleCandidate ->
                artistCandidates.forEach { artistCandidate ->
                    add("$titleCandidate $artistCandidate")
                }
            }
            addAll(titleCandidates)
        }.map(String::trim).filter(String::isNotBlank).distinct()

        val searchMatches = searchQueries.mapNotNull { query ->
            bestMatch(
                results = requestArray(path = "search", params = mapOf("q" to query)),
                requestedTitle = title,
                requestedArtist = artistName,
                requestedAlbum = albumTitle,
                durationMs = durationMs,
                attribution = "Lyrics via LRCLIB",
            )
        }.filterNotNull()

        return (exactMatches + searchMatches)
            .distinctBy { "${normalizeLookupValue(it.title)}|${normalizeLookupValue(it.artist)}|${normalizeLookupValue(it.album.orEmpty())}|${it.payload.syncedLyrics.size}|${it.payload.plainLyrics.size}" }
            .sortedWith(
                compareBy<ScoredDesktopLyricsMatch> { if (it.payload.syncedLyrics.isNotEmpty()) 0 else 1 }
                    .thenBy { it.score }
                    .thenByDescending { it.payload.syncedLyrics.size }
                    .thenByDescending { it.payload.plainLyrics.size },
            )
            .take(8)
    }

    private fun List<ScoredDesktopLyricsMatch>.bestResult(): ScoredDesktopLyricsMatch? =
        sortedWith(
            compareBy<ScoredDesktopLyricsMatch> { if (it.payload.syncedLyrics.isNotEmpty()) 0 else 1 }
                .thenBy { it.score }
                .thenByDescending { it.payload.syncedLyrics.size }
                .thenByDescending { it.payload.plainLyrics.size },
        ).firstOrNull()

    private fun bestMatch(
        results: JSONArray,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        attribution: String,
    ): ScoredDesktopLyricsMatch? {
        if (results.length() == 0) return null
        return (0 until results.length())
            .mapNotNull(results::optJSONObject)
            .mapNotNull { candidate ->
                candidate.toScoredMatch(
                    requestedTitle = requestedTitle,
                    requestedArtist = requestedArtist,
                    requestedAlbum = requestedAlbum,
                    durationMs = durationMs,
                    attribution = attribution,
                )
            }
            .bestResult()
    }

    private fun JSONObject.toScoredMatch(
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        attribution: String,
    ): ScoredDesktopLyricsMatch? {
        val payload = parseDesktopLyricsPayload(this, attribution) ?: return null
        val metadata = DesktopLyricsCandidateMetadata(
            title = optString("trackName").ifBlank { optString("name") },
            artist = optString("artistName"),
            album = optString("albumName").takeIf(String::isNotBlank),
            durationSeconds = optDouble("duration").takeIf { !it.isNaN() && it > 0.0 },
        )
        return createScoredMatch(
            metadata = metadata,
            requestedTitle = requestedTitle,
            requestedArtist = requestedArtist,
            requestedAlbum = requestedAlbum,
            durationMs = durationMs,
            payload = payload,
        )
    }

    private fun createScoredMatch(
        metadata: DesktopLyricsCandidateMetadata,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        payload: DesktopLyricsPayload,
    ): ScoredDesktopLyricsMatch? {
        val titleMatch = bestTitleMatch(requestedTitle, metadata.title.ifBlank { requestedTitle })
        val artistMatch = bestArtistMatch(requestedArtist, metadata.artist.ifBlank { requestedArtist })
        val albumMatch = if (requestedAlbum != null && metadata.album != null) bestAlbumMatch(requestedAlbum, metadata.album) else null
        val titleOverlap = titleMatch.overlap
        val artistOverlap = artistMatch.overlap
        val albumOverlap = albumMatch?.overlap ?: 1.0
        val durationDeltaSeconds = if (durationMs > 0L && metadata.durationSeconds != null) {
            abs(metadata.durationSeconds - (durationMs / 1_000.0))
        } else {
            null
        }

        if (artistOverlap < 0.72) return null
        if (titleOverlap < 0.68 && !shareCoreText(titleMatch.expected, titleMatch.actual)) return null
        if (requestedAlbum != null && metadata.album != null && albumOverlap < 0.34 && titleOverlap < 0.92) return null
        if (durationDeltaSeconds != null) {
            val maxDelta = if (payload.syncedLyrics.isNotEmpty()) 18.0 else 12.0
            if (durationDeltaSeconds > maxDelta) return null
        }

        var score = 0.0
        score += similarityPenalty(titleMatch.expected, titleMatch.actual, titleOverlap)
        score += similarityPenalty(artistMatch.expected, artistMatch.actual, artistOverlap)
        if (albumMatch != null) {
            score += similarityPenalty(albumMatch.expected, albumMatch.actual, albumOverlap) * 0.55
        }
        durationDeltaSeconds?.let { score += it * if (payload.syncedLyrics.isNotEmpty()) 0.12 else 0.18 }
        if (payload.syncedLyrics.isEmpty()) score += 0.9
        val maxAcceptedScore = if (payload.syncedLyrics.isNotEmpty()) 6.8 else 5.3
        if (score > maxAcceptedScore) return null

        return ScoredDesktopLyricsMatch(
            payload = payload,
            score = score,
            title = metadata.title.ifBlank { requestedTitle },
            artist = metadata.artist.ifBlank { requestedArtist },
            album = metadata.album ?: requestedAlbum,
        )
    }

    private fun requestObject(
        path: String,
        params: Map<String, String>,
        notFoundCode: Int,
    ): JSONObject? {
        val response = request(path, params) ?: return null
        if (response.code == notFoundCode) return null
        if (response.code !in 200..299) return null
        return runCatching { JSONObject(response.body) }.getOrNull()
    }

    private fun requestArray(
        path: String,
        params: Map<String, String>,
    ): JSONArray {
        val response = request(path, params) ?: return JSONArray()
        if (response.code !in 200..299) return JSONArray()
        return runCatching {
            val body = response.body.trim()
            when {
                body.startsWith("[") -> JSONArray(body)
                body.isBlank() -> JSONArray()
                else -> JSONArray()
            }
        }.getOrDefault(JSONArray())
    }

    private fun request(
        path: String,
        params: Map<String, String>,
    ): DesktopLyricsHttpResponse? {
        val query = params.entries.joinToString("&") { (key, value) ->
            "${encodeUrlPart(key)}=${encodeUrlPart(value)}"
        }
        val url = buildString {
            append("https://lrclib.net/api/")
            append(path)
            if (query.isNotBlank()) {
                append("?")
                append(query)
            }
        }
        val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 6_000
            connection.readTimeout = 6_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "VerseFlowDesktop/1.0")

            val stream = when (connection.responseCode) {
                in 200..299 -> connection.inputStream
                else -> connection.errorStream
            } ?: return DesktopLyricsHttpResponse(connection.responseCode, "")

            DesktopLyricsHttpResponse(
                code = connection.responseCode,
                body = stream.bufferedReader().use { it.readText() },
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun searchMusixmatchCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
        trackIsrc: String? = null,
    ): List<DesktopLyricsSearchCandidate> {
        val apiKey = apiKeyProvider().trim()
        if (apiKey.isBlank()) return emptyList()

        val titleCandidates = titleCandidateStrings(title)
        val artistCandidates = artistCandidateStrings(artistName)
        val albumCandidates = albumTitle?.let(::albumCandidateStrings).orEmpty()

        val queryInputs = buildList {
            trackIsrc?.takeIf(String::isNotBlank)?.let { isrc ->
                add(MusixmatchQuery(isrc = isrc))
            }
            titleCandidates.forEach { titleCandidate ->
                artistCandidates.forEach { artistCandidate ->
                    if (albumCandidates.isEmpty()) {
                        add(MusixmatchQuery(title = titleCandidate, artist = artistCandidate, album = null))
                    } else {
                        albumCandidates.forEach { albumCandidate ->
                            add(MusixmatchQuery(title = titleCandidate, artist = artistCandidate, album = albumCandidate))
                        }
                    }
                }
            }
        }.distinct().take(12)

        return queryInputs
            .mapNotNull { query ->
                val matchedTrack = requestMusixmatchTrack(query, apiKey) ?: return@mapNotNull null
                val payload = requestMusixmatchPayload(matchedTrack, apiKey) ?: return@mapNotNull null
                val metadata = DesktopLyricsCandidateMetadata(
                    title = matchedTrack.title,
                    artist = matchedTrack.artist,
                    album = matchedTrack.album,
                    durationSeconds = matchedTrack.durationSeconds,
                )
                createScoredMatch(
                    metadata = metadata,
                    requestedTitle = title,
                    requestedArtist = artistName,
                    requestedAlbum = albumTitle,
                    durationMs = durationMs,
                    payload = payload,
                )?.let { match ->
                    DesktopLyricsSearchCandidate(
                        title = match.title,
                        artist = match.artist,
                        album = match.album ?: albumTitle,
                        source = "Musixmatch",
                        payload = match.payload,
                        score = match.score,
                    )
                }
            }
            .distinctBy { "${normalizeLookupValue(it.title)}|${normalizeLookupValue(it.artist)}|${it.typeLabel}|${it.source}" }
            .sortedWith(
                compareBy<DesktopLyricsSearchCandidate> { if (it.payload.syncedLyrics.isNotEmpty()) 0 else 1 }
                    .thenBy { it.score }
                    .thenByDescending { it.payload.syncedLyrics.size }
                    .thenByDescending { it.payload.plainLyrics.size },
            )
            .take(6)
    }

    private fun requestMusixmatchTrack(
        query: MusixmatchQuery,
        apiKey: String,
    ): MusixmatchTrackMatch? {
        val payload = musixmatchRequest(
            path = "matcher.track.get",
            params = buildMap {
                put("apikey", apiKey)
                query.isrc?.takeIf(String::isNotBlank)?.let { put("track_isrc", it) }
                query.title?.takeIf(String::isNotBlank)?.let { put("q_track", it) }
                query.artist?.takeIf(String::isNotBlank)?.let { put("q_artist", it) }
                query.album?.takeIf(String::isNotBlank)?.let { put("q_album", it) }
            },
        ) ?: return null

        val trackPayload = payload.optJSONObject("track") ?: return null
        if (trackPayload.optInt("restricted") != 0) return null

        return MusixmatchTrackMatch(
            trackId = trackPayload.optLong("track_id").takeIf { it > 0L },
            commontrackId = trackPayload.optLong("commontrack_id").takeIf { it > 0L },
            title = trackPayload.optString("track_name"),
            artist = trackPayload.optString("artist_name"),
            album = trackPayload.optString("album_name").takeIf(String::isNotBlank),
            durationSeconds = trackPayload.optDouble("track_length").takeIf { !it.isNaN() && it > 0.0 },
            hasLyrics = trackPayload.optInt("has_lyrics") == 1,
            hasSubtitles = trackPayload.optInt("has_subtitles") == 1,
        )
    }

    private fun requestMusixmatchPayload(
        track: MusixmatchTrackMatch,
        apiKey: String,
    ): DesktopLyricsPayload? {
        if (track.hasSubtitles) {
            requestMusixmatchSubtitle(track, apiKey)?.let { return it }
        }
        if (track.hasLyrics) {
            requestMusixmatchPlainLyrics(track, apiKey)?.let { return it }
        }
        return null
    }

    private fun requestMusixmatchSubtitle(
        track: MusixmatchTrackMatch,
        apiKey: String,
    ): DesktopLyricsPayload? {
        val params = buildMap {
            put("apikey", apiKey)
            track.commontrackId?.let { put("commontrack_id", it.toString()) }
                ?: track.trackId?.let { put("track_id", it.toString()) }
        }
        val payload = musixmatchRequest("track.subtitle.get", params) ?: return null
        val subtitle = payload.optJSONObject("subtitle") ?: return null
        val subtitleBody = subtitle.optString("subtitle_body").trim()
        fireTrackingPixel(subtitle.optString("pixel_tracking_url"))
        if (subtitleBody.isBlank()) return null
        return desktopLyricsPayloadFromRawText(
            rawText = subtitleBody,
            attribution = "Lyrics via Musixmatch",
        )
    }

    private fun requestMusixmatchPlainLyrics(
        track: MusixmatchTrackMatch,
        apiKey: String,
    ): DesktopLyricsPayload? {
        val params = buildMap {
            put("apikey", apiKey)
            track.commontrackId?.let { put("commontrack_id", it.toString()) }
                ?: track.trackId?.let { put("track_id", it.toString()) }
        }
        val payload = musixmatchRequest("track.lyrics.get", params) ?: return null
        val lyrics = payload.optJSONObject("lyrics") ?: return null
        fireTrackingPixel(lyrics.optString("pixel_tracking_url"))
        return desktopLyricsPayloadFromRawText(
            rawText = sanitizeMusixmatchLyricsBody(lyrics.optString("lyrics_body")),
            attribution = "Lyrics via Musixmatch",
        )
    }

    private fun musixmatchRequest(
        path: String,
        params: Map<String, String>,
    ): JSONObject? {
        val query = params.entries.joinToString("&") { (key, value) ->
            "${encodeUrlPart(key)}=${encodeUrlPart(value)}"
        }
        val url = buildString {
            append("https://api.musixmatch.com/ws/1.1/")
            append(path)
            if (query.isNotBlank()) {
                append("?")
                append(query)
            }
        }
        val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 6_000
            connection.readTimeout = 6_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "VerseFlowDesktop/1.0")
            val stream = when (connection.responseCode) {
                in 200..299 -> connection.inputStream
                else -> connection.errorStream
            } ?: return null
            val root = JSONObject(stream.bufferedReader().use { it.readText() })
            val message = root.optJSONObject("message") ?: return null
            val header = message.optJSONObject("header")
            if (header?.optInt("status_code") !in setOf(200, 0)) return null
            message.optJSONObject("body")
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun fireTrackingPixel(url: String?) {
        val targetUrl = url?.trim().orEmpty()
        if (targetUrl.isBlank()) return
        runCatching {
            val connection = (URL(targetUrl).openConnection() as? HttpURLConnection) ?: return
            connection.requestMethod = "GET"
            connection.connectTimeout = 3_000
            connection.readTimeout = 3_000
            connection.inputStream.use { input -> input.readBytes() }
            connection.disconnect()
        }
    }
}

private fun buildDesktopLyricsArtistInputs(track: DesktopTrack): List<String> =
    buildDesktopLyricsArtistInputs(
        title = track.title,
        primaryArtist = track.artist,
        artistCredits = track.artistCredits,
    )

private fun buildDesktopLyricsArtistInputs(
    title: String,
    primaryArtist: String,
    artistCredits: List<String> = emptyList(),
): List<String> =
    buildList {
        add(primaryArtist)
        addAll(artistCredits)
        addAll(buildDesktopArtistCredits(primaryArtist, title))
        addAll(splitDesktopArtists(primaryArtist))
        artistCredits.firstOrNull()?.let(::add)
    }
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()

private class DesktopLyricsOvhFallbackRepository {
    fun searchCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): List<DesktopLyricsSearchCandidate> {
        val titleCandidates = titleCandidateStrings(title)
        val artistCandidates = artistCandidateStrings(artistName)
        val directCandidates = mutableListOf<DesktopLyricsSearchCandidate>()

        directPairs(titleCandidates, artistCandidates).forEach { (artistCandidate, titleCandidate) ->
            requestLyrics(artistCandidate, titleCandidate)?.let { lyrics ->
                val plainLyrics = parseDesktopPlainLyrics(lyrics, stripTimestamps = false)
                if (plainLyrics.isNotEmpty()) {
                    directCandidates += DesktopLyricsSearchCandidate(
                        title = titleCandidate,
                        artist = artistCandidate,
                        album = albumTitle,
                        source = "lyrics.ovh",
                        payload = DesktopLyricsPayload(
                            syncedLyrics = emptyList(),
                            plainLyrics = plainLyrics,
                            attribution = "Lyrics via lyrics.ovh",
                        ),
                        score = 0.65,
                    )
                }
            }
        }

        val suggestionQueries = buildList {
            add("$artistName $title")
            add("$title $artistName")
            titleCandidates.forEach { titleCandidate ->
                artistCandidates.forEach { artistCandidate ->
                    add("$artistCandidate $titleCandidate")
                }
            }
        }.map(String::trim).filter(String::isNotBlank).distinct()

        val suggestions = suggestionQueries
            .mapNotNull { query ->
                bestSuggestionMatch(
                    results = requestSuggestions(query),
                    requestedTitle = title,
                    requestedArtist = artistName,
                    requestedAlbum = albumTitle,
                    durationMs = durationMs,
                )
            }
            .sortedBy(DesktopSuggestionMatch::score)
            .distinctBy { "${normalizeLookupValue(it.title)}|${normalizeLookupValue(it.artistName)}" }
            .take(6)

        val suggestionCandidates = suggestions.mapNotNull { suggestion ->
            requestLyrics(suggestion.artistName, suggestion.title)?.let { lyrics ->
                val plainLyrics = parseDesktopPlainLyrics(lyrics, stripTimestamps = false)
                if (plainLyrics.isNotEmpty()) {
                    DesktopLyricsSearchCandidate(
                        title = suggestion.title,
                        artist = suggestion.artistName,
                        album = albumTitle,
                        source = "lyrics.ovh",
                        payload = DesktopLyricsPayload(
                            syncedLyrics = emptyList(),
                            plainLyrics = plainLyrics,
                            attribution = "Lyrics via lyrics.ovh",
                        ),
                        score = suggestion.score,
                    )
                } else {
                    null
                }
            }
        }

        return (directCandidates + suggestionCandidates)
            .distinctBy { "${normalizeLookupValue(it.title)}|${normalizeLookupValue(it.artist)}|${it.source}" }
            .sortedBy { it.score }
            .take(8)
    }

    private fun bestSuggestionMatch(
        results: JSONArray,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
    ): DesktopSuggestionMatch? {
        if (results.length() == 0) return null

        return (0 until results.length())
            .mapNotNull(results::optJSONObject)
            .mapNotNull { candidate ->
                val trackTitle = candidate.optString("title")
                val artist = candidate.optJSONObject("artist")?.optString("name").orEmpty()
                val album = candidate.optJSONObject("album")?.optString("title")
                val durationSeconds = candidate.optDouble("duration").takeIf { !it.isNaN() && it > 0.0 }
                createSuggestionMatch(
                    trackTitle = trackTitle,
                    artistName = artist,
                    albumTitle = album,
                    durationSeconds = durationSeconds,
                    requestedTitle = requestedTitle,
                    requestedArtist = requestedArtist,
                    requestedAlbum = requestedAlbum,
                    durationMs = durationMs,
                )
            }
            .minByOrNull(DesktopSuggestionMatch::score)
    }

    private fun createSuggestionMatch(
        trackTitle: String,
        artistName: String,
        albumTitle: String?,
        durationSeconds: Double?,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
    ): DesktopSuggestionMatch? {
        val titleMatch = bestTitleMatch(requestedTitle, trackTitle)
        val artistMatch = bestArtistMatch(requestedArtist, artistName)
        val albumMatch = if (requestedAlbum != null && albumTitle != null) bestAlbumMatch(requestedAlbum, albumTitle) else null
        val titleOverlap = titleMatch.overlap
        val artistOverlap = artistMatch.overlap
        val albumOverlap = albumMatch?.overlap ?: 1.0
        val durationDelta = if (durationMs > 0L && durationSeconds != null) abs(durationSeconds - (durationMs / 1_000.0)) else null

        if (artistOverlap < 0.72) return null
        if (titleOverlap < 0.68 && !shareCoreText(titleMatch.expected, titleMatch.actual)) return null
        if (requestedAlbum != null && albumTitle != null && albumOverlap < 0.34 && titleOverlap < 0.92) return null
        if (durationDelta != null && durationDelta > 14.0) return null

        var score = 0.0
        score += similarityPenalty(titleMatch.expected, titleMatch.actual, titleOverlap)
        score += similarityPenalty(artistMatch.expected, artistMatch.actual, artistOverlap)
        if (albumMatch != null) score += similarityPenalty(albumMatch.expected, albumMatch.actual, albumOverlap) * 0.55
        durationDelta?.let { score += it * 0.16 }
        if (score > 5.4) return null

        return DesktopSuggestionMatch(title = trackTitle, artistName = artistName, score = score)
    }

    private fun directPairs(titleCandidates: List<String>, artistCandidates: List<String>): List<Pair<String, String>> = buildList {
        artistCandidates.forEach { artistCandidate ->
            titleCandidates.forEach { titleCandidate ->
                add(artistCandidate to titleCandidate)
            }
        }
    }.distinct()

    private fun requestSuggestions(query: String): JSONArray {
        val response = request("suggest/${encodePathSegment(query)}") ?: return JSONArray()
        if (response.code !in 200..299) return JSONArray()
        val json = runCatching { JSONObject(response.body) }.getOrElse { JSONObject() }
        return json.optJSONArray("data") ?: JSONArray()
    }

    private fun requestLyrics(artistName: String, title: String): String? {
        val response = request("v1/${encodePathSegment(artistName)}/${encodePathSegment(title)}") ?: return null
        if (response.code !in 200..299) return null
        return runCatching { JSONObject(response.body) }.getOrNull()?.optString("lyrics")?.takeIf(String::isNotBlank)
    }

    private fun request(path: String): DesktopLyricsHttpResponse? {
        val url = "https://api.lyrics.ovh/$path"
        val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 6_000
            connection.readTimeout = 6_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "VerseFlowDesktop/1.0")

            val stream = when (connection.responseCode) {
                in 200..299 -> connection.inputStream
                else -> connection.errorStream
            } ?: return DesktopLyricsHttpResponse(connection.responseCode, "")

            DesktopLyricsHttpResponse(
                code = connection.responseCode,
                body = stream.bufferedReader().use { it.readText() },
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}

private data class DesktopLyricsHttpResponse(
    val code: Int,
    val body: String,
)

private data class DesktopLyricsCandidateMatch(
    val expected: String,
    val actual: String,
    val overlap: Double,
)

private data class DesktopLyricsCandidateMetadata(
    val title: String,
    val artist: String,
    val album: String?,
    val durationSeconds: Double?,
)

private data class ScoredDesktopLyricsMatch(
    val payload: DesktopLyricsPayload,
    val score: Double,
    val title: String,
    val artist: String,
    val album: String?,
)

private data class MusixmatchQuery(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val isrc: String? = null,
)

private data class MusixmatchTrackMatch(
    val trackId: Long?,
    val commontrackId: Long?,
    val title: String,
    val artist: String,
    val album: String?,
    val durationSeconds: Double?,
    val hasLyrics: Boolean,
    val hasSubtitles: Boolean,
)

private data class DesktopSuggestionMatch(
    val title: String,
    val artistName: String,
    val score: Double,
)

private fun parseDesktopLyricsPayload(payload: JSONObject, attribution: String): DesktopLyricsPayload? {
    val syncedLyrics = payload.optString("syncedLyrics")
        .takeIf(String::isNotBlank)
        ?.let(::parseDesktopSyncedLyrics)
        .orEmpty()
    val plainLyrics = payload.optString("plainLyrics")
        .takeIf(String::isNotBlank)
        ?.let { parseDesktopPlainLyrics(it, stripTimestamps = false) }
        .orEmpty()

    if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) return null

    return DesktopLyricsPayload(
        syncedLyrics = syncedLyrics,
        plainLyrics = if (plainLyrics.isNotEmpty()) plainLyrics else syncedLyrics.map(DesktopLyricLine::text),
        attribution = attribution,
    )
}

private fun JSONArray.toDesktopLyricLines(): List<DesktopLyricLine> =
    (0 until length())
        .mapNotNull(::optJSONObject)
        .mapNotNull { payload ->
            val text = payload.optString("text")
            val timestamp = payload.optLong("timestampMs")
            if (text.isBlank()) null else DesktopLyricLine(timestampMs = timestamp, text = text)
        }

private fun JSONArray.toStringList(): List<String> =
    (0 until length())
        .map { index -> optString(index) }
        .map(String::trim)
        .filter(String::isNotBlank)

private fun normalizeLookupValue(value: String): String =
    value.lowercase().replace(Regex("""[^a-z0-9]+"""), " ").trim()

private fun titleCandidateStrings(value: String): List<String> = baseCandidateStrings(value)

private fun albumCandidateStrings(value: String): List<String> = baseCandidateStrings(value)

private fun artistCandidateStrings(value: String): List<String> {
    val base = baseCandidateStrings(value)
    val splitVariants = base.flatMap(::collaborationSegments)
    return (base + splitVariants).filter(String::isNotBlank).distinct()
}

private fun bestTitleMatch(expectedRaw: String, actualRaw: String): DesktopLyricsCandidateMatch =
    bestCandidateMatch(titleCandidateStrings(expectedRaw), titleCandidateStrings(actualRaw))

private fun bestArtistMatch(expectedRaw: String, actualRaw: String): DesktopLyricsCandidateMatch =
    bestCandidateMatch(artistCandidateStrings(expectedRaw), artistCandidateStrings(actualRaw))

private fun bestAlbumMatch(expectedRaw: String, actualRaw: String): DesktopLyricsCandidateMatch =
    bestCandidateMatch(albumCandidateStrings(expectedRaw), albumCandidateStrings(actualRaw))

private fun bestCandidateMatch(
    expectedVariants: List<String>,
    actualVariants: List<String>,
): DesktopLyricsCandidateMatch {
    val normalizedExpected = expectedVariants.map(::normalizeLookupValue).filter(String::isNotBlank).ifEmpty { listOf("") }
    val normalizedActual = actualVariants.map(::normalizeLookupValue).filter(String::isNotBlank).ifEmpty { listOf("") }

    return normalizedExpected
        .flatMap { expected ->
            normalizedActual.map { actual ->
                DesktopLyricsCandidateMatch(
                    expected = expected,
                    actual = actual,
                    overlap = overlapRatio(expected, actual),
                )
            }
        }
        .sortedWith(
            compareByDescending<DesktopLyricsCandidateMatch> { it.overlap }
                .thenBy { abs(it.expected.length - it.actual.length) }
                .thenBy { if (shareCoreText(it.expected, it.actual)) 0 else 1 },
        )
        .first()
}

private fun baseCandidateStrings(value: String): List<String> {
    val cleaned = value.trim()
    val withoutParens = cleaned
        .replace(Regex("""\([^)]*\)"""), "")
        .replace(Regex("""\[[^]]*]"""), "")
        .trim()
    val withoutFeatures = withoutParens
        .replace(Regex("""(?i)\b(feat|ft|featuring)\.?\b.*$"""), "")
        .replace(Regex("""(?i)\b(remaster(ed)?|live|explicit|official audio|official video|lyrics?)\b"""), "")
        .replace(Regex("""\s+-\s+.*$"""), "")
        .replace(Regex("""\s+"""), " ")
        .trim()

    return listOf(cleaned, withoutParens, withoutFeatures)
        .filter(String::isNotBlank)
        .distinct()
}

private fun collaborationSegments(value: String): List<String> {
    val separated = value
        .replace(Regex("""(?i)\b(feat|ft|featuring|with|vs|and|x)\b"""), "|")
        .replace("/", "|")
        .replace("&", "|")
        .replace(",", "|")
        .replace(";", "|")
        .split("|")
        .map(String::trim)
        .filter(String::isNotBlank)

    return buildList {
        addAll(separated)
        separated.firstOrNull()?.let(::add)
    }.distinct()
}

private fun overlapRatio(expected: String, actual: String): Double {
    val expectedTokens = expected.split(" ").filter(String::isNotBlank).toSet()
    val actualTokens = actual.split(" ").filter(String::isNotBlank).toSet()
    if (expectedTokens.isEmpty() || actualTokens.isEmpty()) return 0.0
    val overlapCount = expectedTokens.intersect(actualTokens).size.toDouble()
    return overlapCount / maxOf(expectedTokens.size, actualTokens.size).toDouble()
}

private fun shareCoreText(expected: String, actual: String): Boolean =
    expected.isNotBlank() && actual.isNotBlank() && (expected.contains(actual) || actual.contains(expected))

private fun similarityPenalty(expected: String, actual: String, overlap: Double): Double = when {
    expected == actual -> 0.0
    overlap >= 0.94 -> 0.35
    overlap >= 0.82 -> 1.0
    overlap >= 0.68 -> 2.3
    shareCoreText(expected, actual) -> 2.8
    else -> 6.4
}

private fun encodeUrlPart(value: String): String =
    URLEncoder.encode(value, StandardCharsets.UTF_8)

private fun encodePathSegment(value: String): String =
    value.split("/")
        .joinToString("/") { segment -> URLEncoder.encode(segment, StandardCharsets.UTF_8) }

private fun sanitizeMusixmatchLyricsBody(value: String): String =
    value
        .lineSequence()
        .takeWhile { line -> !line.contains("This Lyrics is NOT for Commercial use", ignoreCase = true) }
        .joinToString("\n")
        .trim()
