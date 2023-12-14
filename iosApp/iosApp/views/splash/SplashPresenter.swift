//
//  SplashPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import Foundation
import SwiftUI
import FirebaseMessaging
import shared

class SplashPresenter : ObservableObject{
    @Published var nextPage : String = "" //переход на след страницу
    @Published var isShowAlertStandart: StandartAlertData? = nil
    
    
    let sdk: NetworkManagerIos
    var sharePreferenses : SharedPreferenses
    let sdkKMM: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    
    
    init(){
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
        
        //fatalError("Crash was triggered")
        //netConnection.startMonitoring()
        
        testOpenNextActivity ()
        //verifyUser("9130696928", "123")
    }
    
    func testOpenNextActivity(){
        var login = sharePreferenses.currentLogin ?? nil
        var pass = sharePreferenses.currentPassword ?? nil
        
        //let status = netConnection.connType
        if !NetMonitor.isConnectedToNetwork(){
            self.showStandartAlert("Внимание!", "Отсутствует соединение с интернетом")
        }else{
            if(login==nil || pass==nil || login=="" || pass==""){
                self.showNextpage("Login")
                return
            }else{
                self.verifyUser(login,pass)
            }
        }
    }
    
    func verifyUser (_ login:String?,_ pass:String? ){
//        sdk.doLoginApiCall(login!, pass!, responseF: {(r: [UserResponse]) -> Void in
//            DispatchQueue.main.async {
//                if(r[0].username != nil ){
//                    self.sharePreferenses.currentUserInfo = r[0]
//                    self.updateHeaderInfo()
//
//                }else{
//                    self.sharePreferenses.currentPassword = nil
//                    self.sharePreferenses.currentUserInfo = nil
//                    self.showNextpage("Login")
//                }
//            }
//        }, errorM: {(e: String) -> Void in
//            DispatchQueue.main.async {
//                //self.sharePreferenses.currentPassword = nil
//                //self.sharePreferenses.currentUserInfo = nil
//
//                LoggingTree.INSTANCE.e("SplashPresenter/verifyUser \(e)")
//                self.showNextpage("Login")
//            }
//        })
        
        
        
        sdkKMM.doLoginApiCall(username: login!, password:pass!, completionHandler: { response, error in
            if let res : UserResponse = response {
                DispatchQueue.main.async {
                    if(res.response[0].username != nil ){
                        
                        self.sharePreferenses.currentUserInfo = res.response[0]
                        self.updateHeaderInfo()
                        
                    }else{
                        self.sharePreferenses.currentPassword = nil
                        self.sharePreferenses.currentUserInfo = nil
                        self.showNextpage("Login")
                    }
                }
            } else {
                
                if let t=error{
                    LoggingTree.INSTANCE.e("SplashPresenter/verifyUser", t)
                }
                
                self.showNextpage("Login")
            }
        })
    }
    
    
    func updateHeaderInfo(){
        sdk.getCenterApiCall(idCenter: String(Int(truncating: self.sharePreferenses.currentUserInfo!.idCenter!)), responseF: {(l: [CenterItem]) -> Void in
            DispatchQueue.main.async {
                self.sharePreferenses.currentCenterInfo = l[0]
                self.getCurrentDocInfo()
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("SplashPresenter/updateHeaderInfo \(e)")
                self.showNextpage("Login")
            }
        })
    }
    
    func getCurrentDocInfo(){
        let dbN = sharePreferenses.currentCenterInfo!.db_name!
        let accessesT = sharePreferenses.currentUserInfo!.apiKey!
        let idD = String(Int(truncating: sharePreferenses.currentUserInfo!.idUser!))
        
        sdk.getDoctorById(dbName: dbN, accessToken: accessesT, docId: idD, responseF: {(l: DoctorItem) -> Void in
            DispatchQueue.main.async {
                self.sharePreferenses.currentDocInfo = l
                self.firebaseId()
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("SplashPresenter/getCurrentDocInfo \(e)")
                self.showNextpage("Login")
            }
        })
    }
    
    func firebaseId (){
        if(self.sharePreferenses.showNofication == true){
            Messaging.messaging().token { token, error in
                if let error = error {
                    LoggingTree.INSTANCE.e("SplashPresenter/firebaseId", error)
                    self.showNextpage("Main")
                } else if let token = token {
                    //print(">>>> \(token) \n")
                    self.sendFcmToken(token: token)
                }
            }
        }else{
            self.showNextpage("Main")
        }
    }
    
    func sendFcmToken(token : String){
     
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.apiKey!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idUser=String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        //let idBranch=String(Int.init(self.sharePreferenses.currentUserInfo!.idBranch!))        

        sdk.updateFcmToken(fbToken: token, idDoc: idUser, dbName: h_dbName, accessToken: apiKey, responseF: {(l: SimpleResponseString) -> Void in
            DispatchQueue.main.async {
                self.showNextpage("Main")
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("SplashPresenter/sendFcmToken \(e)")
                self.showNextpage("Main")
            }
        })
        
    }
    
    func showNextpage(_ page : String){
        //netConnection.stopMonitoring()
        self.nextPage = page
        
    }
    
    func showStandartAlert(_ title: String, _ text: String){
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false ,  someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in})
    }
    
}
