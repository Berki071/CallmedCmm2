//
//  TimetableDocView.swift
//  CallMed_ios
//
//  Created by Mihail on 09.12.2022.
//

import SwiftUI

struct BranchLineItem: Equatable, Identifiable, Hashable{
    let id = UUID()
    var item1: SettingsAllBranchHospitalItem
    var item2: SettingsAllBranchHospitalItem?
    
    static func == (lhs: BranchLineItem, rhs: BranchLineItem) -> Bool {
        if(lhs==nil || rhs==nil || lhs.item1.id_filial==nil || rhs.item1.id_filial==nil){
            return false
        }
        
        if(lhs.item1.id_filial! > rhs.item1.id_filial!){
            return true
        }else{
            return false
        }
    }
}

struct TimetableDocView: View {
    @ObservedObject var mainPresenter : TimetableDocPresenter
    var clickButterMenu: (() -> Void)?
    
    @Environment(\.scenePhase) var scenePhase
    
    @State private var selectedTab: Int = 0
    
    init(clickButterMenu: (() -> Void)?){
        self.clickButterMenu = clickButterMenu
        _mainPresenter = ObservedObject(wrappedValue: TimetableDocPresenter())
    }
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                Picker("", selection: $selectedTab) {
                    Text("Расписание по дням")
                        .tag(0)
                    Text("Расписание на месяц")
                        .tag(1)
                }
                .frame(height: 48.0)
                .pickerStyle(SegmentedPickerStyle())
                
                if(selectedTab == 0){
                    TimetableDocDayView(mainPresenter: mainPresenter)
                }else{
                    TimetableDocMonthView(mainPresenter: mainPresenter)
                    
                }
                
                Spacer()
            }
            .padding(.top, 44.0)
            
            VStack(spacing: 0){
                MyToolBar(title1: "Расписание", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                    self.clickButterMenu?()
                }, strSerch: nil)
                
                Spacer()
            }
            
            if(self.mainPresenter.isShowAlertStandart != nil){
                StandartAlert(dataOb: self.mainPresenter.isShowAlertStandart!)
            }
            
            if(self.mainPresenter.showDialogLoading == true){
                LoadingView()
            }
            
        }
        .onChange(of: scenePhase) { newPhase in
                if newPhase == .active {
                    self.mainPresenter.selectNeqBranch()
                    //print(">>>>>>> Active")
                } else if newPhase == .inactive {
                    //print(">>>>>>> Inactive")
                } else if newPhase == .background {
                   //print(">>>>>>> Background")
                }
            }
    }
}

extension UISegmentedControl { /// Picker костль для размеров
    override open func didMoveToSuperview() {
        super.didMoveToSuperview()
        self.setContentHuggingPriority(.defaultLow, for: .vertical)  // << here !!
    }
}

struct TimetableDocView_Previews: PreviewProvider {
    static var previews: some View {
        TimetableDocView(clickButterMenu: nil)
    }
}
