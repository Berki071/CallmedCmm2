//
//  MyToolBar.swift
//  CallMed_ios
//
//  Created by Mihail on 12.12.2022.
//

import SwiftUI

struct MyToolBar: View {
    @State var title : String
    @State var isShowSearchBtn : Bool
    var clickHumburger : (() -> Void)?
    var textSearch: Binding<String>?
    
    var isShowSearchView : Binding<Bool>? = nil
    @State private var isEditing = false
    var isShowHumburgerBtn : Bool = true
    
    var isShowImageFreeLine: Bool
    var isShowShareBtn: (() -> Void)?

 

    init(title1 : String, isShowSearchBtn : Bool, clickHumburger : @escaping () -> Void, strSerch : Binding<String>?, isShowImageFreeLine: Bool = true, isShowShareBtn: (() -> Void)? = nil, isShowSearchView: Binding<Bool>? = nil){
        self.title = title1
        self.isShowSearchBtn = isShowSearchBtn
        self.clickHumburger = clickHumburger
        self.textSearch = strSerch
        self.isShowImageFreeLine = isShowImageFreeLine
        self.isShowShareBtn = isShowShareBtn
        if(isShowSearchView != nil){
            self.isShowSearchView = isShowSearchView
        }
    }
    
    init(title1 : String, clickHumburger : @escaping () -> Void, isShowImageFreeLine: Bool){
        self.title = title1
        self.clickHumburger = clickHumburger
        self.isShowSearchBtn = false
        self.isShowImageFreeLine = isShowImageFreeLine
        self.isShowShareBtn = nil
    }
    init(title1 : String){
        self.title = title1
        self.isShowSearchBtn = false
        self.isShowHumburgerBtn = false
        self.isShowImageFreeLine = true
        self.isShowShareBtn = nil
    }
    
    
    var body: some View {
        ZStack{
            
            if(self.isShowSearchView == nil ||  self.isShowSearchView!.wrappedValue == false){
                
                HStack{
                    let tp = self.isShowHumburgerBtn
                    if (self.isShowHumburgerBtn == true) {
                        Button {
                            self.clickHumburger?()
                        } label: {
                            if(isShowImageFreeLine){
                                Image(systemName: "line.horizontal.3")
                                    .foregroundColor(Color.white)
                                    .padding(.leading, 12.0)
                                    .imageScale(.large)
                            }else{
                                Image(systemName: "arrow.left")
                                    .foregroundColor(Color.white)
                                    .padding(.leading, 12.0)
                                    .imageScale(.large)
                            }
                        }
                    }
                    
                    Spacer()
                    
                    if(isShowSearchBtn){
                        Button {
                            if(self.isShowSearchView != nil){
                                self.isShowSearchView!.wrappedValue = true
                            }
                        } label: {
                            Image("search")
                                .foregroundColor(Color.white)
                                .padding(.trailing, 12.0)
                                .imageScale(.large)
                        }
                    }
                    
                    if(isShowShareBtn != nil){
                        Button {
                            self.isShowShareBtn?()
                        } label: {
                            Image("share_symbol")
                                .foregroundColor(Color.white)
                                .padding(.trailing, 12.0)
                                .imageScale(.large)
                        }
                    }
                }
                
                HStack{
                    Spacer()
                    Text(title)
                        .font(.title2)
                        .foregroundColor(Color.white)
                    
                    Spacer()
                }
                .padding(.horizontal, 34.0)
                
            }else{
                HStack {
                    
                    if let textSearch = textSearch {
                        TextField("Поиск ...", text: textSearch)
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
                    }
                    
                   // if isEditing {
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
                        
                    //}
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

struct MyToolBar_Previews: PreviewProvider {
    @State static private var na = "TEstTitle"
    @State static private var na2 : String = "" //$na2
    
    static var previews: some View {
        MyToolBar(title1: na, isShowSearchBtn: true, clickHumburger: {() -> Void in }, strSerch: $na2, isShowImageFreeLine: false, isShowShareBtn: {() -> Void in } )
        //MyToolBar(title1: "my test")
    }
}
