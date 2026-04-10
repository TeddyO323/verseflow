package com.example.verseflow.desktop

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.prefs.Preferences
import javax.imageio.ImageIO
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class DesktopLyricLine(
    val timestampMs: Long,
    val text: String,
)

data class DesktopTrack(
    val id: String,
    val path: String,
    val isrc: String? = null,
    val addedAtMs: Long = 0L,
    val releaseDate: String? = null,
    val title: String,
    val artist: String,
    val artistCredits: List<String> = listOf(artist),
    val albumArtist: String = artist,
    val album: String,
    val genre: String,
    val durationMs: Long,
    val mood: String,
    val palette: List<Color>,
    val lyrics: List<DesktopLyricLine> = emptyList(),
    val plainLyrics: List<String> = lyrics.map(DesktopLyricLine::text),
    val lyricsAttribution: String? = null,
    val artworkBytes: ByteArray? = null,
)

data class DesktopThemePreset(
    val name: String,
    val description: String,
)

data class DesktopLibraryUiState(
    val sourcePaths: List<String> = emptyList(),
    val tracks: List<DesktopTrack> = emptyList(),
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
) {
    val rootPath: String?
        get() = sourcePaths.firstOrNull()
}

data class DesktopAlbumSummary(
    val title: String,
    val artist: String,
    val genre: String,
    val releaseDate: String? = null,
    val trackCount: Int,
    val durationMs: Long,
    val newestAddedAtMs: Long,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
)

data class DesktopArtistSummary(
    val name: String,
    val trackCount: Int,
    val genres: List<String>,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
)

class DesktopLibraryStore {
    private val preferences = Preferences.userRoot().node("com/example/verseflow/desktop")

    fun loadLibraryPaths(): List<String> {
        val raw = preferences.get(KEY_LIBRARY_PATHS, null)?.trim().orEmpty()
        if (raw.isNotBlank()) {
            return runCatching {
                val payload = org.json.JSONArray(raw)
                (0 until payload.length())
                    .map { index -> payload.optString(index).trim() }
                    .filter(String::isNotBlank)
                    .distinct()
            }.getOrDefault(emptyList())
        }

        return loadRootPath()?.let(::listOf).orEmpty()
    }

    fun saveLibraryPaths(paths: List<String>) {
        val normalized = paths
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinct()
        if (normalized.isEmpty()) {
            preferences.remove(KEY_LIBRARY_PATHS)
            preferences.remove(KEY_ROOT_PATH)
        } else {
            preferences.put(KEY_LIBRARY_PATHS, org.json.JSONArray(normalized).toString())
            preferences.put(KEY_ROOT_PATH, normalized.first())
        }
    }

    fun loadRootPath(): String? =
        preferences.get(KEY_ROOT_PATH, null)
            ?.trim()
            ?.takeIf(String::isNotEmpty)

    fun saveRootPath(path: String?) {
        saveLibraryPaths(path?.let(::listOf).orEmpty())
    }

    fun loadSidebarCollapsed(): Boolean =
        preferences.getBoolean(KEY_SIDEBAR_COLLAPSED, false)

    fun saveSidebarCollapsed(collapsed: Boolean) {
        preferences.putBoolean(KEY_SIDEBAR_COLLAPSED, collapsed)
    }

    fun loadFavoriteTrackPaths(): List<String> =
        preferences.get(KEY_FAVORITE_TRACK_PATHS, null)
            ?.split(FavoritesSeparator)
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            ?: emptyList()

    fun saveFavoriteTrackPaths(paths: List<String>) {
        if (paths.isEmpty()) {
            preferences.remove(KEY_FAVORITE_TRACK_PATHS)
        } else {
            preferences.put(KEY_FAVORITE_TRACK_PATHS, paths.distinct().joinToString(FavoritesSeparator))
        }
    }

    private companion object {
        const val KEY_ROOT_PATH = "library_root_path"
        const val KEY_LIBRARY_PATHS = "library_paths_json"
        const val KEY_SIDEBAR_COLLAPSED = "sidebar_collapsed"
        const val KEY_FAVORITE_TRACK_PATHS = "favorite_track_paths"
        const val FavoritesSeparator = "||"
    }
}

