# VerseFlow iPhone Foundation

This folder contains the first iPhone implementation pass for VerseFlow.

What is here:

- a SwiftUI app shell
- iPhone theme presets aligned with the Android direction
- seeded preview/sample data
- first-pass screens for:
  - Home
  - Library
  - Search
  - Queue
  - Now Playing
  - Lyrics
  - Settings

## Current state

This is an iPhone foundation pass, not feature parity with Android yet.

It is focused on:

- app structure
- visual direction
- theme system
- artwork-reactive now playing and lyrics screens
- a usable SwiftUI screen map to build on

It does not yet include:

- local library scanning
- real playback
- lyrics lookup
- metadata search
- MediaSession / remote control equivalents
- persistence

## Project generation

The iOS app is described with `project.yml`.

To generate an Xcode project, use XcodeGen on a Mac with full Xcode installed:

```bash
cd iosApp
xcodegen generate
```

That will create the Xcode project for `VerseFlowiOS`.

## Files

- `project.yml`
- `VerseFlowiOS/VerseFlowiOSApp.swift`
- `VerseFlowiOS/Models.swift`
- `VerseFlowiOS/Theme.swift`
- `VerseFlowiOS/VerseFlowRootView.swift`
