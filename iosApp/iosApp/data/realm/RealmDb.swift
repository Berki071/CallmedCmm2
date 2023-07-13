//
//  RealmDb.swift
//  CallMed_ios
//
//  Created by Mihail on 23.06.2023.
//

import Foundation
//import RealmSwift

//class RealmDb {
//    static let shared = RealmDb()
//    private init() { }
//    
//    
//   // let config = RealmConfiguration.create(schema = setOf(MessageRoomItem::class))
//    //let realm = try! Realm()
//    
//    var latchWrite = false
//    
//    func deleteMessage(_ item: MessageRoomItem){
//        let realm = try! Realm()
//        
//        try! realm.write {
//            let tmpItem: MessageRoomItem? = realm.objects(MessageRoomItem.self).where{$0.id_message == item.id_message}.first
//            if let tmpItmem = tmpItem {
//                realm.delete(tmpItmem)
//            }
//        }
//        
//        
////        try! realm.write { query<MessageRoomItem>("idMessage == $0", item.idMessage).first().find()
////            ?.also { delete(it) }
////        }
//        
//    }
//    
//    func isPossibleDeleteCheckMsgUserAfterSelect(_ item: MessageRoomItem) -> Bool {
//        let realm = try! Realm()
//        
//        let listItems: Results<MessageRoomItem> = realm.objects(MessageRoomItem.self).where{$0.id_room == item.id_room!}
//        if(listItems.count == 0){
//            return true
//        }
//
//        let listItems2: Results<MessageRoomItem> =  listItems.where{$0.id_message > item.id_message}
//        
//        if(listItems2.count == 0){
//            return true
//        }
//
//        for i in listItems2 {
//            if(i.otpravitel == "kl"){
//                return false
//            }
//        }
//
//        return true
//    }
//    
//    func addListMessages(_ list: [MessageRoomItem]) -> [MessageRoomItem]{
//        let realm = try! Realm()
//        
//        latchWrite = true
//        
//        var listNewM: [MessageRoomItem] = []
//        
//        for i in list {
//            let item: MessageRoomItem? = realm.objects(MessageRoomItem.self).where{$0.id_message == i.id_message!}.first
//            if(item == nil){
//                listNewM.append(i)
//            }
//        }
//        
//        try! realm.write {
//            for i in listNewM {
//                //let tmp = i.idAsString()
//                realm.add(i)
//            }
//            
//            latchWrite = false
//        }
//        
//        return listNewM
//    }
//    
//    func getAllMessageByIdRoom(_ idRoom: String) -> [MessageRoomItem]{
//        let realm = try! Realm()
//        
//        // RealmResults<MessageRoomItem>
//        let incompleteItems: Results<MessageRoomItem> = realm.objects(MessageRoomItem.self).where{$0.id_room == idRoom}
//
//        if(incompleteItems.count==0){
//            return []
//        }else{
//            var newArr:[MessageRoomItem] = []
//            //let oldArray = incompleteItems.toArray()
//            
//            for i in incompleteItems{
//                let tmp = RealmHelper.DetachedCopy(itemRealm: i)
//                
//                if let tmp = tmp {
//                    newArr.append(tmp)
//                }
//                
//            }
//            return newArr
//        }
//    }
//    
//    func getMaxIdMessageByIdRoom(_ idRoom: String) -> Int {
//        let realm = try! Realm()
//        
//        let maxIdMessage: Int? = realm.objects(MessageRoomItem.self).where{$0.id_room == idRoom}.max(ofProperty: "id_message") as Int?
//        //let maxIdMessage: Int? = realm.query<MessageRoomItem>("idRoom == $0", idRoom).max("idMessage", Int::class).find()
//        return maxIdMessage ?? 0
//    }
//
//}
//
//
//
//extension Results {
//    func toArray() -> [Element] {
//      return compactMap {
//        $0
//      }
//    }
// }
