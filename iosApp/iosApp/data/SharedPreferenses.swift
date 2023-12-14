//
//  SharedPreferenses.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import Foundation
import shared

class SharedPreferenses{
    let CURRENT_LOGIN_KEY="CURRENT_LOGIN_KEY"
    let CURRENT_PASSWORD_KEY="CURRENT_PASSWORD_KEY"
    let USERS_LOGIN_KEY="USERS_LOGIN_KEY"
    let PREF_KEY_CENTER_INFO="PREF_KEY_CENTER_INFO"
    let PREF_KEY_CURRENT_DOC_INFO="PREF_KEY_CURRENT_DOC_INFO"
    let PREF_KEY_DOCTOR_SELCT_BRANCH = "PREF_KEY_DOCTOR_SELCT_BRANCH"
    let PREF_KEY_SHOW_NOTIFICATIONS = "PREF_KEY_SHOW_NOTIFICATIONS"
    let PREF_KEY_DATE_SHOW_RASP_NOTY = "PREF_KEY_DATE_SHOW_RASP_NOTY"
    let ANALISE_FILE_KEY = "ANALISE_FILE_KEY"
    let ELECTRONIC_CONCLUSIONS_KEY = "ELECTRONIC_CONCLUSIONS_KEY"
    
    let PREF_KEY_LOGIN_ERROR = "PREF_KEY_LOGIN_ERROR"
    
    
    let defaults : UserDefaults
    
    init(){
        defaults = UserDefaults.standard
        
        defaults.register(
            defaults: [
                PREF_KEY_SHOW_NOTIFICATIONS: true
            ]
        )
    }
    
    var loginError : String?{
        get{
            return defaults.string(forKey: PREF_KEY_LOGIN_ERROR)
        }
        
        set ( nVal){
            defaults.set(nVal, forKey: PREF_KEY_LOGIN_ERROR)
        }
    }
    
    var currentLogin : String?{
        get{
            return defaults.string(forKey: CURRENT_LOGIN_KEY)
        }
        
        set ( nVal){
            defaults.set(nVal, forKey: CURRENT_LOGIN_KEY)
        }
    }
    
    var currentPassword : String?{
        get{
            return defaults.string(forKey: CURRENT_PASSWORD_KEY)
        }
        
        set ( nVal){
            defaults.set(nVal, forKey: CURRENT_PASSWORD_KEY)
        }
    }
    
    var currentUserInfo : UserResponse.UserItem?{
        get{
            let res = defaults.string(forKey: USERS_LOGIN_KEY)
            
            if res == nil {
                return nil
            }else{
                do{
                    let tmp = try MUtils.companion.stringToUserResponse(str: res!) as UserResponse.UserItem
                    return tmp
                }catch{
                    print("Неожиданная ошибка: \(error).")
                    return nil
                }
            }
            
        }
        
        set ( nVal){
            
            if nVal != nil{
                do{
                    let str : String? = try MUtils.companion.userResponseToString(cl: nVal!)
                    defaults.set(str, forKey: USERS_LOGIN_KEY)
                }catch{
                    print("Неожиданная ошибка: \(error).")
                }
            }else{
                defaults.set(nil, forKey: USERS_LOGIN_KEY)
            }
        }
    }
    
    var currentCenterInfo : CenterItem?{
        get{
            if let data = defaults.data(forKey: PREF_KEY_CENTER_INFO) {
                do {
                    // Create JSON Decoder
                    let decoder = JSONDecoder()
                    // Decode Note
                    let note = try decoder.decode(CenterItem.self, from: data)
                    return note
                } catch {
                    return nil
                }
            }else{
                return nil
            }
        }
        
        set ( nVal){
            if nVal != nil{
                do {
                    // Create JSON Encoder
                    let encoder = JSONEncoder()
                    // Encode Note
                    let data = try encoder.encode(nVal)
                    // Write/Set Data
                    defaults.set(data, forKey: PREF_KEY_CENTER_INFO)
                } catch {
                    print("Unable to Encode Note (\(error))")
                }
            }else{
                defaults.set(nil, forKey: PREF_KEY_CENTER_INFO)
            }
        }
    }
    
