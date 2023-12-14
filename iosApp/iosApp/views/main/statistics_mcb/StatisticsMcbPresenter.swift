//
//  StatisticsMcbPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 24.01.2023.
//

import Foundation
import UIKit

class StatisticsMcbPresenter: ObservableObject {
    let sdk: NetworkManagerIos
    var sharePreferenses : SharedPreferenses
    //let netConnection = NetMonitor.shared
    
    @Published var showLoading: Bool = false
    @Published var showEmpty: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
    
    @Published var dateFrom: String = ""
    @Published var dateTo: String = ""
    
    
    @Published var listMcbMod : [MkbItem] = []
    @Published var sortByMcb: Bool = false
    @Published var sortByCount: Bool = false
    
    init(){
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
        //netConnection.startMonitoring()
        
        initDates()
    }
    
    private func initDates(){
        let date = Date()
        
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        
        let strD = dateFormatter.string(from: date)
        
        dateFrom = strD
        dateTo = strD
    }
    
    func loadStatMkb(){
     
        self.showLoading = true
        
        let apiKey = String.init(self.sharePreferenses.currentUserInfo!.apiKey!)
        let h_dbName = self.sharePreferenses.currentCenterInfo!.db_name!
        let idUser=String(Int.init(self.sharePreferenses.currentDocInfo!.id_doctor!))

        sdk.loadStatMkb(dateFrom: self.dateFrom, dateTo: self.dateTo, idDoc: idUser, dbName: h_dbName, accessToken: apiKey, responseF: {(i: [String]?) -> Void in
            self.processingStatMcb(i)
            self.showLoading = false
        }, errorM: {(e: String) -> Void in
            LoggingTree.INSTANCE.e("StatisticsMcbPresenter/loadStatMkb \(e)")
            self.showLoading = false
            self.showStandartAlert("Ошибка","Не удалось обновить")
        })
        
    }
    
    func processingStatMcb( _ list: [String]?){
        if(list == nil){
            self.showEmpty = true
            listMcbMod = []
            return
        }
        
        if(list == nil || list!.count == 0 || (list!.count == 1 && list![0] == nil)){
            self.showEmpty = true
            listMcbMod = []
        }else{
            self.showEmpty = false
            
            var newList: [MkbItem] = []
            
            for i in list! {
                var tLatch = false
                
                for j in newList.indices {
                    if(newList[j].kodMkb == i){
                        tLatch = true
                        newList[j].count = newList[j].count + 1
                        break
                    }
                }
                
                if(tLatch == false){
                    newList.append(MkbItem(kodMkb:i, count: 1))
                }
            }
            
            self.listMcbMod = newList
            
            self.sortingByMcb()
        }
    }
    
    func showStandartAlert(_ title: String, _ text: String){
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false ,  someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in})
    }
    
    func sortingByMcb(){
        self.sortByMcb = true
        self.sortByCount = false
        listMcbMod.sort{ (s1 : MkbItem, s2 : MkbItem) -> Bool in
            s1.kodMkb.compare(s2.kodMkb) == .orderedAscending}
    }
    
    func sortingByCount(){
        self.sortByMcb = false
        self.sortByCount = true
        listMcbMod.sort{ (s1 : MkbItem, s2 : MkbItem) -> Bool in
            s1.count > (s2.count)}
    }
    
    func onSendClick(){
        let str = self.createStrFromArr()
        
        let textShare = [ str ]
        
        guard let source = UIApplication.shared.windows.last?.rootViewController else {
            return
        }
        let activityViewController = UIActivityViewController(activityItems: textShare , applicationActivities: nil)
        activityViewController.popoverPresentationController?.sourceView = source.view
        source.present(activityViewController, animated: true, completion: nil)
    }
    
    private func createStrFromArr() -> String{
        var tmp = "КОД МКБ10" + ": " + "КОЛИЧЕСТВО" + "\n"
        
        listMcbMod.forEach{i in
            tmp += i.kodMkb + ": " + String.init(i.count) + "\n"
        }
        
        return tmp
    }
}
