package com.example.verseflow.data

fun buildArtistCredits(primaryArtist: String, title: String): List<String> =
    (splitArtistCredits(primaryArtist) + extractFeaturedArtistsFromTitle(title))
        .filter(String::isNotBlank)
        .distinct()

fun splitArtistCredits(raw: String): List<String> =
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
    featureArtistPattern
        .findAll(title)
        .flatMap { match ->
            splitArtistCredits(match.groupValues.getOrNull(1).orEmpty()).asSequence()
        }
        .distinct()
        .toList()

private val featureArtistPattern = Regex("""(?i)\b(?:feat(?:uring)?|ft)\.?\s+([^\)\]]+)""")
