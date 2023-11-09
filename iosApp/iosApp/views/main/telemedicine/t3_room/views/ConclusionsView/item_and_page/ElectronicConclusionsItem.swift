//
//  ElectronicConclusionsItem.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ElectronicConclusionsItem: View {
    @State var item : DataClassForElectronicRecyIos
    var clickShowBigImage: ((DataClassForElectronicRecyIos) -> ())?
    var recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
    
    @State var isShowLoad = false
    var addFileEvent: (() -> Void)?
    
    let dm = DownloadManager()
    let sharePreferenses = SharedPreferenses()
    
    @State var isOpenBotBar = false
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                HStack{
                    Text(item.item.datePer ?? " ")
                        //.font(.footnote)
                        .font(.system(size: 13))
                        .fontWeight(.bold)
                        .padding(.leading)
                    
                    Text(item.item.textTitle ?? " ")
                        //.font(.footnote)
                        .font(.system(size: 13))
                        .padding(.leading)
//                    Text("asfsdf sdsf sdfs dfssfsdfsfd sdfsdfsadfsdf sdfg sdgsdfgsdfgsdg sddfg sdfgsdfgsdfgsdfgsdfgsdfg sddfgsdfgsdgsdfgs fgsdf")
//                        //.font(.footnote)
//                        .font(.system(size: 13))
//                        .padding(.leading)
                    Spacer()
                }
                .padding(.vertical, 6)
                .frame(minHeight: 44.0)
                .onTapGesture {
                    self.isOpenBotBar = !isOpenBotBar
                }
                
                
                if(isOpenBotBar){
                    if(item.item.isDownloadIn){
                        HStack{
                            HStack{
                                Spacer()
                                Image("visibility_symbol")
                                    .foregroundColor(Color("color_primary"))
                                Spacer()
                            }
                            .onTapGesture {
                                self.clickShowBigImage?(item)
                            }
                            
                            Divider()
                                .frame(height: 20.0)
                                .background(Color("black_bg25"))
                            
                            
                            HStack{
                                Spacer()
                                Image("delete_symbol")
                                    .foregroundColor(Color("red"))
                                Spacer()
                            }
                            .onTapGesture {
                                self.isShowLoad = true
                                if(item.item is AnaliseResponse){
                                    let itm = (item.item as! AnaliseResponse)
                                    
                                    let nameU = String.init(recordTItem.fullNameKl ?? "")
                                    let fName = self.dm.getNameFile(itm.linkToPDF!)
                                    let searchI = sharePreferenses.getElectronicConclusions(pref: Constants.ConclusionAn, nameU: nameU, date: itm.getDateForZakl()!, spec: fName)
                                    
                                    let fileName : String
                                    if(searchI == nil){
                                        fileName = self.dm.getNameFile(itm.linkToPDF!)
                                    }else{
                                        fileName = searchI!.getFileName()
                                    }
                                     
                                    self.dm.deleteFile(fileName: fileName, response: {(i: Bool) -> Void in
                                        if(i){
                                            self.item.item.pathToFile = ""
                                            self.isShowLoad = false
                                        }
                                    })
                                }else{
                                    let itm = (item.item as! ResultZakl2Response.ResultZakl2Item)
                                    let nameU = String.init(recordTItem.fullNameKl ?? "")
                                    
                                    let searchI = sharePreferenses.getElectronicConclusions(pref: Constants.ConclusionZacPDF, nameU: nameU, date: itm.datePer!, spec: itm.nameSpec!)
                                    
                                    let fileName : String
                                    if(searchI == nil){
                                        fileName = itm.getNameFileWithoutExtension(name: String.init(recordTItem.fullNameKl ?? ""))
                                    }else{
                                        fileName = searchI!.getFileName()
                                    }
                                    
                                    self.dm.deleteFile(fileName: fileName, response: {(i: Bool) -> Void in
                                        if(i){
                                            item.item.pathToFile = ""
                                            self.isShowLoad = false
                                        }
                                    })
                                }
                            }
                        }
                        .frame(height: 44.0)
                        .background(Color("black_bg3"))
                    }else{
                        HStack{
                            Spacer()
                            Image("file_download_symbol")
                                .foregroundColor(Color("color_primary"))
                            Spacer()
                        }
                        .frame(height: 44.0)
                        .background(Color("black_bg3"))
                        .onTapGesture {
                            self.isShowLoad = true
                            
                            let nameU = String.init(recordTItem.fullNameKl ?? "")
                           
                            if(item.item is AnaliseResponse){
                                let itm = (item.item as! AnaliseResponse)
                                let fName = self.dm.getNameFile(itm.linkToPDF!)
                            
                                let tmpI = FileNameInfo(pref: Constants.ConclusionAn, date: itm.getDateForZakl()!, nameSpec: fName, nameUser: nameU)
                                sharePreferenses.addElectronicConclusions(item: tmpI)
                                
                                self.dm.downloadFileForAnaliseResult(fName: tmpI.getFileName(), imagePathServerString: itm.linkToPDF!,
                                                                     apiKey: String.init(self.recordTItem.token_kl ?? ""),
                                                                     result: {(i: String?) -> () in
                                    self.isShowLoad = false
                                    if(i != nil){
                                        self.item.item.pathToFile = i!
                                    }
                                })
                            }else{
                                let itm = (item.item as! ResultZakl2Response.ResultZakl2Item)
                                
                                let tmpI = FileNameInfo(pref: Constants.ConclusionZacPDF, date: itm.datePer!, nameSpec: itm.nameSpec!, nameUser: nameU)
                                
                                do{
                                    //tmpI.getFileName()
                                    try self.dm.loadFile2ForElectronicConclusions(tmpI: tmpI, item: itm, result: {(i: String?) -> () in
                                        self.isShowLoad = false
                                        if(i != nil){
                                            //print(">>>>> \(i)")
                                            ///self.item.item.pathToFile = i!
                                            self.addFileEvent?()
                                        }
                                    }, recordTItem: recordTItem)
                                }catch {
                                    LoggingTree.INSTANCE.e("ElectronicConclusionsItem/loadFile2", error)
                                }
                            }
                            
                        }
                        
                    }
                }
            }
            
            if(isShowLoad){
                HStack{
                    ZStack{
                        Color("black_bg")
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
                            .foregroundColor(Color("color_primary"))
                    }
                }
                .frame(height: isOpenBotBar ? 88.0 : 44.0)
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: 0)
                .stroke(Color("black_bg25"), lineWidth: 1)
        )
    }
}

struct ElectronicConclusionsItem_Previews: PreviewProvider {
    static var previews: some View {
        ElectronicConclusionsItem(item: DataClassForElectronicRecyIos(item: AnaliseResponseIos(date: "анализ такойто и потому то 01.01.2022")), clickShowBigImage: nil, recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem())
    }
}

