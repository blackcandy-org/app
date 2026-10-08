import SwiftUI
import AlertKit
import sharedKit

struct SongsScreen: View {
    private let viewModel: SongsViewModel = KoinHelper().getSongsViewModel()

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(uiState.songs, id: \.id) { song in
                        SongItem(
                            song: song,
                            onPlayNext: { viewModel.playNext(songId: song.id) },
                            onPlayLast: { viewModel.playLast(songId: song.id) }
                        )
                        .onTapGesture {
                            viewModel.playNow(songId: song.id)
                        }
                        .onAppear {
                            if song.id == uiState.songs.last?.id {
                                viewModel.loadNextPage()
                            }
                        }
                    }
                }
                .padding(.vertical, CustomStyle.spacing(.narrow))

                if uiState.isLoading && !uiState.songs.isEmpty {
                    ProgressView()
                        .padding(.bottom, CustomStyle.spacing(.medium))
                }
            }
            .overlay {
                if uiState.songs.isEmpty && uiState.isLoading {
                    ProgressView()
                }
            }
            .alertMessage(
                uiState.alertMessage?.action != nil ? uiState.alertMessage : nil,
                onShown: { viewModel.alertMessageShown() },
                onAction: { viewModel.alertActionPerformed(action: $0) }
            )
        }
        .collect(flow: viewModel.uiState) { uiState in
            guard let alertMessage = uiState.alertMessage, alertMessage.action == nil else { return }

            AlertKitAPI.present(
                title: AlertMessageCover.toString(alertMessage),
                style: .iOS17AppleMusic,
                haptic: .success
            )

            viewModel.alertMessageShown()
        }
        .task {
            viewModel.loadFirstPage()
        }
        .navigationTitle("label.songs")
    }
}

private struct SongItem: View {
    let song: Song
    let onPlayNext: () -> Void
    let onPlayLast: () -> Void

    var body: some View {
        HStack(spacing: CustomStyle.spacing(.small)) {
            AsyncImage(url: URL(string: song.albumImageUrls.small)) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.secondary.opacity(0.2)
            }
            .frame(width: CustomStyle.songCoverSize, height: CustomStyle.songCoverSize)
            .clipShape(RoundedRectangle(cornerRadius: CustomStyle.cornerRadius(.medium)))

            VStack(alignment: .leading, spacing: CustomStyle.spacing(.tiny)) {
                Text(song.name)
                    .customStyle(.mediumFont)
                    .lineLimit(1)

                Text(song.artistName)
                    .customStyle(.smallFont)
                    .foregroundStyle(.secondary)
                    .lineLimit(1)
            }

            Spacer()

            Menu {
                Button("label.play_next", action: onPlayNext)
                Button("label.play_last", action: onPlayLast)
            } label: {
                Label("label.more", systemImage: "ellipsis")
                    .labelStyle(.iconOnly)
                    .padding(CustomStyle.spacing(.small))
            }
            .tint(.secondary)
        }
        .padding(.leading, CustomStyle.spacing(.medium))
        .padding(.trailing, CustomStyle.spacing(.narrow))
        .padding(.vertical, CustomStyle.spacing(.narrow))
        .contentShape(Rectangle())
    }
}
