import HotwireNative
import SwiftUI
import UIKit

class ArtistsViewController: UIHostingController<ArtistsScreen>, PathConfigurationIdentifiable {
    static var pathConfigurationIdentifier: String { "artists" }

    init() {
        super.init(rootView: ArtistsScreen())
        title = String(localized: "label.artists")
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
