//
//  DropdownSelectorCategory.swift
//  CallMed_ios
//
//  Created by Mihail on 16.12.2022.
//

import SwiftUI

struct DropdownSelectorCategory: View {
    @State private var shouldShowDropdown = false
    @Binding var selectedOption: SettingsAllBranchHospitalItem?
    var options: [SettingsAllBranchHospitalItem]
    var onOptionSelected: ((_ option: SettingsAllBranchHospitalItem) -> Void)?
    private let buttonHeight: CGFloat = 48
    
    var body: some View {
        Button(action: {
                   self.shouldShowDropdown.toggle()
               }) {
                   VStack(spacing: 0){
                       Spacer()
                       HStack {
                           Image("account_balance")
                               .imageScale(.large)
                               .foregroundColor(Color("color_primary"))
                               .font(Font.system(size: 16, weight: .bold))
                           
                           Spacer()
                           
                           Text(selectedOption?.naim_filial ?? "")
                               .font(.system(size: 16))
                               .foregroundColor(Color.black)
                           
                           Spacer()
                           
                           Image(systemName: self.shouldShowDropdown ? "arrowtriangle.up.fill" : "arrowtriangle.down.fill")
                               .resizable()
                               .frame(width: 9, height: 5)
                               .font(Font.system(size: 9, weight: .medium))
                               .foregroundColor(Color("color_primary"))
                       }
                       .padding(.horizontal)
                       Spacer()
                       
                       VStack { Divider().frame(height: 1.0).background(Color.black) }
                   }
               }
               
               .frame(width: .infinity, height: self.buttonHeight)
               .overlay(
                   RoundedRectangle(cornerRadius: 0)
                       .stroke(Color.white, lineWidth: 1)
               )
               .background(Color.white)
               .overlay(
                   VStack {
                       if self.shouldShowDropdown  {
                           Spacer(minLength: buttonHeight )  //+10
                           DropdownCategory(options: self.options, onOptionSelected: { option in
                               shouldShowDropdown = false
                               selectedOption = option
                               self.onOptionSelected?(option)
                           })
                       }
                   }, alignment: .topLeading
               )
               .background(
                   RoundedRectangle(cornerRadius: 5).fill(Color.white)
               )
    }
}

struct DropdownSelector_Previews: PreviewProvider {
    
    @State static var selectedOption: SettingsAllBranchHospitalItem? = SettingsAllBranchHospitalItem(id_filial: 2, naim_filial: "name2")
    
    static var previews: some View {
        let hint = "hint my"
        
        let cat1 = SettingsAllBranchHospitalItem(id_filial: 1, naim_filial: "name1")
        let cat2 = SettingsAllBranchHospitalItem(id_filial: 2, naim_filial: "name2")
        let cat3 = SettingsAllBranchHospitalItem(id_filial: 3, naim_filial: "name3")
        let arr = [cat1,cat2,cat3]
        
        
        DropdownSelectorCategory(selectedOption: $selectedOption , options : arr)
    }
}
