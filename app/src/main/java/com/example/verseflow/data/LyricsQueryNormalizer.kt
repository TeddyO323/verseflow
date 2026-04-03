package com.example.verseflow.data

internal data class LyricsCandidateMatch(
    val expected: String,
    val actual: String,
    val overlap: Double,
)

internal fun normalizeLookupValue(value: String): String = value
    .lowercase()
    .replace(Regex("""[^a-z0-9]+"""), " ")
    .trim()

internal fun titleCandidateStrings(value: String): List<String> = baseCandidateStrings(value)

internal fun albumCandidateStrings(value: String): List<String> = baseCandidateStrings(value)

internal fun preferredTitleQuery(value: String): String =
    titleCandidateStrings(value).lastOrNull() ?: value.trim()

internal fun artistCandidateStrings(value: String): List<String> {
    val base = baseCandidateStrings(value)
    val splitVariants = base.flatMap(::collaborationSegments)
    return (base + splitVariants)
        .filter(String::isNotBlank)
        .distinct()
}

internal fun preferredArtistQuery(value: String): String {
    val simplified = baseCandidateStrings(value).lastOrNull() ?: value.trim()
    val primary = simplified
        .replace(Regex("""(?i)\b(feat|ft|featuring|with|vs|x)\b"""), "|")
        .split("/")
        .flatMap { it.split("|") }
        .map(String::trim)
        .firstOrNull(String::isNotBlank)

    return primary ?: simplified
}

internal fun bestTitleMatch(
    expectedRaw: String,
    actualRaw: String,
): LyricsCandidateMatch = bestCandidateMatch(
    expectedVariants = titleCandidateStrings(expectedRaw),
    actualVariants = titleCandidateStrings(actualRaw),
)

internal fun bestArtistMatch(
    expectedRaw: String,
    actualRaw: String,
): LyricsCandidateMatch = bestCandidateMatch(
    expectedVariants = artistCandidateStrings(expectedRaw),
    actualVariants = artistCandidateStrings(actualRaw),
)

internal fun bestAlbumMatch(
    expectedRaw: String,
    actualRaw: String,
): LyricsCandidateMatch = bestCandidateMatch(
    expectedVariants = albumCandidateStrings(expectedRaw),
    actualVariants = albumCandidateStrings(actualRaw),
)

private fun bestCandidateMatch(
    expectedVariants: List<String>,
    actualVariants: List<String>,
): LyricsCandidateMatch {
    val normalizedExpected = expectedVariants.map(::normalizeLookupValue).filter(String::isNotBlank).ifEmpty { listOf("") }
    val normalizedActual = actualVariants.map(::normalizeLookupValue).filter(String::isNotBlank).ifEmpty { listOf("") }

    return normalizedExpected
        .flatMap { expected ->
            normalizedActual.map { actual ->
                LyricsCandidateMatch(
                    expected = expected,
                    actual = actual,
                    overlap = overlapRatio(expected, actual),
                )
            }
        }
        .sortedWith(
            compareByDescending<LyricsCandidateMatch> { it.overlap }
                .thenBy { kotlin.math.abs(it.expected.length - it.actual.length) }
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

internal fun shareCoreText(expected: String, actual: String): Boolean =
    expected.isNotBlank() &&
        actual.isNotBlank() &&
        (expected.contains(actual) || actual.contains(expected))
