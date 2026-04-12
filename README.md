# VerseFlow

VerseFlow is a local-first music player project built with Kotlin, Jetpack Compose, and Material 3 across Android and desktop experiences.

It is designed around one core experience: a cinematic local music player with a polished real-time lyrics screen.

[Downloads](./releases/)

The project currently focuses on:

- polished Android phone, Android Auto, macOS desktop, and Windows desktop UI
- local on-device music playback
- album-art-reactive visuals
- synced and plain lyrics discovery
- manual lyrics selection
- media-session-backed background playback
- reusable Compose architecture that can keep expanding later

This project is no longer just a static UI prototype. It now includes real local playback, media notifications, lyrics lookup, metadata enrichment, caching, and device-library browsing across multiple VerseFlow surfaces.

## Installation

### Clone the project

```bash
git clone <your-repository-url>
cd VerseFlow
```

### Android setup

Requirements:

- Android Studio
- JDK 17 or higher
- Android SDK installed
- Android phone or emulator

Steps:

1. Open the project in Android Studio
2. Let Gradle sync finish
3. Make sure the `app` run configuration is selected
4. Connect a phone or start an emulator
5. Run the app

Build from terminal:

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew --no-daemon :app:assembleDebug --console=plain
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Android Auto / car-installed Android testing

VerseFlow also has a car-focused Android build flow for Android Auto experiments and direct car-screen testing.

Build from terminal:

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew --no-daemon :app:assembleDebug --console=plain
```

For local staging, release files are copied into the `releases/` folder. For publishing, use GitHub Releases instead of committing large binaries into Git history.

### Mac desktop setup

Requirements:

- IntelliJ IDEA
- Kotlin Multiplatform plugin
- JDK 17 or higher
- macOS device

Steps:

1. Open the same project in IntelliJ IDEA
2. Let Gradle import finish
3. Run the desktop app with the Gradle task:

```bash
./gradlew :desktopApp:run
```

Desktop compile check:

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew --no-daemon :desktopApp:compileKotlin --console=plain
```

Build the macOS DMG:

```bash
JAVA_HOME='/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home' ./gradlew --no-daemon :desktopApp:packageDmg --console=plain
```

## Available Versions

Currently available:

- Android phone app
- Android Auto / car-installed Android build
- macOS desktop app
- Windows desktop app

Still being built:

- iOS app

## Status

Current state:

- Android phone app is runnable and usable on a real device
- Android Auto / car-installed Android flow is available for in-car testing
- macOS desktop app is implemented and packaged as a DMG
- Windows desktop app is implemented and packaged as an EXE / MSI
- local songs can be loaded from device storage through `MediaStore`
- real audio playback works for local files
- playback continues in the background for local songs
- media notification and lock-screen controls are wired through Media3 `MediaSessionService`
- synced and plain lyrics can be fetched, cached, and manually selected
- editing music info is implemented as app-only overrides
- songs can be hidden from VerseFlow or deleted from device storage

This is still a debug-stage app, not a production release.

## Main Features

### Music library

- Browse local songs
- Browse albums
- Browse artists
- Browse playlists
- Browse folders
- Browse genres
- Search across songs, albums, artists, and playlists

### Playback

- Play local songs from device storage
- Mini player at the bottom of the app
- Full Now Playing screen
- Previous / play-pause / next
- Shuffle and repeat
- Play queue
- Album-focused playback
- Playlist playback
- Artist top-track playback
- playback resumption support

### Lyrics

- Dedicated full-screen lyrics view
- Synced lyrics when available
- Plain lyrics fallback when synced lyrics are unavailable
- Manual lyrics search and manual match selection
- Cached lyrics for previously matched songs
- `Jump Live` behavior for returning to the active lyric after manual scrolling

### Metadata and discovery

- Artist bio and profile-photo search
- Album bio and info search
- Manual search fallback for ambiguous artist and album matches
- Song metadata search with local override storage
- Favourite artists based on library track counts
- Play history views and recap data

### Desktop experience

- Native macOS desktop app
- Desktop play history recaps, streaks, and heatmap
- Immersive and monochrome desktop themes
- Desktop manual metadata editing and search workflows

### Android Auto / car mode

- Car-focused now playing and lyrics layouts
- Android Auto testing support
- Car library browsing and playback flow

### Device integration

- Reads local music through Android `MediaStore`
- Background playback through `MediaSessionService`
- Media-style notification for local playback
- Lock-screen controls
- Bluetooth / headset media-button support through the media session path

### Personalization

- Multiple theme presets:
  - `Nebula`
  - `Eclipse`
  - `Crimson`
  - `Solar`
  - `Cobalt`
  - `Arctic`
  - `Rose`
  - `Mint`
  - `Amber`
  - `Mono`
