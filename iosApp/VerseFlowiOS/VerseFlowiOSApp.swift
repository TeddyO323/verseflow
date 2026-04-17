import SwiftUI

@main
struct VerseFlowiOSApp: App {
    @StateObject private var appState = VerseFlowAppState()

    var body: some Scene {
        WindowGroup {
            VerseFlowRootView()
                .environmentObject(appState)
        }
    }
}
