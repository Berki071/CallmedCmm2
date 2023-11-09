//
//  T3ItemImg.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared
import shared

struct T3ItemImg: View {
    @StateObject var mainPresenter: T3ItemImgPresenter
    
    var showBigImage: (MessageRoomResponse.MessageRoomItem) -> Void
    var clickRemuveItem: (MessageRoomResponse.MessageRoomItem) -> Void
    
    @State var size: CGSize = .zero
    
    init(item: MessageRoomResponse.MessageRoomItem, showBigImage: @escaping (MessageRoomResponse.MessageRoomItem) -> Void, clickRemuveItem: @escaping  (MessageRoomResponse.MessageRoomItem) -> Void, idRoom: String,  showAlert: @escaping  (String, String) -> Void){
        _mainPresenter = StateObject(wrappedValue:T3ItemImgPresenter(item: item, idRoom: idRoom, showAlert: showAlert))
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
                }else{
                    Spacer()
                        .frame(width: 8)
                }
                
                ZStack{
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
                            //.foregroundColor(Color.white)
                                .padding([.trailing, .bottom], 2)
                            
                        }
                    }
                    .padding(4)
                    .background(mainPresenter.item.otpravitel != "kl" ? Color("msgRight") : Color("msgLeft"))
                    .cornerRadius(6)
                    .saveSize(in: $size)
                    
                    if(mainPresenter.isShowLoad){
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
          
                
            
                
                if(mainPresenter.item.otpravitel != "kl"){
                    Spacer()
                        .frame(width: 8)
                }else{
                    Spacer()
                    
                    Spacer()
                        .frame(width: 30)
                }
                
            }
            
        }
        .onTapGesture {
            if(self.mainPresenter.item.text != nil && !self.mainPresenter.item.text!.isEmpty){
                self.showBigImage(self.mainPresenter.item)
            }
        }
        .onLongPressGesture(minimumDuration: 0.2) {
            if(self.mainPresenter.item.text != nil && !self.mainPresenter.item.text!.isEmpty){
                self.clickRemuveItem(self.mainPresenter.item)
            }
        }
    }
    
}
    


struct SizeCalculator: ViewModifier {
    
    @Binding var size: CGSize
    
    func body(content: Content) -> some View {
        content
            .background(
                GeometryReader { proxy in
                    Color.clear // we just want the reader to get triggered, so let's use an empty color
                        .onAppear {
                            size = proxy.size
                        }
                }
            )
    }
}
extension View {
    func saveSize(in size: Binding<CGSize>) -> some View {
        modifier(SizeCalculator(size: size))
    }
}
    


struct T3ItemImg_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemImg(item: MessageRoomResponse.MessageRoomItem(), showBigImage: {(MessageRoomItem) -> Void in }, clickRemuveItem: {(MessageRoomItem) -> Void in }, idRoom: "1", showAlert: {(i: String, j: String) -> Void in })
    }
}
