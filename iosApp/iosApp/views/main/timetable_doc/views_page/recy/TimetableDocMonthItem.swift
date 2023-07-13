//
//  TimetableDocMonthItem.swift
//  CallMed_ios
//
//  Created by Mihail on 20.01.2023.
//

import SwiftUI

struct TimetableDocMonthItem: View {
    @State var item : AllRaspSotrItem
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                //VStack { Divider().frame(height: 1.0).background(Color.gray) }
                
                HStack(spacing: 2){
                    Spacer()
                        .frame(width: 0)
                    
                    Text(item.data ?? "")
                        .font(.system(size: 16))
                        .foregroundColor(Color("text_gray"))
                        .frame(width: 85.0)
                    
                    
                    HStack{ Divider().frame(width: 1.0).background(Color.gray) }
                    
                    HStack(spacing: 2){
                        Text(item.start ?? "")
                            .font(.system(size: 16))
                            .foregroundColor(Color("text_gray"))
                        Text("-")
                            .foregroundColor(Color("text_gray"))
                            .font(.system(size: 16))
                        Text(item.end ?? "")
                            .foregroundColor(Color("text_gray"))
                            .font(.system(size: 16))
                    }
                    .frame(width: 105.0)
                    
                    HStack{ Divider().frame(width: 1.0).background(Color.gray) }
                    
                    Spacer()
                    Text(item.naim_filial ?? "")
                        .foregroundColor(Color("text_gray"))
                        .font(.system(size: 16))
                    Spacer()
                }
                .padding(.trailing)
                .frame(height: 40.0)
                
                VStack { Divider().frame(height: 1.0).background(Color.gray) }
            }
            
        }
    }
  
}

struct TimetableDocMonthItem_Previews: PreviewProvider {
    static var previews: some View {
        TimetableDocMonthItem(item: AllRaspSotrItem())
    }
}
