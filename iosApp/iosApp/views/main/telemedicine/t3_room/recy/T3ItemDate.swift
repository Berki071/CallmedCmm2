//
//  T3ItemDate.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct T3ItemDate: View {
    var item: MessageRoomItem
    
    var body: some View {
        
        HStack{
            Spacer()
            Text(MDate.getNewFormatString(item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_ddMMyyyy))
                .font(.system(size: 15))
                .foregroundColor(Color("text_gray"))
                .padding(8.0)
                .background(Color.white)
                .cornerRadius(6)
                .overlay(
                    RoundedRectangle(cornerRadius: 6)
                        .stroke(Color("black_bg3"), lineWidth: 1)
                )
            Spacer()
        }
        .padding(.top, 4)
        
    }
}

struct T3ItemDate_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemDate(item: MessageRoomItem())
    }
}

