//
//  T3ItemMsg.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct T3ItemMsg: View {
    var item: MessageRoomItem
    var clickRemuveItem: (MessageRoomItem) -> Void
    
    var body: some View {
        HStack{
            if(item.otpravitel != "kl"){
                Spacer()
                    .frame(width: 30)
                
                Spacer()
                
                VStack(alignment: .trailing){
                    Text(item.text!)
                        .font(.system(size: 15))
                        .foregroundColor(Color.black)
                    
                        if(item.data != nil && !item.data!.isEmpty && item.idMessage != nil){
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
                
                VStack(alignment: .leading){
                    Text(item.text!)
                        .font(.system(size: 15))
                        .foregroundColor(Color.black)
                    
                        if(item.data != nil && !item.data!.isEmpty && item.idMessage != nil){
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
        .onLongPressGesture(minimumDuration: 0.2) {
            self.clickRemuveItem(item)
        }
      
    }
}

struct T3ItemMsg_Previews: PreviewProvider {
    static var previews: some View {
        T3ItemMsg(item: MessageRoomItem(), clickRemuveItem: {(MessageRoomItem) -> Void in})
    }
}
