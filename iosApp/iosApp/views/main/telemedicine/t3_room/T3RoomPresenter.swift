//
//  T3RoomPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 08.06.2023.
//

import Foundation
import SwiftUI
import shared

class T3RoomPresenter: ObservableObject {
    
    @Published var recordTItem: AllRecordsTelemedicineItem?
    
    @Published var isShowMediaView: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
    @Published var showDialogLoading: Bool = false
    
    @Published var isStartCamera = true //ImagePicker показать камеру или галерею.. костыль. тут так как шит не видит изменений в переменной когда она лежит  во вью
    
    let sdk: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var sharePreferenses = SharedPreferenses()
    let workWithFiles = WorkWithFiles()
    
    // начало чата
    @Published var recyList: [MessageRoomItem] = []  //последний итем скрол
    var lastIdMessage = 0
    @Published var isShowBigImageOrFile: URL? = nil
    
    var isStoppedT3View = true  //для остановки цикличныз запросов
    var proxy: ScrollViewProxy? = nil //для скролла
    
    init(item: AllRecordsTelemedicineItem){
        self.isStoppedT3View = false
    
        if(item != nil){
            let tmIdC = item.tmId == nil ? "null" : String(Int.init(item.tmId!))
            self.getOneRecordInfo(String(Int.init(item.idRoom!)), tmIdC)
        }
    }
    init(idRoom: String, idTm: String){
        self.isStoppedT3View = false
    
        self.getOneRecordInfo(idRoom, idTm)
    }
    
    
    func  setUp(){
        if(self.recordTItem != nil && recyList.count == 0) {
            let idRoom = self.recordTItem!.idRoom!
            self.getAllMessageFromRealm(idRoom: Int.init(idRoom))
            
            DispatchQueue.main.async {
                self.getNewMessagesInLoopFromServer(idRoom: String(Int.init(idRoom)))
            }
            
            
            let tmIdC = self.recordTItem!.tmId == nil ? "null" : String(Int.init(self.recordTItem!.tmId!))
            
            PadForMyFirebaseMessagingService.shared.showIdRoom = String(Int.init(self.recordTItem!.idRoom!))
            PadForMyFirebaseMessagingService.shared.listenerT3 = { () -> Void in
                self.getOneRecordInfo(String(Int.init(self.recordTItem!.idRoom!)), tmIdC)
            }
        }
    }
    
    
    
    func getAllMessageFromRealm (idRoom: Int){
        let list = RealmDb.shared.getAllMessageByIdRoom(idRoom: String(idRoom))
        if(!list.isEmpty){
            var newList = self.processingAddTariffMessages(list)
            newList = self.processingAddDateInMessages(newList)
            //self.recyList = newList
            self.addToRecyListAndClearAndAddLastItemForScroll(newList)
        }else{
            //self.recyList = []
            self.addToRecyListAndClearAndAddLastItemForScroll([])
        }
    }
    
    func getNewMessagesInLoopFromServer(idRoom: String) {
        // за счет повторения запроса в цикле должна вызываться только раз и крутится внутри while
        
        if(self.isStoppedT3View == true){
            return
        }
        
        if(self.recordTItem == nil){
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                self.getNewMessagesInLoopFromServer(idRoom: idRoom)
            }
            return
        }
        
