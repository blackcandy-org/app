import SwiftUI
import sharedKit

struct ArtistsScreen: View {
    private let viewModel: ArtistsViewModel = KoinHelper().getArtistsViewModel()

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
                    ForEach(uiState.artists, id: \.id) { artist in
                        CoverCard(
                            name: artist.name,
                            imageUrl: artist.imageUrls.medium
                        )
                        .onAppear {
                            if artist.id == uiState.artists.last?.id {
                                viewModel.loadNextPage()
                            }
                        }
                    }
                }
                .padding(CustomStyle.spacing(.medium))

                if uiState.isLoading && !uiState.artists.isEmpty {
                    ProgressView()
                        .padding(.bottom, CustomStyle.spacing(.medium))
                }
            }
            .overlay {
                if uiState.artists.isEmpty && uiState.isLoading {
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
        .navigationTitle("label.artists")
    }
}
