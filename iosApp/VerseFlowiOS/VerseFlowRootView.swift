import SwiftUI

struct VerseFlowRootView: View {
    @EnvironmentObject private var appState: VerseFlowAppState

    private var theme: VerseFlowTheme { appState.selectedTheme.theme }

    var body: some View {
        TabView(selection: $appState.selectedTab) {
            VerseFlowHomeView(theme: theme)
                .tabItem { Label("Home", systemImage: "house.fill") }
                .tag(VerseFlowTab.home)

            VerseFlowLibraryView(theme: theme)
                .tabItem { Label("Library", systemImage: "books.vertical.fill") }
                .tag(VerseFlowTab.library)

            VerseFlowSearchView(theme: theme)
                .tabItem { Label("Search", systemImage: "magnifyingglass") }
                .tag(VerseFlowTab.search)

            VerseFlowQueueView(theme: theme)
                .tabItem { Label("Queue", systemImage: "music.note.list") }
                .tag(VerseFlowTab.queue)

            VerseFlowNowPlayingView(theme: theme)
                .tabItem { Label("Playing", systemImage: "play.circle.fill") }
                .tag(VerseFlowTab.nowPlaying)

            VerseFlowLyricsView(theme: theme)
                .tabItem { Label("Lyrics", systemImage: "quote.bubble.fill") }
                .tag(VerseFlowTab.lyrics)

            VerseFlowSettingsView(theme: theme)
                .tabItem { Label("Settings", systemImage: "gearshape.fill") }
                .tag(VerseFlowTab.settings)
        }
        .tint(theme.primary)
        .background(theme.background.ignoresSafeArea())
    }
}

private struct VerseFlowHomeView: View {
    @EnvironmentObject private var appState: VerseFlowAppState
    let theme: VerseFlowTheme

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    Text("VerseFlow")
                        .font(.largeTitle.weight(.bold))
                        .foregroundStyle(theme.textPrimary)

                    Text("Continue listening")
                        .font(.title2.weight(.semibold))
                        .foregroundStyle(theme.textPrimary)

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 16) {
                            ForEach(PreviewLibrary.sampleSongs) { song in
                                Button {
                                    appState.currentSong = song
                                    appState.selectedTab = .nowPlaying
                                } label: {
                                    VStack(alignment: .leading, spacing: 10) {
                                        PreviewArtworkView(name: song.artworkName, gradient: song.gradient, size: CGSize(width: 156, height: 156))
                                        Text(song.title)
                                            .font(.headline)
                                            .foregroundStyle(theme.textPrimary)
                                            .lineLimit(1)
                                        Text(song.artist)
                                            .font(.subheadline)
                                            .foregroundStyle(theme.textSecondary)
                                            .lineLimit(1)
                                    }
                                    .frame(width: 156, alignment: .leading)
                                }
                            }
                        }
                    }

                    Text("Favourite artists")
                        .font(.title2.weight(.semibold))
                        .foregroundStyle(theme.textPrimary)

                    VStack(spacing: 12) {
                        ForEach(PreviewLibrary.sampleArtists) { artist in
                            HStack(spacing: 14) {
                                PreviewArtworkView(name: artist.imageName, gradient: [.gray.opacity(0.6), .gray.opacity(0.25)], size: CGSize(width: 64, height: 64))
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(artist.name)
                                        .font(.headline)
                                        .foregroundStyle(theme.textPrimary)
                                    Text(artist.genres.joined(separator: " • "))
                                        .font(.subheadline)
                                        .foregroundStyle(theme.textSecondary)
                                }
                                Spacer()
                            }
                            .verseFlowCard(theme: theme)
                        }
                    }
                }
                .padding(20)
            }
            .background(theme.background.ignoresSafeArea())
        }
    }
}

private struct VerseFlowLibraryView: View {
    let theme: VerseFlowTheme

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    Text("Library")
                        .font(.largeTitle.weight(.bold))
                        .foregroundStyle(theme.textPrimary)

                    Text("Albums")
                        .font(.title2.weight(.semibold))
                        .foregroundStyle(theme.textPrimary)

                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 16) {
                        ForEach(PreviewLibrary.sampleAlbums) { album in
                            VStack(alignment: .leading, spacing: 10) {
                                PreviewArtworkView(name: album.artworkName, gradient: album.gradient, size: CGSize(width: 160, height: 160))
                                Text(album.title)
                                    .font(.headline)
                                    .foregroundStyle(theme.textPrimary)
                                    .lineLimit(1)
                                Text(album.artist)
                                    .font(.subheadline)
                                    .foregroundStyle(theme.textSecondary)
                            }
                        }
                    }
                }
                .padding(20)
            }
            .background(theme.background.ignoresSafeArea())
        }
    }
}

private struct VerseFlowSearchView: View {
    @EnvironmentObject private var appState: VerseFlowAppState
    let theme: VerseFlowTheme

