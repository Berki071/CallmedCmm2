//
//  T3ItemFile.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct T3ItemFile: View {
    var item: MessageRoomItem
    var showBigDoc: (MessageRoomItem) -> Void
    var clickRemuveItem: (MessageRoomItem) -> Void
    
    var body: some View {
        VStack{
            Spacer()
                .frame(height: 6)
            HStack{
                if(item.otpravitel != "kl"){
                    Spacer()
                        .frame(width: 30)
                    
                    Spacer()
                    
                    VStack(alignment: .trailing, spacing: 0){
                        
                        HStack{
                            Image("picture_pdf")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(maxWidth: 40, maxHeight: 40)
                                .foregroundColor(Color.white)
                                .padding(.top, 2)
                            
                            Text(item.text!)
                                .font(.system(size: 15))
                                .foregroundColor(Color.black)
                            
                            Spacer()
                        }
                        
                        if(item.data != nil && !item.data!.isEmpty && item.idMessage != nil){
                            Spacer()
                                .frame(height: 4)
                            
                            Text(MDate.getNewFormatString(item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                .foregroundColor(Color("text_gray"))
                                .font(.system(size: 10))
                                .foregroundColor(Color.white)
                                .multilineTextAlignment(.center)
                                
                        }
                    }
                    .padding([.top, .leading, .trailing], 6.0)
                    .padding(.bottom, 2.0)
                    .background(Color("msgRight"))
                    .cornerRadius(6)
                    
                    Spacer()
                        .frame(width: 8)
                }
                
                
                
                if(item.otpravitel == "kl"){
                    Spacer()
                        .frame(width: 8)
                    
                    VStack(alignment: .leading, spacing: 0){
                        
                        HStack{
                            Image("picture_pdf")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(maxWidth: 40, maxHeight: 40)
                                .foregroundColor(Color.white)
                                .padding(.top, 2)
                            
                            Text(item.text!)
                                .font(.system(size: 15))
                                .foregroundColor(Color.black)
                            
                            Spacer()
                        }
                        
                        if(item.data != nil && !item.data!.isEmpty && item.idMessage != nil){
                            Spacer()
                                .frame(height: 4)
                            
                            Text(MDate.getNewFormatString(item.data!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm))
                                .foregroundColor(Color("text_gray"))
                                .font(.system(size: 10))
                                .foregroundColor(Color.white)
                                .multilineTextAlignment(.center)
                                
                        }
                    }
                    .padding([.top, .leading, .trailing], 6.0)
                    .padding(.bottom, 2.0)
                    .background(Color("msgLeft"))
                    .cornerRadius(6)
                    
                    Spacer()
                    
                    Spacer()
                        .frame(width: 30)
                }
            }
            .onTapGesture {
                self.showBigDoc(item)
            }
            .onLongPressGesture(minimumDuration: 0.2) {
                self.clickRemuveItem(item)
            }
        }
    }
}

struct T3ItemFile_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemFile(item: MessageRoomItem(), showBigDoc: {(MessageRoomItem) -> Void in }, clickRemuveItem: {(MessageRoomItem) -> Void in })
    }
}

