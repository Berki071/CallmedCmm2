//
//  BranchListPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 12.12.2022.
//

import Foundation
import SwiftUI

class BranchListPresenter : ObservableObject{
    let sdk: NetworkManagerIos
    var sharePreferenses : SharedPreferenses
    //let netConnection = NetMonitor.shared
    
    var showDialogLoading: Binding<Bool>
    
    @Published var listBranch : [BranchLineItem] = []
    var selectBrtanch : SettingsAllBranchHospitalItem? = nil
    
 
    var listenerSelectedBranch: (SettingsAllBranchHospitalItem?) -> Void
    
    init(showDialogLoading: Binding<Bool>, listenerSelectedBranch: @escaping (SettingsAllBranchHospitalItem?) -> Void){
        self.showDialogLoading = showDialogLoading
        self.listenerSelectedBranch = listenerSelectedBranch
        
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
        //netConnection.startMonitoring()
        
        getgetBranchByIdDoc()
    }
    
    func getgetBranchByIdDoc(){
        showLoading(true)
        
        let idDoc = String(sharePreferenses.currentDocInfo?.id_doctor ?? 0)
        let dbName = sharePreferenses.currentCenterInfo?.db_name ?? ""
        let accessToc = sharePreferenses.currentUserInfo?.token ?? ""
        
        sdk.getAllHospitalBranchForDoc(idDoc: idDoc, dbName: dbName,accessToken:accessToc, responseF: {(r: [SettingsAllBranchHospitalItem]) -> Void in
            
            if(r.count>1 || r[0].naim_filial != nil){
                self.processingGetBranch(listT: r)
            }else{
                self.showLoading(false)
                self.listenerSelectedBranch(nil)
            }
            
        }, errorM: {(e: String) -> Void in
            LoggingTree.INSTANCE.e("BranchListPresenter/getgetBranchByIdDoc \(e)")
            self.showLoading(false)
        })
    }
    
    func processingGetBranch(listT: [SettingsAllBranchHospitalItem]){
        var list = listT
        var tmpList :[BranchLineItem] = []
        var cuerrentFBranch = self.sharePreferenses.doctorSelectBranch
        var isSerch = false
        
        list = list.sorted { $0.naim_filial!.compare($1.naim_filial!) == .orderedAscending }
        
        if(cuerrentFBranch == nil || cuerrentFBranch!.isEmpty){
            cuerrentFBranch = String( list[0].id_filial ?? 0)
        }
        
        for i in stride(from: 0, through: list.count-1, by: 2){
            if(String(list[i].id_filial ?? 0) == cuerrentFBranch){
                isSerch = true
                list[i].isFavorite = true
                selectBrtanch = list[i]
                listenerSelectedBranch(selectBrtanch!)
            }else if((i+1)<list.count && String(list[i+1].id_filial ?? 0) == cuerrentFBranch){
                isSerch = true
                list[i+1].isFavorite = true
                selectBrtanch = list[i+1]
                listenerSelectedBranch(selectBrtanch!)
            }
            
            tmpList.append(BranchLineItem(item1: list[i], item2: (((i+1)<list.count) ?  list[i+1] : nil)))
        }
        
        if(!isSerch){
            tmpList[0].item1.isFavorite = true
            selectBrtanch = tmpList[0].item1
            listenerSelectedBranch(selectBrtanch!)
        }
        
        self.listBranch = tmpList
    }
    
    
    func clickFavoriteBranch(item : SettingsAllBranchHospitalItem){
        if(item.id_filial == selectBrtanch!.id_filial){
            return
        }
        
        selectBrtanch = item
        self.listenerSelectedBranch(selectBrtanch!)
                
        for i in listBranch.indices {
            if(listBranch[i].item1.id_filial == item.id_filial){
                listBranch[i].item1.isFavorite = true
            }else{
                listBranch[i].item1.isFavorite = false
            }
            
            if(listBranch[i].item2 != nil){
                if(listBranch[i].item2!.id_filial == item.id_filial){
                    listBranch[i].item2!.isFavorite = true
                }else{
                    listBranch[i].item2!.isFavorite = false
                }
            }
        }
        
        objectWillChange.send()
    }
    
    func showLoading(_ isShow : Bool){
        if(isShow){
            self.showDialogLoading.wrappedValue = true
        }else{
            self.showDialogLoading.wrappedValue = false
        }
    }
    
}
