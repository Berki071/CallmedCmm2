//
//  BranchListView.swift
//  CallMed_ios
//
//  Created by Mihail on 12.12.2022.
//

import SwiftUI

struct BranchListView: View {
    @StateObject var presenter: BranchListPresenter
    
    init(showDialogLoading: Binding<Bool>, listenerSelectedBranch: @escaping (SettingsAllBranchHospitalItem?) -> Void){
        _presenter = StateObject(wrappedValue: BranchListPresenter(showDialogLoading: showDialogLoading, listenerSelectedBranch: listenerSelectedBranch))
    }
    
    var body: some View {
        if(self.presenter.listBranch.count > 0){
            VStack{
                ForEach(self.presenter.listBranch) { item in
                    ItemBranch(item: item, clickItem: {(i: SettingsAllBranchHospitalItem) -> Void in
                        self.presenter.clickFavoriteBranch(item: i)
                    })
                }
            }
            .padding(0.0)
        }
    }
}

struct BranchListView_Previews: PreviewProvider {
    @State static var showDialogLoading : Bool = false
    
    static var previews: some View {
        BranchListView(showDialogLoading: $showDialogLoading, listenerSelectedBranch: {(SettingsAllBranchHospitalResponse) -> Void in})
    }
}
