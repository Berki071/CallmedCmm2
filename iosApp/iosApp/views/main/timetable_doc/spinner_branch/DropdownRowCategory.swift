//
//  DropdownRowCategory.swift
//  CallMed_ios
//
//  Created by Mihail on 16.12.2022.
//

import SwiftUI

struct DropdownRowCategory: View {
    var option: SettingsAllBranchHospitalItem
    var onOptionSelected: ((_ option: SettingsAllBranchHospitalItem) -> Void)?

    
    var body: some View {
        Button(action: {
            if let onOptionSelected = self.onOptionSelected {
                onOptionSelected(self.option)
            }
        }) {
            HStack {
                Text(option.naim_filial!)
                    .font(.system(size: 16))
                    .foregroundColor(Color.black)
                Spacer()
            }
            .padding(.all, 10.0)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 5)
    }
}

struct DropdownRow_Previews: PreviewProvider {
    static var previews: some View {
        let cat1 = SettingsAllBranchHospitalItem(id_filial: 1, naim_filial: "name1")
        
        DropdownRowCategory(option : cat1)
    }
}