suspend fun scanDesktopLibrary(root: Path): List<DesktopTrack> = withContext(Dispatchers.IO) {
    scanDesktopLibrary(listOf(root))
}

suspend fun scanDesktopLibrary(roots: List<Path>): List<DesktopTrack> = withContext(Dispatchers.IO) {
    val normalizedRoots = roots
        .mapNotNull { root ->
            runCatching { root.toAbsolutePath().normalize() }.getOrNull()
        }
        .distinct()
        .filter(Files::exists)
    if (normalizedRoots.isEmpty()) return@withContext emptyList()

    normalizedRoots
        .asSequence()
        .flatMap { root ->
            Files.walk(root).use { stream ->
                stream
                    .iterator()
                    .asSequence()
                    .filter { path -> path.isRegularFile() && path.isSupportedAudioFile() }
                    .toList()
                    .asSequence()
            }
        }
        .distinctBy { path -> runCatching { path.toAbsolutePath().normalize().toString() }.getOrDefault(path.toString()) }
        .mapNotNull(::readDesktopTrack)
        .sortedWith(
            compareBy<DesktopTrack>(
                { it.artist.lowercase() },
                { it.album.lowercase() },
                { it.title.lowercase() },
            )
        )
        .toList()
}

fun summarizeAlbums(tracks: List<DesktopTrack>): List<DesktopAlbumSummary> =
    tracks
        .groupBy { desktopAlbumKey(it.albumArtist, it.album) }
        .values
        .map { groupedTracks ->
            val first = groupedTracks.first()
            DesktopAlbumSummary(
                title = first.album,
                artist = first.albumArtist,
                genre = groupedTracks.map(DesktopTrack::genre).groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Unclassified",
                releaseDate = groupedTracks.mapNotNull(DesktopTrack::releaseDate).firstOrNull(),
                trackCount = groupedTracks.size,
                durationMs = groupedTracks.sumOf(DesktopTrack::durationMs),
                newestAddedAtMs = groupedTracks.maxOfOrNull(DesktopTrack::addedAtMs) ?: 0L,
                palette = first.palette,
                artworkBytes = groupedTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
            )
        }
        .sortedBy { it.title.lowercase() }

fun summarizeArtists(tracks: List<DesktopTrack>): List<DesktopArtistSummary> =
    tracks
        .flatMap { track ->
            track.artistCredits
                .ifEmpty { listOf(track.artist) }
                .distinct()
                .map { artistName -> artistName to track }
        }
        .groupBy(
            keySelector = { (artistName, _) -> artistName },
            valueTransform = { (_, track) -> track },
        )
        .map { (artist, groupedTracks) ->
            DesktopArtistSummary(
                name = artist,
                trackCount = groupedTracks.distinctBy(DesktopTrack::id).size,
                genres = groupedTracks.map(DesktopTrack::genre).distinct().sorted(),
                palette = groupedTracks.first().palette,
                artworkBytes = null,
            )
        }
        .sortedBy { it.name.lowercase() }

fun summarizeGenres(tracks: List<DesktopTrack>): List<Pair<String, Int>> =
    tracks
        .groupingBy { it.genre.ifBlank { "Unclassified" } }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }

private fun readDesktopTrack(path: Path): DesktopTrack? {
    val seed = path.toString()
    val fileNameTitle = prettifyName(path.name.substringBeforeLast('.'))
    val albumFallback = path.parent?.fileName?.toString()?.let(::prettifyName).orEmpty().ifBlank { "Singles" }
    val artistFallback = path.parent?.parent?.fileName?.toString()?.let(::prettifyName).orEmpty().ifBlank { "Unknown Artist" }

    return runCatching {
        val audioFile = AudioFileIO.read(path.toFile())
        val tag = audioFile.tag
        val header = audioFile.audioHeader

        val title = tag?.getFirst(FieldKey.TITLE).orEmpty().ifBlank { fileNameTitle }
        val artist = tag?.getFirst(FieldKey.ARTIST).orEmpty().ifBlank { artistFallback }
        val artistCredits = buildDesktopArtistCredits(
            primaryArtist = artist,
            title = title,
        ).ifEmpty { listOf(artist) }
        val albumArtist = tag?.getFirst(FieldKey.ALBUM_ARTIST).orEmpty().ifBlank {
            artistCredits.firstOrNull().orEmpty().ifBlank { artist }
        }
        val album = tag?.getFirst(FieldKey.ALBUM).orEmpty().ifBlank { albumFallback }
        val genre = tag?.getFirst(FieldKey.GENRE).orEmpty().ifBlank { "Unclassified" }
        val releaseDate = tag?.getFirst(FieldKey.YEAR).orEmpty().trim().ifBlank { null }
        val durationMs = ((header?.trackLength ?: 0) * 1000L).coerceAtLeast(0L)
        val localLyrics = loadDesktopLocalLyrics(path)
        val artworkBytes = tag?.firstArtwork?.binaryData?.takeIf { it.isNotEmpty() }
        val addedAtMs = runCatching { Files.getLastModifiedTime(path).toMillis() }.getOrDefault(0L)

        DesktopTrack(
            id = stableId(seed),
            path = path.toString(),
            isrc = tag?.getFirst(FieldKey.ISRC).orEmpty().ifBlank { null },
            addedAtMs = addedAtMs,
            releaseDate = releaseDate,
            title = title,
            artist = artist,
            artistCredits = artistCredits,
            albumArtist = albumArtist,
            album = album,
            genre = genre,
            durationMs = durationMs,
            mood = genre.takeIf { it.isNotBlank() && it != "Unclassified" } ?: "Local file",
            palette = paletteFromArtworkBytes(artworkBytes) ?: paletteFromSeed("$artist::$album::$title"),
            lyrics = localLyrics.syncedLyrics,
            plainLyrics = localLyrics.plainLyrics,
            lyricsAttribution = localLyrics.attribution,
            artworkBytes = artworkBytes,
        )
    }.getOrElse {
        val localLyrics = loadDesktopLocalLyrics(path)
        val artistCredits = buildDesktopArtistCredits(
            primaryArtist = artistFallback,
            title = fileNameTitle,
        ).ifEmpty { listOf(artistFallback) }
        DesktopTrack(
            id = stableId(seed),
            path = path.toString(),
            isrc = null,
            addedAtMs = runCatching { Files.getLastModifiedTime(path).toMillis() }.getOrDefault(0L),
            releaseDate = null,
            title = fileNameTitle,
            artist = artistFallback,
            artistCredits = artistCredits,
            albumArtist = artistCredits.firstOrNull().orEmpty().ifBlank { artistFallback },
            album = albumFallback,
            genre = "Unclassified",
            durationMs = 0L,
            mood = "Local file",
            palette = paletteFromSeed(seed),
            lyrics = localLyrics.syncedLyrics,
            plainLyrics = localLyrics.plainLyrics,
            lyricsAttribution = localLyrics.attribution,
        )
    }
}

private fun Path.isSupportedAudioFile(): Boolean =
    extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS

fun desktopAlbumKey(albumArtist: String, albumTitle: String): String =
    "${albumArtist.normalizeAlbumKeyPart()}::${albumTitle.normalizeAlbumKeyPart()}"

private fun String.normalizeAlbumKeyPart(): String =
    lowercase()
        .replace(Regex("""\s+"""), " ")
        .trim()

fun buildDesktopArtistCredits(primaryArtist: String, title: String): List<String> =
    (splitDesktopArtists(primaryArtist) + extractFeaturedArtistsFromTitle(title))
        .filter(String::isNotBlank)
        .distinct()

