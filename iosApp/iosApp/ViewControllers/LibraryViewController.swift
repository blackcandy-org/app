import HotwireNative
import SwiftUI
import UIKit

class LibraryViewController: UIHostingController<LibraryScreen>, PathConfigurationIdentifiable {
    static var pathConfigurationIdentifier: String { "library" }

    init(url: URL, navigator: NavigationHandler) {
        super.init(rootView: LibraryScreen(navigateTo: { [weak navigator] path in
            navigator?.route(URL(string: path, relativeTo: url)!.absoluteURL)
        }))
        title = String(localized: "label.library")
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
