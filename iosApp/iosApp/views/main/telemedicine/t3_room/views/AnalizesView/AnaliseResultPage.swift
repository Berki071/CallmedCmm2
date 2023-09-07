//
//  AnaliseResultPage.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AnaliseResultPage: View {
    var clickBack: (() -> Void)?
    @StateObject var presenter: AnaliseResultPresenter
    @State var itemForShowBigImage: AnaliseResponseIos? = nil
    
    init(clickBack: (() -> Void)?, recordTItem: AllRecordsTelemedicineItem){
        self.clickBack = clickBack
        _presenter = StateObject(wrappedValue: AnaliseResultPresenter(recordTItem: recordTItem))
    }
    
    var body: some View {
        if(itemForShowBigImage != nil){
            ShowImagePage(itemForShowBigImage: self.$itemForShowBigImage)
            
        }else{
            ZStack{
                VStack{
                    List(self.presenter.analiseResultList) {
                        AnaliseResultItem(item: $0, clickShowBigImage: {(i: AnaliseResponseIos) -> () in
                            self.itemForShowBigImage = i
                        }, recordTItem: self.presenter.recordTItem)
                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                            .listRowSeparator(.hidden)
                    }
                    .listStyle(PlainListStyle())
                }
                .padding(.top, 44.0)
                .frame(maxWidth: .infinity)
                .background(.white)
                
                VStack(spacing: 0){
                    MyToolBar(title1: "Результаты анализов", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                        self.clickBack?()
                    }, strSerch: nil, isShowImageFreeLine: false)
                    
                    
                    Spacer()
                }
                
                if(self.presenter.showEmptyScreen){
                    VStack{
                        
                        Image("sh_analise")
                            .resizable(resizingMode: .stretch)
                            .frame(width: 190.0, height: 150.0)
                        
                        Spacer()
                            .frame(height: 20.0)
                        
                        Text("Здесь будут отображаться результаты сданных Вами анализов")
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
                            //self.presenter.getAnalisePrice()
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

struct AnaliseResult_Previews: PreviewProvider {
    static var previews: some View {
        AnaliseResultPage(clickBack: nil, recordTItem: AllRecordsTelemedicineItem())
    }
}
