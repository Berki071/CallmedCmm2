//
//  SettingsView.swift
//  CallMed_ios
//
//  Created by Mihail on 16.01.2023.
//

import SwiftUI

struct SettingsView: View {
    @ObservedObject var mainPresenter : SettingsPresenter
    var clickButterMenu: (() -> Void)?

    init(clickButterMenu: (() -> Void)?){
        self.clickButterMenu = clickButterMenu
        _mainPresenter = ObservedObject(wrappedValue: SettingsPresenter())
    }
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                Spacer()
                    .frame(height: 4.0)
                
                HStack{
                    Toggle("Показ уведомлений", isOn: $mainPresenter.isShowNotifacation)
                        .padding(/*@START_MENU_TOKEN@*/.all, 8.0/*@END_MENU_TOKEN@*/)
                        .onChange(of: mainPresenter.isShowNotifacation) { value in
                            self.mainPresenter.showNotificationValueChanget(value)
                         }

                }
                .overlay(
                    RoundedRectangle(cornerRadius: 6)
                        .stroke(Color("black_bg3"), lineWidth: 1)
                       
                )
                
                Spacer()
            }
            .padding(.top, 44.0)
            .padding(.horizontal, 4.0)
            
            VStack(spacing: 0){
                MyToolBar(title1: "Настройки", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                    self.clickButterMenu?()
                }, strSerch: nil)
                
                Spacer()
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

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView(clickButterMenu: nil)
    }
}
