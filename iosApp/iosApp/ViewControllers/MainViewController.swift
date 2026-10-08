import SwiftUI
import UIKit
import sharedKit

class MainViewController: UIHostingController<MainScreen> {
    private let musicServiceViewModel: MusicServiceViewModel = KoinHelper().getMusicServiceViewModel()

    init() {
        super.init(rootView: MainScreen())
        musicServiceViewModel.setupMusicServiceController()
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
