////
////  RealmHelper.swift
////  CallMed_ios
////
////  Created by Михаил Хари on 02.07.2023.
////
//
//import Foundation
////import RealmSwift
//
//class RealmHelper {
//    //Used to expose generic
//    static func DetachedCopy(itemRealm : MessageRoomItem) -> MessageRoomItem?{
//        let _id = itemRealm._id
//        let id_room : String? = itemRealm.id_room
//        let id_message : Int? = itemRealm.id_message
//        let name_tm : String? = itemRealm.name_tm
//        let id_tm : Int? = itemRealm.id_tm
//        let data : String? = itemRealm.data
//        let text : String? = itemRealm.text
//        let otpravitel : String? = itemRealm.otpravitel
//        let view_kl : String? = itemRealm.view_kl
//        let view_sotr : String? = itemRealm.view_sotr
//        let type : String? = itemRealm.type
//        
//        var tmpItem = MessageRoomItem()
//        tmpItem._id = _id
//        tmpItem.id_room = id_room
//        tmpItem.id_message = id_message
//        tmpItem.name_tm = name_tm
//        tmpItem.id_tm = id_tm
//        tmpItem.data = data
//        tmpItem.text = text
//        tmpItem.otpravitel = otpravitel
//        tmpItem.view_kl = view_kl
//        tmpItem.view_sotr = view_sotr
//        tmpItem.type = type
//        
//        let jsonEncoder = JSONEncoder()
//        let jsonData2 = try? jsonEncoder.encode(tmpItem)
//        //let json = String(data: jsonData2, encoding: String.Encoding.utf8)
//        if let jsonData2 = jsonData2 {
//            return try? JSONDecoder().decode(MessageRoomItem.self, from: jsonData2)
//        }else {
//            return nil
//        }
//    }
//}
