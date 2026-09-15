import UIKit

func currentSceneDelegate() -> SceneDelegate? {
    UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate
}

func changeRootViewController(viewController: UIViewController) {
    currentSceneDelegate()?.window?.rootViewController = viewController
}
