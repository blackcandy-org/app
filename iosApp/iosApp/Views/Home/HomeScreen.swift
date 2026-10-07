import SwiftUI
import sharedKit

struct HomeScreen: View {
    private let viewModel: HomeViewModel = KoinHelper().getHomeViewModel()

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            ScrollView {
                VStack(alignment: .leading, spacing: CustomStyle.spacing(.wide)) {
                    AlbumSection(title: "label.recently_played", albums: uiState.recentlyPlayedAlbums)
                    AlbumSection(title: "label.recently_added", albums: uiState.recentlyAddedAlbums)
                }
                .padding(CustomStyle.spacing(.medium))
            }
            .alertMessage(
                uiState.alertMessage,
                onShown: { viewModel.alertMessageShown() },
                onAction: { viewModel.alertActionPerformed(action: $0) }
            )
        }
        .task {
            viewModel.load()
        }
    }
}

private struct AlbumSection: View {
    let title: LocalizedStringKey
    let albums: [Album]

    private let columns = [
        GridItem(
            .adaptive(minimum: CustomStyle.coverGridMinWidth),
            spacing: CustomStyle.spacing(.medium),
            alignment: .top
        )
    ]

    var body: some View {
        if !albums.isEmpty {
            VStack(alignment: .leading, spacing: CustomStyle.spacing(.medium)) {
                Text(title)
                    .font(.title2)
                    .fontWeight(.bold)

                LazyVGrid(columns: columns, spacing: CustomStyle.spacing(.large)) {
                    ForEach(albums, id: \.id) { album in
                        CoverCard(
                            name: album.name,
                            imageUrl: album.imageUrls.medium,
                            subtitle: album.artistName
                        )
                    }
                }
            }
        }
    }
}
