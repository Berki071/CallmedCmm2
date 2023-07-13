//
//  AppState.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation

struct NotificationData{
    var typeMsg: String?
    var idRoom: String?
    var idTm: String?
}

class AppState: ObservableObject{
    static let shared = AppState()
     @Published var pageToNavigationTo : NotificationData?
    
    func setDateNoti(_ userInfo: [AnyHashable : Any]){
        let typeMsg: String? = userInfo["type_message"] == nil ? nil : String(describing: userInfo["type_message"]!)
        let idRoom = userInfo["idRoom"] == nil ? nil : String(describing: userInfo["idRoom"]!)
        let idTm = userInfo["idTm"] == nil ? nil : String(describing: userInfo["idTm"]!)
        
        self.pageToNavigationTo = NotificationData(typeMsg: typeMsg, idRoom: idRoom, idTm: idTm)
    }
}