- User display name in Settings
- App-only metadata overrides for title, artist, album, and genre
- Song hiding inside VerseFlow without deleting the real file

## Screens

The app currently includes:

- Splash
- Home
- Library
- Search
- Play Queue
- Now Playing
- Lyrics
- Album Detail
- Artist Detail
- Playlist Detail
- Settings

## Pages

Screenshots for the main app pages are stored in the `media/` folder.

### Home

Path: `media/home.jpeg`

![Home](./media/home.jpeg)

### Songs Page

Path: `media/songspage.jpeg`

![Songs Page](./media/songspage.jpeg)

### Album Detail

Path: `media/album.jpeg`

![Album Detail](./media/album.jpeg)

### Artist Detail

Path: `media/artist.jpeg`

![Artist Detail](./media/artist.jpeg)

### Genres

Path: `media/genres.jpeg`

![Genres](./media/genres.jpeg)

### Now Playing

Path: `media/nowplaying.jpeg`

![Now Playing](./media/nowplaying.jpeg)

### Lyrics Page

Path: `media/lyrics.gif`

![Lyrics Page](./media/lyrics.gif)

### Settings

Path: `media/settings.jpeg`

![Settings](./media/settings.jpeg)

## Gestures and Interactions

### Now Playing

- Swipe left to move to the next song
- Swipe right to move to the previous song
- Swipe up over the artwork area to open lyrics
- Tap the mini player to open the full player

### Lyrics

- Swipe right to return to Now Playing
- Scroll manually through lyrics without being forced back immediately
- Tap `Jump Live` to return to the lyric line that matches the current playback position

## Lyrics Pipeline

VerseFlow does not use audio fingerprinting yet.

Lyrics are currently found using metadata and safe fallback sources in this order:

1. embedded lyrics inside the local file
2. LRCLIB lookup
3. lyrics.ovh fallback for plain lyrics
4. manual search and manual match selection

Additional behavior:

- The app prefers synced lyrics over plain lyrics when a strong match exists
- The app uses normalized title and artist matching to handle cases like:
  - `24 Hours (feat. 2 Chainz)`
  - `TeeFLii/2 Chainz`
- Found lyrics are cached locally so the app does not need to search every time
- If the app cannot verify a lyrics match safely, it shows `No lyrics found` instead of attaching risky lyrics from the wrong song

### Important note about synced lyrics

Not every song online has timestamped lyrics.

So a song may still show:

- synced lyrics
- only plain lyrics
- no lyrics found

depending on source availability and match confidence.

## Music Sources

VerseFlow currently supports:

- local audio files visible to Android through `MediaStore`

It does not support:

- DRM-protected offline downloads from streaming apps like Spotify, Apple Music, or YouTube Music

## Song Actions

From song overflow menus, the app supports:

- Add to playlist
- Add to play queue
- Add / remove favourites
- Remove from VerseFlow
- Delete from device
- Open artist
- Open album
- Edit Music Info

### Remove from VerseFlow

This hides the song inside VerseFlow only.

It does not delete the real file from the phone.

### Delete from device

This attempts real file deletion for local songs.

The app uses Android’s system-approved media deletion path where applicable.

### Edit Music Info

This is currently app-only.

That means edited title, artist, album, and genre are only changed inside VerseFlow. The real audio file tags are not rewritten yet.

## Themes

Theme switching is available in Settings.

Current presets:

- `Nebula Dark`: original futuristic dark theme
- `Eclipse OLED`: cleaner near-black theme
- `Aurora Glow`: warmer colorful premium theme
- `Cobalt Luxe`: elegant blue-family theme centered around `#00f`

## Notifications and Background Playback

For local songs, VerseFlow now uses Media3 session-based playback.

That means:

- audio can continue in the background
- Android shows a media notification
- lock-screen controls are available
- Bluetooth/headset playback buttons can control the session

Important limitation:

- demo/mock songs are not real files, so true background audio behavior mainly applies to local on-device tracks

## Settings: What Works and What Is Still Placeholder

### Working settings

- Display name
- Theme selection
- Synced lyrics default preference
- Stored preference toggles for future use

### Partially implemented / placeholder settings

- `Autoplay next tracks`
  - stored, but not fully enforced across every playback path
- `Immersive motion`
  - stored, but not yet driving a full app-wide motion toggle system
- `Allow explicit content`
  - stored, but not filtering the library yet
- `Download over Wi-Fi only`
  - stored, but there is no real download pipeline yet
- `Language`
  - stored, but the app is not localized into multiple languages yet
