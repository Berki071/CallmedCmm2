//
//  UserBar.swift
//  Medhelp
//
//  Created by Mihail on 17.11.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared


struct TopBar: View {
    var sharePreferenses : SharedPreferenses
    
    @State var curentCenterInfo : CenterResponse.CenterItem
    @State var currenDocInfo: DoctorItem
    @State var currentUserInfo: UserResponse.UserItem
    
    @State var iuImageLogo : UIImage =  UIImage(named: "sh_center1")!

    init(){
        sharePreferenses = SharedPreferenses()
        curentCenterInfo = sharePreferenses.currentCenterInfo ?? CenterResponse.CenterItem()
        currenDocInfo = sharePreferenses.currentDocInfo ?? DoctorItem()
        currentUserInfo = sharePreferenses.currentUserInfo ?? UserResponse.UserItem()
    }
    
    func loadImg(){
        if(curentCenterInfo != nil && curentCenterInfo.logo != nil){
            let imagePathString = curentCenterInfo.logo! + "&token=" + currentUserInfo.apiKey!
            
            DownloadManager(imagePathString,  resultUiImage: {(tmp : UIImage) -> Void in
                iuImageLogo = tmp
            })
        }
    }
    
    var body: some View {
        let _ = loadImg()
        
        VStack(spacing: 0){
            Spacer()
            
            HStack{
                Spacer()
            }
            
            //log test
            Image(uiImage: self.iuImageLogo)
                .resizable()
                .scaledToFit()
                .padding(.leading, 8.0)
                .padding(.vertical, 8.0)
                .frame(width: 180.0, height: 90.0)
            
            VStack{
           
                HStack{
                    Spacer()
                        .frame(width: 12.0)
                    
                    Image("account_balance")
                        .imageScale(.large)
                        .foregroundColor(Color.white)
                        .font(Font.system(size: 14, weight: .bold))
                    
                    Text(curentCenterInfo.title ?? "dasdfafasdfasfa")
                        .font(.system(size: 18))
                        .foregroundColor(Color.white)
                        .multilineTextAlignment(.center)
                    
                    Spacer()
                }
                    
                Spacer()
                    .frame(height: 4.0)
                
                HStack{
                    Spacer()
                        .frame(width: 12.0)
                    
                    Image("person_pin")
                        .imageScale(.large)
                        .foregroundColor(Color.white)
                        .font(Font.system(size: 14, weight: .bold))
                    
                    Text(currenDocInfo.full_name ?? "asdfasfdasdfasdf")
                        .foregroundColor(Color.white)
                        .multilineTextAlignment(.center)
                        .font(.system(size: 14))
                    
                    Spacer()
                }
                Spacer()
                    .frame(height: 12.0)
            }
            Spacer()
        }
        .frame(height: 140.0)
        .background(Color("color_primary"))
    }
    
    
}

struct TopBar_Previews: PreviewProvider {
    static var previews: some View {
        TopBar()
    }
}
