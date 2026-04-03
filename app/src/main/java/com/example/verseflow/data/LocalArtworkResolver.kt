package com.example.verseflow.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.verseflow.model.AccentPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object LocalArtworkResolver {
    private const val ArtworkSizePx = 1_024
    private const val PaletteSampleSize = 36

    private val artworkCache = object : LruCache<String, CachedArtwork>(48) {}

    suspend fun loadArtwork(
        context: Context,
        artworkUri: String?,
        fallbackMediaUri: String? = null,
    ): ImageBitmap? = withContext(Dispatchers.IO) {
        loadCachedArtwork(context, artworkUri, fallbackMediaUri)?.bitmap?.asImageBitmap()
    }

    suspend fun loadArtworkBackdropPalette(
        context: Context,
        artworkUri: String?,
        fallbackMediaUri: String? = null,
    ): BackdropPaletteResult? = withContext(Dispatchers.IO) {
        loadCachedArtwork(context, artworkUri, fallbackMediaUri)?.let { cached ->
            BackdropPaletteResult(
                palette = cached.palette,
                isMonochrome = cached.isMonochrome,
            )
        }
    }

    private fun loadCachedArtwork(
        context: Context,
        artworkUri: String?,
        fallbackMediaUri: String? = null,
    ): CachedArtwork? {
        val candidates = buildList {
            artworkUri?.trim()?.takeIf(String::isNotEmpty)?.let(::add)
            fallbackMediaUri?.trim()?.takeIf(String::isNotEmpty)?.let(::add)
        }.distinct()

        for (candidate in candidates) {
            artworkCache.get(candidate)?.let { return it }
            decodeBitmap(context, candidate)?.let { bitmap ->
                val cachedArtwork = CachedArtwork(
                    bitmap = bitmap,
                    palette = extractBackdropPalette(bitmap),
                    isMonochrome = isMostlyMonochrome(bitmap),
                )
                artworkCache.put(candidate, cachedArtwork)
                return cachedArtwork
            }
        }
        return null
    }

    private fun decodeBitmap(
        context: Context,
        rawUri: String,
    ): Bitmap? {
        val uri = runCatching { Uri.parse(rawUri) }.getOrNull() ?: return null

        runCatching {
            context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
        }.getOrNull()?.let { return it }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runCatching {
                context.contentResolver.loadThumbnail(
                    uri,
                    Size(ArtworkSizePx, ArtworkSizePx),
                    null,
                )
            }.getOrNull()?.let { return it }
        }

        val retriever = MediaMetadataRetriever()
        return runCatching {
            retriever.setDataSource(context, uri)
            retriever.embeddedPicture?.let { data ->
                BitmapFactory.decodeByteArray(data, 0, data.size)
            }
        }.getOrNull().also {
            runCatching { retriever.release() }
        }
    }

    private fun isMostlyMonochrome(bitmap: Bitmap): Boolean {
        val sample = Bitmap.createScaledBitmap(bitmap, PaletteSampleSize, PaletteSampleSize, true)
        val pixels = IntArray(sample.width * sample.height)
        sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)

        var saturationTotal = 0f
        var colorVarianceTotal = 0f
        var count = 0

        val hsv = FloatArray(3)
        pixels.forEach { pixel ->
            val alpha = AndroidColor.alpha(pixel)
            if (alpha < 24) return@forEach
            AndroidColor.colorToHSV(pixel, hsv)
            saturationTotal += hsv[1]
            colorVarianceTotal += (
                abs(AndroidColor.red(pixel) - AndroidColor.green(pixel)) +
                    abs(AndroidColor.green(pixel) - AndroidColor.blue(pixel)) +
                    abs(AndroidColor.red(pixel) - AndroidColor.blue(pixel))
                ) / 765f
            count += 1
        }

        if (count == 0) return false
        val averageSaturation = saturationTotal / count
        val averageVariance = colorVarianceTotal / count
        return averageSaturation < 0.12f && averageVariance < 0.08f
    }

    private fun extractBackdropPalette(bitmap: Bitmap): AccentPalette {
        val sample = Bitmap.createScaledBitmap(bitmap, PaletteSampleSize, PaletteSampleSize, true)
        val pixels = IntArray(sample.width * sample.height)
        sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)

        var redTotal = 0f
        var greenTotal = 0f
        var blueTotal = 0f
        var brightnessTotal = 0f
        var count = 0f
        var mostSaturatedColor = AndroidColor.rgb(180, 180, 180)
        var secondAccentColor = AndroidColor.rgb(220, 220, 220)
        var highestAccentScore = -1f
        var secondAccentScore = -1f
        val hsv = FloatArray(3)

        pixels.forEach { pixel ->
            val alpha = AndroidColor.alpha(pixel) / 255f
            if (alpha < 0.10f) return@forEach

            val red = AndroidColor.red(pixel).toFloat()
            val green = AndroidColor.green(pixel).toFloat()
            val blue = AndroidColor.blue(pixel).toFloat()
            redTotal += red * alpha
            greenTotal += green * alpha
            blueTotal += blue * alpha
            count += alpha

            AndroidColor.colorToHSV(pixel, hsv)
            brightnessTotal += hsv[2] * alpha
            val accentScore = (hsv[1] * 1.8f) + hsv[2]
            if (accentScore > highestAccentScore) {
                secondAccentScore = highestAccentScore
                secondAccentColor = mostSaturatedColor
                highestAccentScore = accentScore
                mostSaturatedColor = pixel
            } else if (accentScore > secondAccentScore) {
                secondAccentScore = accentScore
                secondAccentColor = pixel
            }
        }

        if (count <= 0f) {
            return AccentPalette(
                background = Color(0xFF080B11),
                primary = Color(0xFF6A8CFF),
                secondary = Color(0xFF8AF5FF),
                tertiary = Color(0xFFB7C5FF),
                glow = Color(0xFF9FAFFF),
            )
        }

        val averageRed = redTotal / count
        val averageGreen = greenTotal / count
        val averageBlue = blueTotal / count
        val averageBrightness = brightnessTotal / count
        val isMonochrome = isMostlyMonochrome(sample)

        val averageColor = colorFromRgb(averageRed, averageGreen, averageBlue)
        val primaryColor = if (isMonochrome) {
            grayscaleFromBrightness(max(averageBrightness, 0.62f))
        } else {
            composeColor(mostSaturatedColor).boostSaturation(1.18f).liftValue(1.06f)
        }
        val secondaryColor = if (isMonochrome) {
            grayscaleFromBrightness(max(averageBrightness - 0.08f, 0.50f))
        } else {
            composeColor(secondAccentColor).boostSaturation(1.08f).liftValue(1.02f)
        }
        val tertiaryColor = if (isMonochrome) {
            grayscaleFromBrightness(max(averageBrightness + 0.10f, 0.70f))
        } else {
            averageColor.boostSaturation(0.78f).liftValue(1.12f)
        }

        return AccentPalette(
            background = if (isMonochrome) {
                grayscaleFromBrightness(max(averageBrightness * 0.18f, 0.08f))
            } else {
                averageColor.darkenTo(0.16f)
            },
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = tertiaryColor,
            glow = if (isMonochrome) {
                grayscaleFromBrightness(max(averageBrightness + 0.18f, 0.78f))
            } else {
                secondaryColor.liftValue(1.10f)
            },
        )
    }

    private fun colorFromRgb(
        red: Float,
        green: Float,
        blue: Float,
    ): Color = Color(
        red = (red / 255f).coerceIn(0f, 1f),
        green = (green / 255f).coerceIn(0f, 1f),
        blue = (blue / 255f).coerceIn(0f, 1f),
    )

    private fun composeColor(pixel: Int): Color = Color(
        red = AndroidColor.red(pixel) / 255f,
        green = AndroidColor.green(pixel) / 255f,
        blue = AndroidColor.blue(pixel) / 255f,
    )

    private fun grayscaleFromBrightness(value: Float): Color {
        val channel = value.coerceIn(0f, 1f)
        return Color(channel, channel, channel)
    }

    private fun Color.boostSaturation(multiplier: Float): Color {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(
            AndroidColor.rgb(
                (red * 255).toInt(),
                (green * 255).toInt(),
                (blue * 255).toInt(),
            ),
            hsv,
        )
        hsv[1] = (hsv[1] * multiplier).coerceIn(0f, 1f)
        return Color.hsv(hsv[0], hsv[1], hsv[2])
    }

    private fun Color.liftValue(multiplier: Float): Color {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(
            AndroidColor.rgb(
                (red * 255).toInt(),
                (green * 255).toInt(),
                (blue * 255).toInt(),
            ),
            hsv,
        )
        hsv[2] = (hsv[2] * multiplier).coerceIn(0f, 1f)
        return Color.hsv(hsv[0], hsv[1], hsv[2])
    }

    private fun Color.darkenTo(targetValue: Float): Color {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(
            AndroidColor.rgb(
                (red * 255).toInt(),
                (green * 255).toInt(),
                (blue * 255).toInt(),
            ),
            hsv,
        )
        hsv[2] = min(hsv[2], targetValue.coerceIn(0f, 1f))
        return Color.hsv(hsv[0], hsv[1], hsv[2])
    }
}

data class BackdropPaletteResult(
    val palette: AccentPalette,
    val isMonochrome: Boolean,
)

private data class CachedArtwork(
    val bitmap: Bitmap,
    val palette: AccentPalette,
    val isMonochrome: Boolean,
)