- `Storage & Cache`
  - still a placeholder section, no clear-cache or download-management UI yet

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Media3
- ExoPlayer
- `MediaSessionService`
- SharedPreferences for lightweight persistence

## Project Structure

```text
app/src/main/java/com/example/verseflow/
├── data/
├── model/
├── ui/
│   ├── components/
│   ├── navigation/
│   ├── preview/
│   ├── screens/
│   │   ├── album/
│   │   ├── artist/
│   │   ├── home/
│   │   ├── library/
│   │   ├── lyrics/
│   │   ├── player/
│   │   ├── playlist/
│   │   ├── queue/
│   │   ├── search/
│   │   ├── settings/
│   │   └── splash/
│   └── theme/
├── MainActivity.kt
├── VerseFlowApp.kt
├── VerseFlowPlaybackService.kt
└── VerseFlowViewModel.kt
```

## Key Files

- `MainActivity.kt`
  - app entry point
- `VerseFlowApp.kt`
  - app shell, navigation host setup, mini player, dialogs
- `VerseFlowViewModel.kt`
  - app state, playback orchestration, library mutations, lyrics state
- `VerseFlowPlaybackService.kt`
  - Media3 background playback service
- `ui/navigation/VerseFlowNavigation.kt`
  - screen routing
- `data/DeviceAudioStoreLoader.kt`
  - local device library loading
- `data/LrcLibLyricsRepository.kt`
  - synced lyrics lookup
- `data/LyricsOvhFallbackRepository.kt`
  - plain lyrics fallback
- `data/LyricsCacheStore.kt`
  - cached lyrics persistence
- `data/LibraryCustomizationStore.kt`
  - hidden songs and app-only metadata overrides

## Build and Run

### Requirements

- Android Studio
- JDK 17
- Android device or emulator

### Run in Android Studio

1. Open the project in Android Studio
2. Let Gradle sync finish
3. Select a real device or emulator
4. Run the `app` configuration

### Build from terminal

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew --no-daemon :app:assembleDebug --console=plain
```

### APK output

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Permissions

The app uses:

- `READ_MEDIA_AUDIO` on Android 13+
- `READ_EXTERNAL_STORAGE` on older Android versions
- `INTERNET` for lyrics lookup
- foreground media playback permissions for background audio

## Current Limitations

- No backend or cloud sync
- No real streaming service integration
- No real download manager
- No equalizer, crossfade, or advanced audio controls
- No file-tag rewriting yet
- No full localization yet
- No account/auth system
- No analytics / crash-reporting integration
- No production release signing or Play Store packaging yet

## Play Store Readiness

VerseFlow is not yet production-ready just because the UI is polished.

Before Play Store release, the project should still add:

- release signing and App Bundle generation
- crash reporting
- Android vitals monitoring
- more device QA
- playback resumption polish
- notification and media-session edge-case testing
- clearer licensing strategy for lyrics sources

## Future Roadmap

Possible next steps:

- production hardening for Play Store
- playback resumption after reboot / system dismissal
- equalizer and audio enhancements
- richer cache and storage controls
- advanced lyrics features
  - translation
  - karaoke word timing
  - lyric source picker
- real metadata rewriting to audio files
- iOS app

## macOS Desktop App

VerseFlow already has a working macOS desktop app built with Compose for Desktop.

Current desktop capabilities include:

- local library scanning
- desktop playback and now playing
- synced and plain lyrics
- artist, album, and song metadata search
- play history recaps and listening patterns
- desktop-only theme system, including immersive artwork tinting
- DMG packaging for local distribution

## Windows Desktop App

VerseFlow also has a working Windows desktop build powered by the same Compose Desktop app used on macOS.

Current Windows desktop capabilities include:

- local library scanning
- desktop playback and now playing
- synced and plain lyrics
- artist, album, and song metadata search
- play history recaps and listening patterns
- immersive and monochrome desktop themes
- EXE and MSI packaging for distribution

## Android Auto

VerseFlow also includes an Android Auto / car-oriented Android experience.

Current Android Auto and car-focused work includes:

- in-car now playing and lyrics layouts
- car library browsing
- album-art-reactive backdrop on now playing and lyrics
- car testing previews in Android Studio
- release APK staging through the `releases/` folder for publishing

## Notes

- This repository now represents a multi-surface VerseFlow project spanning Android phone, Android Auto experimentation, macOS desktop, and Windows desktop.
- The project has intentionally prioritized design polish, local playback, and lyrics-first UX before broader platform expansion.

## License

No license file has been added yet.

If you plan to open-source or distribute this project publicly, add a proper `LICENSE` file first.
