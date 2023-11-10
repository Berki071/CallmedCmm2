//
//  T3ItemFile.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct T3ItemFile: View {
    @StateObject var mainPresenter: T3ItemFilePresenter
    
    var showBigDoc: (MessageRoomResponse.MessageRoomItem) -> Void
    var clickRemuveItem: (MessageRoomResponse.MessageRoomItem) -> Void
    
    @State var size: CGSize = .zero
    
    init(item: MessageRoomResponse.MessageRoomItem, showBigDoc: @escaping (MessageRoomResponse.MessageRoomItem) -> Void, clickRemuveItem: @escaping (MessageRoomResponse.MessageRoomItem) -> Void , idRoom: String,  showAlert: @escaping  (String, String) -> Void){
        _mainPresenter = StateObject(wrappedValue: T3ItemFilePresenter(item: item, idRoom: idRoom, showAlert: showAlert))
        self.clickRemuveItem = clickRemuveItem
        self.showBigDoc = showBigDoc
    }
    
    var body: some View {
        VStack{
            Spacer()
                .frame(height: 6)
            HStack{
                if(self.mainPresenter.item.otpravitel != "kl"){
                    Spacer()
                        .frame(width: 30)
                    
                    Spacer()
                }else{
                    Spacer()
                        .frame(width: 8)
                }
                    
                ZStack{
                    VStack(alignment: .trailing, spacing: 0){
                        HStack{
                            Image("picture_pdf")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(maxWidth: 40, maxHeight: 40)
                                .foregroundColor(Color.white)
                                .padding(.top, 2)
                            
                            Text(self.mainPresenter.nameF)
                                .lineLimit(2)
                                .font(.system(size: 15))
                                .foregroundColor(Color.black)
                            
                            
                            
                            Spacer()
                        }
                        
                        if(self.mainPresenter.item.data != nil && !self.mainPresenter.item.data!.isEmpty && self.mainPresenter.item.idMessage != nil){
                            Spacer()
                                .frame(height: 4)
                            
                            Text(MDate.getNewFormatString(self.mainPresenter.item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                .foregroundColor(Color("text_gray"))
                                .font(.system(size: 10))
                                .foregroundColor(Color.white)
                                .multilineTextAlignment(.center)
                            
                        }
                    }
                    .padding([.top, .leading, .trailing], 6.0)
                    .padding(.bottom, 2.0)
                    .background(self.mainPresenter.item.otpravitel != "kl" ? Color("msgRight") : Color("msgLeft"))
                    .cornerRadius(6)
                    .saveSize(in: $size)
                    
                    
                    if(self.mainPresenter.isShowLoad){
                        ZStack{
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
                                .foregroundColor(Color("color_primary"))
                            //.id(UUID())
                        }
                        .frame(width: size.width,  height: size.height)
                        .background(Color("black_bg"))
                        .cornerRadius(6)
                    }
                }
                
                
                if(self.mainPresenter.item.otpravitel != "kl"){
                    Spacer()
                        .frame(width: 8)
                }else{
                    Spacer()
                    
                    Spacer()
                        .frame(width: 30)
                }
                
            }
            .onTapGesture {
                if(self.mainPresenter.item.text != nil && !self.mainPresenter.item.text!.isEmpty && self.mainPresenter.item.text != "null"){
                    self.showBigDoc(self.mainPresenter.item)
                }
            }
            .onLongPressGesture(minimumDuration: 0.2) {
                if(self.mainPresenter.item.text != nil && !self.mainPresenter.item.text!.isEmpty){
                    self.clickRemuveItem(self.mainPresenter.item)
                }
            }
        }
    }
}

struct T3ItemFile_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemFile(item: MessageRoomResponse.MessageRoomItem(), showBigDoc: {(MessageRoomItem) -> Void in }, clickRemuveItem: {(MessageRoomItem) -> Void in }, idRoom: "1", showAlert: {(i: String, j: String) -> Void in })
    }
}

