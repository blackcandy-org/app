import SwiftUI
import sharedKit

struct AlbumsScreen: View {
    private let viewModel: AlbumsViewModel = KoinHelper().getAlbumsViewModel()

    private let columns = [
        GridItem(
            .adaptive(minimum: CustomStyle.coverGridMinWidth),
            spacing: CustomStyle.spacing(.medium),
            alignment: .top
        )
    ]

    var body: some View {
        Observing(viewModel.uiState) { uiState in
            ScrollView {
                LazyVGrid(columns: columns, spacing: CustomStyle.spacing(.large)) {
                    ForEach(uiState.albums, id: \.id) { album in
                        CoverCard(
                            name: album.name,
                            imageUrl: album.imageUrls.medium,
                            subtitle: album.artistName
                        )
                        .onAppear {
                            if album.id == uiState.albums.last?.id {
                                viewModel.loadNextPage()
                            }
                        }
                    }
                }
                .padding(CustomStyle.spacing(.medium))

                if uiState.isLoading && !uiState.albums.isEmpty {
                    ProgressView()
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
                onAction: { viewModel.alertActionPerformed(action: $0) }
            )
        }
        .task {
            viewModel.loadFirstPage()
        }
        .navigationTitle("label.albums")
    }
}
