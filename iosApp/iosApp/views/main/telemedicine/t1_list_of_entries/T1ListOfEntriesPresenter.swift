//
//  T1ListOfEntriesPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 08.06.2023.
//

import Foundation
import shared


class T1ListOfEntriesPresenter: ObservableObject {
    @Published var isShowBigTextAlert: ShowBigTextAlertData? = nil
    
    @Published var isShowRoomView: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem? = nil
    @Published var showEmptyScreen: Bool = false
    @Published var showDialogLoading: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
   
    
    let sdk: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var sharePreferenses : SharedPreferenses = SharedPreferenses()
    
    @Published var whatDataShow: String = Constants.WhatDataShow.ACTIVE()
    @Published var recordsTelemedicineListNew :[DataForListOfEntriesRecy] = []
    var recordstTelemedicineListOldFull :[AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem] = []
    @Published var recordstTelemedicineListOldFilter :[AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem] = []
    
    @Published var isShowRoomView2: NotificationData? = nil
    
    var listener: T1ListOfEntriesItemListener?
    
    var textSearch: String = "" {
        didSet{
             print(">>>>>>>> >>>>>> \(textSearch)")
             self.filterList()
         }
    }
    @Published var isShowSearchView = false
    
    init(){
        
        if(!self.isCheckLoginAndPassword()){
            return
        }
        
        listener = T1ListOfEntriesItemListener(
            showInfo: {(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) -> Void in
                if(item.aboutfull != nil){
                    self.isShowBigTextAlert = ShowBigTextAlertData(title: "Подробная информация", text: item.aboutfull!, clickClose: {()->Void in
                        self.isShowBigTextAlert = nil
                    })
                }
            },
            goToRoom: {(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) -> Void in
                //LoggingTree.INSTANCE.d("переход в комнату idRoom tmId \(item.tmId)")
                self.isShowRoomView = item
                
            },
            closeTm: {(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) -> Void in
                self.closeRecordTelemedicine(item)
            },
            sendNotyReminder: {(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem, msg: String, type: String) -> Void in
                //todo
//                self.sendMsgNotificationTimeReminder(item, msg)
//                self.updateTelemedicineReminderDocAboutRecord(item,type)
            }
        )
        
        self.getData((whatDataShow == Constants.WhatDataShow.ACTIVE()) ? "new" : "old")
        self.areThereAnyNewTelemedicineMsg()
        
        PadForMyFirebaseMessagingService.shared.listenerT1 = { () -> Void in
            self.getData((self.whatDataShow == Constants.WhatDataShow.ACTIVE()) ? "new" : "old")
        }
        
        if(AppState.shared.pageToNavigationTo != nil){
            isShowRoomView2 = AppState.shared.pageToNavigationTo
            AppState.shared.pageToNavigationTo = nil
            return
        }

    }
    
    func isCheckLoginAndPassword() -> Bool{
        if(sharePreferenses.currentUserInfo == nil){
            return false
        }
        
        let idUser = sharePreferenses.currentUserInfo!.id_doc_center
        let pass = sharePreferenses.currentPassword
        if (idUser == 0 || pass == "") {
            return false
        }else{
            return true
        }
    }
    