    var body: some View {
        NavigationStack {
            VStack(spacing: 18) {
                TextField("Search songs, albums, artists", text: $appState.searchQuery)
                    .textFieldStyle(.plain)
                    .padding(16)
                    .background(theme.surface, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
                    .foregroundStyle(theme.textPrimary)

                VStack(spacing: 12) {
                    ForEach(filteredSongs) { song in
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(song.title)
                                    .foregroundStyle(theme.textPrimary)
                                Text("\(song.artist) • \(song.album)")
                                    .font(.subheadline)
                                    .foregroundStyle(theme.textSecondary)
                            }
                            Spacer()
                        }
                        .verseFlowCard(theme: theme)
                    }
                }

                Spacer()
            }
            .padding(20)
            .background(theme.background.ignoresSafeArea())
            .navigationTitle("Search")
        }
    }

    private var filteredSongs: [VerseFlowSong] {
        let query = appState.searchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        if query.isEmpty { return PreviewLibrary.sampleSongs }
        return PreviewLibrary.sampleSongs.filter {
            $0.title.localizedCaseInsensitiveContains(query) ||
            $0.artist.localizedCaseInsensitiveContains(query) ||
            $0.album.localizedCaseInsensitiveContains(query)
        }
    }
}

private struct VerseFlowQueueView: View {
    @EnvironmentObject private var appState: VerseFlowAppState
    let theme: VerseFlowTheme

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Play Queue")
                        .font(.largeTitle.weight(.bold))
                        .foregroundStyle(theme.textPrimary)

                    ForEach(PreviewLibrary.sampleSongs) { song in
                        HStack(spacing: 14) {
                            PreviewArtworkView(name: song.artworkName, gradient: song.gradient, size: CGSize(width: 56, height: 56))
                            VStack(alignment: .leading, spacing: 4) {
                                Text(song.title)
                                    .foregroundStyle(theme.textPrimary)
                                Text(song.artist)
                                    .font(.subheadline)
                                    .foregroundStyle(theme.textSecondary)
                            }
                            Spacer()
                            if song.id == appState.currentSong.id {
                                Text("LIVE")
                                    .font(.caption.weight(.bold))
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(theme.primary.opacity(0.18), in: Capsule())
                                    .foregroundStyle(theme.primary)
                            }
                        }
                        .verseFlowCard(theme: theme)
                    }
                }
                .padding(20)
            }
            .background(theme.background.ignoresSafeArea())
        }
    }
}

private struct VerseFlowNowPlayingView: View {
    @EnvironmentObject private var appState: VerseFlowAppState
    let theme: VerseFlowTheme

    var body: some View {
        let song = appState.currentSong

        ZStack {
            ArtworkReactiveBackground(song: song)
            ScrollView {
                VStack(spacing: 22) {
                    PreviewArtworkView(name: song.artworkName, gradient: song.gradient, size: CGSize(width: 300, height: 300))
                        .padding(.top, 24)

                    VStack(spacing: 8) {
                        Text(song.title)
                            .font(.system(size: 32, weight: .bold, design: .default))
                            .foregroundStyle(.white)
                        Text(song.artist)
                            .font(.title3.weight(.medium))
                            .foregroundStyle(.white.opacity(0.82))
                        Text(song.album)
                            .font(.subheadline)
                            .foregroundStyle(.white.opacity(0.65))
                    }

                    VStack(spacing: 10) {
                        GeometryReader { geometry in
                            ZStack(alignment: .leading) {
                                Capsule().fill(Color.white.opacity(0.18))
                                Capsule()
                                    .fill(Color.white)
                                    .frame(width: geometry.size.width * appState.playbackProgress)
                            }
                        }
                        .frame(height: 4)

                        HStack {
                            Text("1:14")
                            Spacer()
                            Text("3:18")
                        }
                        .font(.caption)
                        .foregroundStyle(.white.opacity(0.70))
                    }

                    HStack(spacing: 26) {
                        Image(systemName: "backward.fill")
                        Button {
                            appState.isPlaying.toggle()
                        } label: {
                            Image(systemName: appState.isPlaying ? "pause.circle.fill" : "play.circle.fill")
                                .font(.system(size: 68))
                        }
                        Image(systemName: "forward.fill")
                    }
                    .font(.system(size: 24))
                    .foregroundStyle(.white)

                    Button {
                        appState.selectedTab = .lyrics
                    } label: {
                        Text("Open Lyrics")
                            .font(.headline)
                            .padding(.horizontal, 22)
                            .padding(.vertical, 14)
                            .background(.white.opacity(0.12), in: Capsule())
                            .foregroundStyle(.white)
                    }
                }
                .padding(24)
            }
        }
        .ignoresSafeArea()
    }
}

