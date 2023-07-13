//
//  T3ItemImg.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct T3ItemImg: View {
    @StateObject var mainPresenter: T3ItemPresenter
    
    var showBigImage: (MessageRoomItem) -> Void
    var clickRemuveItem: (MessageRoomItem) -> Void
    
    init(item: MessageRoomItem, showBigImage: @escaping (MessageRoomItem) -> Void, clickRemuveItem: @escaping  (MessageRoomItem) -> Void){
        _mainPresenter = StateObject(wrappedValue:T3ItemPresenter(item: item))
        self.showBigImage = showBigImage
        self.clickRemuveItem = clickRemuveItem
    }
    
    var body: some View {
        VStack{
            Spacer()
                .frame(height: 6)
            
            HStack{
                if(mainPresenter.item.otpravitel != "kl"){
                    Spacer()
                        .frame(width: 30)
                    
                    Spacer()
                    
                    ZStack(alignment: .bottomTrailing){
                        
                        if(self.mainPresenter.iuImageLogo != nil ){
                            Image(uiImage: self.mainPresenter.iuImageLogo!)
                                .resizable()
                                .cornerRadius( 6)
                                .scaledToFit()
                                .frame( maxHeight: 350)
                        }else{
                            Image("photo_camera")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(maxWidth: 40, maxHeight: 40)
                                .foregroundColor(Color("semi_gray"))
                        }
                        
                        if(mainPresenter.item.data != nil && !mainPresenter.item.data!.isEmpty && mainPresenter.item.idMessage != nil){
                            Text(MDate.getNewFormatString(mainPresenter.item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                .foregroundColor(Color.white)
                                .font(.system(size: 10))
                                .foregroundColor(Color.white)
                                .padding([.trailing, .bottom], 2)
                                
                        }
                    }
                    .padding([.top, .leading, .trailing], 4.0)
                    .padding(.bottom, 4.0)
                    .background(Color("msgRight"))
                    .cornerRadius(6)
                    
                    Spacer()
                        .frame(width: 8)
                }
                
                
                
                if(mainPresenter.item.otpravitel == "kl"){
                    Spacer()
                        .frame(width: 8)
                    
                    ZStack(alignment: .bottomTrailing){
                        if(self.mainPresenter.iuImageLogo != nil ){
                            Image(uiImage: self.mainPresenter.iuImageLogo!)
                                .resizable()
                                .cornerRadius( 6)
                                .aspectRatio(contentMode: .fit)
                                .frame(maxHeight: 350)
                                
                        }else{
                            Image("photo_camera")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(maxWidth: 40, maxHeight: 40)
                                .foregroundColor(Color("semi_gray"))
                        }
                        
                        if(mainPresenter.item.data != nil && !mainPresenter.item.data!.isEmpty && mainPresenter.item.idMessage != nil){
                            Text(MDate.getNewFormatString(mainPresenter.item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                .foregroundColor(Color.white)
                                .font(.system(size: 10))
                                .foregroundColor(Color.white)
                                .padding([.trailing, .bottom], 2)
                                
                        }
                    }
                    .padding([.top, .leading, .trailing], 4.0)
                    .padding(.bottom, 4.0)
                    .background(Color("msgLeft"))
                    .cornerRadius(6)
                    
                    Spacer()
                    
                    Spacer()
                        .frame(width: 30)
                }
            }
            .onTapGesture {
                self.showBigImage(self.mainPresenter.item)
            }
            .onLongPressGesture(minimumDuration: 0.2) {
                self.clickRemuveItem(self.mainPresenter.item)
            }
        }
    }
    
}

struct T3ItemImg_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemImg(item: MessageRoomItem(), showBigImage: {(MessageRoomItem) -> Void in }, clickRemuveItem: {(MessageRoomItem) -> Void in })
    }
}
