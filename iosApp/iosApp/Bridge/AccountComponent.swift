import Foundation
import SwiftUI
import UIKit
import HotwireNative
import sharedKit

class AccountComponent: BridgeComponent {
    private  var menuItems: [MenuItem] = []

    private var viewController: WebViewController? {
        delegate?.destination as? WebViewController
    }

    private var viewModel: WebViewModel? {
        viewController?.viewModel
    }

    override class var name: String { "account" }

    override func onReceive(message: Message) {
        switch message.event {
        case "connect":
            handleConnectEvent()
        case "menuItemConnected:settings":
            handleMenuItemConnectedEvent("settings")
        case "menuItemConnected:about":
            handleMenuItemConnectedEvent("about")
        case "menuItemConnected:logout":
            handleMenuItemConnectedEvent("logout")
        default:
            break
        }
    }

    private func handleConnectEvent() {
        guard let viewController else { return }

        let action = UIAction { [unowned self] _ in
            viewController.present(
                UIHostingController(
                    rootView: AccountMenu(menuItems: menuItems)
                ),
                animated: true
            )
        }

        let item = UIBarButtonItem(title: String(localized: "label.account"), primaryAction: action)
        item.image = .init(systemName: "person.circle")

        viewController.navigationItem.rightBarButtonItem = item
    }

    private func handleMenuItemConnectedEvent(_ id: String) {
        if menuItems.contains(where: { $0.id == id }) { return }

        switch id {
        case "settings":
            menuItems.append(
                .init(
                    id: "settings",
                    title: String(localized: "label.settings"),
                    action: {
                        self.reply(to: "menuItemConnected:settings")
                    }
                )
            )
        case "about":
            menuItems.append(
                .init(
                    id: "about",
                    title: String(localized: "label.about"),
                    action: {
                        self.reply(to: "menuItemConnected:about")
                    }
                )
            )
        case "logout":
            menuItems.append(
                .init(
                    id: "logout",
                    type: .destructive,
                    title: String(localized: "label.logout"),
                    action: {
                        self.viewModel?.logout(onSuccess: {
                            changeRootViewController(viewController: LoginViewController())
                        })
                    }
                )
            )
        default:
            break
        }
    }
}
