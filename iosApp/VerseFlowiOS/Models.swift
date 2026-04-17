import Foundation
import SwiftUI

enum VerseFlowThemePreset: String, CaseIterable, Identifiable {
    case nebula
    case eclipse
    case crimson
    case solar
    case cobalt
    case arctic
    case rose
    case mint
    case amber
    case mono

    var id: String { rawValue }

    var title: String {
        switch self {
        case .nebula: return "Nebula"
        case .eclipse: return "Eclipse"
        case .crimson: return "Crimson"
        case .solar: return "Solar"
        case .cobalt: return "Cobalt"
        case .arctic: return "Arctic"
        case .rose: return "Rose"
        case .mint: return "Mint"
        case .amber: return "Amber"
        case .mono: return "Mono"
        }
    }
}

struct VerseFlowSong: Identifiable, Hashable {
    let id: String
    let title: String
    let artist: String
    let album: String
    let genre: String
    let releaseYear: String
    let duration: TimeInterval
    let artworkName: String
    let lyrics: [String]
    let gradient: [Color]
}

struct VerseFlowAlbum: Identifiable, Hashable {
    let id: String
    let title: String
    let artist: String
    let genre: String
    let releaseDate: String
    let about: String
    let artworkName: String
    let gradient: [Color]
    let songs: [VerseFlowSong]
}

struct VerseFlowArtist: Identifiable, Hashable {
    let id: String
    let name: String
    let about: String
    let imageName: String
    let genres: [String]
}

enum VerseFlowTab: Hashable {
    case home
    case library
    case search
    case queue
    case nowPlaying
    case lyrics
    case settings
}

final class VerseFlowAppState: ObservableObject {
    @Published var selectedTheme: VerseFlowThemePreset = .nebula
    @Published var selectedTab: VerseFlowTab = .home
    @Published var currentSong: VerseFlowSong = PreviewLibrary.sampleAlbums[0].songs[0]
    @Published var playbackProgress: Double = 0.38
    @Published var isPlaying: Bool = true
    @Published var searchQuery: String = ""

    var currentAlbum: VerseFlowAlbum {
        PreviewLibrary.sampleAlbums.first(where: { $0.title == currentSong.album }) ?? PreviewLibrary.sampleAlbums[0]
    }
}

enum PreviewLibrary {
    static let sampleSongs: [VerseFlowSong] = [
        VerseFlowSong(
            id: "song-1",
            title: "Midnight Echo",
            artist: "Nova Rey",
            album: "Crimson Skyline",
            genre: "Synthwave",
            releaseYear: "2012",
            duration: 226,
            artworkName: "preview-cover-red",
            lyrics: [
                "City lights bloom in slow motion",
                "Every window hums a neon prayer",
                "We keep moving like the dark is open",
                "Midnight echoes hanging in the air"
            ],
            gradient: [Color(red: 0.16, green: 0.02, blue: 0.05), Color(red: 0.72, green: 0.14, blue: 0.20)]
        ),
        VerseFlowSong(
            id: "song-2",
            title: "Glass Hearts",
            artist: "Nova Rey",
            album: "Crimson Skyline",
            genre: "Synthwave",
            releaseYear: "2012",
            duration: 208,
            artworkName: "preview-cover-red",
            lyrics: [
                "We kept our glass hearts hidden in the bassline",
                "Every chorus felt like headlights in the rain"
            ],
            gradient: [Color(red: 0.16, green: 0.02, blue: 0.05), Color(red: 0.72, green: 0.14, blue: 0.20)]
        ),
        VerseFlowSong(
            id: "song-3",
            title: "Harborline",
            artist: "Ari Vale",
            album: "Tidal Bloom",
            genre: "Alt-Pop",
            releaseYear: "2018",
            duration: 238,
            artworkName: "preview-cover-blue",
            lyrics: [
                "Harbor lights and a fast lane heart",
                "Everything restless finds a start"
            ],
            gradient: [Color(red: 0.03, green: 0.11, blue: 0.20), Color(red: 0.18, green: 0.47, blue: 0.82)]
        ),
        VerseFlowSong(
            id: "song-4",
            title: "Gold Static",
            artist: "Kairo",
            album: "Golden Static",
            genre: "Electronic",
            releaseYear: "2008",
            duration: 214,
            artworkName: "preview-cover-gold",
            lyrics: [
                "Gold static in the rear-view mirror",
                "Every mile glows louder"
            ],
            gradient: [Color(red: 0.16, green: 0.11, blue: 0.02), Color(red: 0.82, green: 0.63, blue: 0.19)]
        )
    ]

    static let sampleAlbums: [VerseFlowAlbum] = [
        VerseFlowAlbum(
            id: "album-1",
            title: "Crimson Skyline",
            artist: "Nova Rey",
            genre: "Synthwave",
            releaseDate: "October 12, 2012",
            about: "Crimson Skyline is a cinematic late-night album built around glowing synth lines, neon drums, and a heavy sense of motion after dark.",
            artworkName: "preview-cover-red",
            gradient: [Color(red: 0.16, green: 0.02, blue: 0.05), Color(red: 0.72, green: 0.14, blue: 0.20)],
            songs: Array(sampleSongs.prefix(2))
        ),
        VerseFlowAlbum(
            id: "album-2",
            title: "Tidal Bloom",
            artist: "Ari Vale",
            genre: "Alt-Pop",
            releaseDate: "June 08, 2018",
            about: "Tidal Bloom leans brighter and more open, with clean pop hooks, coastal textures, and a calmer emotional arc.",
            artworkName: "preview-cover-blue",
            gradient: [Color(red: 0.03, green: 0.11, blue: 0.20), Color(red: 0.18, green: 0.47, blue: 0.82)],
            songs: [sampleSongs[2]]
        ),
        VerseFlowAlbum(
            id: "album-3",
            title: "Golden Static",
            artist: "Kairo",
            genre: "Electronic",
            releaseDate: "March 03, 2008",
            about: "Golden Static is a warmer, older electronic record with a strong throwback feel and a sharper rhythmic edge.",
            artworkName: "preview-cover-gold",
            gradient: [Color(red: 0.16, green: 0.11, blue: 0.02), Color(red: 0.82, green: 0.63, blue: 0.19)],
            songs: [sampleSongs[3]]
        )
    ]

    static let sampleArtists: [VerseFlowArtist] = [
        VerseFlowArtist(
            id: "artist-1",
            name: "Nova Rey",
            about: "Nova Rey is a preview artist used to stage VerseFlow's iPhone home, now playing, lyrics, and artist flows.",
            imageName: "preview-cover-red",
            genres: ["Synthwave", "Electronic"]
        ),
        VerseFlowArtist(
            id: "artist-2",
            name: "Ari Vale",
            about: "Ari Vale represents the lighter pop side of the preview library with brighter palettes and a softer mood.",
            imageName: "preview-cover-blue",
            genres: ["Alt-Pop"]
        ),
        VerseFlowArtist(
            id: "artist-3",
            name: "Kairo",
            about: "Kairo adds a more throwback, electronic angle to the iPhone sample library.",
            imageName: "preview-cover-gold",
            genres: ["Electronic"]
        )
    ]
}