        checkLastIdMessage()
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        sdk.getMessagesRoom(idRoom: idRoom, idLastMsg: String(lastIdMessage),
                            h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idDoc, completionHandler: { response, error in
            if let res : MessageRoomResponse = response {
                
                if(self.isStoppedT3View == true){
                    return
                }
                
                let time: String = MDate.getCurrentDate(MDate.DATE_FORMAT_HHmmss)
                //print(">>>>!!!!>>>>>> \(time) T3RoomPresenter getNewMessagesInLoopFromServer")
                
                if (res.response.count > 1 || res.response[0].idMessage != nil) {
                    
                    self.processingOnImageOrFile(res.response)
                    
                    let listNewMFromRealm = self.addMessagesToRealm(res.response, true)
                    
                    if(!listNewMFromRealm.isEmpty){
                        self.processingAndAddListItemsInRecy(listNewMFromRealm)
                        self.checkLastIdMessage()
                    }else{
                        self.checkLastIdMessage()
                    }
                    
                }
                self.showLoading(false)
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    self.getNewMessagesInLoopFromServer(idRoom: idRoom)
                }
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/getNewMessagesInLoopFromServer \(error)")
                }
                self.showLoading(false)
                
                if(self.isStoppedT3View == true){
                    return
                }
        
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    self.getNewMessagesInLoopFromServer(idRoom: idRoom)
                }
            }
        })
    }
    
    func addMessagesToRealm(_ list: [MessageRoomItem], _ isNeedProcessing: Bool = false) -> [MessageRoomItem]{
        // от вставки двух сообщений сразy
        //вызывать принудительно в основном потоке
        
        
        if(RealmDb.shared.latchWrite == false) {
            return RealmDb.shared.addListMessages(list: list)
        }else{
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                let rList = RealmDb.shared.addListMessages(list: list)

                if(isNeedProcessing) {
                    if(rList.count > 0){
                        self.processingAndAddListItemsInRecy(rList)
                    }
                    self.checkLastIdMessage()
                }
            }
            
            return []
        }
    }
    
    func processingAndAddListItemsInRecy(_ list: [MessageRoomItem]){
   
        var lastItem: MessageRoomItem? = nil
        if(recyList.count == 1){
            lastItem = recyList[recyList.count-1]
        }else if(recyList.count > 1){
            lastItem = recyList[recyList.count-2]
        }

        var newList = self.processingAddTariffMessages(list, lastItem)
        newList = self.processingAddDateInMessages(newList, lastItem)
        
        self.addToRecyListAndClearAndAddLastItemForScroll(newList)
        //recyList += newList
        
        do{
            try self.proxy?.scrollTo(18881412322155,anchor: .top)
            //print(">>>>>>!!!>>>>> 1 self.proxy?.scrollTo(18881412322155,anchor: .top)")
        }catch{
            print(">>>>> \(error)")
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            do{
                try self.proxy?.scrollTo(18881412322155,anchor: .top)
            }catch{
                print(">>>>> \(error)")
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            do{
                try self.proxy?.scrollTo(18881412322155,anchor: .top)
            }catch{
                print(">>>>> \(error)")
            }
        }
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 4) {
            do{
                try self.proxy?.scrollTo(18881412322155,anchor: .top)
            }catch{
                print(">>>>> \(error)")
            }
        }
      
    }
    
    func addToRecyListAndClearAndAddLastItemForScroll(_ listT: [MessageRoomItem]){
        if(listT.count > 0){
            if(recyList.count != 0){
                for i in stride(from: recyList.count-1, to: 0, by: -1) {
                    if(recyList[i].type == "scrollItem"){
                        recyList.remove(at: i)
                    }
                }
            }
            
            var lictNew = listT
            var tmpItem = MessageRoomItem()
            tmpItem.type = "scrollItem"
            tmpItem.idMessage = 18881412322155
            lictNew.append(tmpItem)
            
            recyList += lictNew
        }else{
            recyList = []
        }
        
    }
    
    
    func processingOnImageOrFile(_ list: [MessageRoomItem]){
         for i in list {
             if(i.type == Constants.MsgRoomType.IMG() || i.type == Constants.MsgRoomType.FILE()){
                 let ext = (i.type == Constants.MsgRoomType.IMG()) ? "png" : "pdf"
                 
                 let newFileName = self.workWithFiles.getNewNameForNewFile(String(Int.init(recordTItem!.idRoom!)), ext, String(Int.init(i.idMessage!)))

                 if(newFileName == nil){
                     self.showAlet("Ошибка!", "Не удалось создать файл для сохранения")
                 }else{
                     let tmp = workWithFiles.base64ToFile(i.text!, newFileName!, errorListener: {(i:String, j:String) in
                         //self.showAlet(i, j)
                     })

                     if (!tmp){
                         self.showAlet("Ошибка!", "Не удалось скопировать файл для сохранения")
                     }else{
                         i.text = newFileName
                     }
                 }
             }
         }
     }
    

    
    func checkLastIdMessage(){
        if(recordTItem != nil){
            self.lastIdMessage = Int(RealmDb.shared.getMaxIdMessageByIdRoom(idRoom: String(Int.init(self.recordTItem!.idRoom!))))
        }
    }
    
    
    func processingAddTariffMessages(_ list: [MessageRoomItem], _ lastMsg: MessageRoomItem? = nil) -> [MessageRoomItem]{
        var nList: [MessageRoomItem] = []
        
        if(lastMsg == nil) {
            var tmp = MessageRoomItem()
            tmp.data = list[0].data!
            tmp.type = Constants.MsgRoomType.TARIFF()
            tmp.idTm = list[0].idTm
            tmp.nameTm = list[0].nameTm!
            tmp.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
            nList.append(tmp)
            nList.append(list[0])
        }else{
            if(lastMsg!.idTm != list[0].idTm){
                var tmp = MessageRoomItem()
                tmp.data = list[0].data
                tmp.type = Constants.MsgRoomType.TARIFF()
                tmp.idTm = list[0].idTm
                tmp.nameTm = list[0].nameTm!
                tmp.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
                nList.append(tmp)
            }
            nList.append(list[0])
        }
        
        if(list.count > 1){
            for index in 1...list.count-1 {
                if (list[index].nameTm != list[index-1].nameTm) {
                    var tmp = MessageRoomItem()
                    tmp.data = list[index].data
                    tmp.type = Constants.MsgRoomType.TARIFF()
                    tmp.idTm = list[index].idTm
                    tmp.nameTm = list[index].nameTm!
                    tmp.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
                    nList.append(tmp)
                }
                nList.append(list[index])
            }
        }
        
        return nList
    }
    
    func processingAddDateInMessages(_ list: [MessageRoomItem], _ lastMsg: MessageRoomItem? = nil) -> [MessageRoomItem]{
        var nList: [MessageRoomItem] = []
        
        if(lastMsg == nil) {
            var tmp = MessageRoomItem()
            tmp.data = list[0].data
            tmp.type = Constants.MsgRoomType.DATE()
            tmp.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
            nList.append(tmp)
            nList.append(list[0])
        }else{
            if(lastMsg!.data!.prefix(10) != list[0].data!.prefix(10)){
                var tmp = MessageRoomItem()
                tmp.data = list[0].data
                tmp.type = Constants.MsgRoomType.DATE()
                tmp.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
                nList.append(tmp)
            }
            nList.append(list[0])
        }
        
        if(list.count > 1){
            for index in 1...list.count-1 {
                if(list[index].data!.prefix(10) != list[index-1].data!.prefix(10)){
                    var tmp = MessageRoomItem()
                    tmp.data = list[index].data
                    tmp.type = Constants.MsgRoomType.DATE()
                    tmp.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
                    nList.append(tmp)
                }
                nList.append(list[index])
            }
        }
        
        return nList
    }
    
    
    func showBigImage(_ item: MessageRoomItem){
        let pathDir = workWithFiles.getDocumentsDirectory()
        
        if let destinationUrl = pathDir?.appendingPathComponent(item.text!) {
            self.isShowBigImageOrFile = destinationUrl
        }else{
            LoggingTree.INSTANCE.e("T3RoomPresenter/showBigImage error create URL")
            self.showAlet("Ошибка!", "Не удалось открыть")
        }
    }
    
    func clickRemuveItem(_ item: MessageRoomItem){
        let isPossibleDeleteCheckMsgUserAfterSelect = RealmDb.shared.isPossibleDeleteCheckMsgUserAfterSelect(item: item)
        
        if(isPossibleDeleteCheckMsgUserAfterSelect && self.recordTItem != nil) {
            if (item.otpravitel == "sotr" && self.recordTItem!.status != Constants.TelemedicineStatusRecord.complete()) {
                var msg = ""
                if (item.type == Constants.MsgRoomType.TEXT()) {
                    msg = "Удалить собщение \"\(item.text!)\"?"
                } else {
                    msg = "Удалить файл?"
                }
                
                self.isShowAlertStandart = StandartAlertData(titel: "Удаление!", text: msg, isShowCansel: true, someFuncOk: {() -> Void in
                    self.isShowAlertStandart = nil
                    self.deleteMessageFromServer(item)
                }, someFuncCancel: {() -> Void in
                    self.isShowAlertStandart = nil
                } )
            }
        }
    }
    func deleteMessageFromServer(_ item: MessageRoomItem){
        self.showLoading(true) //скроет показ загрузки getAllMessagesInLoop
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        let idMsg = String(Int.init(item.idMessage!))
        
        sdk.deleteMessageFromServer(idMsg: idMsg,
                                    h_Auth: apiKey, h_dbName: h_dbName, h_idKl: idDoc,completionHandler: { response, error in
            if let res : SimpleResponseBoolean2 = response {
                if(item.type == Constants.MsgRoomType.IMG() || item.type == Constants.MsgRoomType.FILE()){
                    self.workWithFiles.deleteFileFromDocumentsByName(item.text!)
                 }

                RealmDb.shared.deleteMessage(item: item)
                
                self.deleteItemFromChatList(item)
                 
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/deleteMessageFromServer", t)
                    self.showLoading(false)
                }
                
                RealmDb.shared.deleteMessage(item: item)
                              
                self.deleteItemFromChatList(item)
                               
            }
        })
    }
    
    func deleteItemFromChatList(_ item: MessageRoomItem){
        let position: Int? = recyList.firstIndex(where: { $0 == item })
        
        if(position == nil){
            return
        }
        
        recyList.remove(at: position!)
        
        var positionDate: Int? = nil
        var positionTariff: Int? = nil
        
        //удаляет ненужные даты и тарифы в начале и середине списка
        for i in stride(from: recyList.count-2, to: 0, by: -1) {
            if(recyList[i].type == Constants.MsgRoomType.DATE()){
                if(positionDate == nil){
                    positionDate = i
                }else{
                    recyList.remove(at: positionDate!)
                    positionDate = i
                }
            }
            
            if(recyList[i].type == Constants.MsgRoomType.TARIFF()){
                if(positionTariff == nil){
                    positionTariff = i
                }else{
                    recyList.remove(at: positionTariff!)
                    positionTariff = i
                }
            }
            
            if(recyList[i].type != Constants.MsgRoomType.DATE() && recyList[i].type != Constants.MsgRoomType.TARIFF()){
                positionDate = nil
                positionTariff = nil
            }
        }
        
        // если positionDate или positionTariff не равны нулю значит в конце списка ненужные дата или тарифф
        if(positionDate != nil){
            recyList.remove(at: positionDate!)
            positionDate = nil
        }
         
        if(positionTariff != nil){
            recyList.remove(at: positionTariff!)
            positionTariff = nil
        }

    }
    //old name updateItemIdMessageById ObjectId
    func updateItemIdMessageByIdChatList(_ idAsStr: String, _ idMessage: KotlinInt, _ date: String, _ text: String? = nil){
        for i in 0...self.recyList.count-2 {
               if(recyList[i].idMessage! == idMessage && recyList[i].idAsString() != idAsStr){
                   let position: Int? = recyList.firstIndex(where: { $0 == recyList[i]})
                   if(position == nil){
                       return
                   }
                   recyList.remove(at: position!)
                   break
               }
           }

           for i in 0...self.recyList.count-2 {
               if(recyList[i].idAsString() == idAsStr){
                   recyList[i].idMessage = idMessage
                   recyList[i].data = date
                   if(text != nil){
                       recyList[i].text=text
                   }
                   break
               }
           }
    }
    
    func sendMessage(_ msg: String){
        let trimMsg = msg.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if(trimMsg.count > 0 && recordTItem != nil) {
            var msgItem = MessageRoomItem()
            msgItem.idRoom = String(Int.init(recordTItem!.idRoom!))
            msgItem.data = MDate.getCurrentDate(MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
            msgItem.type = Constants.MsgRoomType.TEXT()
            msgItem.text = msg
            msgItem.otpravitel = "sotr"
            msgItem.idTm = recordTItem!.tmId!
            msgItem.nameTm = recordTItem!.tmName
            msgItem.viewKl = "true"
            msgItem.viewSotr = "false"
            msgItem.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
            
            self.sendMessageToServer(String(Int.init(recordTItem!.idKl!)), msgItem, String(Int.init(recordTItem!.idFilial!)))
        }
    }
    
    func sendMessageToServer(_ idUser: String, _ item: MessageRoomItem, _ idBranch: String){
//        if(item.type != Constants.MsgRoomType.TEXT()){
//            let valueText: String
//            valueText = workWithFiles.fileToBase64ByName(item.text!)
//            
//            let nameFile = "tmp_2_\(item.text!)"
//            
//            let tmp = workWithFiles.base64ToFile(valueText, nameFile, errorListener: {(i:String, j:String) in
//                //self.showAlet(i, j)
//            })
//
//            if (!tmp){
//                self.showAlet("Ошибка lyyyy!", "Не удалось скопировать файл для сохранения ssss")
//            }else{
//                item.text = nameFile
//            }
//        }
        
        
        self.processingAndAddListItemsInRecy([item])
        let valueText: String
        if(item.type == Constants.MsgRoomType.TEXT())
        {
            valueText = item.text!
        }else{
            valueText = workWithFiles.fileToBase64ByName(item.text!)
        }
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        let idTm = String(Int.init(item.idTm!))
        
        sdk.sendMessageFromRoom(idRoom: item.idRoom!, idTm: idTm, idUser: idUser, typeMsg: item.type!, text: valueText,
                                h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idDoc, h_idFilial: idBranch,
                                  completionHandler: { response, error in
            if let res : SendMessageFromRoomResponse = response {
                if(res.response[0].idMessage != nil){
                    if(res.response[0].idMessage! == -1){
                        self.getOneRecordInfo(item.idRoom!, String(Int.init(item.idTm!)))
                        self.deleteItemFromChatList(item)
                        return
                    }
                    
                    if(self.lastIdMessage < Int.init(res.response[0].idMessage!)){
                        self.lastIdMessage = Int.init(res.response[0].idMessage!)
                    }
                    
                    //если файл то надо пересохранить с idMessage
                    let newUriFileStr: String?
                    if(item.type == Constants.MsgRoomType.TEXT()) {
                        newUriFileStr = nil
                    } else {
                        newUriFileStr = self.workWithFiles.renameImageWithId(item, String(Int.init(res.response[0].idMessage!)))
                    }
                    
                    item.idMessage = res.response[0].idMessage
                    item.data = res.response[0].dataMessage
                    if(newUriFileStr != nil){
                        item.text = newUriFileStr
                    }
                    
                    
                    
                    self.updateItemIdMessageByIdChatList(item.idAsString(), res.response[0].idMessage!, res.response[0].dataMessage!, newUriFileStr)
                    self.addMessagesToRealm([item])
                    
                    DispatchQueue.main.async {
                        self.sendMsgNotification()
                    }
                    
                }else{
                    self.deleteItemFromChatList(item)
                }

            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/sendMessageToServer", t)
                }
                
                self.deleteItemFromChatList(item)
                               
            }
        })
    }
    
    
    func sendStatusNotification(_ status: String, _ item: AllRecordsTelemedicineItem, _ type: String) {
        if(self.recordTItem != nil){
            let notiObj = self.creteJSONObjectNotificationForStatus(status, item, type)
            let servK = self.recordTItem!.serverKey!
            self.sendMsgNotification2(notiObj, servK, Constants.SENDER_ID_FCM_PATIENT, false, String.init(Int.init(item.tmId!)), status)
        }
    }
    func creteJSONObjectNotificationForStatus(_ status: String, _ item: AllRecordsTelemedicineItem, _ type: String) -> String {
        let noti: [String: Any] = [
            "title": "Медицинский помощник.Пациент",
            "body": status,
            "sound": "Enabled"
        ]
        
        let dopData: [String: Any] = [
            "type_message": String.init(type),
            "idRoom": String.init(Int.init(item.idRoom!)),
            "idTm": String.init(Int.init(item.tmId!)),
            "id_kl": String.init(Int.init(item.idKl!)),
            "id_filial": String.init(Int.init(item.idFilial!))
        ]
        
        let obj: [String: Any] = [
            "to": item.fcmKl!,
            "notification": noti,
            "data": dopData
        ]
        
        return self.workWithFiles.jsonToString(obj)
    }
    func sendMsgNotification() {
        if(recordTItem != nil){
            let idRoom = String(Int.init(recordTItem!.idRoom!))
            let idTm = String(Int.init(recordTItem!.tmId!))
            
            let notiObj = self.creteJSONObjectNotification(recordTItem!.fcmKl!, idRoom, idTm)
            let servK = recordTItem!.serverKey
            self.sendMsgNotification2(notiObj, servK!, Constants.SENDER_ID_FCM_PATIENT, true, String.init(Int.init(recordTItem!.tmId!)))
        }
    }
    func creteJSONObjectNotification(_ fcmKey: String, _ idRoom: String, _ idTm: String) -> String {
        let noti: [String: Any] = [
            "title": "Медицинский помощник.Сотрудник",
            "body": "Новое сообщение",
            "sound": "Enabled"
        ]
        
        let dopData: [String: Any] = [
            "type_message": Constants.TelemedicineNotificationType.MESSAGE.rawValue,
            "idRoom": idRoom,
            "idTm": idTm
        ]
        
        let obj: [String: Any] = [
            "to": fcmKey,
            "notification": noti,
            "data": dopData
        ]
        
        return self.workWithFiles.jsonToString(obj)
    }
    func sendMsgNotification2(_ json: String, _ serverKey: String, _ senderId: String, _ isMsg: Bool, _ tmId: String, _ status: String = "") {
        sdk.sendMsgFCM(json: json, serverKey: serverKey, senderId: senderId,
                                  completionHandler: { response, error in
            if let res : FCMResponse = response {
                if(!isMsg){
                    LoggingTree.INSTANCE.d("Отправлено уведомление клиенту об изменении статусу приема tmId \(tmId) \(status)")
                }
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/sendMsgNotification2", t)
                }
              
            }
        })
    }

    
    func getOneRecordInfo(_ idRoom: String, _ idTm: String, _ isRecordItemNull: Bool = true){
        self.showLoading(true)
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        sdk.getSelectRecordsTelemedicine(idRoom: idRoom, idTm: idTm,
                                         h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idDoc,
                                  completionHandler: { response, error in
            if let res : AllRecordsTelemedicineResponse = response {
                if(res.response[0].idRoom != nil){
                    //let tmp1 = list![0].dataStart
                   
                    self.recordTItem = res.response[0]
                    self.checkActiveItemOnComplete(res.response[0])
                    self.setUp()
                    self.showLoading(false)
                    
                }else{
                    self.recordTItem = nil
                    LoggingTree.INSTANCE.e("T3RoomPresenter/getOneRecordInfo response[0]==null")
                    self.showLoading(false)
                    self.showAlet("Ошибка","Что-то пошло не так.")
                }

            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/getOneRecordInfo", t)
                }
            
                self.showLoading(false)
                self.showAlet("Ошибка","Что-то пошло не так.")
            }
        })
        
    }
    func checkActiveItemOnComplete(_ response: AllRecordsTelemedicineItem){
        if (response.status! == Constants.TelemedicineStatusRecord.active() && response.dataServer != nil && response.dataStart != nil) {
            let currentTimeLong: Date = MDate.stringToDate(response.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            let dateStartLong: Date = MDate.stringToDate(response.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            let dateEndLong: Date = MDate.datePlasTimeInterval(dateStartLong, Int.init(response.tmTimeForTm!))

            if (currentTimeLong >= dateEndLong){
                self.closeRecordTelemedicine(response)
            }
        }
    }
    //func closeRecordTelemedicine(_ idRoom: String, _ idTm: String){
    func closeRecordTelemedicine(_ item: AllRecordsTelemedicineItem, _ isSendNoty: Bool = false, _ isDoc: Bool = false){
        self.showLoading(true)
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        let idR = String(Int.init(item.idRoom!))
        let idTm =  String(Int.init(item.tmId!))
        
        sdk.closeRecordTelemedicine(idRoom: idR, idTm: idTm,
                                    h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idDoc,
                                  completionHandler: { response, error in
            if let res : SimpleResponseBoolean2 = response {
                if( !isDoc ){
                    LoggingTree.INSTANCE.d("Закрыт по истечению времени tmId \(idTm)")
                }else{
                    LoggingTree.INSTANCE.d("Закрыт доктором \(idTm)")
                }
                
                self.recordTItem?.status = Constants.TelemedicineStatusRecord.complete()
                
                DispatchQueue.main.async {
                    self.getOneRecordInfo(idR, idTm, false)
                }
                
                if(isSendNoty){
                    DispatchQueue.main.async {
                        self.sendStatusNotification(Constants.TelemedicineChangeStatusAppointment.END.rawValue, item, Constants.TelemedicineNotificationType.END_APPOINTMENT.rawValue)
                    }
                }
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/closeRecordTelemedicine", t)
                }
                self.recordTItem?.status = Constants.TelemedicineStatusRecord.complete()
            }
        })
        
    }
    
    
    func createImageMsg(_ imgFileName: String){
        var msgItem = MessageRoomItem()
        msgItem.idRoom = String(Int.init(recordTItem!.idRoom!))
        msgItem.data = MDate.getCurrentDate(MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
        msgItem.type = Constants.MsgRoomType.IMG()
        msgItem.text = imgFileName
        msgItem.otpravitel = "sotr"
        msgItem.idTm = recordTItem!.tmId!
        msgItem.nameTm = recordTItem!.tmName
        msgItem.viewKl = "true"
        msgItem.viewSotr = "false"
        msgItem.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
        
        self.sendMessageToServer(String(Int.init(recordTItem!.idKl!)), msgItem, String(Int.init(recordTItem!.idFilial!)))
        
    }
    func createFileMsg(_ fileName: String){
        var msgItem = MessageRoomItem()
        msgItem.idRoom = String(Int.init(recordTItem!.idRoom!))
        msgItem.data = MDate.getCurrentDate(MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
        msgItem.type = Constants.MsgRoomType.FILE()
        msgItem.text = fileName
        msgItem.otpravitel = "sotr"
        msgItem.idTm = recordTItem!.tmId!
        msgItem.nameTm = recordTItem!.tmName
        msgItem.viewKl = "true"
        msgItem.viewSotr = "false"
        msgItem.idMessage = KotlinInt(integerLiteral: Int.random(in: -1000000..<0))
        
        self.sendMessageToServer(String(Int.init(recordTItem!.idKl!)), msgItem, String(Int.init(recordTItem!.idFilial!)))
        
    }
    
    func saveImageByUrl(_ url: URL){
        let idCenter: String = String(self.sharePreferenses.currentCenterInfo!.id_center ?? -1)
        let idRoom: String = String(Int.init(recordTItem!.idRoom!))
        let curentTimeMils = String(MDate.getCurrentDate1970())
        
        let fileName = "\(Constants.PREFIX_NAME_FILE)_\(idCenter)_\(idRoom)_\(curentTimeMils).png"
        
        workWithFiles.saveImageByUrl(url: url, saveName: fileName, errorListener: {(i:String, j:String) in
            self.showAlet(i, j)
        }, successfully: {(fName: String) -> Void in
            self.createImageMsg(fName)
        })
    }
    func saveFileByUrl(_ url: URL){
        let idCenter: String = String(self.sharePreferenses.currentCenterInfo!.id_center ?? -1)
        let idRoom: String = String(Int.init(recordTItem!.idRoom!))
        let curentTimeMils = String(MDate.getCurrentDate1970())
        
        let fileName = "\(Constants.PREFIX_NAME_FILE)_\(idCenter)_\(idRoom)_\(curentTimeMils).pdf"
        
        workWithFiles.saveFileByUrl(url: url, saveName: fileName, errorListener: {(i:String, j:String) in
            DispatchQueue.main.async {
                self.showAlet(i, j)
            }
        }, successfully: {(fName: String) -> Void in
            DispatchQueue.main.async {
                self.createFileMsg(fName)
            }
        })
    }
    
    
    func clickStartReception(){
        self.isShowAlertStandart = StandartAlertData(titel: "Внимание!", text: "Вы действительно хотите начать прием?", isShowCansel: true, someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
            
            if(self.recordTItem != nil ){
                if(self.recordTItem!.status == Constants.TelemedicineStatusRecord.wait() && self.recordTItem!.statusPay != "true"){
                    self.showAlet("Внимание!", "Прием не оплачен")
                    return
                }
                
                self.toActiveRecordTelemedicine(self.recordTItem!)
            }
            
        }, someFuncCancel: {() -> Void in
            self.isShowAlertStandart = nil
        }, textBtnOk: "да", textBtnCancel: "нет" )
    }
    func clickCompleteReception(){
        self.isShowAlertStandart = StandartAlertData(titel: "Внимание!", text: "Вы действительно хотите завершить прием?", isShowCansel: true, someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
            
            self.closeRecordTelemedicine(self.recordTItem!, true, true)
            
        }, someFuncCancel: {() -> Void in
            self.isShowAlertStandart = nil
        }, textBtnOk: "да", textBtnCancel: "нет" )
    }
    
    func toActiveRecordTelemedicine(_ item : AllRecordsTelemedicineItem){
        self.showLoading(true)
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        let idRoom: String = String(Int.init(item.idRoom!))
        let idTm: String = String(Int.init(item.tmId!))
        
        sdk.toActiveRecordTelemedicine(idRoom: idRoom, idTm: idTm,
                                       h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idDoc,
                                  completionHandler: { response, error in
            if let res : SimpleResponseBoolean2 = response {
                LoggingTree.INSTANCE.d("в Активные tmId \(item.tmId)")
                DispatchQueue.main.async {
                    if(self.recordTItem != nil ){
                        self.recordTItem?.status = Constants.TelemedicineStatusRecord.active()
                    }
                    
                    self.getOneRecordInfo(idRoom, idTm, false)
                    self.sendStatusNotification(Constants.TelemedicineChangeStatusAppointment.START.rawValue , item , Constants.TelemedicineNotificationType.START_APPOINTMENT.rawValue )
                }

            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T3RoomPresenter/toActiveRecordTelemedicine \(t)")
                }
                self.showLoading(false)
                self.showAlet("Ошибка", "Что-то пошло не так.")
            }
        })
                    
    }
    
    
    func showLoading(_ isShow : Bool){
        if(self.showDialogLoading == isShow){
            return
        }
        
        if(isShow){
            self.showDialogLoading = true
        }else{
            self.showDialogLoading = false
        }
    }
    func showAlet(_ title: String, _ text: String) {
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false, someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in } )
    }
    
}
