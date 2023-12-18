//
//  LoginPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 07.12.2022.
//

import Foundation
import FirebaseMessaging
import UIKit
import shared

class LoginPresenter: ObservableObject{
    @Published var nextPage : String = "" //переход на след страницу
    @Published var isShowAlertStandart: StandartAlertData? = nil
    @Published var showLoading: Bool = false
    
    let sdk: NetworkManagerIos
    var sharePreferenses : SharedPreferenses
    let sdkKMM: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    
    @Published var username: String = ""
    @Published var password: String = ""
    @Published var togleState: Bool = true
    
    init(){
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
       //netConnection.startMonitoring()
        
        //let status = netConnection.connType
        if !NetMonitor.isConnectedToNetwork(){
            self.showStandartAlert("Внимание!", "Отсутствует соединение с интернетом")
        }
   
        self.updateUsernameHint()
    }
    
    func updateUsernameHint(){
        let login = sharePreferenses.currentLogin
        let pass = sharePreferenses.currentPassword
        
        if(login != nil && !login!.isEmpty){
            self.username = login!
        }
        
        if(pass != nil && !pass!.isEmpty){
            self.password = pass!
        }
    }
    
    func onLoginClick(){
        
        if(self.username.isEmpty || self.password.isEmpty){
            self.showStandartAlert("Внимание!", "Все поля дожны быть заполнены")
            return
        }
        
        let tmp = self.username.count
        
        if(self.username.count != 10){
            self.showStandartAlert("Внимание!", "Пожалуйста введите корректные данные")
            return
        }
        
        
        if !NetMonitor.isConnectedToNetwork(){
            self.showStandartAlert("Внимание!", "Отсутствует соединение с интернетом")
        }else{
            verifyUser(self.username, self.password)
        }
    }
    
    func verifyUser (_ login:String?,_ pass:String? ){
        self.showLoading = true
        //
        //        sdk.doLoginApiCall(login!, pass!, responseF: {(r: [UserResponse]) -> Void in
        //            DispatchQueue.main.async {
        //                print(" doLoginApiCall comlete >>>>>> /n/n")
        //                if(r[0].username != nil ){
        //
        //                    //let tmp1 = r[0].vrach
        //                    self.sharePreferenses.currentUserInfo = r[0]
        //                    if(self.togleState){
        //                        self.sharePreferenses.currentLogin = login
        //                        self.sharePreferenses.currentPassword = pass
        //                    }else{
        //                        self.sharePreferenses.currentPassword = nil
        //                    }
        //
        //                    self.getCenterInfo()
        //
        //                }else{
        //                    self.showLoading = false
        //                    self.showStandartAlert("Внимание!", "Неверное имя пользователя или пароль")
        //                }
        //            }
        //        }, errorM: {(e: String) -> Void in
        //            DispatchQueue.main.async {
        //                self.sharePreferenses.currentPassword = nil
        //                self.sharePreferenses.currentUserInfo = nil
        //                print(" doLoginApiCall errrrrror >>>>>> " + e + "/n/n")
        //                self.sharePreferenses.loginError = "LoginPresenter/verifyUser SL \(e)"
        //                LoggingTree.INSTANCE.e("LoginPresenter/verifyUser \(e)")
        //                self.showLoading = false
        //                self.showStandartAlert("", "Что-то пошло не так.")
        //            }
        //        })
        
        sdkKMM.doLoginApiCall(username: login!, password:pass!, completionHandler: { response, error in
            if let res : UserResponse = response {
                DispatchQueue.main.async {
                    print(" doLoginApiCall comlete >>>>>> /n/n")
                    if(res.response[0].username != nil ){
                        
                        //let tmp1 = r[0].vrach
                        self.sharePreferenses.currentUserInfo = res.response[0]
                        if(self.togleState){
                            self.sharePreferenses.currentLogin = login
                            self.sharePreferenses.currentPassword = pass
                        }else{
                            self.sharePreferenses.currentPassword = nil
                        }
                        
                        self.getCenterInfo()
                        
                    }else{
                        self.showLoading = false
                        self.showStandartAlert("Внимание!", "Неверное имя пользователя или пароль")
                    }
                }
            } else {
                
                if let t=error{
                    LoggingTree.INSTANCE.e("LoginPresenter/verifyUser", t)
                    print(" doLoginApiCall errrrrror >>>>>> " + t.localizedDescription + "/n/n")
                    self.sharePreferenses.loginError = "LoginPresenter/verifyUser SL \(t.localizedDescription)"
                }
                
                DispatchQueue.main.async {
                    self.sharePreferenses.currentPassword = nil
                    self.sharePreferenses.currentUserInfo = nil
                    self.showLoading = false
                    self.showStandartAlert("", "Что-то пошло не так.")
                }
            }
        })
    }
    
    func getCenterInfo(){
//        sdk.getCenterApiCall(idCenter: String(Int(truncating: self.sharePreferenses.currentUserInfo!.idCenter!)), responseF: {(l: [CenterItem]) -> Void in
//            DispatchQueue.main.async {
//                self.sharePreferenses.currentCenterInfo = l[0]
//                self.getCurrentDocInfo()
//            }
//        }, errorM: {(e: String) -> Void in
//            DispatchQueue.main.async {
//                LoggingTree.INSTANCE.e("LoginPresenter/updateHeaderInfo \(e)")
//                self.showLoading = false
//                self.showStandartAlert("Внимание!", "Ошибка загрузки информации о центре")
//            }
//        })
        
        let idCent=String(Int.init(truncating: self.sharePreferenses.currentUserInfo!.idCenter!))
        
        sdkKMM.centerApiCall(currentCenterId: idCent, completionHandler: { response, error in
            if let res : CenterResponse = response {
                self.sharePreferenses.currentCenterInfo = res.response[0]
                self.getCurrentDocInfo()
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("LoginPresenter/updateHeaderInfo", t)
                }
                self.showLoading = false
                self.showStandartAlert("Внимание!", "Ошибка загрузки информации о центре")
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
                
                //self.showNextpage("Main")
                self.firebaseId ()
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("LoginPresenter/getCurrentDocInfo \(e)")
                self.showStandartAlert("Внимание!", "Ошибка загрузки информации о сотруднике")
            }
        })
    }
    
    func firebaseId (){
        if(self.sharePreferenses.showNofication == true){
            Messaging.messaging().token { token, error in
                if let error = error {
                    LoggingTree.INSTANCE.e("LoginPresenter/firebaseId", error)
                    self.showLoading = false
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
                self.showLoading = false
                self.showNextpage("Main")
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("LoginPresenter/sendFcmToken \(e)")
                self.showLoading = false
                self.showNextpage("Main")
            }
        })
        
    }
    
    func showNextpage(_ page : String){
        //let ver: String = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
        var systemVersion = UIDevice.current.systemVersion
    
        LoggingTree.INSTANCE.v("Вход в приложение ios v." + systemVersion)
        
        self.nextPage = page
    }
    
    func showStandartAlert(_ title: String, _ text: String){
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false ,  someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in})
    }
}