fun splitDesktopArtists(raw: String): List<String> =
    raw
        .replace(Regex("""(?i)\bfeat(?:uring)?\.?\b"""), "|")
        .replace(Regex("""(?i)\bft\.?\b"""), "|")
        .replace(Regex("""(?i)\band\b"""), "|")
        .replace("&", "|")
        .replace("/", "|")
        .replace(",", "|")
        .split("|")
        .map { artist ->
            artist
                .replace(Regex("""\s+"""), " ")
                .trim()
                .trim('(', ')', '[', ']')
        }
        .filter(String::isNotBlank)
        .distinct()

private fun extractFeaturedArtistsFromTitle(title: String): List<String> =
    FeatureArtistPattern
        .findAll(title)
        .flatMap { match ->
            splitDesktopArtists(match.groupValues.getOrNull(1).orEmpty()).asSequence()
        }
        .distinct()
        .toList()

fun stableId(value: String): String =
    value.hashCode().absoluteValue.toString()

private fun paletteFromSeed(seed: String): List<Color> {
    val base = seed.hashCode().absoluteValue
    val options = listOf(
        listOf(Color(0xFF0F1330), Color(0xFF0000FF), Color(0xFF66F2FF)),
        listOf(Color(0xFF120A2A), Color(0xFF4328A8), Color(0xFF8FD4FF)),
        listOf(Color(0xFF07111F), Color(0xFF0D5BFF), Color(0xFF61F2FF)),
        listOf(Color(0xFF0B1020), Color(0xFF2A50FF), Color(0xFFA8D9FF)),
        listOf(Color(0xFF160B26), Color(0xFF5A26FF), Color(0xFF5CCFFF)),
    )
    return options[base % options.size]
}

private fun paletteFromArtworkBytes(artworkBytes: ByteArray?): List<Color>? {
    val bytes = artworkBytes?.takeIf(ByteArray::isNotEmpty) ?: return null
    val image = runCatching { ImageIO.read(ByteArrayInputStream(bytes)) }.getOrNull() ?: return null
    if (image.width <= 0 || image.height <= 0) return null

    val sampleStepX = max(image.width / 18, 1)
    val sampleStepY = max(image.height / 18, 1)
    val sampledColors = buildList {
        for (y in 0 until image.height step sampleStepY) {
            for (x in 0 until image.width step sampleStepX) {
                val argb = image.getRGB(x, y)
                val alpha = (argb ushr 24) and 0xFF
                if (alpha < 28) continue
                val red = (argb ushr 16) and 0xFF
                val green = (argb ushr 8) and 0xFF
                val blue = argb and 0xFF
                add(
                    Color(
                        red = red / 255f,
                        green = green / 255f,
                        blue = blue / 255f,
                        alpha = alpha / 255f,
                    ),
                )
            }
        }
    }
    if (sampledColors.isEmpty()) return null

    val average = sampledColors.averageColor()
    val darkest = sampledColors.minByOrNull(::colorLuminance) ?: average
    val accent = sampledColors.maxByOrNull { colorSaturation(it) * 1.4f + colorLuminance(it) } ?: average

    val base = mixColor(darkest, average, 0.36f)
    val mid = mixColor(average, Color(0xFF121625), 0.18f)
    val highlight = mixColor(accent, average, 0.22f)

    return listOf(
        base.darken(0.28f),
        mid,
        highlight.lighten(0.12f),
    )
}

private fun List<Color>.averageColor(): Color {
    if (isEmpty()) return Color(0xFF101728)

    var red = 0f
    var green = 0f
    var blue = 0f
    var alpha = 0f

    forEach { color ->
        red += color.red
        green += color.green
        blue += color.blue
        alpha += color.alpha
    }

    val count = size.toFloat()
    return Color(
        red = red / count,
        green = green / count,
        blue = blue / count,
        alpha = alpha / count,
    )
}

private fun colorLuminance(color: Color): Float =
    (0.2126f * color.red) + (0.7152f * color.green) + (0.0722f * color.blue)

private fun colorSaturation(color: Color): Float {
    val maxChannel = max(color.red, max(color.green, color.blue))
    val minChannel = min(color.red, min(color.green, color.blue))
    return if (maxChannel == 0f) 0f else (maxChannel - minChannel) / maxChannel
}

