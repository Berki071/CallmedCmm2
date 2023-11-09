//
//  AnaliseResultItem.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AnaliseResultItem: View {
    @State var isShowLoad = false
    let dm = DownloadManager()
    let sharePreferenses = SharedPreferenses()
    
    @State var item : AnaliseResponseIos
    var clickShowBigImage: ((AnaliseResponseIos) -> ())?
    var recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
    
    var body: some View {
        ZStack{
            HStack(spacing: 0){
                HStack(spacing: 0){
                    Text("Результаты анализов от:")
                        //.font(.callout)
                        .font(.system(size: 16))
                        .fontWeight(.bold)
                        .padding(.horizontal, 8.0)
                    Text(self.item.date!)
                        .padding(.trailing, 8.0)
                        //.font(.callout)
                        .font(.system(size: 16))
                    Spacer()
                    
                    if(!item.pathToFile.isEmpty){
                        Image("visibility_symbol")
                            .foregroundColor(Color("color_primary"))
                            .padding(4.0)
                    }
                    
                    Divider()
                        .frame(height: 20.0)
                        .background(Color("black_bg25"))
                }
                .onTapGesture {
                    if(!item.pathToFile.isEmpty){
                        self.clickShowBigImage?(item)
                    }
                }
                
                if(item.pathToFile.isEmpty){
                    Image("file_download_symbol")
                        .foregroundColor(Color("color_primary"))
                        .padding(12.0)
                        .onTapGesture {
                            self.isShowLoad = true
                            
                            let nameU = String.init(self.recordTItem.fullNameKl ?? "")
                            let fName = self.dm.getNameFile(item.linkToPDF!)
                            
                            let tmpI = FileNameInfo(pref: Constants.Analise, date: item.getDateForZakl()!, nameSpec: fName, nameUser: nameU)
                            sharePreferenses.addAnaliseFileName(item: tmpI)
                        
                            let apiKey = String.init(self.recordTItem.token_kl ?? "")
                            self.dm.downloadFileForAnaliseResult(fName: tmpI.getFileName(), imagePathServerString: item.linkToPDF!,
                                                                 apiKey: apiKey,
                                                                 result: {(i: String?) -> () in
                                self.isShowLoad = false
                                if(i != nil){
                                    self.item.pathToFile = i!
                                }
                            })
                        }
                }else{
                    Image("delete_symbol")
                        .foregroundColor(Color("red"))
                        .padding(12.0)
                        .onTapGesture {
                            self.isShowLoad = true
                            
                            let nameU = String.init(self.recordTItem.fullNameKl ?? "")
                            let fName = self.dm.getNameFile(item.linkToPDF!)
                            let searchI = sharePreferenses.getAnaliseFileName(pref: Constants.Analise, nameU: nameU, date: item.getDateForZakl()!, spec: fName)
                            
                            let fileName : String
                            if(searchI == nil){
                                fileName = self.dm.getNameFile(item.linkToPDF!)
                            }else{
                                fileName = searchI!.getFileName()
                            }
                            
                            
                            self.dm.deleteFile(fileName: fileName, response: {(i: Bool) -> Void in
                                if(i){
                                    item.pathToFile = ""
                                    self.isShowLoad = false
                                }
                            })
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
            }
        }
        .frame(height: 44.0)
        .overlay(
            RoundedRectangle(cornerRadius: 0)
                .stroke(Color("black_bg25"), lineWidth: 1)
        )
    }
}



struct AnaliseResultItem_Previews: PreviewProvider {
    static var previews: some View {
        AnaliseResultItem(item: AnaliseResponseIos(date: "01.01.2022"), clickShowBigImage: nil, recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem())
    }
}
