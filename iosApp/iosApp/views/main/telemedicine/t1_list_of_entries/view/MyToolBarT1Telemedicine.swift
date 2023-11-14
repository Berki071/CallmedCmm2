//
//  MyToolBarT1Telemedicine.swift
//  CallMed_ios
//
//  Created by Mihail on 20.06.2023.
//

import SwiftUI

struct MyToolBarT1Telemedicine: View {
    var clickHumburger : (() -> Void)?
    var clickWhatDataShow : ((String) -> Void)?
    var whatDataShow: Binding<String>
    
    @State private var isEditing = false
    
    @State var isShowSearchBtn : Bool = false
    var isShowSearchView : Binding<Bool>? = nil
    var textSearch: Binding<String>?
    
    
    init(clickHumburger : @escaping () -> Void, clickWhatDataShow: @escaping (String) -> Void, whatDataShow: Binding<String>, isShowSearchView: Binding<Bool>, strSerch : Binding<String>){
        self.clickHumburger = clickHumburger
        self.clickWhatDataShow = clickWhatDataShow
        self.whatDataShow = whatDataShow
        self.isShowSearchView = isShowSearchView
        self.textSearch = strSerch
    }
    
    var body: some View {
        ZStack{
            
            if(self.isShowSearchView == nil ||  self.isShowSearchView!.wrappedValue == false){
                
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
                    
                    if(self.isShowSearchBtn){
                        Button {
                            self.isShowSearchView!.wrappedValue = true
                        } label: {
                            Image("search")
                                .foregroundColor(Color.white)
                                .padding(.trailing, 12.0)
                                .imageScale(.large)
                        }
                    }
                    
                    if(self.whatDataShow.wrappedValue == Constants.WhatDataShow.ACTIVE()){
                        Button {
                            self.clickWhatDataShow?(Constants.WhatDataShow.ARCHIVE())
                            self.isShowSearchBtn = true
                        } label: {
                            Image("archive")
                                .foregroundColor(Color.white)
                                .padding(.trailing, 12.0)
                                .imageScale(.large)
                        }
                    }else{
                        Button {
                            self.clickWhatDataShow?(Constants.WhatDataShow.ACTIVE())
                            self.isShowSearchBtn = false
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
            }else{
                HStack {
                    
                    //if let textSearch = textSearch {
                        TextField("Поиск ...", text: textSearch!)
                            .padding(7)
                            .padding(.horizontal, 25)
                            .background(Color(.systemGray6))
                            .cornerRadius(8)
                            .overlay(
                                HStack {
                                    Image(systemName: "magnifyingglass")
                                        .foregroundColor(.gray)
                                        .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
                                        .padding(.leading, 8)
                                    
                                    if isEditing {
                                        Button(action: {
                                            self.textSearch?.wrappedValue =  ""
                                        }) {
                                            Image(systemName: "multiply.circle.fill")
                                                .foregroundColor(.gray)
                                                .padding(.trailing, 8)
                                        }
                                    }
                                }
                            )
                            .padding(.leading, 7)
                            .onTapGesture {
                                self.isEditing = true
                            }
                    //}
                    
                   
                        Button(action: {
                            self.textSearch?.wrappedValue = ""
                            self.isEditing = false
                            delayChangeShowSearchView()
                        }) {
                            Text("Отмена")
                                .foregroundColor(Color.white)
                                .padding(.all, 7.0)
                                .background(Color("black_bg25"))
                                .cornerRadius(4)
                        }
                        .padding(.trailing, 7)
                        .transition(.move(edge: .trailing))
                        .animation(.default)
                        
                  
                }
            }
            
        }
        .frame(height: 44.0)
        .background(Color("color_primary"))
    }
    
    
    private func delayChangeShowSearchView() {
         // Delay seconds
         DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
             if(self.isShowSearchView != nil){
                 self.isShowSearchView!.wrappedValue = false
             }
         }
     }
}

struct MyToolBarT1Telemedicine_Previews: PreviewProvider {
    @State static var whatDataShow: String = Constants.WhatDataShow.ACTIVE()
    @State static private var na2 : String = ""
    @State static private var na3 : Bool = false
    
    static var previews: some View {
        MyToolBarT1Telemedicine(clickHumburger: {() -> Void in }, clickWhatDataShow: {(String) -> Void in }, whatDataShow: $whatDataShow, isShowSearchView: $na3, strSerch: $na2 )
    }
}
