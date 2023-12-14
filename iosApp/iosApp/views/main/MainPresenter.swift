//
//  MainPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 07.12.2022.
//

import Foundation
import SwiftUI
import UserNotifications

class MainPresenter: ObservableObject{
    @Published var nextPage : String = "" //переход на след страницу
    
    @Published var titleTop : String = ""
    
    @Published var showMenu = false
//    {
//        didSet{
//            print("")
//        }
//    }
    @Published var selectMenuPage = 0
    @Published var selectMenuAlert = 0  //1 алер выхода
    
    var isPermissionNotyDone = false
    
    let sdk: NetworkManagerIos
    var sharePreferenses : SharedPreferenses
    //let netConnection = NetMonitor.shared

    
    init(startPage: Int){
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
        //netConnection.startMonitoring()
        
        
        //selectMenuPage = checkOpenMenuPage(startPage)
   
        if(AppState.shared.pageToNavigationTo == nil ){
            selectMenuPage = checkOpenMenuPage(startPage)
        }else{
            selectMenuPage = 3
        }
    
    
        
        
        if(sharePreferenses.currentCenterInfo != nil && sharePreferenses.currentCenterInfo!.title != nil){
            titleTop=sharePreferenses.currentCenterInfo!.title!
        }
        
//        self.requestPermissionNoty()
//        self.startCheckShowNotyRasp()
    }
    
    func checkOpenMenuPage(_ startPage: Int) -> Int {
        if(startPage > -1){
            return startPage
        }else{
            if(sharePreferenses.currentUserInfo!.vrach == "true"){
                return 1
            }else{
                return 0
            }
        }
    }
    
//    func requestPermissionNoty(){
//        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { success, error in
//            if success {
//                self.isPermissionNotyDone = true
//            }else{
//                self.isPermissionNotyDone = false
//            }
//
//            if let error = error {
//                LoggingTree.INSTANCE.e("MainPresenter/requestPermissionNoty \(error)")
//            }
//        }
//    }
    
    func logOut(){
        LoggingTree.INSTANCE.v("Выход из учетной записи")
        
        self.sharePreferenses.currentPassword = "";
        self.sharePreferenses.currentLogin = nil;
        self.sharePreferenses.currentUserInfo = nil;
        self.sharePreferenses.doctorSelectBranch = nil;
        
        nextPage = "Login"
    }
    
//    func startCheckShowNotyRasp(){
//
//        DispatchQueue.main.asyncAfter(deadline: .now() + 10, execute: {
//            let currentDay = MDate.currentDateClearString()
//            let dayNotyRaspInPref = self.sharePreferenses.dataShowRaspNoty
////
////            if((dayNotyRaspInPref != nil && currentDay == dayNotyRaspInPref) || !self.sharePreferenses.showNofication){
////                return
////            }
////
//            let currentTime = MDate.curentTimeDate()
//            let startPeriod = MDate.stringToDate("20:30", MDate.DATE_FORMAT_HHmm)
//            let endPeriod = MDate.stringToDate("21:00", MDate.DATE_FORMAT_HHmm)
//
//            //if(currentTime >= startPeriod && currentTime <= endPeriod){
//                self.requestServerDataForNoty()
//            //}
//
//            self.startCheckShowNotyRasp()
//        })
//    }

//    func requestServerDataForNoty(){
//
//        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
//        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
//        let idUser=String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
//
//        let dateTomottow =  MDate.getNextDateFromCurrentInDaysString(1)
//
//        sdk.findRaspTomorrow(date: dateTomottow, idDoc: idUser, dbName: h_dbName, accessToken: apiKey, responseF: {(resp: [ScheduleTomorrowItem])  -> Void in
//            DispatchQueue.main.async {
//                if(self.isPermissionNotyDone == true && (resp.count>1 || resp[0].data != nil)){
//                    self.sharePreferenses.dataShowRaspNoty =  MDate.currentDateClearString()
//
//                    var msg = ""
//
//                    for i in resp{
//                        msg += "\(i.naim_filial!): с \(i.start!) по \(i.end!) (\(i.kab!))\n"
//                    }
//
//                    msg = msg.trimmingCharacters(in: .whitespacesAndNewlines)
//
//                    self.sendYourselfANotification("Расписание на завтра!", msg)
//                }
//            }
//        }, errorM: {(e: String) -> Void in
//            DispatchQueue.main.async {
//                LoggingTree.INSTANCE.e("MainPresenter/requestServerDataForNoty \(e)")
//
//            }
//        })
//    }
    
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
}
