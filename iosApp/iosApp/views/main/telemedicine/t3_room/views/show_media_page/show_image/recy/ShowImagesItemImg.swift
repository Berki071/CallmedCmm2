//
//  ShowImagesItemImg.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI

struct ShowImagesItemImg: View {
    @ObservedObject var mainPresenter: ShowImagesItemImgPresenter
    
    var item: ShowImagesFilesItemData
    let listener: ShowImagesItemListener
    
    
    
    init (item: ShowImagesFilesItemData, listener: ShowImagesItemListener){
        self.item = item
        self.listener = listener
        mainPresenter = ShowImagesItemImgPresenter(item: item)
    }
 
    
    var body: some View {
        VStack{
            HStack{
                Spacer()
                    .frame(width: 8.0)
                if(self.mainPresenter.image == nil){
                    Image("draft")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(Color("semi_gray"))
                        .padding(8.0)
                        .frame(width: 100, height: 100)
                }else{
                    Image(uiImage: self.mainPresenter.image!)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .padding(8.0)
                        .frame(width: 100, height: 100)
                }
                    
                
                Text(item.url!.lastPathComponent)
                    .foregroundColor(Color("text_gray"))
                    .font(.system(size: 15))
                
                Spacer()
                    
            }
            HStack{
                Spacer()
                Text(MDate.dateToString(item.date,MDate.DATE_FORMAT_ddMMyyyy))
                    .font(.system(size: 10))
                    .foregroundColor(Color("text_gray"))
                    .padding(.bottom, 4)
                Spacer()
                    .frame(width: 8.0)
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: 6)
                .stroke(Color("black_bg3"), lineWidth: 2)
        )
        .padding(2)
        .onTapGesture {
            self.listener.simpleClick(item)
        }
        .onLongPressGesture(minimumDuration: 0.1) {
            self.listener.longClick(item)
        }
    }

}

struct ShowImagesItemImg_Previews: PreviewProvider {
    static var previews: some View {
        ShowImagesItemImg(item: ShowImagesFilesItemData(date: MDate.curentTimeDate(), url: nil), listener: ShowImagesItemListener(simpleClick: {(ShowImagesItemData) -> Void in }, longClick: {(ShowImagesItemData) -> Void in }))
    }
}
