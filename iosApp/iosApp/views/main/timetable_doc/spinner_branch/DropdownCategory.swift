//
//  DropdownCategory.swift
//  CallMed_ios
//
//  Created by Mihail on 16.12.2022.
//

import SwiftUI

struct DropdownCategory: View {
    var options: [SettingsAllBranchHospitalItem]
    var onOptionSelected: ((_ option: SettingsAllBranchHospitalItem) -> Void)?

    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                ForEach(self.options, id: \.self) { option in
                    DropdownRowCategory(option: option, onOptionSelected: self.onOptionSelected)
                        
                }
            }
        }
        .frame(height: getHeightScroll())
        .padding(.vertical, 5)
        .background(Color("lightGray"))
        .cornerRadius(0)
        .overlay(
            RoundedRectangle(cornerRadius: 0)
                .stroke(Color.gray, lineWidth: 1)
        )
    }
    
    func getHeightScroll() -> CGFloat {
        let maxH = UIScreen.main.bounds.size.height - 140
        let maxByCount =  CGFloat(50 * options.count)
        
        if(maxByCount > maxH){
            return maxH
        }else{
            return maxByCount
        }
    }
}

struct Dropdown_Previews: PreviewProvider {
    static var previews: some View {
        let cat1 = SettingsAllBranchHospitalItem(id_filial: 1, naim_filial: "name1")
        let cat2 = SettingsAllBranchHospitalItem(id_filial: 2, naim_filial: "name2")
        let cat3 = SettingsAllBranchHospitalItem(id_filial: 3, naim_filial: "name3")
        let arr = [cat1,cat2,cat3]
        
        DropdownCategory(options : arr)
    }
}