    var currentDocInfo: DoctorItem?{
        get{
            if let data = defaults.data(forKey: PREF_KEY_CURRENT_DOC_INFO) {
                do {
                    // Create JSON Decoder
                    let decoder = JSONDecoder()
                    // Decode Note
                    let note = try decoder.decode(DoctorItem.self, from: data)
                    return note
                } catch {
                    return nil
                }
            }else{
                return nil
            }
        }
        
        set ( nVal){
            if nVal != nil{
                do {
                    // Create JSON Encoder
                    let encoder = JSONEncoder()
                    // Encode Note
                    let data = try encoder.encode(nVal)
                    // Write/Set Data
                    defaults.set(data, forKey: PREF_KEY_CURRENT_DOC_INFO)
                } catch {
                    print("Unable to Encode Note (\(error))")
                }
            }else{
                defaults.set(nil, forKey: PREF_KEY_CURRENT_DOC_INFO)
            }
        }
    }
    
    var doctorSelectBranch : String?{
        get{
            return defaults.string(forKey: PREF_KEY_DOCTOR_SELCT_BRANCH)
        }
        
        set ( nVal){
            defaults.set(nVal, forKey: PREF_KEY_DOCTOR_SELCT_BRANCH)
        }
    }
    
    //значение по умолчанию задано выше
    var showNofication : Bool{
        get{
            return defaults.bool(forKey: PREF_KEY_SHOW_NOTIFICATIONS)
        }
        
        set ( nVal){
            defaults.set(nVal, forKey: PREF_KEY_SHOW_NOTIFICATIONS)
        }
    }
    
    var dataShowRaspNoty: String?{
        get{
            return defaults.string(forKey: PREF_KEY_DATE_SHOW_RASP_NOTY)
        }
        set (boo) {
            defaults.set(boo, forKey: PREF_KEY_DATE_SHOW_RASP_NOTY)
        }
    }
    
    func addAnaliseFileName(item: FileNameInfo) {
        if item != nil{
            do{
                var arrTmmp = getAnaliseFileName()
                
                let jsonEncoder = JSONEncoder()
                let jsonData = try jsonEncoder.encode(item)
                var jsonString = String(data: jsonData, encoding: .utf8) ?? "parse json error1"
                
                arrTmmp.append(jsonString)
               
                defaults.set(arrTmmp, forKey: ANALISE_FILE_KEY)
            }catch{
                print("Неожиданная ошибка: \(error).")
            }
        }
    }
    func getAnaliseFileName() -> [String]{
        let savedArray = defaults.object(forKey: ANALISE_FILE_KEY) as? [String] ?? [String]()
        return savedArray
    }
    func getAnaliseFileName(pref: String, nameU: String, date: String, spec: String) -> FileNameInfo? {
        let arrTmp = getAnaliseFileName()
           
        var tkTmp: FileNameInfo? = nil
        
        for jsonStr in arrTmp {
            let data = jsonStr.data(using: .utf8)!
         
            let tk = try? JSONDecoder().decode(FileNameInfo.self, from: data)
            
            if(tk != nil && pref==tk!.pref && nameU==tk!.nameUser && date==tk?.date && spec==tk?.nameSpec){
                tkTmp = tk
            }
        }
        return tkTmp
    }
    
    
    func addElectronicConclusions(item: FileNameInfo) {
        if item != nil{
            do{
                var arrTmmp = getElectronicConclusions()
                
                let jsonEncoder = JSONEncoder()
                let jsonData = try jsonEncoder.encode(item)
                var jsonString = String(data: jsonData, encoding: .utf8) ?? "parse json error1"
                
                arrTmmp.append(jsonString)
               
                defaults.set(arrTmmp, forKey: ELECTRONIC_CONCLUSIONS_KEY)
            }catch{
                print("Неожиданная ошибка: \(error).")
            }
        }
    }
    func getElectronicConclusions() -> [String]{
        let savedArray = defaults.object(forKey: ELECTRONIC_CONCLUSIONS_KEY) as? [String] ?? [String]()
        return savedArray
    }
    func getElectronicConclusions(pref: String, nameU: String, date: String, spec: String) -> FileNameInfo? {
        let arrTmp = getElectronicConclusions()
        
        var tkTmp: FileNameInfo? = nil
           
        for jsonStr in arrTmp {
            let data = jsonStr.data(using: .utf8)!
         
            let tk = try? JSONDecoder().decode(FileNameInfo.self, from: data)
            
            if(pref == Constants.ConclusionAn){
                if(tk != nil && pref==tk!.pref && nameU==tk!.nameUser && date==tk?.date && spec==tk?.nameSpec){
                    tkTmp = tk
                }
            }else{
                if(tk != nil && (tk!.pref==Constants.ConclusionZacPDF || tk!.pref==Constants.ConclusionZacHtml) && nameU==tk!.nameUser && date==tk?.date && spec==tk?.nameSpec){
                    tkTmp = tk
                }
            }
            
          
        }
        return tkTmp
    }
}
