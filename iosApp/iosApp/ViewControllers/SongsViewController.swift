import HotwireNative
import SwiftUI
import UIKit

class SongsViewController: UIHostingController<SongsScreen>, PathConfigurationIdentifiable {
    static var pathConfigurationIdentifier: String { "songs" }

    init() {
        super.init(rootView: SongsScreen())
        title = String(localized: "label.songs")
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
