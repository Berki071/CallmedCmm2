//
//  MyToolBarT1Telemedicine.swift
//  CallMed_ios
//
//  Created by Mihail on 20.06.2023.
//

import SwiftUI

struct MyToolBarT1Telemedicine: View {
    //let title : String = "Рабочие чаты"
    var clickHumburger : (() -> Void)?
    var clickWhatDataShow : ((String) -> Void)?
    var whatDataShow: Binding<String>
    
    init(clickHumburger : @escaping () -> Void, clickWhatDataShow: @escaping (String) -> Void, whatDataShow: Binding<String>){
        self.clickHumburger = clickHumburger
        self.clickWhatDataShow = clickWhatDataShow
        self.whatDataShow = whatDataShow
    }
    
    var body: some View {
        ZStack{
            HStack{
                Button {
                    self.clickHumburger?()
                } label: {
                    Image(systemName: "line.horizontal.3")
                        .foregroundColor(Color.white)
                        .padding(.leading, 12.0)
                        .imageScale(.large)
                }
                
                
                Spacer()
                
                if(self.whatDataShow.wrappedValue == Constants.WhatDataShow.ACTIVE()){
                    Button {
                        self.clickWhatDataShow?(Constants.WhatDataShow.ARCHIVE())
                    } label: {
                        Image("archive")
                            .foregroundColor(Color.white)
                            .padding(.trailing, 12.0)
                            .imageScale(.large)
                    }
                }else{
                    Button {
                        self.clickWhatDataShow?(Constants.WhatDataShow.ACTIVE())
                    } label: {
                        Image("unarchive")
                            .foregroundColor(Color.white)
                            .padding(.trailing, 12.0)
                            .imageScale(.large)
                    }
                }
                
            }
            
            HStack{
                Spacer()
                if(self.whatDataShow.wrappedValue == Constants.WhatDataShow.ACTIVE()){
                    Text("Рабочие чаты")
                        .font(.title2)
                        .foregroundColor(Color.white)
                }else{
                    Text("Архивные чаты")
                        .font(.title2)
                        .foregroundColor(Color.white)
                }
                
                Spacer()
            }
            .padding(.horizontal, 34.0)
            
            
        }
        .frame(height: 44.0)
        .background(Color("color_primary"))
    }
}

struct MyToolBarT1Telemedicine_Previews: PreviewProvider {
    @State static var whatDataShow: String = Constants.WhatDataShow.ACTIVE()
    static var previews: some View {
        MyToolBarT1Telemedicine(clickHumburger: {() -> Void in }, clickWhatDataShow: {(String) -> Void in }, whatDataShow: $whatDataShow)
    }
}
