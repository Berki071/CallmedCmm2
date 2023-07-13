//
//  ShowBigTextAlert.swift
//  CallMed_ios
//
//  Created by Mihail on 19.06.2023.
//

import SwiftUI

struct ShowBigTextAlertData{
    let title : String
    let text: String
    let clickClose: (() -> Void)
}

struct ShowBigTextAlert: View {
    var dataOb: ShowBigTextAlertData
    
    var body: some View {
        ZStack{
           Color("black_bg")
           
           ZStack{
               VStack{
                   MyToolBar(title1: dataOb.title)
                   
                   ScrollView {
                       Text(dataOb.text)
                           .font(.system(size: 15))
                           .multilineTextAlignment(.leading)
                           .padding(4)
                   }
                   .frame(maxHeight: 400)
                   
                   HStack{
                       Button(action: {
                           dataOb.clickClose()
                       }) {
                           Text("OK")
                             .frame(minWidth: 100, maxWidth: .infinity, minHeight: 44, maxHeight: 44, alignment: .center)
                             .foregroundColor(Color.white)
                             .background(Color("color_primary"))
                             .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                             .font(.system(size: 16))
                       }
                   }
                   .padding([.leading, .bottom, .trailing])
                   
                   
               }
               
//               VStack(spacing: 0){
//                   //44.0
//                   MyToolBar(title1: dataOb.title, isShowSearchBtn: false, clickHumburger: {() -> Void in}, strSerch: nil)
//                   Spacer()
//               }
           }
           //.frame(height: 200.0)
           .background(Color(.white))
           .cornerRadius(6)
           .padding([.leading, .bottom, .trailing], 16.0)
           
           
       }
       .ignoresSafeArea()
    }
}

struct ShowBigTextAlert_Previews: PreviewProvider {
    static let dataOb =  ShowBigTextAlertData(title: "title", text: "text", clickClose: {() -> Void in })
    static var previews: some View {
        ShowBigTextAlert(dataOb: dataOb)
    }
}