private struct VerseFlowLyricsView: View {
    @EnvironmentObject private var appState: VerseFlowAppState
    let theme: VerseFlowTheme

    var body: some View {
        let song = appState.currentSong

        ZStack {
            ArtworkReactiveBackground(song: song)
            VStack(spacing: 0) {
                VStack(spacing: 8) {
                    Text(song.title)
                        .font(.title.weight(.bold))
                        .foregroundStyle(.white)
                    Text(song.artist)
                        .font(.subheadline)
                        .foregroundStyle(.white.opacity(0.72))
                }
                .frame(maxWidth: .infinity)
                .padding(.top, 72)
                .padding(.bottom, 18)
                .background(.black.opacity(0.18))

                Divider().overlay(.white.opacity(0.14))

                ScrollView {
                    VStack(spacing: 28) {
                        ForEach(Array(song.lyrics.enumerated()), id: \.offset) { index, line in
                            Text(line)
                                .font(index == 1 ? .title2.weight(.bold) : .title3.weight(.medium))
                                .foregroundStyle(index == 1 ? .white : .white.opacity(0.74))
                                .multilineTextAlignment(.center)
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .padding(.horizontal, 28)
                    .padding(.vertical, 30)
                }

                Divider().overlay(.white.opacity(0.18))

                VStack(spacing: 12) {
                    GeometryReader { geometry in
                        ZStack(alignment: .leading) {
                            Capsule().fill(Color.white.opacity(0.18))
                            Capsule()
                                .fill(Color.white)
                                .frame(width: geometry.size.width * appState.playbackProgress)
                        }
                    }
                    .frame(height: 3)

                    HStack(spacing: 28) {
                        Image(systemName: "backward.fill")
                        Button {
                            appState.isPlaying.toggle()
                        } label: {
                            Image(systemName: appState.isPlaying ? "pause.circle.fill" : "play.circle.fill")
                                .font(.system(size: 56))
                        }
                        Image(systemName: "forward.fill")
                    }
                    .font(.system(size: 22))
                    .foregroundStyle(.white)
                }
                .padding(.horizontal, 24)
                .padding(.top, 14)
                .padding(.bottom, 24)
                .background(.black.opacity(0.24))
            }
        }
        .ignoresSafeArea()
    }
}

private struct VerseFlowSettingsView: View {
    @EnvironmentObject private var appState: VerseFlowAppState
    let theme: VerseFlowTheme

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    Text("Settings")
                        .font(.largeTitle.weight(.bold))
                        .foregroundStyle(theme.textPrimary)

                    Text("Themes")
                        .font(.title2.weight(.semibold))
                        .foregroundStyle(theme.textPrimary)

                    VStack(spacing: 12) {
                        ForEach(VerseFlowThemePreset.allCases) { preset in
                            Button {
                                appState.selectedTheme = preset
                            } label: {
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text(preset.title)
                                            .foregroundStyle(theme.textPrimary)
                                        Text("iPhone foundation theme")
                                            .font(.subheadline)
                                            .foregroundStyle(theme.textSecondary)
                                    }
                                    Spacer()
                                    if appState.selectedTheme == preset {
                                        Image(systemName: "checkmark.circle.fill")
                                            .foregroundStyle(theme.primary)
                                    }
                                }
                                .verseFlowCard(theme: theme)
                            }
                        }
                    }
                }
                .padding(20)
            }
            .background(theme.background.ignoresSafeArea())
        }
    }
}

private struct PreviewArtworkView: View {
    let name: String
    let gradient: [Color]
    let size: CGSize

    var body: some View {
        ZStack {
            LinearGradient(colors: gradient, startPoint: .topLeading, endPoint: .bottomTrailing)
            VStack(spacing: 8) {
                Text("VERSEFLOW")
                    .font(.caption2.weight(.bold))
                    .tracking(2)
                    .foregroundStyle(.white.opacity(0.72))
                Text(name.replacingOccurrences(of: "preview-cover-", with: "").uppercased())
                    .font(.system(size: min(size.width, size.height) * 0.12, weight: .black))
                    .foregroundStyle(.white)
            }
            .padding()
        }
        .frame(width: size.width, height: size.height)
        .clipShape(RoundedRectangle(cornerRadius: 28, style: .continuous))
    }
}

private struct ArtworkReactiveBackground: View {
    let song: VerseFlowSong

    var body: some View {
        ZStack {
            LinearGradient(colors: song.gradient, startPoint: .topLeading, endPoint: .bottomTrailing)
                .ignoresSafeArea()
            PreviewArtworkView(name: song.artworkName, gradient: song.gradient, size: CGSize(width: 520, height: 520))
                .scaleEffect(1.5)
                .blur(radius: 90)
                .opacity(0.65)
            Rectangle()
                .fill(.black.opacity(0.38))
                .ignoresSafeArea()
        }
    }
}