private fun mixColor(start: Color, end: Color, fraction: Float): Color {
    val safeFraction = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + ((end.red - start.red) * safeFraction),
        green = start.green + ((end.green - start.green) * safeFraction),
        blue = start.blue + ((end.blue - start.blue) * safeFraction),
        alpha = start.alpha + ((end.alpha - start.alpha) * safeFraction),
    )
}

private fun Color.lighten(amount: Float): Color =
    mixColor(this, Color.White, amount.coerceIn(0f, 1f))

private fun Color.darken(amount: Float): Color =
    mixColor(this, Color.Black, amount.coerceIn(0f, 1f))

private val FeatureArtistPattern = Regex("""(?i)\b(?:feat(?:uring)?|ft)\.?\s+([^\)\]]+)""")

private fun prettifyName(raw: String): String =
    raw.replace('_', ' ')
        .replace('-', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()
        .ifBlank { "Unknown" }

private data class DesktopLocalLyricsPayload(
    val syncedLyrics: List<DesktopLyricLine> = emptyList(),
    val plainLyrics: List<String> = emptyList(),
    val attribution: String? = null,
)

private fun loadDesktopLocalLyrics(path: Path): DesktopLocalLyricsPayload {
    loadLyricsSidecar(path)?.let { return it }
    return loadEmbeddedLyrics(path)
}

private fun loadLyricsSidecar(path: Path): DesktopLocalLyricsPayload? {
    val baseName = path.fileName.toString().substringBeforeLast('.')
    val candidates = listOf("lrc", "txt").map { extension ->
        path.resolveSibling("$baseName.$extension")
    }
    val sidecar = candidates.firstOrNull(Files::exists) ?: return null
    val rawLyrics = runCatching { Files.readString(sidecar) }.getOrNull()?.trim().orEmpty()
    if (rawLyrics.isBlank()) return null

    val syncedLyrics = parseDesktopSyncedLyrics(rawLyrics)
    val plainLyrics = parseDesktopPlainLyrics(rawLyrics, stripTimestamps = true)
    if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) return null

    return DesktopLocalLyricsPayload(
        syncedLyrics = syncedLyrics,
        plainLyrics = if (plainLyrics.isNotEmpty()) plainLyrics else syncedLyrics.map(DesktopLyricLine::text),
        attribution = "Local lyrics file",
    )
}

private fun loadEmbeddedLyrics(path: Path): DesktopLocalLyricsPayload {
    val parsed = runCatching {
        Files.newInputStream(path).use { input ->
            val header = ByteArray(Id3HeaderLength)
            if (!input.readFully(header)) return@use DesktopLocalLyricsPayload()
            if (!header.copyOfRange(0, 3).contentEquals(byteArrayOf('I'.code.toByte(), 'D'.code.toByte(), '3'.code.toByte()))) {
                return@use DesktopLocalLyricsPayload()
            }

            val versionMajor = header[3].toInt() and 0xFF
            if (versionMajor !in 3..4) return@use DesktopLocalLyricsPayload()

            val flags = header[5].toInt() and 0xFF
            val size = synchsafeInt(header, 6)
            if (size <= 0) return@use DesktopLocalLyricsPayload()

            val payload = ByteArray(size)
            if (!input.readFully(payload)) return@use DesktopLocalLyricsPayload()

            val normalizedPayload = if (flags and 0x80 != 0) {
                removeUnsynchronization(payload)
            } else {
                payload
            }

            parseId3Lyrics(versionMajor, normalizedPayload)
        }
    }.getOrDefault(DesktopLocalLyricsPayload())

    return parsed
}

