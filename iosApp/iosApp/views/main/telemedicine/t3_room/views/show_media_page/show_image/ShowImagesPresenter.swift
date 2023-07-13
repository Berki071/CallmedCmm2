//
//  ShowImagesPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared


class ShowImagesPresenter: ObservableObject {
    var itemRecord: Binding<AllRecordsTelemedicineItem?>
    
    @Published var arrayImagePng: [ShowImagesFilesItemData] = []
    @Published var isShowAlertStandart: StandartAlertData? = nil
    
    var sharePreferenses : SharedPreferenses = SharedPreferenses()

    
    init(item: Binding<AllRecordsTelemedicineItem?>){
        self.itemRecord = item
   
        if(itemRecord.wrappedValue != nil){
            self.getData()
        }
    }
    
    func getData(){
        let path = getDocumentsDirectory()
        
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "png" }  // во внутренню папку все сохраняютсяв пнг
           
            if(allFilesPngURL.count > 0){
                let prefix = Constants.PREFIX_NAME_FILE
                let idCenter: String = String(self.sharePreferenses.currentUserInfo!.id_center ?? -1)
                let idRoom: String = String(Int.init(itemRecord.wrappedValue!.idRoom!))
                
                let searchStr = prefix + "_" + idCenter + "_" + idRoom
                
                var tmpURLList: [ShowImagesFilesItemData] = []
                let tmpDate = MDate.getCurrentDate()
                
                for i in allFilesPngURL {
                    if i.absoluteString.contains(searchStr){
                        let dateI = (try? i.resourceValues(forKeys: [.creationDateKey]))?.creationDate ?? tmpDate
                        tmpURLList.append(ShowImagesFilesItemData(date: dateI, url: i))
                    }
                }
                
                if(tmpURLList.count > 0){
                    tmpURLList = tmpURLList.sorted(by: {
                        return $0.date  > $1.date
                        }
                    )
                    
                    self.processingData(tmpURLList)
                }else{
                    self.arrayImagePng = []
                }
            }else{
                self.arrayImagePng = []
            }
        
        } catch {
            LoggingTree.INSTANCE.e("ShowImagesPresenter/getData", error)
            //print(">>> error")
        }
    }
    func processingData(_ list: [ShowImagesFilesItemData]) {
        var newList: [ShowImagesFilesItemData] = []
        
        newList.append(ShowImagesFilesItemData(date: list[0].date, url: nil))
        
        for i in list {
            if(MDate.dateToString(newList.last!.date,MDate.DATE_FORMAT_ddMMyyyy)  != MDate.dateToString(i.date, MDate.DATE_FORMAT_ddMMyyyy) ){
                newList.append(ShowImagesFilesItemData(date: i.date, url: nil))
            }
            newList.append(i)
        }
        
        self.arrayImagePng = newList
    }
    
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
    
    func deleteItemQuestion(_ item: ShowImagesFilesItemData){
        self.isShowAlertStandart = StandartAlertData(titel: "Удаление", text: "Удалить файл \(item.url!.lastPathComponent)?", isShowCansel: true, someFuncOk: {() -> Void in
           
            try? FileManager.default.removeItem(at: item.url!)
            
            self.getData()
            
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in
            self.isShowAlertStandart = nil
        })
    }
    
    
    
}
