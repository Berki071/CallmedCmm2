//
//  PadForMyFirebaseMessagingService.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import UserNotifications

class PadForMyFirebaseMessagingService {
    static let shared = PadForMyFirebaseMessagingService()
    private init() { }
    
    
    var showIdRoom: String? = nil
    var listenerT1: (() -> Void)? = nil
    var listenerT3: (() -> Void)? = nil
    
    
    func onMessageReceived(_ userInfo: [AnyHashable : Any]){
       // responseMessages[200]
        let typeMsg: String? = userInfo["type_message"] == nil ? nil : String(describing: userInfo["type_message"]!)
        
        if let typeMsg = typeMsg{
            
            let idRoom = userInfo["idRoom"] == nil ? nil : String(describing: userInfo["idRoom"]!)
            let idTm = userInfo["idTm"] == nil ? nil : String(describing: userInfo["idTm"]!)
            
//            let titleM = userInfo["title"] == nil ? nil : String(describing: userInfo["title"]!)
//            let bodyM = userInfo["body"] == nil ? nil : String(describing: userInfo["body"]!)
            
            if(idRoom != nil && idTm != nil){
                if(typeMsg == Constants.TelemedicineNotificationType.PAY.rawValue){
                    listenerT1?()
                    listenerT3?()
                }
            }
        }
    }
    
//    func sendYourselfANotification (_ title: String, _ msg: String){
//        let content = UNMutableNotificationContent()
//        content.title = title
//        content.subtitle = msg
//        
//        content.sound = UNNotificationSound.default
//        
//        // show this notification five seconds from now
//        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 5, repeats: false)
//        
//        // choose a random identifier
//        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)
//        
//        // add our notification request
//        UNUserNotificationCenter.current().add(request)
//    }
    
    func isShowNotiFromIOSApp(_ userInfo: [AnyHashable : Any]) -> Bool {
        let idRoom = userInfo["idRoom"] == nil ? nil : String(describing: userInfo["idRoom"]!)
        
        if(listenerT3 == nil || (listenerT3 != nil && idRoom != nil && showIdRoom != nil && idRoom != showIdRoom)){
            return true
        }else{
            return false
        }
    }
}