private fun parseId3Lyrics(
    versionMajor: Int,
    tagBytes: ByteArray,
): DesktopLocalLyricsPayload {
    var offset = 0
    var fallback = DesktopLocalLyricsPayload()

    while (offset + 10 <= tagBytes.size) {
        val idBytes = tagBytes.copyOfRange(offset, offset + 4)
        if (idBytes.all { it.toInt() == 0 }) break

        val frameId = idBytes.toString(Charsets.ISO_8859_1)
        val frameSize = if (versionMajor == 4) synchsafeInt(tagBytes, offset + 4) else bigEndianInt(tagBytes, offset + 4)
        if (frameSize <= 0 || offset + 10 + frameSize > tagBytes.size) break

        val frameData = tagBytes.copyOfRange(offset + 10, offset + 10 + frameSize)
        when (frameId) {
            "USLT" -> parseUnsynchronizedLyricsFrame(frameData)?.let { return it }
            "TXXX" -> if (fallback.plainLyrics.isEmpty() && fallback.syncedLyrics.isEmpty()) {
                parseUserTextLyricsFrame(frameData)?.let { fallback = it }
            }
            "COMM" -> if (fallback.plainLyrics.isEmpty() && fallback.syncedLyrics.isEmpty()) {
                parseCommentLyricsFrame(frameData)?.let { fallback = it }
            }
        }

        offset += 10 + frameSize
    }

    return fallback
}

private fun parseUnsynchronizedLyricsFrame(frameData: ByteArray): DesktopLocalLyricsPayload? {
    if (frameData.size <= 4) return null
    val encoding = frameData[0].toInt() and 0xFF
    val charset = charsetForEncoding(encoding) ?: return null
    val descriptionEnd = findTerminator(frameData, start = 4, encoding = encoding)
    val lyricsStart = descriptionEnd + terminatorLength(encoding)
    if (lyricsStart > frameData.size) return null
    return desktopLyricsPayloadFromRaw(
        rawText = decodeText(frameData, lyricsStart, frameData.size - lyricsStart, charset),
        attribution = "Embedded lyrics from local file",
    )
}

private fun parseUserTextLyricsFrame(frameData: ByteArray): DesktopLocalLyricsPayload? {
    if (frameData.isEmpty()) return null
    val encoding = frameData[0].toInt() and 0xFF
    val charset = charsetForEncoding(encoding) ?: return null
    val descriptionEnd = findTerminator(frameData, start = 1, encoding = encoding)
    val description = decodeText(frameData, 1, descriptionEnd - 1, charset)
    if (!descriptionLooksLikeLyrics(description)) return null
    val valueStart = descriptionEnd + terminatorLength(encoding)
    if (valueStart > frameData.size) return null
    return desktopLyricsPayloadFromRaw(
        rawText = decodeText(frameData, valueStart, frameData.size - valueStart, charset),
        attribution = "Embedded lyrics from local file",
    )
}

private fun parseCommentLyricsFrame(frameData: ByteArray): DesktopLocalLyricsPayload? {
    if (frameData.size <= 4) return null
    val encoding = frameData[0].toInt() and 0xFF
    val charset = charsetForEncoding(encoding) ?: return null
    val descriptionEnd = findTerminator(frameData, start = 4, encoding = encoding)
    val description = decodeText(frameData, 4, descriptionEnd - 4, charset)
    if (!descriptionLooksLikeLyrics(description)) return null
    val valueStart = descriptionEnd + terminatorLength(encoding)
    if (valueStart > frameData.size) return null
    return desktopLyricsPayloadFromRaw(
        rawText = decodeText(frameData, valueStart, frameData.size - valueStart, charset),
        attribution = "Embedded lyrics from local file",
    )
}

private fun desktopLyricsPayloadFromRaw(
    rawText: String,
    attribution: String,
): DesktopLocalLyricsPayload? {
    val syncedLyrics = parseDesktopSyncedLyrics(rawText)
    val plainLyrics = parseDesktopPlainLyrics(rawText, stripTimestamps = true)
    if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) return null
    return DesktopLocalLyricsPayload(
        syncedLyrics = syncedLyrics,
        plainLyrics = if (plainLyrics.isNotEmpty()) plainLyrics else syncedLyrics.map(DesktopLyricLine::text),
        attribution = attribution,
    )
}

