import UIKit
import HotwireNative
import LNPopupUI
import sharedKit

class MainViewController: HotwireTabBarController {
    private let musicServiceViewModel: MusicServiceViewModel = KoinHelper().getMusicServiceViewModel()

    init(serverAddress: String, navigatorDelegate: NavigatorDelegate?) {
        super.init(navigatorDelegate: navigatorDelegate)

        load(buildMainTabs(serverAddress: serverAddress))

        presentPopupBar {
            PlayerScreen()
        }

        musicServiceViewModel.setupMusicServiceController()
    }
}
