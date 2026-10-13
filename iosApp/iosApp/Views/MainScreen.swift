import SwiftUI
import LNPopupUI

enum MainTab: Hashable {
    case home
    case library
    case libraryRoute(LibraryScreen.Route)
}

struct MainScreen: View {
    @Environment(\.horizontalSizeClass) private var sizeClass

    @State private var selection: MainTab = .home
    @State private var libraryPath: [LibraryScreen.Route] = []

    var body: some View {
        TabView(selection: $selection) {
            Tab("label.home", systemImage: "house", value: MainTab.home) {
                NavigationStack {
                    HomeScreen()
                }
            }

            Tab("label.library", systemImage: "square.stack", value: MainTab.library) {
                NavigationStack(path: $libraryPath) {
                    LibraryScreen()
                }
            }
            .hidden(sizeClass != .compact)

            TabSection("label.library") {
                Tab("label.albums", systemImage: "square.stack", value: MainTab.libraryRoute(.albums)) {
                    NavigationStack {
                        AlbumsScreen()
                    }
                }

                Tab("label.artists", systemImage: "music.mic", value: MainTab.libraryRoute(.artists)) {
                    NavigationStack {
                        ArtistsScreen()
                    }
                }

                Tab("label.songs", systemImage: "music.note", value: MainTab.libraryRoute(.songs)) {
                    NavigationStack {
                        SongsScreen()
                    }
                }

                Tab("label.playlists", systemImage: "music.note.list", value: MainTab.libraryRoute(.playlists)) {
                    NavigationStack {
                        PlaylistsScreen()
                    }
                }
            }
            .hidden(sizeClass == .compact)
        }
        .tabViewStyle(.sidebarAdaptable)
        .onChange(of: sizeClass, initial: true) { _, sizeClass in
            if sizeClass == .compact, case let .libraryRoute(route) = selection {
                libraryPath = [route]
                selection = .library
            } else if sizeClass == .regular, selection == .library {
                selection = .libraryRoute(libraryPath.last ?? .albums)
            }
        }
        .popup(isBarPresented: .constant(true)) {
            PlayerScreen()
        }
    }
}
