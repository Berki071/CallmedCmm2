//
//  AnaliseResultPresenter.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

class AnaliseResultPresenter: ObservableObject {
    @Published var showDialogLoading: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
    @Published var showDialogErrorScreen: Bool = false
    @Published var showEmptyScreen: Bool = false
    
    @Published var analiseResultList : [AnaliseResponseIos] = []
    
    let sdk: NetworkManagerCompatibleKMM
    let sharePreferenses : SharedPreferenses
    let dm = DownloadManager()
    
    var recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
    
    init(recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem){
        self.recordTItem = recordTItem
        
        sdk=NetworkManagerCompatibleKMM()
        sharePreferenses = SharedPreferenses()
        
        self.updateAnaliseList()
    }
    
    func updateAnaliseList(){
        self.analiseResultList = []
        
        if(self.sharePreferenses.currentUserInfo == nil || self.sharePreferenses.currentCenterInfo == nil){
            LoggingTree.INSTANCE.e("AnaliseResultPresenter/updateAnaliseList sharePreferenses.currentUserInfo == nil || sharePreferenses.currentCenterInfo == nil")
            return
        }
        
        self.showLoading(true)
        
        let apiKey = String.init(self.recordTItem.token_kl ?? "")
        let idUser = String(Int.init(truncating: self.recordTItem.idKl ?? 0))
        let idBranch = String(Int.init(truncating: self.recordTItem.idFilial ?? 0))
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name ?? ""
        
        sdk.getResultAnalysis(h_Auth: apiKey, h_dbName: h_dbName, h_idKl: idUser, h_idFilial:  idBranch,
                                  completionHandler: { response, error in
            if let res : AnaliseResultResponse = response {
                self.showLoading(false)
                self.showErrorScreen(false)
                
                if(res.response.count > 1 || res.response[0].linkToPDF != nil){
                    DispatchQueue.main.async {
                        self.showEmptyScreen = false
                    }

                    res.response.sort (by: {(s1 : AnaliseResponse, s2 : AnaliseResponse) -> Bool in
                        let n1 = s1.getDateForZakl()
                        let n2 = s2.getDateForZakl()
                        
                        if(n1 == nil || n2 == nil || n1!.isEmpty || n2!.isEmpty || n1 == n2 ){
                            return false
                        }
                        
                        let dateFormatter = DateFormatter()
                        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
                        
                        let d1 = dateFormatter.date(from: n1!)!
                        let d2 = dateFormatter.date(from: n2!)!
                        
                        return d2<d1
                    }
                    )

                    let nameClient = String.init(self.recordTItem.fullNameKl ?? "")
                    self.dm.checkFileForAnaliseResult(list: res.response, nameUserIn: nameClient)

                    var tmpList :[AnaliseResponseIos] = []
                    res.response.forEach{ i in
                        tmpList.append(AnaliseResponseIos(item: i))
                    }
                    
                    DispatchQueue.main.async {
                        self.analiseResultList = tmpList
                    }

                }else{
                    self.analiseResultList = []
                    self.showEmptyScreen = true
                }
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("AnaliseResultPresenter/updateAnaliseList", t)
                }
                
                self.showLoading(false)
                self.showErrorScreen(true)
            }
        })
        
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
        if isShow {
            showDialogErrorScreen = true
            //serviceListForRecy = []
        }else{
            showDialogErrorScreen = false
        }
    }
    

}
