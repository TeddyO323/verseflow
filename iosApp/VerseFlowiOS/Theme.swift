import SwiftUI

struct VerseFlowTheme {
    let background: Color
    let surface: Color
    let elevatedSurface: Color
    let primary: Color
    let secondary: Color
    let textPrimary: Color
    let textSecondary: Color
    let separator: Color
}

extension VerseFlowThemePreset {
    var theme: VerseFlowTheme {
        switch self {
        case .nebula:
            return VerseFlowTheme(
                background: Color(red: 0.03, green: 0.04, blue: 0.10),
                surface: Color(red: 0.07, green: 0.09, blue: 0.16),
                elevatedSurface: Color(red: 0.10, green: 0.12, blue: 0.22),
                primary: Color(red: 0.41, green: 0.61, blue: 1.0),
                secondary: Color(red: 0.42, green: 0.90, blue: 0.98),
                textPrimary: .white,
                textSecondary: Color.white.opacity(0.72),
                separator: Color.white.opacity(0.10)
            )
        case .eclipse:
            return VerseFlowTheme(
                background: .black,
                surface: Color(red: 0.05, green: 0.05, blue: 0.06),
                elevatedSurface: Color(red: 0.08, green: 0.08, blue: 0.09),
                primary: Color(red: 0.70, green: 0.70, blue: 0.76),
                secondary: Color(red: 0.42, green: 0.90, blue: 0.98),
                textPrimary: .white,
                textSecondary: Color.white.opacity(0.68),
                separator: Color.white.opacity(0.08)
            )
        case .crimson:
            return VerseFlowTheme(
                background: Color(red: 0.10, green: 0.02, blue: 0.04),
                surface: Color(red: 0.16, green: 0.04, blue: 0.07),
                elevatedSurface: Color(red: 0.22, green: 0.05, blue: 0.10),
                primary: Color(red: 0.95, green: 0.36, blue: 0.42),
                secondary: Color(red: 1.0, green: 0.78, blue: 0.70),
                textPrimary: .white,
                textSecondary: Color.white.opacity(0.74),
                separator: Color.white.opacity(0.10)
            )
        case .solar:
            return VerseFlowTheme(
                background: Color(red: 0.11, green: 0.08, blue: 0.03),
                surface: Color(red: 0.17, green: 0.12, blue: 0.05),
                elevatedSurface: Color(red: 0.23, green: 0.16, blue: 0.06),
                primary: Color(red: 0.96, green: 0.73, blue: 0.22),
                secondary: Color(red: 1.0, green: 0.89, blue: 0.61),
                textPrimary: .white,
                textSecondary: Color.white.opacity(0.74),
                separator: Color.white.opacity(0.10)
            )
        case .cobalt:
            return VerseFlowTheme(
                background: Color(red: 0.03, green: 0.07, blue: 0.13),
                surface: Color(red: 0.05, green: 0.12, blue: 0.20),
                elevatedSurface: Color(red: 0.07, green: 0.16, blue: 0.26),
                primary: Color(red: 0.38, green: 0.61, blue: 1.0),
                secondary: Color(red: 0.69, green: 0.86, blue: 1.0),
                textPrimary: .white,
                textSecondary: Color.white.opacity(0.72),
                separator: Color.white.opacity(0.10)
            )
        case .arctic:
            return VerseFlowTheme(
                background: Color(red: 0.92, green: 0.97, blue: 1.0),
                surface: .white,
                elevatedSurface: Color(red: 0.86, green: 0.93, blue: 0.98),
                primary: Color(red: 0.20, green: 0.46, blue: 0.76),
                secondary: Color(red: 0.34, green: 0.65, blue: 0.89),
                textPrimary: Color(red: 0.08, green: 0.12, blue: 0.18),
                textSecondary: Color(red: 0.25, green: 0.33, blue: 0.42),
                separator: Color.black.opacity(0.08)
            )
        case .rose:
            return VerseFlowTheme(
                background: Color(red: 0.99, green: 0.95, blue: 0.97),
                surface: .white,
                elevatedSurface: Color(red: 0.98, green: 0.89, blue: 0.93),
                primary: Color(red: 0.79, green: 0.33, blue: 0.50),
                secondary: Color(red: 0.93, green: 0.63, blue: 0.74),
                textPrimary: Color(red: 0.20, green: 0.11, blue: 0.15),
                textSecondary: Color(red: 0.36, green: 0.24, blue: 0.28),
                separator: Color.black.opacity(0.08)
            )
        case .mint:
            return VerseFlowTheme(
                background: Color(red: 0.93, green: 0.99, blue: 0.97),
                surface: .white,
                elevatedSurface: Color(red: 0.87, green: 0.97, blue: 0.93),
                primary: Color(red: 0.16, green: 0.58, blue: 0.45),
                secondary: Color(red: 0.45, green: 0.79, blue: 0.67),
                textPrimary: Color(red: 0.07, green: 0.17, blue: 0.15),
                textSecondary: Color(red: 0.22, green: 0.35, blue: 0.31),
                separator: Color.black.opacity(0.08)
            )
        case .amber:
            return VerseFlowTheme(
                background: Color(red: 0.99, green: 0.96, blue: 0.89),
                surface: Color(red: 1.0, green: 0.99, blue: 0.96),
                elevatedSurface: Color(red: 0.95, green: 0.89, blue: 0.72),
                primary: Color(red: 0.68, green: 0.45, blue: 0.10),
                secondary: Color(red: 0.84, green: 0.63, blue: 0.18),
                textPrimary: Color(red: 0.20, green: 0.13, blue: 0.03),
                textSecondary: Color(red: 0.38, green: 0.29, blue: 0.13),
                separator: Color.black.opacity(0.08)
            )
        case .mono:
            return VerseFlowTheme(
                background: Color(red: 0.95, green: 0.95, blue: 0.95),
                surface: .white,
                elevatedSurface: Color(red: 0.90, green: 0.90, blue: 0.90),
                primary: .black,
                secondary: Color(red: 0.35, green: 0.35, blue: 0.35),
                textPrimary: .black,
                textSecondary: Color.black.opacity(0.62),
                separator: Color.black.opacity(0.10)
            )
        }
    }
}

extension View {
    func verseFlowCard(theme: VerseFlowTheme) -> some View {
        self
            .padding(18)
            .background(theme.surface.opacity(0.92), in: RoundedRectangle(cornerRadius: 24, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .stroke(theme.separator, lineWidth: 1)
            )
    }
}
