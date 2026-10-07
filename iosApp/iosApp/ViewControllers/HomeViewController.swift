import HotwireNative
import SwiftUI
import UIKit

class HomeViewController: UIHostingController<HomeScreen>, PathConfigurationIdentifiable {
    static var pathConfigurationIdentifier: String { "home" }

    init() {
        super.init(rootView: HomeScreen())
        title = String(localized: "label.home")
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
