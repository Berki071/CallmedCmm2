//
//  ShowImagesItemDate.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI

struct ShowImagesItemDate: View {
    var item: ShowImagesFilesItemData
    
    var body: some View {
        //VStack{
            HStack{
                Spacer()
                Text(MDate.dateToString(item.date, MDate.DATE_FORMAT_ddMMyyyy))
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
//            Divider()
//        }
    }
}

struct ShowImagesItem_Previews: PreviewProvider {
    static var previews: some View {
        ShowImagesItemDate(item: ShowImagesFilesItemData(date: MDate.curentTimeDate(), url: nil))
    }
}

