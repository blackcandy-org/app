import SwiftUI

struct LibraryScreen: View {
    var body: some View {
        List {
            NavigationLink(value: Route.albums) {
                Label("label.albums", systemImage: "square.stack")
            }

            NavigationLink(value: Route.artists) {
                Label("label.artists", systemImage: "music.mic")
            }

            NavigationLink(value: Route.songs) {
                Label("label.songs", systemImage: "music.note")
            }

            NavigationLink(value: Route.playlists) {
                Label("label.playlists", systemImage: "music.note.list")
            }
        }
        .navigationTitle("label.library")
        .navigationDestination(for: Route.self) { route in
            switch route {
            case .albums:
                AlbumsScreen()
            case .artists:
                ArtistsScreen()
            case .songs:
                SongsScreen()
            case .playlists:
                PlaylistsScreen()
            }
        }
    }
}

extension LibraryScreen {
    enum Route: Hashable {
        case albums
        case artists
        case songs
        case playlists
    }
}
