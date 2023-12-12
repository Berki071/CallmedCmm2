//
//  MainView.swift
//  CallMed_ios
//
//  Created by Mihail on 07.12.2022.
//

import SwiftUI

struct MainView: View {
    @ObservedObject var mainPresenter: MainPresenter
    
    @ObservedObject var appState = AppState.shared
    
    init(startPage: Int) {
        _mainPresenter = ObservedObject(wrappedValue: MainPresenter(startPage: startPage))
        
        CheckUpdate.shared.showUpdate(withConfirmation: false)
    }
    
    var body: some View {
        let drag = DragGesture()
            .onEnded {
                if $0.translation.width < -100 {
                    withAnimation {
                        self.mainPresenter.showMenu = false
                    }
                }
                
                if $0.translation.width > 100 {
                    //withAnimation {
                        self.mainPresenter.showMenu = true
                   // }
                }
            }
        
        if(self.mainPresenter.nextPage == "Login"){
            LoginView()
        }else{
            ZStack{
                
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        
                        MyPageViews(mainP: self.mainPresenter)
                            .frame(width: geometry.size.width, height: geometry.size.height)
                            .disabled(self.mainPresenter.showMenu ? true : false)

                        
                        if self.mainPresenter.showMenu {
                            MenuView(selectitem : self.$mainPresenter.selectMenuPage, selectMenuAlert: self.$mainPresenter.selectMenuAlert, showMenu: self.$mainPresenter.showMenu)
                                .transition(.move(edge: .leading))
                        }
                        
                        
                        if(self.mainPresenter.selectMenuAlert == 1){
                            StandartAlert(dataOb: StandartAlertData(titel: "", text: "Вы действительно хотите выйти из учетной записи?", isShowCansel: true, someFuncOk: {() -> Void in
                                self.mainPresenter.selectMenuAlert = 0
                                self.mainPresenter.logOut()
                            }, someFuncCancel: {() -> Void in
                                self.mainPresenter.selectMenuAlert = 0
                            }, textBtnOk: "Да", textBtnCancel: "Нет"))
                        }
                    }
                    .background(Color.white)
                    .gesture(drag)
                }
            }
            .onReceive(appState.$pageToNavigationTo) { (nav) in
                if nav != nil {
                    self.mainPresenter.selectMenuPage = 3
                }
            }
        }
        
    }
}

struct MyPageViews: View {
    @StateObject var mainP : MainPresenter
   
    var body: some View {
 
        if(self.mainP.selectMenuPage == 1){
            TimetableDocView(clickButterMenu:{() -> Void in
                self.mainP.showMenu = true
            })
        }else if(self.mainP.selectMenuPage == 2){
            StatisticsMcbView(clickButterMenu:{() -> Void in
                self.mainP.showMenu = true
            })
        }else if(self.mainP.selectMenuPage == 0){
            SettingsView(clickButterMenu:{() -> Void in
                self.mainP.showMenu = true
            })
        }else if(self.mainP.selectMenuPage == 3){
            T1ListOfEntriesView(clickButterMenu:{() -> Void in
                self.mainP.showMenu = true
            })
        }
    }
}


struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView(startPage: 0)
    }
}
