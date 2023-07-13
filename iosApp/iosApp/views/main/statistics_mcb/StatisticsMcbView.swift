//
//  StatisticsMcbView.swift
//  CallMed_ios
//
//  Created by Mihail on 24.01.2023.
//

import SwiftUI

struct StatisticsMcbView: View {
    @ObservedObject var mainPresenter = StatisticsMcbPresenter()
    var clickButterMenu: (() -> Void)?
    
    @State var isShowSelectDateDialog : String? = nil
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                SelectDatesForStatisticMcbVIew(isShowSelectDateDialog: $isShowSelectDateDialog, dateFrom: $mainPresenter.dateFrom, dateTo: $mainPresenter.dateTo, sendClick: {()->Void in
                    self.mainPresenter.loadStatMkb()
                })
                
                if(self.mainPresenter.listMcbMod.count > 0)
                //if(true)
                {
                    VStack(spacing: 0){
                        HStack(spacing: 2){
                            Spacer()
                            
                            if(self.mainPresenter.sortByMcb == true){
                                Text( "Код МКБ10")
                                    .font(.system(size: 16))
                                    .fontWeight(.bold)
                                    .foregroundColor(Color("text_gray"))
                                    .underline()
                            }else{
                                Text( "Код МКБ10")
                                    .font(.system(size: 16))
                                    .fontWeight(.bold)
                                    .foregroundColor(Color("text_gray"))
                                    .onTapGesture{
                                        self.mainPresenter.sortingByMcb()
                                    }
                            }
                            Spacer()
                            
                            if(self.mainPresenter.sortByCount == true){
                                Text( "Количество")
                                    .font(.system(size: 16))
                                    .fontWeight(.bold)
                                    .foregroundColor(Color("text_gray"))
                                    .underline()
                            }else{
                                Text( "Количество")
                                    .font(.system(size: 16))
                                    .fontWeight(.bold)
                                    .foregroundColor(Color("text_gray"))
                                    .onTapGesture{
                                        self.mainPresenter.sortingByCount()
                                    }
                            }
                            Spacer()
                        }
                        .padding(.trailing)
                        .frame(height: 40.0)
                        .background(Color("textSideMenu10"))
                        
                    }
                    
                    ScrollView {
                        VStack(spacing: 0){
                            ForEach(self.mainPresenter.listMcbMod) { item in
                                StatisticsMcbItem(item: item)
                                    .listRowInsets(EdgeInsets())
                            }
                        }
                    }
                    .frame(height: .infinity)
                    Spacer()
                    
                     HStack{
                         HStack{
                             Text("Отправить")
                                 .foregroundColor(.white)
                                 .padding(.all, 9)
                         }
                         
                         .frame(maxWidth: .infinity)
                         .background(Color("color_primary"))
                         .cornerRadius(5.0)
                         .onTapGesture{
                             self.mainPresenter.onSendClick()
                         }
                     }
                     .padding(.horizontal, 8.0)
                }
                
                if(self.mainPresenter.showEmpty == true){
                    Spacer()
                    Image("sh_chat")
                        .resizable(resizingMode: .stretch)
                        .frame(width: 130.0, height: 130.0)
                    
                    Text("За выбранный период нет информации")
                    
                    Spacer()
                        .frame(height: 50.0)
                }
                
                Spacer()
            }
            .padding(.top, 44.0)
            
            
            VStack(spacing: 0){
                MyToolBar(title1: "Статистика МКБ", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                    self.clickButterMenu?()
                }, strSerch: nil)
                
                Spacer()
            }
            
            if(isShowSelectDateDialog != nil){
                SelectDateDialog(id: isShowSelectDateDialog!, cancel: {() -> Void in
                    isShowSelectDateDialog = nil
                }, select: {(i: String, j: String) -> Void in
                    isShowSelectDateDialog = nil
                    
                    if(i == "from" ){
                        mainPresenter.dateFrom = j
                    }else{
                        mainPresenter.dateTo = j
                    }
                })
            }
            
            if(self.mainPresenter.isShowAlertStandart != nil){
                StandartAlert(dataOb: self.mainPresenter.isShowAlertStandart!)
            }
            if(self.mainPresenter.showLoading == true){
                LoadingView()
            }
            
        }
        
        
    }
}

struct StatisticsMcbView_Previews: PreviewProvider {
    static var previews: some View {
        StatisticsMcbView(mainPresenter: StatisticsMcbPresenter())
    }
}
