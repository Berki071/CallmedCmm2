//
//  SelectDateDialog.swift
//  Medhelp
//
//  Created by Mihail on 27.10.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SelectDateDialog: View {
    @State private var selectDate = Date()
    
    var id : String
    var cancel: (() -> Void)?
    var select: ((String,String) -> Void)?
    
    var body: some View {
      
        ZStack{
            Color("black_bg")
            
            ZStack{
                
                VStack{
                    DatePicker( "Дата",selection: $selectDate, in: ...Date(), displayedComponents: .date)
                        .environment(\.locale, Locale.init(identifier: Locale.preferredLanguages.first!))
                        .labelsHidden()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color("bg3_no_transparent"))
                        .cornerRadius(4)
                        .datePickerStyle(.graphical)
                        .padding()
                    
                    HStack{
                        Button(action: {
                            self.cancel?()
                        }) {
                            Text("Отмена")
                                .frame(minWidth: 100, maxWidth: .infinity, minHeight: 44, maxHeight: 44, alignment: .center)
                                .foregroundColor(Color.white)
                                .background(Color("color_primary"))
                                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                                .font(.system(size: 16))
                        }
                        
                        Button(action: {
                            self.select?(id, dateToString())
                        }) {
                            Text("OK")
                                .frame(minWidth: 100, maxWidth: .infinity, minHeight: 44, maxHeight: 44, alignment: .center)
                                .foregroundColor(Color.white)
                                .background(Color("color_primary"))
                                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                                .font(.system(size: 16))
                        }
                    }
                    .padding([.leading, .bottom, .trailing])
                }
                .background(.white)
                .padding(.horizontal)
                

            }
        }
    }
    
    
    func dateToString() -> String {
        let formatter3 = DateFormatter()
        formatter3.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        return formatter3.string(from: selectDate)
    }
}

struct SelectDateDialog_Previews: PreviewProvider {
    static var previews: some View {
        SelectDateDialog(id: "null")
    }
}
