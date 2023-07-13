//
//  SharedPreferenses.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import Foundation

class SharedPreferenses{
    let CURRENT_LOGIN_KEY="CURRENT_LOGIN_KEY"
    let CURRENT_PASSWORD_KEY="CURRENT_PASSWORD_KEY"
    let USERS_LOGIN_KEY="USERS_LOGIN_KEY"
    let PREF_KEY_CENTER_INFO="PREF_KEY_CENTER_INFO"
    let PREF_KEY_CURRENT_DOC_INFO="PREF_KEY_CURRENT_DOC_INFO"
    let PREF_KEY_DOCTOR_SELCT_BRANCH = "PREF_KEY_DOCTOR_SELCT_BRANCH"
    let PREF_KEY_SHOW_NOTIFICATIONS = "PREF_KEY_SHOW_NOTIFICATIONS"
    let PREF_KEY_DATE_SHOW_RASP_NOTY = "PREF_KEY_DATE_SHOW_RASP_NOTY"
    
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
    
    var currentUserInfo : UserResponse?{
        get{
            if let data = defaults.data(forKey: USERS_LOGIN_KEY) {
                do {
                    // Create JSON Decoder
                    let decoder = JSONDecoder()
                    // Decode Note
                    let note = try decoder.decode(UserResponse.self, from: data)
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
                    defaults.set(data, forKey: USERS_LOGIN_KEY)
                } catch {
                    print("Unable to Encode Note (\(error))")
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
    
}
