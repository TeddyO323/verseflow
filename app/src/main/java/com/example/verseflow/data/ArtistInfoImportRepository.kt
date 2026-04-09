package com.example.verseflow.data

import android.content.Context
import com.example.verseflow.model.ArtistSearchCandidate
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.net.UnknownHostException

data class ArtistImportPayload(
    val bio: String,
    val photoUri: String?,
)

class ArtistInfoImportRepository(
    private val appContext: Context,
) {
    fun importArtist(name: String): ArtistImportPayload {
        val payload = exactArtistTitles(name)
            .firstNotNullOfOrNull { title ->
                runCatching { fetchWikipediaSummaryPayload(title) }.getOrNull()
                    ?.takeIf { isLikelyArtistBiography(it, name) }
            }
            ?: run {
                val candidateTitle = searchWikipediaArtistCandidate(name)
                    ?: error("VerseFlow found related Wikipedia results, but not a reliable artist biography for $name.")
                fetchWikipediaSummaryPayload(candidateTitle)
            }
        return payload.toImportPayload()
    }

    fun searchCandidates(query: String): List<ArtistSearchCandidate> {
        val encodedQuery = URLEncoder.encode(query.trim(), Charsets.UTF_8.name())
        val url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encodedQuery&utf8=1&format=json&srlimit=8"
        val response = request(url)
        if (response.code !in 200..299) {
            error("Wikipedia search failed with HTTP ${response.code}.")
        }
        val root = runCatching { JSONObject(response.body) }.getOrNull() ?: return emptyList()
        val results = root.optJSONObject("query")?.optJSONArray("search") ?: return emptyList()
        return buildList {
            for (index in 0 until results.length()) {
                val item = results.optJSONObject(index) ?: continue
                val title = item.optString("title").takeIf(String::isNotBlank) ?: continue
                add(
                    ArtistSearchCandidate(
                        pageTitle = title,
                        description = item.optString("snippet").replace(Regex("<[^>]+>"), "").trim(),
                    ),
                )
            }
        }
    }

    fun importArtistFromPageTitle(pageTitle: String): ArtistImportPayload =
        fetchWikipediaSummaryPayload(pageTitle).toImportPayload()

    private fun searchWikipediaArtistCandidate(artistName: String): String? {
        val query = URLEncoder.encode("$artistName singer musician", Charsets.UTF_8.name())
        val url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$query&utf8=1&format=json&srlimit=5"
        val response = request(url)
        if (response.code !in 200..299) {
            error("Wikipedia search failed with HTTP ${response.code}.")
        }
        val root = runCatching { JSONObject(response.body) }.getOrNull() ?: return null
        val results = root.optJSONObject("query")?.optJSONArray("search") ?: return null
        for (index in 0 until results.length()) {
            val item = results.optJSONObject(index) ?: continue
            val title = item.optString("title").takeIf(String::isNotBlank) ?: continue
            val snippet = item.optString("snippet")
            if (looksLikeArtistCandidate(title, snippet, artistName)) {
                return title
            }
        }
        return null
    }

    private fun exactArtistTitles(artistName: String): List<String> = listOf(
        artistName,
        "$artistName (singer)",
        "$artistName (musician)",
        "$artistName (rapper)",
        "$artistName (band)",
    )

    private fun fetchWikipediaSummaryPayload(pageTitle: String): JSONObject {
        val encodedTitle = URLEncoder.encode(pageTitle, Charsets.UTF_8.name()).replace("+", "%20")
        val response = request("https://en.wikipedia.org/api/rest_v1/page/summary/$encodedTitle")
        if (response.code !in 200..299) {
            error("Wikipedia summary failed with HTTP ${response.code}.")
        }
        return runCatching { JSONObject(response.body) }.getOrNull()
            ?: error("Wikipedia summary failed with HTTP ${response.code}.")
    }

    private fun request(url: String): HttpResponse {
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "VerseFlow-Android/1.0")
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            HttpResponse(statusCode, body)
        } catch (_: UnknownHostException) {
            error("Wikipedia search failed. Check your internet connection and try again.")
        } catch (error: Exception) {
            error("Wikipedia search failed: ${error.message ?: "Unknown network error."}")
        } finally {
            connection.disconnect()
        }
    }

    private fun firstBiographyParagraph(extract: String?): String? =
        extract
            ?.split("\n\n")
            ?.map(String::trim)
            ?.firstOrNull { paragraph ->
                paragraph.length > 80 &&
                    !paragraph.startsWith("This is a list of", ignoreCase = true) &&
                    !paragraph.contains("album by", ignoreCase = true) &&
                    !paragraph.contains("song by", ignoreCase = true)
            }

    private fun isLikelyArtistBiography(payload: JSONObject, artistName: String): Boolean {
        val title = payload.optString("title")
        val description = payload.optString("description")
        val extract = payload.optString("extract")
        return looksLikeArtistCandidate(title, "$description $extract", artistName)
    }

    private fun looksLikeArtistCandidate(title: String, snippet: String, artistName: String): Boolean {
        val normalizedTitle = title.lowercase()
        val normalizedSnippet = snippet.lowercase()
        val normalizedArtist = artistName.lowercase()
        if (!normalizedTitle.contains(normalizedArtist) && !normalizedSnippet.contains(normalizedArtist)) return false
        if (
            normalizedTitle.startsWith("list of") ||
            normalizedTitle.contains("discography") ||
            normalizedTitle.contains("album") ||
            normalizedTitle.contains("song") ||
            normalizedTitle.contains("soundtrack")
        ) return false
        if (
            normalizedSnippet.contains("album by") ||
            normalizedSnippet.contains("song by") ||
            normalizedSnippet.contains("list of")
        ) return false
        return normalizedSnippet.contains("singer") ||
            normalizedSnippet.contains("musician") ||
            normalizedSnippet.contains("artist") ||
            normalizedSnippet.contains("rapper") ||
            normalizedSnippet.contains("band") ||
            normalizedSnippet.contains("songwriter")
    }

    private fun downloadArtistImage(url: String): String? {
        val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
        return runCatching {
            connection.requestMethod = "GET"
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            if (connection.responseCode !in 200..299) return null
            val directory = File(appContext.filesDir, "artist-images").apply { mkdirs() }
            val fileName = "artist_${System.currentTimeMillis()}.jpg"
            val outputFile = File(directory, fileName)
            connection.inputStream.use { input ->
                outputFile.outputStream().use { output -> input.copyTo(output) }
            }
            outputFile.toURI().toString()
        }.getOrNull().also {
            connection.disconnect()
        }
    }

    private data class HttpResponse(
        val code: Int,
        val body: String,
    )

    private fun JSONObject.imageSource(): String? =
        optJSONObject("originalimage")?.optString("source")?.takeIf(String::isNotBlank)
            ?: optJSONObject("thumbnail")?.optString("source")?.takeIf(String::isNotBlank)

    private fun JSONObject.toImportPayload(): ArtistImportPayload {
        val bio = firstBiographyParagraph(optString("extract"))
            ?: error("Wikipedia found a page, but it didn't return a usable bio.")
        val photoUri = imageSource()
            ?.let(::downloadArtistImage)
        return ArtistImportPayload(
            bio = bio,
            photoUri = photoUri,
        )
    }
}
