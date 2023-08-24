//
//  T3ItemRecordAudio.swift
//  iosApp
//
//  Created by Михаил Хари on 17.08.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct T3ItemRecordAudio: View {
    @StateObject var mainPresenter: T3ItemRecordAudioPresenter
    var clickRemuveItem: (MessageRoomItem) -> Void
    
    init(item: MessageRoomItem, clickRemuveItem: @escaping  (MessageRoomItem) -> Void){
        _mainPresenter = StateObject(wrappedValue: T3ItemRecordAudioPresenter(item: item))
        self.clickRemuveItem = clickRemuveItem
    }
    
    var body: some View {
        VStack{
            Spacer()
                .frame(height: 6)
            HStack{
                if(self.mainPresenter.item.otpravitel != "kl"){
                    Spacer()
                        .frame(width: 60)
                    
                    Spacer()
            
                    HStack(spacing: 0){
                        VStack(spacing: 0){
                            if(self.mainPresenter.isShowPlayIamge){
                                Image("play_arrow")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(maxWidth: 40, maxHeight: 40)
                                    .foregroundColor(Color("dark_gray"))
                                    .padding(5)
                                    .onTapGesture {
                                        self.mainPresenter.clickPlay()
                                    }
                            }else{
                                Image("stop")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(maxWidth: 40, maxHeight: 40)
                                    .foregroundColor(Color("dark_gray"))
                                    .padding(5)
                                    .onTapGesture {
                                        self.mainPresenter.clickStop()
                                    }
                            }
                            
                            Spacer()
                        }
                        .padding(.horizontal, 8)
                        
                        VStack(spacing: 0){
                            Spacer()
                                .frame(height: 18)
                            ProgressView(value: self.mainPresenter.progressBarValue, total: 100)
                                .tint(Color("dark_gray"))
                            Spacer()
                            HStack(spacing: 0){
                                Text(self.mainPresenter.duration)
                                    .foregroundColor(Color("text_gray"))
                                    .font(.system(size: 10))
                                    .foregroundColor(Color.white)
                                    .multilineTextAlignment(.center)
                                
                                Spacer()
                                    .frame(height: 4)
                                Text(MDate.getNewFormatString(self.mainPresenter.item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                    .foregroundColor(Color("text_gray"))
                                    .font(.system(size: 10))
                                    .foregroundColor(Color.white)
                                    .multilineTextAlignment(.center)
                            }
                        
                        }
                        .padding(.trailing, 8)
                        
                    }
                    .padding([.top, .leading, .trailing], 6.0)
                    .padding(.bottom, 2.0)
                    .background(Color("msgRight"))
                    .cornerRadius(6)
                    
                }
                
                
                
                if(self.mainPresenter.item.otpravitel == "kl"){
                    Spacer()
                        .frame(width: 8)
        
                    
                    HStack(spacing: 0){
                        VStack(spacing: 0){
                            if(self.mainPresenter.isShowPlayIamge){
                                Image("play_arrow")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(maxWidth: 40, maxHeight: 40)
                                    .foregroundColor(Color("dark_gray"))
                                    .padding(5)
                                    .onTapGesture {
                                        self.mainPresenter.clickPlay()
                                    }
                            }else{
                                Image("stop")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(maxWidth: 40, maxHeight: 40)
                                    .foregroundColor(Color("dark_gray"))
                                    .padding(5)
                                    .onTapGesture {
                                        self.mainPresenter.clickStop()
                                    }
                            }
                            
                            Spacer()
                        }
                        .padding(.horizontal, 8)
                        
                        VStack(spacing: 0){
                            Spacer()
                                .frame(height: 18)
                            ProgressView(value: self.mainPresenter.progressBarValue, total: 100)
                                .tint(Color("dark_gray"))
                            Spacer()
                            HStack(spacing: 0){
                                Text(self.mainPresenter.duration)
                                    .foregroundColor(Color("text_gray"))
                                    .font(.system(size: 10))
                                    .foregroundColor(Color.white)
                                    .multilineTextAlignment(.center)
                                
                                Spacer()
                                    .frame(height: 4)
                                Text(MDate.getNewFormatString(self.mainPresenter.item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                    .foregroundColor(Color("text_gray"))
                                    .font(.system(size: 10))
                                    .foregroundColor(Color.white)
                                    .multilineTextAlignment(.center)
                            }
                        
                        }
                        .padding(.trailing, 8)
                        
                    }
                    .padding([.top, .leading, .trailing], 6.0)
                    .padding(.bottom, 2.0)
                    .background(Color("msgLeft"))
                    .cornerRadius(6)
                    
                    Spacer()
                    
                    Spacer()
                        .frame(width: 60)
                }
            }
            .onTapGesture {}
            .onLongPressGesture(minimumDuration: 0.2) {
                self.clickRemuveItem(self.mainPresenter.item)
            }
        }
        .frame(height: 60)
    }
}

struct T3ItemRecordAudio_Previews: PreviewProvider {
    @State static var msgItem : MessageRoomItem = MessageRoomItem()
   
    
    static var previews: some View {
        T3ItemRecordAudio(item: msgItem, clickRemuveItem: {(MessageRoomItem) -> Void in })
    }
}
