//
//  ElectronicConclusionsPage.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ElectronicConclusionsPage: View {
    var clickBack: (() -> Void)?
    @StateObject var presenter: ElectronicConclusionsPresenter
    @State var itemForShowBigImage: DataClassForElectronicRecyIos? = nil
    
    init(clickBack: (() -> Void)?, recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem){
        self.clickBack = clickBack
        _presenter = StateObject(wrappedValue: ElectronicConclusionsPresenter(recordTItem: recordTItem))
    }
    
    var body: some View {
        if(itemForShowBigImage != nil){
            ShowImagePage(itemForShowBigImage2: self.$itemForShowBigImage)
        }else{
            ZStack{
                VStack{
                    let nameU = String.init(self.presenter.recordTItem.fullNameKl ?? "")
                    
                    List(self.presenter.listForRecy) {
                        ElectronicConclusionsItem(item: $0, clickShowBigImage: {(i: DataClassForElectronicRecyIos) -> () in
                            self.itemForShowBigImage = i
                        }, recordTItem: self.presenter.recordTItem
                                                  , addFileEvent: {() -> Void in
                            self.presenter.dm.checkFilesForDataClassForElectronicRecy(list: self.presenter.listForRecy, nameUserIn: nameU)
                        })
                        .listRowInsets(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0))
                        .listRowSeparator(.hidden)
                    }
                    .listStyle(PlainListStyle())
                }
                .padding(.top, 44.0)
                .frame(maxWidth: .infinity)
                .background(.white)
                
                VStack(spacing: 0){
                    MyToolBar(title1: "Электронные заключения", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                        self.clickBack?()
                    }, strSerch: nil, isShowImageFreeLine: false)
                    
                    
                    Spacer()
                }
                
                if(self.presenter.showEmptyScreen){
                    VStack{
                        
                        Image("sh_profile")
                            .resizable(resizingMode: .stretch)
                            .frame(width: 190.0, height: 150.0)
                        
                        Spacer()
                            .frame(height: 20.0)
                        
                        Text("Здесь будут отображаться заключения")
                            .multilineTextAlignment(.center)
                            .font(.system(size: 16))
                        
                    }
                }
                
                if(self.presenter.showDialogErrorScreen){
                    VStack{
                        Text("Не удалось загрузить данные.\n Проверьте подключение \nк интернету и \nповторите попытку.")
                            .multilineTextAlignment(.center)
                            .font(.system(size: 16))
                        
                        Spacer()
                            .frame(height: 20.0)
                        
                        Button("Повторить") {
                            self.presenter.showErrorScreen(false)
                            self.presenter.getData()
                        }
                        .padding(.all, 12)
                        .foregroundColor(.white)
                        .background(Color("color_primary"))
                        .cornerRadius(5.0)
                        .font(.system(size: 16))
                    }
                }
                
                if(self.presenter.showDialogLoading == true){
                    LoadingView()
                }
                
                if(self.presenter.isShowAlertStandart != nil){
                    StandartAlert(dataOb: presenter.isShowAlertStandart!)
                }
                
            }
        }
        
    }
}

struct ElectronicConclusionsView_Previews: PreviewProvider {
    static var previews: some View {
        ElectronicConclusionsPage(clickBack: nil, recordTItem: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem())
    }
}
