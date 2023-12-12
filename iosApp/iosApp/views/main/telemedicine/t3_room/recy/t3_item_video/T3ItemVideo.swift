//
//  T3ItemVideo.swift
//  iosApp
//
//  Created by Михаил Хари on 28.11.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import AVKit

struct T3ItemVideo: View {
    @StateObject var mainPresenter: T3ItemVideoPresenter
    var clickRemuveItem: (MessageRoomResponse.MessageRoomItem) -> Void
    var pub = NotificationCenter.default.publisher(for: .AVPlayerItemDidPlayToEndTime)

    
    init(item: MessageRoomResponse.MessageRoomItem, clickRemuveItem: @escaping  (MessageRoomResponse.MessageRoomItem) -> Void, idRoom: String,  showAlert: @escaping  (String, String) -> Void){
       // print(">>>>>>> T3ItemVideo.init \(item.idMessage)")
        
        _mainPresenter = StateObject(wrappedValue: T3ItemVideoPresenter(item: item, idRoom: idRoom, showAlert: showAlert))
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
                    ZStack(alignment: .bottom){
                        if(self.mainPresenter.player == nil){
                            if(self.mainPresenter.iuImageLogo != nil ){
                                GeometryReader { proxy in
                                    ZStack(alignment: .bottom){
                                        Image(uiImage: self.mainPresenter.iuImageLogo!)
                                            .resizable()
                                            .cornerRadius( 6)
                                            .scaledToFit()
                                            .frame(width: self.mainPresenter.widthVideoPlayer, height: self.mainPresenter.heightVideoPlayer)
                                    }
                                    .frame(width: 280, height: 280, alignment: .center)
                                }
                                .frame(width: 280, height: 280, alignment: .center)
                                .cornerRadius(140)
                                
                            }else{
                                Image("videocam")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .padding(46)
                                    .frame(width: 280,  height: 280, alignment: .center)
                                    
                            }
                        }else{
                            GeometryReader { proxy in
                                ZStack(alignment: .bottom){
                                    VideoPlayer(player: self.mainPresenter.player!)
                                        .onReceive(pub) { (output) in
                                            //print(" >>>>>>>>>>Video Finished")
                                            self.mainPresenter.finishShowTimePlayback()
                                        }
                                        .allowsHitTesting(false)
                                        .frame(width: self.mainPresenter.widthVideoPlayer, height: self.mainPresenter.heightVideoPlayer)
                                }
                                .frame(width: 280, height: 280, alignment: .center)
//                                .onTapGesture {
//                                    self.mainPresenter.cklickVideo()
//                                }
                            }
                            .frame(width: 280, height: 280, alignment: .center)
                            .cornerRadius(140)
                        }
                                   
                    
                        HStack{
                            if(self.mainPresenter.isShowTimer){
                                Text(self.mainPresenter.tiemerTime)
                                    .foregroundColor(.black)
                                    .font(.system(size: 10))
                                    .padding([.leading, .bottom], 2)
                            }
                            
                            Spacer()
                            
                            if(mainPresenter.item.data != nil && !mainPresenter.item.data!.isEmpty && mainPresenter.item.idMessage != nil){
                                Text(MDate.getNewFormatString(mainPresenter.item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                    .foregroundColor(.black)
                                    .font(.system(size: 10))
                                    .padding([.trailing, .bottom], 2)
                            }
                        }
                        .frame(width: 280)
                    }
                    .padding(8)
                    
                    
                    Circle()
                        .strokeBorder(mainPresenter.item.otpravitel != "kl" ? Color("msgRight") : Color("msgLeft"), lineWidth: 4)
                        .frame(width: 288, height: 288)
                    
                    if(mainPresenter.isShowLoad){
                        ZStack{
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
                                .foregroundColor(Color("color_primary"))
                        }
                        .frame(width: 288, height: 288)
                        .background(Color("black_bg"))
                        .cornerRadius(148)
                    }
                    
          
                }
                .background(Circle().fill(.white))
                .frame(width: 288, height: 288)
               //.cornerRadius(148)
             
                
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
        //.background(.red)
        .onTapGesture {
            self.mainPresenter.cklickVideo()
        }
        .onLongPressGesture(minimumDuration: 0.2) {
            if(self.mainPresenter.item.text != nil && !self.mainPresenter.item.text!.isEmpty){
                self.clickRemuveItem(self.mainPresenter.item)
            }
        }
    }
}

struct T3ItemVideo_Previews: PreviewProvider {
    @State static var msgItem : MessageRoomResponse.MessageRoomItem = MessageRoomResponse.MessageRoomItem()
    
    static var previews: some View {
        T3ItemVideo(item: msgItem, clickRemuveItem: {(MessageRoomItem) -> Void in }, idRoom: "1", showAlert: {(i: String, j: String) -> Void in })
    }
}


