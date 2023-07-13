import SwiftUI
import FirebaseCore
import Firebase
import FirebaseMessaging
import UIKit
import UserNotifications


class AppDelegate: NSObject, UIApplicationDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

        FirebaseApp.configure()
        FirebaseConfiguration.shared.setLoggerLevel(.min)

        registrationForNotifications(application)

        Messaging.messaging().delegate = self

        return true
    }
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken

        let tokenComponents = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let deviceTokenString = tokenComponents.joined()
        //print(">>>>2 \(deviceTokenString)")
    }

    func application(_ application: UIApplication,didFailToRegisterForRemoteNotificationsWithError error: Error){
        LoggingTree.INSTANCE.e("AppDelegate/veriapplication_error", error)
       //print(">>>>1 \(error)")
    }

    func registrationForNotifications(_ application: UIApplication){

        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self

            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions) { _, _ in }
        } else {
            let settings: UIUserNotificationSettings =
            UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }

        application.registerForRemoteNotifications()
        //        DispatchQueue.main.async {
        //            UIApplication.shared.registerForRemoteNotifications()
        //        }
    }
}


extension AppDelegate: UNUserNotificationCenterDelegate {
    // Receive displayed notifications for iOS 10 devices.
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler:
        @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        //нотификация
        //срабатывает при запущеном приложениии, можно управлять показом
        
        //print (">>>>>>>!!>>>>>> 1")
        let userInfo: [AnyHashable : Any] = notification.request.content.userInfo
        PadForMyFirebaseMessagingService.shared.onMessageReceived(userInfo)
      
        if(PadForMyFirebaseMessagingService.shared.isShowNotiFromIOSApp(userInfo)){
            completionHandler([[.banner, .sound]])
        }
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        //нотификация
        //срабатывает при клике на нотификашку (на пререднем плане, свернуто, не запущено)
        
       // print (">>>>>>>!!>>>>>> 2")
        
        let userInfo: [AnyHashable : Any] = response.notification.request.content.userInfo
        
        PadForMyFirebaseMessagingService.shared.onMessageReceived(userInfo)
        AppState.shared.setDateNoti(userInfo)
        completionHandler()
    }
}

extension AppDelegate: MessagingDelegate {
  func messaging(
    _ messaging: Messaging,
    didReceiveRegistrationToken fcmToken: String?
  ) {
      //print(">>>>>> messaging")

    let tokenDict = ["token": fcmToken ?? ""]
    NotificationCenter.default.post(
      name: Notification.Name("FCMToken"),
      object: nil,
      userInfo: tokenDict)
  }
}



@main
struct iosApp: App {
   @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            SplashView()
        }
    }
}

