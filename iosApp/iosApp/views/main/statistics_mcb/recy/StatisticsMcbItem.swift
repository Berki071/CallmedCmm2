//
//  StatisticsMcbItem.swift
//  CallMed_ios
//
//  Created by Mihail on 26.01.2023.
//

import SwiftUI

struct StatisticsMcbItem: View {
    var item : MkbItem
    
    var body: some View {
        ZStack{
                HStack(spacing: 0){
                    
                   // Spacer()

                    Text(item.kodMkb)
                        .font(.system(size: 16))
                        .foregroundColor(Color("text_gray"))
                       
                        .frame(maxWidth: .infinity)
                       
                    //Spacer()
                       
                    Text(String.init(item.count))
                        .font(.system(size: 16))
                        .foregroundColor(Color("text_gray"))
                        .frame(maxWidth: .infinity)
                    //Spacer()
                }
                .padding(.all, 6.0)
                .frame(height: 40.0)
        }
        .overlay(
            RoundedRectangle(cornerRadius: 0)
                .stroke(Color("black_bg3"), lineWidth: 1)
        )
    }
}

struct StatisticsMcbItem_Previews: PreviewProvider {
    static var previews: some View {
        StatisticsMcbItem(item: MkbItem(kodMkb: "cod mcb", count: 5))
    }
}