internal fun parseDesktopSyncedLyrics(rawLyrics: String): List<DesktopLyricLine> {
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
                DesktopLyricLine(
                    timestampMs = (minutes * 60_000L) + (seconds * 1_000L) + fractionMs,
                    text = lyricText,
                )
            }
        }
        .distinctBy { "${it.timestampMs}:${it.text}" }
        .sortedBy(DesktopLyricLine::timestampMs)
        .toList()
}

internal fun parseDesktopPlainLyrics(
    rawLyrics: String,
    stripTimestamps: Boolean,
): List<String> = rawLyrics
    .replace("\u0000", "\n")
    .lineSequence()
    .map { line ->
        val cleaned = if (stripTimestamps) {
            line.replace(Regex("""\[\d{1,2}:\d{2}(?:[.:,]\d{1,3})?]"""), "")
        } else {
            line
        }
        cleaned.trim()
    }
    .filter(String::isNotEmpty)
    .toList()

private fun descriptionLooksLikeLyrics(description: String): Boolean {
    val normalized = description.lowercase().replace(Regex("""[^a-z]+"""), " ").trim()
    if (normalized.isBlank()) return false
    return normalized.contains("lyric") || normalized.contains("unsynced") || normalized.contains("unsynchronized")
}

private fun charsetForEncoding(encoding: Int) = when (encoding) {
    0 -> Charsets.ISO_8859_1
    1 -> Charsets.UTF_16
    2 -> Charsets.UTF_16BE
    3 -> Charsets.UTF_8
    else -> null
}

private fun findTerminator(data: ByteArray, start: Int, encoding: Int): Int {
    val terminatorLength = terminatorLength(encoding)
    var index = start.coerceAtLeast(0)
    while (index + terminatorLength <= data.size) {
        val isTerminator = if (terminatorLength == 1) {
            data[index].toInt() == 0
        } else {
            data[index].toInt() == 0 && data[index + 1].toInt() == 0
        }
        if (isTerminator) return index
        index += terminatorLength
    }
    return data.size
}

private fun terminatorLength(encoding: Int): Int = when (encoding) {
    1, 2 -> 2
    else -> 1
}

private fun decodeText(data: ByteArray, start: Int, length: Int, charset: java.nio.charset.Charset): String {
    if (length <= 0 || start !in 0..data.size) return ""
    val safeLength = length.coerceAtMost(data.size - start)
    return data.copyOfRange(start, start + safeLength)
        .toString(charset)
        .trim('\u0000', '\uFEFF', '\uFFFE')
        .trim()
}

private fun bigEndianInt(data: ByteArray, start: Int): Int =
    ((data[start].toInt() and 0xFF) shl 24) or
        ((data[start + 1].toInt() and 0xFF) shl 16) or
        ((data[start + 2].toInt() and 0xFF) shl 8) or
        (data[start + 3].toInt() and 0xFF)

private fun synchsafeInt(data: ByteArray, start: Int): Int =
    ((data[start].toInt() and 0x7F) shl 21) or
        ((data[start + 1].toInt() and 0x7F) shl 14) or
        ((data[start + 2].toInt() and 0x7F) shl 7) or
        (data[start + 3].toInt() and 0x7F)

private fun removeUnsynchronization(data: ByteArray): ByteArray {
    val output = ArrayList<Byte>(data.size)
    var index = 0
    while (index < data.size) {
        val current = data[index]
        if (current.toInt() == 0xFF && index + 1 < data.size && data[index + 1].toInt() == 0x00) {
            output += current
            index += 2
        } else {
            output += current
            index += 1
        }
    }
    return output.toByteArray()
}

private fun java.io.InputStream.readFully(buffer: ByteArray): Boolean {
    var total = 0
    while (total < buffer.size) {
        val read = read(buffer, total, buffer.size - total)
        if (read <= 0) return false
        total += read
    }
    return true
}

private const val Id3HeaderLength = 10

private val SUPPORTED_AUDIO_EXTENSIONS = setOf(
    "mp3",
    "m4a",
    "aac",
    "wav",
    "aiff",
    "aif",
    "flac",
    "ogg",
    "opus",
)
