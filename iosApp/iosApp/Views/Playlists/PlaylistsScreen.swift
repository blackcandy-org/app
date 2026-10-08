import SwiftUI
import sharedKit

struct PlaylistsScreen: View {
    private let viewModel: PlaylistsViewModel = KoinHelper().getPlaylistsViewModel()

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(uiState.playlists, id: \.id) { playlist in
                        PlaylistItem(playlist: playlist)
                            .onAppear {
                                if playlist.id == uiState.playlists.last?.id {
                                    viewModel.loadNextPage()
                                }
                            }
                    }
                }
                .padding(.vertical, CustomStyle.spacing(.narrow))

                if uiState.isLoading && !uiState.playlists.isEmpty {
                    ProgressView()
                        .padding(.bottom, CustomStyle.spacing(.medium))
                }
            }
            .overlay {
                if uiState.playlists.isEmpty && uiState.isLoading {
                    ProgressView()
                }
            }
            .alertMessage(
                uiState.alertMessage,
                onShown: { viewModel.alertMessageShown() },
                onAction: { viewModel.alertActionPerformed(action: $0) }
            )
        }
        .task {
            viewModel.loadFirstPage()
        }
        .navigationTitle("label.playlists")
    }
}

private struct PlaylistItem: View {
    let playlist: Playlist

    var body: some View {
        HStack(spacing: CustomStyle.spacing(.small)) {
            Image(systemName: playlist.isFavorite ? "heart.fill" : "music.note.list")
                .foregroundStyle(playlist.isFavorite ? Color.red : Color.primary)
                .frame(width: CustomStyle.spacing(.wide))

            Text(playlist.name)
                .customStyle(.mediumFont)
                .lineLimit(1)

            Spacer()
        }
        .padding(.horizontal, CustomStyle.spacing(.medium))
        .padding(.vertical, CustomStyle.spacing(.small))
    }
}
