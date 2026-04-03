package com.example.verseflow.data

import android.content.Context
import android.net.Uri
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.Charset

object LocalLyricsMetadataResolver {
    private const val Id3HeaderLength = 10

    private val lyricsCache = object : LruCache<String, List<String>>(96) {}

    suspend fun loadPlainLyrics(
        context: Context,
        mediaUri: String?,
    ): List<String> = withContext(Dispatchers.IO) {
        val key = mediaUri?.trim().orEmpty()
        if (key.isEmpty()) return@withContext emptyList()

        lyricsCache.get(key)?.let { return@withContext it }

        val lyrics = runCatching {
            parseEmbeddedLyrics(context, key)
        }.getOrDefault(emptyList())

        lyricsCache.put(key, lyrics)
        lyrics
    }

    private fun parseEmbeddedLyrics(
        context: Context,
        rawUri: String,
    ): List<String> {
        val uri = Uri.parse(rawUri)
        val parsed = context.contentResolver.openInputStream(uri)?.use { input ->
            val header = ByteArray(Id3HeaderLength)
            if (!input.readFully(header)) return emptyList()
            if (!header.copyOfRange(0, 3).contentEquals(byteArrayOf('I'.code.toByte(), 'D'.code.toByte(), '3'.code.toByte()))) {
                return emptyList()
            }

            val versionMajor = header[3].toInt() and 0xFF
            if (versionMajor !in 3..4) return emptyList()

            val flags = header[5].toInt() and 0xFF
            val size = synchsafeInt(header, 6)
            if (size <= 0) return emptyList()

            val payload = ByteArray(size)
            if (!input.readFully(payload)) return emptyList()

            val normalizedPayload = if (flags and 0x80 != 0) {
                removeUnsynchronization(payload)
            } else {
                payload
            }
            versionMajor to normalizedPayload
        } ?: return emptyList()

        return parseId3v23Or24(
            versionMajor = parsed.first,
            tagBytes = parsed.second,
        )
    }

    private fun parseId3v23Or24(
        versionMajor: Int,
        tagBytes: ByteArray,
    ): List<String> {
        var offset = 0
        var fallbackLyrics: List<String> = emptyList()

        while (offset + 10 <= tagBytes.size) {
            val idBytes = tagBytes.copyOfRange(offset, offset + 4)
            if (idBytes.all { it.toInt() == 0 }) break

            val frameId = idBytes.toString(Charsets.ISO_8859_1)
            val frameSize = if (versionMajor == 4) {
                synchsafeInt(tagBytes, offset + 4)
            } else {
                bigEndianInt(tagBytes, offset + 4)
            }
            if (frameSize <= 0 || offset + 10 + frameSize > tagBytes.size) break

            val frameData = tagBytes.copyOfRange(offset + 10, offset + 10 + frameSize)
            when (frameId) {
                "USLT" -> {
                    parseUnsynchronizedLyricsFrame(frameData)?.let { return it }
                }
                "TXXX" -> {
                    if (fallbackLyrics.isEmpty()) {
                        parseUserTextLyricsFrame(frameData)?.let { fallbackLyrics = it }
                    }
                }
                "COMM" -> {
                    if (fallbackLyrics.isEmpty()) {
                        parseCommentLyricsFrame(frameData)?.let { fallbackLyrics = it }
                    }
                }
            }

            offset += 10 + frameSize
        }

        return fallbackLyrics
    }

    private fun parseUnsynchronizedLyricsFrame(frameData: ByteArray): List<String>? {
        if (frameData.size <= 4) return null
        val encoding = frameData[0].toInt() and 0xFF
        val charset = charsetForEncoding(encoding) ?: return null
        val descriptionEnd = findTerminator(frameData, start = 4, encoding = encoding)
        val lyricsStart = descriptionEnd + terminatorLength(encoding)
        if (lyricsStart > frameData.size) return null
        return normalizeLyrics(
            decodeText(frameData, lyricsStart, frameData.size - lyricsStart, charset),
        )
    }

