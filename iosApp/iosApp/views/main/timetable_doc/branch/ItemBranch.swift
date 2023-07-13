//
//  ItemBranch.swift
//  CallMed_ios
//
//  Created by Mihail on 13.12.2022.
//

import SwiftUI

struct ItemBranch: View {
    var item : BranchLineItem
    var clickItem : (SettingsAllBranchHospitalItem) -> Void
    
    init(item : BranchLineItem, clickItem : @escaping (SettingsAllBranchHospitalItem) -> Void){
        self.item = item
        self.clickItem = clickItem
    }
    
    var body: some View {
        HStack(spacing: 0){
            Spacer()
                .frame(width: 4.0)
            HStack{
                Spacer()
                Text(item.item1.naim_filial ?? "")
                    .multilineTextAlignment(.center)
                    .lineLimit(2)
                    .padding(.vertical, 0.0)
                Spacer()
            }
            .frame(height: 45.0)
            .overlay(
                RoundedRectangle(cornerRadius: 4)
                    .stroke(Color("black_bg25"), lineWidth: 1)
            )
            .background(item.item1.isFavorite ? Color("textSideMenu10") : Color("transparent"))
            .contentShape(Rectangle())
            .onTapGesture {
                clickItem(item.item1)
            }
            
            Spacer()
                .frame(width: 4.0)
            
            if(item.item2 != nil) {
                HStack{
                    Spacer()
                    Text(item.item2!.naim_filial ?? "")
                        .multilineTextAlignment(.center)
                        .lineLimit(2)
                        .padding(.vertical, 0.0)
                    
                    Spacer()
                }
                .frame(height: 45)
                .overlay(
                    RoundedRectangle(cornerRadius: 4)
                        .stroke(Color("black_bg25"), lineWidth: 1)
                )
                .background(item.item2!.isFavorite ? Color("textSideMenu10") : Color("transparent"))
                .contentShape(Rectangle())
                .onTapGesture {
                    clickItem(item.item2!)
                }
            }else{
                HStack{
                    Spacer()
                    Text(" ")
                    Spacer()
                }
            }
            
            Spacer()
                .frame(width: 4.0)
        }
        //.frame(height: 60.0)
    }
}

struct ItemBranch_Previews: PreviewProvider {
    
   // let tt1 = SettingsAllBranchHospitalResponse()
    //tt1.nameBranch = "Name1"
    //let tt2 = SettingsAllBranchHospitalResponse()
    //tt2.nameBranch = "Name2"
    //let tttb = BranchLineItem(item1: SettingsAllBranchHospitalResponse(), item2: SettingsAllBranchHospitalResponse())

    static var previews: some View {
    
        ItemBranch(item: BranchLineItem(item1: SettingsAllBranchHospitalItem(), item2: SettingsAllBranchHospitalItem()), clickItem: {(SettingsAllBranchHospitalResponse) -> Void in})
    }
}
