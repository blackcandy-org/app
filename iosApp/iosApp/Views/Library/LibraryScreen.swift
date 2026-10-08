import SwiftUI

struct LibraryScreen: View {
    let navigateTo: (String) -> Void

    var body: some View {
        List {
            LibraryItem(title: "label.albums", systemImage: "square.stack") { navigateTo("/albums") }
            LibraryItem(title: "label.artists", systemImage: "music.mic") { navigateTo("/artists") }
            LibraryItem(title: "label.songs", systemImage: "music.note") { navigateTo("/songs") }
            LibraryItem(title: "label.playlists", systemImage: "music.note.list") { navigateTo("/playlists") }
        }
    }
}

private struct LibraryItem: View {
    let title: LocalizedStringKey
    let systemImage: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Label(title, systemImage: systemImage)
        }
        .foregroundStyle(.primary)
    }
}
