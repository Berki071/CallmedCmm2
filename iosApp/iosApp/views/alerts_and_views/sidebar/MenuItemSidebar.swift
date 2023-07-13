//
//  MenuItemSidebar.swift
//  Medhelp
//
//  Created by Mihail on 18.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuItemSidebar: View {
    var selectitem : Binding<Int>
    var selectMenuAlert : Binding<Int>
    var showMenuFalse : (() -> Void)?
    
    var sharePreferenses : SharedPreferenses
    
    init(selectitem : Binding<Int>, selectMenuAlert : Binding<Int>, showMenuFalse : (() -> Void)?){
        self.selectitem = selectitem
        self.selectMenuAlert = selectMenuAlert
        self.showMenuFalse = showMenuFalse
        
        sharePreferenses = SharedPreferenses()
    }
    
    var body: some View {

        VStack(alignment: .leading, spacing: 0){
            
            VStack(alignment: .leading, spacing: 0){
                //let tmp1 = sharePreferenses.currentUserInfo!.vrach
                if(sharePreferenses.currentUserInfo!.vrach == "true"){
                    ZStack(alignment: .leading){
                        if(selectitem.wrappedValue == 1){
                            Color("textSideMenu10")
                        }
                        HStack {
                            Spacer()
                                .frame(width: 8.0)
                            Image("assignment_turned_in_symbol")
                                .imageScale(.large)
                                .foregroundColor(Color("textSideMenu"))
                            Text("Расписание")
                                .foregroundColor(Color("textSideMenu"))
                                .font(.headline)
                        }
                    }
                    .frame(height: 50.0)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        selectitem.wrappedValue = 1
                        self.showMenuFalse?()
                    }
                }
                
                if(sharePreferenses.currentUserInfo!.chat == "true"){
                    ZStack(alignment: .leading){
                        if(selectitem.wrappedValue == 3){
                            Color("textSideMenu10")
                        }
                        HStack {
                            Spacer()
                                .frame(width: 8.0)
                            Image("featured_play_list")
                                .imageScale(.large)
                                .foregroundColor(Color("textSideMenu"))
                            Text("Телемедицина")
                                .foregroundColor(Color("textSideMenu"))
                                .font(.headline)
                        }
                    }
                    .frame(height: 50.0)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        selectitem.wrappedValue = 3
                        self.showMenuFalse?()
                    }
                }
                
                if(sharePreferenses.currentUserInfo!.vrach == "true"){
                    ZStack(alignment: .leading){
                        if(selectitem.wrappedValue == 2){
                            Color("textSideMenu10")
                        }
                        HStack {
                            Spacer()
                                .frame(width: 8.0)
                            Image("featured_play_list")
                                .imageScale(.large)
                                .foregroundColor(Color("textSideMenu"))
                            Text("Статистика МКБ")
                                .foregroundColor(Color("textSideMenu"))
                                .font(.headline)
                        }
                    }
                    .frame(height: 50.0)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        selectitem.wrappedValue = 2
                        self.showMenuFalse?()
                    }
                }
                
                ZStack(alignment: .leading){
                    if(selectitem.wrappedValue == 0){
                        Color("textSideMenu10")
                    }
                    HStack {
                        Spacer()
                            .frame(width: 8.0)
                        Image("settings")
                            .imageScale(.large)
                            .foregroundColor(Color("textSideMenu"))
                        Text("Настройки")
                            .foregroundColor(Color("textSideMenu"))
                            .font(.headline)
                    }
                }
                .frame(height: 50.0)
                .contentShape(Rectangle())
                .onTapGesture {
                    selectitem.wrappedValue = 0
                    self.showMenuFalse?()
                }
                
                ZStack(alignment: .leading){
                    HStack {
                        Spacer()
                            .frame(width: 8.0)
                        Image("exit_to_app-exit_to_app_symbol")
                            .imageScale(.large)
                            .foregroundColor(Color("textSideMenu"))
                        Text("Выйти")
                            .foregroundColor(Color("textSideMenu"))
                            .font(.headline)
                    }
                }
                .frame(height: 50.0)
                .contentShape(Rectangle())
                .onTapGesture {
                    selectMenuAlert.wrappedValue = 1
                    self.showMenuFalse?()
                }
                Spacer()
            }
            HStack{
                Spacer()
                Text("v. \(getVercion())")
                    .font(.caption)
                    .foregroundColor(Color("semi_gray"))
                Spacer()
            }
        }
    }
    
    func getVercion() -> String{
        return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
    }
}

struct MenuItemSidebar_Previews: PreviewProvider {
    @State static private var na = 0
    @State static private var ni = 0
    
    static var previews: some View {
        MenuItemSidebar(selectitem: $na,selectMenuAlert: $ni, showMenuFalse: nil)
    }
}
