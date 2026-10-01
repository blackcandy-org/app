import HotwireNative
import SwiftUI
import UIKit

class PlaylistsViewController: UIHostingController<PlaylistsScreen>, PathConfigurationIdentifiable {
    static var pathConfigurationIdentifier: String { "playlists" }

    init() {
        super.init(rootView: PlaylistsScreen())
        title = String(localized: "label.playlists")
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
