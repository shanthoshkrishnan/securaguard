import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {

        // Set up the initial view controller
        let window = UIWindow(frame: UIScreen.main.bounds)
        self.window = window

        // Create and set the root view controller
        let storyboard = UIStoryboard(name: "Main", bundle: nil)

        if let loginViewController = storyboard.instantiateViewController(withIdentifier: "LoginViewController") as? LoginViewController {
            let navigationController = UINavigationController(rootViewController: loginViewController)
            window.rootViewController = navigationController
        }

        window.makeKeyAndVisible()

        // Print debug info (Optional)
        print("App launched successfully")

        return true
    }

    // Handle scene transitions (for apps supporting multiple scenes)
    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(
        _ application: UIApplication,
        didDiscardSceneSessions sceneSessions: Set<UISceneSession>
    ) {
        // Handle discarded sessions if needed
    }
}
