package com.example.verseflow.data

import android.net.Uri
import com.example.verseflow.model.LyricLine
import com.example.verseflow.model.LyricsSearchCandidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.abs

sealed interface LyricsLookupResult {
    data class Found(
        val syncedLyrics: List<LyricLine>,
        val plainLyrics: List<String>,
        val attribution: String,
    ) : LyricsLookupResult

    data object NotFound : LyricsLookupResult
}

class LrcLibLyricsRepository {
    private val lyricsOvhFallbackRepository = LyricsOvhFallbackRepository()

    suspend fun lookup(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): LyricsLookupResult = withContext(Dispatchers.IO) {
        val primaryResult = lookupLrcLib(
            title = title,
            artistName = artistName,
            albumTitle = albumTitle,
            durationMs = durationMs,
        )
        if (primaryResult is LyricsLookupResult.Found) {
            return@withContext primaryResult
        }

        lyricsOvhFallbackRepository.lookup(
            title = title,
            artistName = artistName,
            albumTitle = albumTitle,
            durationMs = durationMs,
        )
    }

    suspend fun searchCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): List<LyricsSearchCandidate> = withContext(Dispatchers.IO) {
        val primaryCandidates = searchLrcLibCandidates(
            title = title,
            artistName = artistName,
            albumTitle = albumTitle,
            durationMs = durationMs,
        )
        val fallbackCandidates = lyricsOvhFallbackRepository.searchCandidates(
            title = title,
            artistName = artistName,
            albumTitle = albumTitle,
            durationMs = durationMs,
        )

        (primaryCandidates + fallbackCandidates)
            .distinctBy { normalizeLookupValue("${it.title} ${it.artistName} ${it.albumTitle.orEmpty()} ${it.sourceLabel}") }
            .sortedWith(
                compareBy<LyricsSearchCandidate> { if (it.hasSyncedLyrics) 0 else 1 }
                    .thenBy { it.matchScore }
                    .thenBy { it.title.lowercase() },
            )
            .take(12)
    }

    private fun lookupLrcLib(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): LyricsLookupResult {
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
                        )?.let { payload ->
                            payload.toScoredMatch(
                                requestedTitle = title,
                                requestedArtist = artistName,
                                requestedAlbum = albumTitle,
                                durationMs = durationMs,
                                attribution = "Lyrics via LRCLIB",
                            )
                        }?.let(::add)
                    }
                }
            }
        }
        val exactMatch = exactMatches.bestResult()

        if (exactMatch?.result?.syncedLyrics?.isNotEmpty() == true) {
            return exactMatch.result
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
            val searchResults = requestArray(
                path = "search",
                params = mapOf("q" to query),
            )
            bestMatch(
                results = searchResults,
                requestedTitle = title,
                requestedArtist = artistName,
                requestedAlbum = albumTitle,
                durationMs = durationMs,
                attribution = "Lyrics via LRCLIB",
            )
        }
        val searchMatch = searchMatches.bestResult()

        return when {
            searchMatch?.result?.syncedLyrics?.isNotEmpty() == true -> searchMatch.result
            exactMatch != null -> exactMatch.result
            searchMatch != null -> searchMatch.result
            else -> LyricsLookupResult.NotFound
        }
    }

    private fun searchLrcLibCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): List<LyricsSearchCandidate> {
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
                        )?.toManualCandidate(
                            requestedTitle = title,
                            requestedArtist = artistName,
                            requestedAlbum = albumTitle,
                            durationMs = durationMs,
                            sourceLabel = "LRCLIB",
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

        val searchMatches = buildList {
            searchQueries.forEach { query ->
                val results = requestArray(
                    path = "search",
                    params = mapOf("q" to query),
                )
                (0 until results.length())
                    .mapNotNull(results::optJSONObject)
                    .mapNotNull { candidate ->
                        candidate.toManualCandidate(
                            requestedTitle = title,
                            requestedArtist = artistName,
                            requestedAlbum = albumTitle,
                            durationMs = durationMs,
                            sourceLabel = "LRCLIB",
                            attribution = "Lyrics via LRCLIB",
                        )
                    }
                    .forEach(::add)
            }
        }

        return (exactMatches + searchMatches)
            .distinctBy { normalizeLookupValue("${it.title} ${it.artistName} ${it.albumTitle.orEmpty()}") }
            .sortedWith(
                compareBy<LyricsSearchCandidate> { if (it.hasSyncedLyrics) 0 else 1 }
                    .thenBy { it.matchScore }
                    .thenBy { it.title.lowercase() },
            )
            .take(10)
    }

    private fun List<ScoredLyricsMatch>.bestResult(): ScoredLyricsMatch? =
        sortedWith(
            compareBy<ScoredLyricsMatch> { if (it.result.syncedLyrics.isNotEmpty()) 0 else 1 }
                .thenBy { it.score }
                .thenByDescending { it.result.syncedLyrics.size }
                .thenByDescending { it.result.plainLyrics.size },
        ).firstOrNull()

    private fun JSONObject.toScoredMatch(
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        attribution: String,
    ): ScoredLyricsMatch? {
        val result = parseResult(payload = this, attribution = attribution) as? LyricsLookupResult.Found ?: return null
        val metadata = CandidateMetadata(
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
            result = result,
        )
    }

    private fun JSONObject.toManualCandidate(
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        sourceLabel: String,
        attribution: String,
    ): LyricsSearchCandidate? {
        val result = parseResult(payload = this, attribution = attribution) as? LyricsLookupResult.Found ?: return null
        val metadata = CandidateMetadata(
            title = optString("trackName").ifBlank { optString("name") },
            artist = optString("artistName"),
            album = optString("albumName").takeIf(String::isNotBlank),
            durationSeconds = optDouble("duration").takeIf { !it.isNaN() && it > 0.0 },
        )
        return createManualCandidate(
            metadata = metadata,
            requestedTitle = requestedTitle,
            requestedArtist = requestedArtist,
            requestedAlbum = requestedAlbum,
            durationMs = durationMs,
            result = result,
            sourceLabel = sourceLabel,
        )
    }

    private fun parseResult(
        payload: JSONObject,
        attribution: String,
    ): LyricsLookupResult {
        val syncedLyrics = payload.optString("syncedLyrics")
            .takeIf(String::isNotBlank)
            ?.let(::parseSyncedLyrics)
            .orEmpty()
        val plainLyrics = payload.optString("plainLyrics")
            .takeIf(String::isNotBlank)
            ?.let(::parsePlainLyrics)
            .orEmpty()

        if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) {
            return LyricsLookupResult.NotFound
        }

        return LyricsLookupResult.Found(
            syncedLyrics = syncedLyrics,
            plainLyrics = if (plainLyrics.isNotEmpty()) plainLyrics else syncedLyrics.map(LyricLine::text),
            attribution = attribution,
        )
    }

    private fun parseSyncedLyrics(rawLyrics: String): List<LyricLine> {
        val timestampPattern = Regex("""\[(\d{1,2}):(\d{2})(?:[.:,](\d{1,3}))?]""")
        return rawLyrics
            .lineSequence()
            .flatMap { rawLine ->
                val line = rawLine.trim()
                val matches = timestampPattern.findAll(line).toList()
                if (matches.isEmpty()) return@flatMap emptySequence()
                val lyricText = timestampPattern.replace(line, "").trim()
                if (lyricText.isBlank()) return@flatMap emptySequence()
                matches.asSequence().map { match ->
                    val minutes = match.groupValues[1].toLong()
                    val seconds = match.groupValues[2].toLong()
                    val fraction = match.groupValues[3]
                    val fractionMs = when (fraction.length) {
                        1 -> fraction.toLong() * 100L
                        2 -> fraction.toLong() * 10L
                        3 -> fraction.toLong()
                        else -> 0L
                    }
                    LyricLine(
                        timestampMs = (minutes * 60_000L) + (seconds * 1_000L) + fractionMs,
                        text = lyricText,
                    )
                }
            }
            .distinctBy { "${it.timestampMs}:${it.text}" }
            .sortedBy(LyricLine::timestampMs)
            .toList()
    }

    private fun parsePlainLyrics(rawLyrics: String): List<String> = rawLyrics
        .lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toList()

    private fun bestMatch(
        results: JSONArray,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        attribution: String,
    ): ScoredLyricsMatch? {
        if (results.length() == 0) return null

        val candidates = (0 until results.length())
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

        return candidates.bestResult()
    }

    private fun createScoredMatch(
        metadata: CandidateMetadata,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        result: LyricsLookupResult.Found,
    ): ScoredLyricsMatch? {
        val titleMatch = bestTitleMatch(requestedTitle, metadata.title.ifBlank { requestedTitle })
        val artistMatch = bestArtistMatch(requestedArtist, metadata.artist.ifBlank { requestedArtist })
        val albumMatch = if (requestedAlbum != null && metadata.album != null) {
            bestAlbumMatch(requestedAlbum, metadata.album)
        } else {
            null
        }
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
            val maxDelta = if (result.syncedLyrics.isNotEmpty()) 18.0 else 12.0
            if (durationDeltaSeconds > maxDelta) return null
        }

        var score = 0.0
        score += similarityPenalty(titleMatch.expected, titleMatch.actual, titleOverlap)
        score += similarityPenalty(artistMatch.expected, artistMatch.actual, artistOverlap)
        if (albumMatch != null) {
            score += similarityPenalty(albumMatch.expected, albumMatch.actual, albumOverlap) * 0.55
        }
        durationDeltaSeconds?.let { delta ->
            score += delta * if (result.syncedLyrics.isNotEmpty()) 0.12 else 0.18
        }
        if (result.syncedLyrics.isEmpty()) {
            score += 0.9
        }
        val maxAcceptedScore = if (result.syncedLyrics.isNotEmpty()) 6.8 else 5.3
        if (score > maxAcceptedScore) return null

        return ScoredLyricsMatch(
            result = result,
            score = score,
        )
    }

    private fun createManualCandidate(
        metadata: CandidateMetadata,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
        result: LyricsLookupResult.Found,
        sourceLabel: String,
    ): LyricsSearchCandidate? {
        val titleMatch = bestTitleMatch(requestedTitle, metadata.title.ifBlank { requestedTitle })
        val artistMatch = bestArtistMatch(requestedArtist, metadata.artist.ifBlank { requestedArtist })
        val albumMatch = if (requestedAlbum != null && metadata.album != null) {
            bestAlbumMatch(requestedAlbum, metadata.album)
        } else {
            null
        }
        val titleOverlap = titleMatch.overlap
        val artistOverlap = artistMatch.overlap
        val albumOverlap = albumMatch?.overlap ?: 1.0
        val durationDeltaSeconds = if (durationMs > 0L && metadata.durationSeconds != null) {
            abs(metadata.durationSeconds - (durationMs / 1_000.0))
        } else {
            null
        }

        if (artistOverlap < 0.42) return null
        if (titleOverlap < 0.38 && !shareCoreText(titleMatch.expected, titleMatch.actual)) return null
        if (requestedAlbum != null && metadata.album != null && albumOverlap < 0.18 && titleOverlap < 0.84) return null
        if (durationDeltaSeconds != null) {
            val maxDelta = if (result.syncedLyrics.isNotEmpty()) 36.0 else 24.0
            if (durationDeltaSeconds > maxDelta) return null
        }

        var score = 0.0
        score += similarityPenalty(titleMatch.expected, titleMatch.actual, titleOverlap)
        score += similarityPenalty(artistMatch.expected, artistMatch.actual, artistOverlap)
        if (albumMatch != null) {
            score += similarityPenalty(albumMatch.expected, albumMatch.actual, albumOverlap) * 0.45
        }
        durationDeltaSeconds?.let { delta ->
            score += delta * if (result.syncedLyrics.isNotEmpty()) 0.08 else 0.12
        }
        if (result.syncedLyrics.isEmpty()) {
            score += 0.6
        }
        if (score > 10.5) return null

        val resolvedTitle = metadata.title.ifBlank { requestedTitle }
        val resolvedArtist = metadata.artist.ifBlank { requestedArtist }

        return LyricsSearchCandidate(
            id = normalizeLookupValue("$sourceLabel|$resolvedTitle|$resolvedArtist|${metadata.album.orEmpty()}"),
            title = resolvedTitle,
            artistName = resolvedArtist,
            albumTitle = metadata.album,
            durationMs = metadata.durationSeconds?.times(1_000)?.toLong(),
            sourceLabel = sourceLabel,
            attribution = result.attribution,
            syncedLyrics = result.syncedLyrics,
            plainLyrics = result.plainLyrics,
            matchScore = score,
        )
    }

    private fun similarityPenalty(expected: String, actual: String, overlap: Double): Double = when {
        expected == actual -> 0.0
        overlap >= 0.94 -> 0.35
        overlap >= 0.82 -> 1.0
        overlap >= 0.68 -> 2.3
        shareCoreText(expected, actual) -> 2.8
        else -> 6.4
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
        return runCatching { JSONArray(response.body) }.getOrElse { JSONArray() }
    }

    private fun request(
        path: String,
        params: Map<String, String>,
    ): HttpResponse? {
        val url = Uri.parse("https://lrclib.net/api/$path")
            .buildUpon()
            .apply {
                params.forEach { (key, value) ->
                    appendQueryParameter(key, value)
                }
            }
            .build()
            .toString()

        val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 6_000
            connection.readTimeout = 6_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "VerseFlow/1.0")

            val stream = when (connection.responseCode) {
                in 200..299 -> connection.inputStream
                else -> connection.errorStream
            } ?: return HttpResponse(connection.responseCode, "")

            HttpResponse(
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

private data class CandidateMetadata(
    val title: String,
    val artist: String,
    val album: String?,
    val durationSeconds: Double?,
)

private data class ScoredLyricsMatch(
    val result: LyricsLookupResult.Found,
    val score: Double,
)

private data class HttpResponse(
    val code: Int,
    val body: String,
)
