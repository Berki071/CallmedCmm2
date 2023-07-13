//
//  SelectDatesForStatisticMcbVIew.swift
//  CallMed_ios
//
//  Created by Mihail on 24.01.2023.
//

import SwiftUI

struct SelectDatesForStatisticMcbVIew: View {
    @Binding var isShowSelectDateDialog : String?
    @Binding var dateFrom : String
    @Binding var dateTo : String
    
    var sendClick : (() -> Void)? = nil
    
    
    var body: some View {
        HStack{
            Spacer()
                .frame(width: 4.0)
            Text("С")
                .fontWeight(.bold)
                .foregroundColor(Color("text_gray"))
            Spacer()
                .frame(width: 4.0)
            Text(dateFrom)
                .foregroundColor(Color("text_gray"))
                .underline()
                .onTapGesture {
                    self.isShowSelectDateDialog = "from"
                }
            
            Spacer()
                .frame(width: 12.0)
            
            Text("ПО")
                .foregroundColor(Color("text_gray"))
                .fontWeight(.bold)
            Spacer()
                .frame(width: 4.0)
            Text(dateTo)
                .foregroundColor(Color("text_gray"))
                .underline()
                .onTapGesture {
                    self.isShowSelectDateDialog = "to"
                }
            
            Spacer()
            Button(action: {
                self.sendClick?()
            }) {
                Text("Загрузить")
                    .padding(.horizontal, 8.0)
                    .frame(minHeight: 44, maxHeight: 44, alignment: .center)
                    .foregroundColor(Color.white)
                    .background(Color("color_primary"))
                    .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                    .font(.system(size: 20))
            }
            .padding([.top, .bottom, .trailing], 6.0)
            
        }
       // .background(Color("black_bg3"))
        
    }
}

struct SelectDatesForStatisticMcbVIew_Previews: PreviewProvider {
    @State static var isShowSelectDateDialog : String? = nil
    
    @State static var d1 : String = "01.01.2021"
    @State static var d2 : String = "01.01.2022"
    
    static var previews: some View {
        SelectDatesForStatisticMcbVIew(isShowSelectDateDialog: $isShowSelectDateDialog, dateFrom: $d1, dateTo: $d2)
    }
}
