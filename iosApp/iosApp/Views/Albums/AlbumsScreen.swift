import SwiftUI
import sharedKit

struct AlbumsScreen: View {
    private let viewModel: AlbumsViewModel = KoinHelper().getAlbumsViewModel()

    private let columns = [
        GridItem(
            .adaptive(minimum: CustomStyle.albumGridMinWidth),
            spacing: CustomStyle.spacing(.medium),
            alignment: .top
        )
    ]

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            ScrollView {
                LazyVGrid(columns: columns, spacing: CustomStyle.spacing(.large)) {
                    ForEach(uiState.albums, id: \.id) { album in
                        AlbumCard(album: album)
                            .onAppear {
                                if album.id == uiState.albums.last?.id {
                                    viewModel.loadNextPage()
                                }
                            }
                    }
                }
                .padding(CustomStyle.spacing(.medium))

                if let pageInfo = uiState.pageInfo {
                    Text("label.albums_loaded(\(uiState.albums.count), \(Int(pageInfo.totalCount)))")
                        .customStyle(.smallFont)
                        .foregroundStyle(.secondary)
                        .padding(.bottom, CustomStyle.spacing(.medium))
                }
            }
            .overlay {
                if uiState.albums.isEmpty && uiState.isLoading {
                    ProgressView()
                }
            }
            .alertMessage(
                uiState.alertMessage,
                onShown: { viewModel.alertMessageShown() },
                onRetry: { viewModel.retry() }
            )
        }
        .task {
            viewModel.loadFirstPage()
        }
    }
}

private struct AlbumCard: View {
    let album: Album

    var body: some View {
        VStack(alignment: .leading, spacing: CustomStyle.spacing(.narrow)) {
            AsyncImage(url: URL(string: album.imageUrls.medium)) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.secondary.opacity(0.2)
            }
            .aspectRatio(1, contentMode: .fit)
            .clipShape(RoundedRectangle(cornerRadius: CustomStyle.cornerRadius(.large)))

            VStack(alignment: .leading, spacing: CustomStyle.spacing(.tiny)) {
                Text(album.name)
                    .customStyle(.mediumFont)
                    .fontWeight(.bold)
                    .lineLimit(2)

                Text(album.artistName)
                    .customStyle(.smallFont)
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
