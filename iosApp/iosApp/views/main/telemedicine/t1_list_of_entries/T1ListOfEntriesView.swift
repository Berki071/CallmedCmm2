//
//  T1ListOfEntriesView.swift
//  CallMed_ios
//
//  Created by Mihail on 08.06.2023.
//

import SwiftUI

struct T1ListOfEntriesView: View {
    @StateObject var mainPresenter = T1ListOfEntriesPresenter()
    
    var clickButterMenu: (() -> Void)?
    init(clickButterMenu: (() -> Void)?){
        self.clickButterMenu = clickButterMenu
    }
    
    var body: some View {
        if(self.mainPresenter.isShowRoomView != nil){
            T3RoomView(item: self.mainPresenter.isShowRoomView!, clickBack:{() -> Void in
                self.mainPresenter.isShowRoomView = nil
                self.mainPresenter.getData((self.mainPresenter.whatDataShow == Constants.WhatDataShow.ACTIVE()) ? "new" : "old")
            })
        }else if(self.mainPresenter.isShowRoomView2 != nil) {
            T3RoomView(idRoom: self.mainPresenter.isShowRoomView2!.idRoom!, idTm: self.mainPresenter.isShowRoomView2!.idTm!, clickBack:{() -> Void in
                self.mainPresenter.isShowRoomView2 = nil
                self.mainPresenter.getData((self.mainPresenter.whatDataShow == Constants.WhatDataShow.ACTIVE()) ? "new" : "old")
            })
        }else{
            ZStack{
                VStack{
                    if(self.mainPresenter.recordsTelemedicineListNew.count > 0){
                        List($mainPresenter.recordsTelemedicineListNew) {item in
                            T1ListOfEntriesTitle(item: item, listener: mainPresenter.listener)
                                .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                .listRowSeparator(.hidden)
                        }
                        .listStyle(PlainListStyle())
                        
                    }else if(self.mainPresenter.recordstTelemedicineListOld.count > 0){
                        List(mainPresenter.recordstTelemedicineListOld, id: \.self) {item in
                            T1ListOfEntriesItem(item: item, listener: mainPresenter.listener)
                                .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                .listRowSeparator(.hidden)
                        }
                        .listStyle(PlainListStyle())
                    }
                }
                .padding(.top, 44.0)
                .frame(maxWidth: .infinity)
                .background(.white)
                
                VStack(spacing: 0){
                    MyToolBarT1Telemedicine(clickHumburger: {() -> Void in   //44.0
                        self.clickButterMenu?()
                    }, clickWhatDataShow: {(i: String) -> Void in
                        self.mainPresenter.whatDataShow = i
                        self.mainPresenter.getData((self.mainPresenter.whatDataShow == Constants.WhatDataShow.ACTIVE()) ? "new" : "old")
                    } ,whatDataShow: self.$mainPresenter.whatDataShow)
                    
                    Spacer()
                }
                
                
                
                if(self.mainPresenter.showEmptyScreen){
                    VStack{
                        Image("sh_chat")
                            .resizable(resizingMode: .stretch)
                            .frame(width: 190.0, height: 150.0)
                        
                        Spacer()
                            .frame(height: 20.0)
                        
                        Text("Здесь будут отображаться приемы")
                            .multilineTextAlignment(.center)
                            .font(.system(size: 16))
                    }
                }
                
                
                if(self.mainPresenter.isShowBigTextAlert != nil){
                    ShowBigTextAlert(dataOb: mainPresenter.isShowBigTextAlert!)
                }
                
                if(self.mainPresenter.isShowAlertStandart != nil){
                    StandartAlert(dataOb: mainPresenter.isShowAlertStandart!)
                }
                
                if(self.mainPresenter.showDialogLoading == true){
                    LoadingView()
                }
                
            }
        }
    }
}

struct T1ListOfEntriesView_Previews: PreviewProvider {
    static var previews: some View {
        T1ListOfEntriesView(clickButterMenu: nil)
    }
}
