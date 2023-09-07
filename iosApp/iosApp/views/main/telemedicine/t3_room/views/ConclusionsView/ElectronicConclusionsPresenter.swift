//
//  ElectronicConclusionsPresenter.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

class ElectronicConclusionsPresenter: ObservableObject{
    @Published var showDialogLoading: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
    @Published var showDialogErrorScreen: Bool = false
    @Published var showEmptyScreen: Bool = false
    
    let sdk: NetworkManagerCompatibleKMM
    let sharePreferenses : SharedPreferenses
    let dm = DownloadManager()
    
    var analiseResultList : [AnaliseResponse] = []
    var analiseResultList2 : [ResultZakl2Item] = []
    @Published var listForRecy: [DataClassForElectronicRecyIos] = []
    
    var recordTItem: AllRecordsTelemedicineItem
    
    init(recordTItem: AllRecordsTelemedicineItem){
        self.recordTItem = recordTItem
        
        sdk = NetworkManagerCompatibleKMM()
        sharePreferenses = SharedPreferenses()
        
        self.getData()
    }
    
    
    func getData(){
        self.listForRecy = []
        self.analiseResultList2 = []
        if(self.sharePreferenses.currentUserInfo == nil || self.sharePreferenses.currentCenterInfo == nil){
            LoggingTree.INSTANCE.e("AnaliseResultPresenter/updateAnaliseList sharePreferenses.currentUserInfo == nil || sharePreferenses.centerInfo == nil")
            return
        }
        
        self.showLoading(true)
        
        let apiKey = String.init(self.recordTItem.token_kl ?? "")
        let idUser = String(Int.init(truncating: self.recordTItem.idKl ?? 0))
        let idBranch = String(Int.init(truncating: self.recordTItem.idFilial ?? 0))
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name ?? ""
        
        
        sdk.getResultZakl(h_Auth: apiKey, h_dbName: h_dbName, h_idKl: idUser, h_idFilial:  idBranch,
                                  completionHandler: { response, error in
            if let res : ResultZaklResponse = response {
                DispatchQueue.main.async {
                    self.showErrorScreen(false)
                    
                    self.analiseResultList = res.response
                    self.getData2()
                }
            } else {
                DispatchQueue.main.async {
                    if let t=error{
                        LoggingTree.INSTANCE.e("ElectronicConclusionsPresenter/getData", t)
                    }
                    
                    self.showLoading(false)
                    self.showErrorScreen(true)
                }
            }
        })
        
    }
    func getData2(){
        
        let apiKey = String.init(self.recordTItem.token_kl ?? "")
        let idUser = String(Int.init(truncating: self.recordTItem.idKl ?? 0))
        let idBranch = String(Int.init(truncating: self.recordTItem.idFilial ?? 0))
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name ?? ""
        
        
        sdk.getResultZakl2(h_Auth: apiKey, h_dbName: h_dbName, h_idKl: idUser, h_idFilial:  idBranch,
                                  completionHandler: { response, error in
            if let res : ResultZakl2Response = response {
                self.showErrorScreen(false)
                DispatchQueue.main.async {
                    self.showEmptyScreen = false
                    
                    
                    self.analiseResultList2 = res.response
                    self.processingGetData()
                }
            } else {
                DispatchQueue.main.async {
                    if let t=error{
                        LoggingTree.INSTANCE.e("ElectronicConclusionsPresenter/getData", t)
                    }
                    
                    self.showLoading(false)
                    self.showErrorScreen(true)
                }
            }
        })
    }
    
    
    func processingGetData(){
        var combinedList: [DataClassForElectronicRecyIos] = []
        
        //)
        if(self.analiseResultList != nil && self.analiseResultList.count > 1 ||  self.analiseResultList[0].linkToPDF != nil){
            self.analiseResultList.forEach{ i in
                if(MDate.stringToDate(i.datePer!, MDate.DATE_FORMAT_ddMMyyyy) != nil){
                     combinedList.append(DataClassForElectronicRecyIos(item: i))
                }
            }
        }
        
        if(self.analiseResultList2 != nil && (self.analiseResultList2.count > 1 ||  self.analiseResultList2[0].nameSpec != nil)){
            self.analiseResultList2.forEach{ i in
                if(MDate.stringToDate(i.dataPriema!, MDate.DATE_FORMAT_ddMMyyyy) != nil){
                    combinedList.append(DataClassForElectronicRecyIos(item: i))
                }
            }
        }
        
        if(combinedList.count == 0){
            combinedList = []
        }else{
            testDownloadIn(combinedList)
            
            combinedList.sort (by: {(s1 : DataClassForElectronicRecyIos, s2 : DataClassForElectronicRecyIos) -> Bool in
                let n1 = s1.item.datePer
                let n2 = s2.item.datePer

                if(n1 == nil || n2 == nil || n1!.isEmpty || n2!.isEmpty || n1 == n2 ){
                    return false
                }


                let d1 = MDate.stringToDate(n1!, MDate.DATE_FORMAT_ddMMyyyy)
                let d2 = MDate.stringToDate(n2!, MDate.DATE_FORMAT_ddMMyyyy)
                
//                if(d1 == nil || d2 == nil){
//                    return true
//                }

                return d2 < d1
            }
            )
        }
        
        DispatchQueue.main.async {
            self.listForRecy = combinedList
            
            
            if(self.listForRecy.count == 0){
                self.showEmptyScreen = true
            }else{
                self.showEmptyScreen = false
            }
        }
        self.showLoading(false)
    }
    
    func testDownloadIn(_ list: [DataClassForElectronicRecyIos]){
        self.dm.checkFilesForDataClassForElectronicRecy(list: list, nameUserIn: String.init(recordTItem.fullNameKl ?? ""))
    }

    
    
    
    func showLoading(_ isShow : Bool){
        DispatchQueue.main.async {
            if(isShow){
                self.showDialogLoading = true
            }else{
                self.showDialogLoading = false
            }
        }
    }
    
    func showStandartAlert(_ title: String, _ text: String){
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false ,  someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in})
    }
    
    func showErrorScreen(_ isShow : Bool){
        DispatchQueue.main.async {
            if isShow {
                self.showDialogErrorScreen = true
            }else{
                self.showDialogErrorScreen = false
            }
        }
    }
    
}
