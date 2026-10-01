import HotwireNative
import SwiftUI
import UIKit

class AlbumsViewController: UIHostingController<AlbumsScreen>, PathConfigurationIdentifiable {
    static var pathConfigurationIdentifier: String { "albums" }

    init() {
        super.init(rootView: AlbumsScreen())
        title = String(localized: "label.albums")
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
