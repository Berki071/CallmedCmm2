//
//  LoggingTree.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import Foundation

class LoggingTree{
    static let INSTANCE = LoggingTree()
    private init() {
        versionCode = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as! String? ?? "x"
        
        //netConnection = NetMonitor.shared
        //netConnection.startMonitoring()
    }
    
    //Timber.e - for error (priority 6)
    //Timber.i - for onCreate Activity (priority 4)
    //Timber.v - verbose information about actions (priority 2)
    //Timber.d - ALERT_TYPE (priority 3)
    
    let VERBOSE_INFORMATION="VERBOSE_INFORMATION_ios"
    let CREATE_ACTIVITY="CREATE_ACTIVITY_ios"
    let ERROR="ERROR_ios"
    let ALERT_TYPE="ALERT_TYPE_ios"
    let ERROR_PAYMENT="ERROR_PAYMENT_ios"
    let PAYMENT="PAYMENT_ios"
    
    let sdk: NetworkManagerIos = NetworkManagerIos()
    let sharePreferenses : SharedPreferenses = SharedPreferenses()
    
    var versionCode: String
    
//    init(){
//        sdk=NetworkManager()
//        //sharePreferenses = SharedPreferenses()
//    }
    
    func i (_ msg : String){
        self.sendLogToServer(CREATE_ACTIVITY, msg)
    }
    func v (_ msg : String){
        self.sendLogToServer(VERBOSE_INFORMATION, msg)
    }
    func d (_ msg : String){
        self.sendLogToServer(ALERT_TYPE, msg)
    }
    func e (_ msg : String, _ error : Error){
        self.sendLogToServer(ERROR, msg+"; \(error)")
    }
    func e (_ msg : String){
        self.sendLogToServer(ERROR, msg)
    }
    func ePay (_ msg : String, _ error : Error){
        self.sendLogToServer(ERROR_PAYMENT, msg+"; \(error)")
    }
    func ePay (_ msg : String){
        self.sendLogToServer(ERROR_PAYMENT, msg)
    }
    func p (_ msg : String){
        self.sendLogToServer(PAYMENT, msg)
    }

    
    func sendLogToServer(_ type : String, _ message : String){
        var idUSer="0"
        var idCenter="0"
        var idBranch="-1"
        
        let tmp = sharePreferenses.loginError
        if(tmp != nil && message != tmp){
            sendLogToServer("ERROR_ios", tmp!)
        }
       
  
        
        if(sharePreferenses.currentUserInfo != nil){
            if let t = sharePreferenses.currentUserInfo!.idUser {
                idUSer=String( Int.init(t))
            }
  
            if let t = sharePreferenses.currentUserInfo!.idCenter {
                idCenter = String(Int.init(t))
            }
        }
        
        let versionCode = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as! String? ?? "x"
        
        sdk.sendLogToServer(idUser: idUSer, idCenter: idCenter, idBranch: idBranch, type: type, log: message, versionCode: versionCode,
                            responseF: {(i:Bool) -> Void in
            if(i){
                self.sharePreferenses.loginError=nil
            }
        }, errorM: {(String) -> Void in
            
        })
    }
    
    func reVizov(){
        DispatchQueue.main.asyncAfter(deadline: .now() + 10.0, execute: {
            let tmp = self.sharePreferenses.loginError
            if(tmp != nil){
                self.sendLogToServer("ERROR_ios", tmp!)
            }
           })
    }
}