    private fun parseUserTextLyricsFrame(frameData: ByteArray): List<String>? {
        if (frameData.isEmpty()) return null
        val encoding = frameData[0].toInt() and 0xFF
        val charset = charsetForEncoding(encoding) ?: return null
        val descriptionEnd = findTerminator(frameData, start = 1, encoding = encoding)
        val description = decodeText(frameData, 1, descriptionEnd - 1, charset)
        if (!descriptionLooksLikeLyrics(description)) return null
        val valueStart = descriptionEnd + terminatorLength(encoding)
        if (valueStart > frameData.size) return null
        return normalizeLyrics(
            decodeText(frameData, valueStart, frameData.size - valueStart, charset),
        )
    }

    private fun parseCommentLyricsFrame(frameData: ByteArray): List<String>? {
        if (frameData.size <= 4) return null
        val encoding = frameData[0].toInt() and 0xFF
        val charset = charsetForEncoding(encoding) ?: return null
        val descriptionEnd = findTerminator(frameData, start = 4, encoding = encoding)
        val description = decodeText(frameData, 4, descriptionEnd - 4, charset)
        if (!descriptionLooksLikeLyrics(description)) return null
        val valueStart = descriptionEnd + terminatorLength(encoding)
        if (valueStart > frameData.size) return null
        return normalizeLyrics(
            decodeText(frameData, valueStart, frameData.size - valueStart, charset),
        )
    }

    private fun descriptionLooksLikeLyrics(description: String): Boolean {
        val normalized = description.lowercase().replace(Regex("""[^a-z]+"""), " ").trim()
        if (normalized.isBlank()) return false
        return normalized.contains("lyric") ||
            normalized.contains("unsynced") ||
            normalized.contains("unsynchronized")
    }

    private fun normalizeLyrics(rawText: String): List<String>? {
        val cleaned = rawText
            .replace("\u0000", "\n")
            .lineSequence()
            .map { line ->
                line
                    .replace(Regex("""\[\d{1,2}:\d{2}(?:[.:,]\d{1,3})?]"""), "")
                    .trim()
            }
            .filter(String::isNotEmpty)
            .toList()

        return cleaned.takeIf(List<String>::isNotEmpty)
    }

    private fun charsetForEncoding(encoding: Int): Charset? = when (encoding) {
        0 -> Charsets.ISO_8859_1
        1 -> Charsets.UTF_16
        2 -> Charset.forName("UTF-16BE")
        3 -> Charsets.UTF_8
        else -> null
    }

    private fun findTerminator(
        data: ByteArray,
        start: Int,
        encoding: Int,
    ): Int {
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

    private fun decodeText(
        data: ByteArray,
        start: Int,
        length: Int,
        charset: Charset,
    ): String {
        if (length <= 0 || start !in 0..data.size) return ""
        val safeLength = length.coerceAtMost(data.size - start)
        return data.copyOfRange(start, start + safeLength)
            .toString(charset)
            .trim('\u0000', '\uFEFF', '\uFFFE')
            .trim()
    }

    private fun bigEndianInt(
        data: ByteArray,
        start: Int,
    ): Int = ((data[start].toInt() and 0xFF) shl 24) or
        ((data[start + 1].toInt() and 0xFF) shl 16) or
        ((data[start + 2].toInt() and 0xFF) shl 8) or
        (data[start + 3].toInt() and 0xFF)

    private fun synchsafeInt(
        data: ByteArray,
        start: Int,
    ): Int = ((data[start].toInt() and 0x7F) shl 21) or
        ((data[start + 1].toInt() and 0x7F) shl 14) or
        ((data[start + 2].toInt() and 0x7F) shl 7) or
        (data[start + 3].toInt() and 0x7F)

    private fun removeUnsynchronization(data: ByteArray): ByteArray {
        val output = ArrayList<Byte>(data.size)
        var index = 0
        while (index < data.size) {
            val current = data[index]
            if (
                current.toInt() == 0xFF &&
                index + 1 < data.size &&
                data[index + 1].toInt() == 0x00
            ) {
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
}
