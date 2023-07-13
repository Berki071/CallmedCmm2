//
//  SettingsPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 17.01.2023.
//

import Foundation
import FirebaseMessaging

class SettingsPresenter: ObservableObject{
    let sdk: NetworkManagerIos
    var sharePreferenses : SharedPreferenses
    //let netConnection = NetMonitor.shared
    
    @Published var isShowNotifacation : Bool
    @Published var showLoading: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
    
    init(){
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
        //netConnection.startMonitoring()
        
        isShowNotifacation = sharePreferenses.showNofication
    }
    
    func showNotificationValueChanget(_ value: Bool){
        self.sharePreferenses.showNofication = value
        
        self.showLoading = true
        if(value == false){
            self.sendFcmToken(token: "null")
        }else{
            self.firebaseId()
        }
    }
    
    func firebaseId (){
        if(self.sharePreferenses.showNofication == true){
            Messaging.messaging().token { token, error in
                if let error = error {
                    LoggingTree.INSTANCE.e("SettingsPresenter/firebaseId", error)
                    self.showLoading = false
                    
                    self.showStandartAlert("Ошибка","Не удалось получить токен для уведомлений")
                } else if let token = token {
                    //print(">>>> \(token) \n")
                    self.sendFcmToken(token: token)
                }
            }
        }else{
            self.showLoading = false
        }
    }
    
    func sendFcmToken(token : String){
     
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idUser=String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        //let idBranch=String(Int.init(self.sharePreferenses.currentUserInfo!.idBranch!))

        sdk.updateFcmToken(fbToken: token, idDoc: idUser, dbName: h_dbName, accessToken: apiKey, responseF: {(l: SimpleResponseString) -> Void in
            self.showLoading = false
        }, errorM: {(e: String) -> Void in
            LoggingTree.INSTANCE.e("SettingsPresenter/sendFcmToken \(e)")
            self.showLoading = false
            self.showStandartAlert("Ошибка","Не удалось обновить")
        })
        
    }
    
    func showStandartAlert(_ title: String, _ text: String){
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false ,  someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in})
    }
}