    func getData(_ type: String){
        if(self.sharePreferenses.currentUserInfo == nil || self.sharePreferenses.currentDocInfo == nil){
            LoggingTree.INSTANCE.e("T1SelectSpecialtyPresenter/getDataRecords sharePreferenses.currentUserInfo == nil || sharePreferenses.currentDocInfo == nil")
            return
        }
        
        showLoading(true)
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idUser = String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        sdk.getAllRecordsTelemedicine(type: type, h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idUser,
                                  completionHandler: { response, error in
            if let res : AllRecordsTelemedicineResponse = response {
                DispatchQueue.main.async {
                    if(res.response.count > 1 || res.response[0].idRoom != nil){
                        
                        self.showEmptyScreen(false)
                        
                        if(self.whatDataShow == Constants.WhatDataShow.ACTIVE()){
                            self.recordstTelemedicineListOldFull = []
                            self.recordstTelemedicineListOldFilter = []
                            
                            self.checkActiveItemOnComplete(res.response)
                            let tmpList0 = self.removeWaitItemWhichNoPay(res.response)
                            let tmpList = self.processingDataRecordsForRecy(tmpList0)
                            self.recordsTelemedicineListNew = tmpList
                            
                            if(self.recordsTelemedicineListNew.count == 0){
                                self.showEmptyScreen(true)
                            }
                            
                        }else{
                            self.recordsTelemedicineListNew = []
                            self.recordstTelemedicineListOldFull = res.response
                            self.filterList()
                        }
                    }else{
                        self.recordsTelemedicineListNew = []
                        self.recordstTelemedicineListOldFull = []
                        self.recordstTelemedicineListOldFilter = []
                        self.showEmptyScreen(true)
                    }
                    
                    self.showLoading(false)
                }
            } else {
                if let t = error {
                    LoggingTree.INSTANCE.e("T1ListOfEntriesPresenter/getData \(t)")
                }
                
                self.recordstTelemedicineListOldFull = []
                self.recordstTelemedicineListOldFilter = []
                self.recordsTelemedicineListNew = []
                
                self.showEmptyScreen(true)
                self.showLoading(false)
               
                //todo swipe
            }
        })
    }
    func checkActiveItemOnComplete(_ list: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem]){
        for i in list {
            if(i.status! == Constants.TelemedicineStatusRecord.active()){
                
                let currentTimeServerLong = MDate.stringToDate(i.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
                let dateStartLong = MDate.stringToDate(i.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
                let dateEndLong = MDate.datePlasTimeInterval(dateStartLong, Int.init(i.tmTimeForTm!))
                
                if(dateEndLong <= currentTimeServerLong){
                    DispatchQueue.main.async {
                        self.closeRecordTelemedicine(i)
                    }
                }
                
//                let currentTimeDate = MDate.getCurrentDate()
//                let dateStartDate = MDate.stringToDate(i.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//                let dateEndDate = MDate.datePlasTimeInterval(dateStartDate,  Int.init(truncating: i.tmTimeForTm!))
//
//                if(currentTimeDate > dateEndDate){
//                    DispatchQueue.main.async {
//                        self.closeRecordTelemedicine(i)
//                    }
//                }
            }
        }
    }
    func removeWaitItemWhichNoPay(_ response: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem]) -> [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem]{
        // удалить из списка не оплаченный wait
        var newList: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem] = []
        for i in response{
            if(i.statusPay == nil){
                let tt3 = Int.init(i.idRoom!)
                LoggingTree.INSTANCE.e("T1SelectSpecialtyPresenter/removeWaitItemWhichNoPay i.statusPay == nil idRoom:\(tt3)")
                continue
            }
            
            if(i.status! != Constants.TelemedicineStatusRecord.wait() || i.statusPay! == "true"){
                newList.append(i)
            }
        }
        return newList
    }
    func processingDataRecordsForRecy(_ response: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem]) -> [DataForListOfEntriesRecy]{
        var mainList: [DataForListOfEntriesRecy] = []
        
        var masActive: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem] = []
        var masWait: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem] = []
        
        let waitStr: String = Constants.TelemedicineStatusRecord.wait()
        let activeStr: String = Constants.TelemedicineStatusRecord.active()
        
        for i in response {
            switch(i.status!){
            case waitStr: masWait.append(i)
            case activeStr: masActive.append(i)
            default: break
            }
        }
        
        if(masActive.count > 0){
            mainList.append(DataForListOfEntriesRecy(id: 0, title: "Активные", items: masActive))
        }
        if(masWait.count > 0){
            mainList.append(DataForListOfEntriesRecy(id: 1, title: "Ожидают начала", items: masWait))
        }
        
        if(mainList.count > 0){
            mainList[0].isShowDop = true
        }
        
        return mainList
    }
    
    func filterList(){
        if(self.recordstTelemedicineListOldFull.count == 0){
            self.recordstTelemedicineListOldFilter = []
            return
        }
        
        if(textSearch.isEmpty){
            self.recordstTelemedicineListOldFilter = self.recordstTelemedicineListOldFull
            return
        }
        
        var tmpList : [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem] = []
        
        self.recordstTelemedicineListOldFull.forEach{ i in
            if(i.fullNameKl!.lowercased().contains(textSearch.lowercased())){
                tmpList.append(i)
            }
        }
    
        self.recordstTelemedicineListOldFilter = tmpList
    }
    
    
    func closeRecordTelemedicine(_ item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) {
        self.showLoading(true)
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idUser=String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        let tmId = String.init(Int.init(truncating: item.tmId!))
        let idRoom =  String.init(Int.init(truncating: item.idRoom!))
        
        sdk.closeRecordTelemedicine(idRoom: idRoom, idTm: tmId, h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idUser,
                                  completionHandler: { response, error in
            if let res : SimpleResponseBoolean2 = response {
                LoggingTree.INSTANCE.d("Закрыт по истечению времени tmId \(item.tmId)")
                DispatchQueue.main.async {
                    self.getData("new")
                }
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T1SelectSpecialtyPresenter/closeRecordTelemedicine \(t)")
                }
                DispatchQueue.main.async {
                    self.getData("new")
                }
            }
        })
        
    }
    
    func areThereAnyNewTelemedicineMsg(){
        if(self.sharePreferenses.currentUserInfo == nil || self.sharePreferenses.currentUserInfo!.token == nil ||  self.sharePreferenses.currentCenterInfo == nil ||
           self.sharePreferenses.currentCenterInfo!.db_name == nil){
            return
        }
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.token!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idUser=String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))
        
        sdk.areThereAnyNewTelemedicineMsg(h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idUser,
                                  completionHandler: { response, error in
            if let res : HasPacChatsResponse = response {
                
                if(res.response.count > 1 || res.response[0].idRoom != nil){
                    self.processingHasNewMsg(res.response)
                }else{
                    self.processingHasNewMsg([])
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.areThereAnyNewTelemedicineMsg()
                }
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("T1SelectSpecialtyPresenter/areThereAnyNewTelemedicineMsg", t)
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.areThereAnyNewTelemedicineMsg()
                }
            }
        })
        
    }
    func processingHasNewMsg(_ list: [HasPacChatsResponse.HasPacChatsItem]){
        if(recordsTelemedicineListNew.count == 0 ){
            return
        }
        
        for i in recordsTelemedicineListNew {
            if(i.items.count == 0 ){
                return
            }
            
            for j in i.items {
                let time: String = MDate.getCurrentDate(MDate.DATE_FORMAT_HHmmss)
                
                var isEditLatch = false
                
                for k in list {
                    
                    if(k.idRoom == j.idRoom){
  
                        isEditLatch = true
                        
                        if(!j.isShowNewMsgIco){
                            j.isShowNewMsgIco = true
                        }
                    }
                }
                
                if(!isEditLatch && j.isShowNewMsgIco){
                    j.isShowNewMsgIco = false
                }
            }
        }
    }
    
    
    
    func showEmptyScreen(_ isShow : Bool){
        showEmptyScreen = isShow
    }
    
    func showLoading(_ isShow : Bool){
        if(isShow){
            self.showDialogLoading = true
        }else{
            self.showDialogLoading = false
        }
    }
}
