//
//  T3ItemTariff.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct T3ItemTariff: View {
    var item: MessageRoomResponse.MessageRoomItem
    
    var body: some View {
        HStack{
            Spacer()
            Text(item.nameTm!)
                .font(.system(size: 15))
                .foregroundColor(Color.white)
                .padding(8.0)
                .background(Color("dark_gray"))
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

struct T3ItemTariff_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemTariff(item: MessageRoomResponse.MessageRoomItem())
    }
}

