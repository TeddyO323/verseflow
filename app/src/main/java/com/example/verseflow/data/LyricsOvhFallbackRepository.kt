package com.example.verseflow.data

import android.net.Uri
import com.example.verseflow.model.LyricsSearchCandidate
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LyricsOvhFallbackRepository {
    fun lookup(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): LyricsLookupResult {
        val titleCandidates = titleCandidateStrings(title)
        val artistCandidates = artistCandidateStrings(artistName)

        directPairs(titleCandidates, artistCandidates).forEach { (artistCandidate, titleCandidate) ->
            requestLyrics(
                artistName = artistCandidate,
                title = titleCandidate,
            )?.let { lyrics ->
                val plainLyrics = parsePlainLyrics(lyrics)
                if (plainLyrics.isNotEmpty()) {
                    return LyricsLookupResult.Found(
                        syncedLyrics = emptyList(),
                        plainLyrics = plainLyrics,
                        attribution = "Lyrics via lyrics.ovh",
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

        val suggestion = suggestionQueries
            .mapNotNull { query ->
                bestSuggestionMatch(
                    results = requestSuggestions(query),
                    requestedTitle = title,
                    requestedArtist = artistName,
                    requestedAlbum = albumTitle,
                    durationMs = durationMs,
                )
            }
            .minByOrNull(SuggestionMatch::score)

        if (suggestion != null) {
            requestLyrics(
                artistName = suggestion.artistName,
                title = suggestion.title,
            )?.let { lyrics ->
                val plainLyrics = parsePlainLyrics(lyrics)
                if (plainLyrics.isNotEmpty()) {
                    return LyricsLookupResult.Found(
                        syncedLyrics = emptyList(),
                        plainLyrics = plainLyrics,
                        attribution = "Lyrics via lyrics.ovh",
                    )
                }
            }
        }

        return LyricsLookupResult.NotFound
    }

    fun searchCandidates(
        title: String,
        artistName: String,
        albumTitle: String?,
        durationMs: Long,
    ): List<LyricsSearchCandidate> {
        val titleCandidates = titleCandidateStrings(title)
        val artistCandidates = artistCandidateStrings(artistName)

        val exactMatches = buildList {
            directPairs(titleCandidates, artistCandidates).forEach { (artistCandidate, titleCandidate) ->
                requestLyrics(
                    artistName = artistCandidate,
                    title = titleCandidate,
                )?.let { lyrics ->
                    val plainLyrics = parsePlainLyrics(lyrics)
                    if (plainLyrics.isNotEmpty()) {
                        add(
                            LyricsSearchCandidate(
                                id = normalizeLookupValue("lyrics.ovh|$titleCandidate|$artistCandidate"),
                                title = titleCandidate,
                                artistName = artistCandidate,
                                albumTitle = albumTitle,
                                durationMs = durationMs.takeIf { it > 0L },
                                sourceLabel = "lyrics.ovh",
                                attribution = "Lyrics via lyrics.ovh",
                                plainLyrics = plainLyrics,
                                matchScore = 0.35,
                            ),
                        )
                    }
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

        val suggestionMatches = suggestionQueries
            .flatMap { query ->
                val results = requestSuggestions(query)
                (0 until results.length())
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
                            requestedTitle = title,
                            requestedArtist = artistName,
                            requestedAlbum = albumTitle,
                            durationMs = durationMs,
                            strict = false,
                        )
                    }
            }
            .distinctBy { normalizeLookupValue("${it.title}|${it.artistName}") }
            .sortedBy(SuggestionMatch::score)
            .take(8)
            .mapNotNull { match ->
                requestLyrics(
                    artistName = match.artistName,
                    title = match.title,
                )?.let { lyrics ->
                    val plainLyrics = parsePlainLyrics(lyrics)
                    if (plainLyrics.isEmpty()) return@let null
                    LyricsSearchCandidate(
                        id = normalizeLookupValue("lyrics.ovh|${match.title}|${match.artistName}"),
                        title = match.title,
                        artistName = match.artistName,
                        albumTitle = albumTitle,
                        durationMs = durationMs.takeIf { it > 0L },
                        sourceLabel = "lyrics.ovh",
                        attribution = "Lyrics via lyrics.ovh",
                        plainLyrics = plainLyrics,
                        matchScore = match.score,
                    )
                }
            }

        return (exactMatches + suggestionMatches)
            .distinctBy { normalizeLookupValue("${it.title} ${it.artistName}") }
            .sortedBy(LyricsSearchCandidate::matchScore)
            .take(8)
    }

    private fun bestSuggestionMatch(
        results: JSONArray,
        requestedTitle: String,
        requestedArtist: String,
        requestedAlbum: String?,
        durationMs: Long,
    ): SuggestionMatch? {
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
            .minByOrNull(SuggestionMatch::score)
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
        strict: Boolean = true,
    ): SuggestionMatch? {
        val titleMatch = bestTitleMatch(requestedTitle, trackTitle)
        val artistMatch = bestArtistMatch(requestedArtist, artistName)
        val albumMatch = if (requestedAlbum != null && albumTitle != null) {
            bestAlbumMatch(requestedAlbum, albumTitle)
        } else {
            null
        }
        val titleOverlap = titleMatch.overlap
        val artistOverlap = artistMatch.overlap
        val albumOverlap = albumMatch?.overlap ?: 1.0
        val durationDelta = if (durationMs > 0L && durationSeconds != null) {
            kotlin.math.abs(durationSeconds - (durationMs / 1_000.0))
        } else {
            null
        }

        if (strict) {
            if (artistOverlap < 0.72) return null
            if (titleOverlap < 0.68 && !shareCoreText(titleMatch.expected, titleMatch.actual)) return null
            if (requestedAlbum != null && albumTitle != null && albumOverlap < 0.34 && titleOverlap < 0.92) return null
            if (durationDelta != null && durationDelta > 14.0) return null
        } else {
            if (artistOverlap < 0.42) return null
            if (titleOverlap < 0.38 && !shareCoreText(titleMatch.expected, titleMatch.actual)) return null
            if (requestedAlbum != null && albumTitle != null && albumOverlap < 0.18 && titleOverlap < 0.84) return null
            if (durationDelta != null && durationDelta > 28.0) return null
        }

        var score = 0.0
        score += similarityPenalty(titleMatch.expected, titleMatch.actual, titleOverlap)
        score += similarityPenalty(artistMatch.expected, artistMatch.actual, artistOverlap)
        if (albumMatch != null) {
            score += similarityPenalty(albumMatch.expected, albumMatch.actual, albumOverlap) * 0.55
        }
        durationDelta?.let { score += it * 0.16 }
        val maxScore = if (strict) 5.4 else 10.0
        if (score > maxScore) return null

        return SuggestionMatch(
            title = trackTitle,
            artistName = artistName,
            score = score,
        )
    }

    private fun directPairs(
        titleCandidates: List<String>,
        artistCandidates: List<String>,
    ): List<Pair<String, String>> = buildList {
        artistCandidates.forEach { artistCandidate ->
            titleCandidates.forEach { titleCandidate ->
                add(artistCandidate to titleCandidate)
            }
        }
    }.distinct()

    private fun requestSuggestions(query: String): JSONArray {
        val response = requestArray(
            baseUrl = "https://api.lyrics.ovh",
            path = "suggest/$query",
        )
        return response.optJSONArray("data") ?: JSONArray()
    }

    private fun requestLyrics(
        artistName: String,
        title: String,
    ): String? {
        val response = requestObject(
            baseUrl = "https://api.lyrics.ovh",
            path = "v1/$artistName/$title",
        ) ?: return null
        return response.optString("lyrics").takeIf(String::isNotBlank)
    }

    private fun requestObject(
        baseUrl: String,
        path: String,
    ): JSONObject? {
        val response = request(baseUrl = baseUrl, path = path) ?: return null
        if (response.code !in 200..299) return null
        return runCatching { JSONObject(response.body) }.getOrNull()
    }

    private fun requestArray(
        baseUrl: String,
        path: String,
    ): JSONObject {
        val response = request(baseUrl = baseUrl, path = path) ?: return JSONObject()
        if (response.code !in 200..299) return JSONObject()
        return runCatching { JSONObject(response.body) }.getOrElse { JSONObject() }
    }

    private fun request(
        baseUrl: String,
        path: String,
    ): LyricsOvhHttpResponse? {
        val url = "$baseUrl/${Uri.encode(path, "/")}"

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
            } ?: return LyricsOvhHttpResponse(connection.responseCode, "")

            LyricsOvhHttpResponse(
                code = connection.responseCode,
                body = stream.bufferedReader().use { it.readText() },
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun parsePlainLyrics(rawLyrics: String): List<String> = rawLyrics
        .lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toList()

    private fun similarityPenalty(expected: String, actual: String, overlap: Double): Double = when {
        expected == actual -> 0.0
        overlap >= 0.94 -> 0.35
        overlap >= 0.82 -> 1.0
        overlap >= 0.68 -> 2.3
        shareCoreText(expected, actual) -> 2.8
        else -> 6.4
    }
}

private data class SuggestionMatch(
    val title: String,
    val artistName: String,
    val score: Double,
)

private data class LyricsOvhHttpResponse(
    val code: Int,
    val body: String,
)
