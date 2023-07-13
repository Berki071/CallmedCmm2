//
//  MenuView.swift
//  iosApp
//
//  Created by Михаил Хари on 08.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI


struct MenuView: View {
    var selectitem : Binding<Int>
    var selectMenuAlert : Binding<Int>
    var showMenu : Binding<Bool>
    
    var sharePreferenses : SharedPreferenses

    
    init(selectitem : Binding<Int>, selectMenuAlert: Binding<Int>, showMenu : Binding<Bool>){
        self.selectitem = selectitem
        self.selectMenuAlert = selectMenuAlert
        self.showMenu = showMenu

        sharePreferenses = SharedPreferenses()
    }
    
    var body: some View {
        ZStack{
            VStack{
                Color("black_bg")
            }
            
            HStack(spacing: 0){
                VStack(alignment: .leading, spacing: 0) {
                    
                    let topCameraPading = UIDevice.modelName
                    if(topCameraPading != "-"){
                        HStack{
                            Spacer()
                                .frame(height: 40.0)
                        }
                        .background(Color("color_primary"))
                    }else{
                        HStack{
                            Spacer()
                                .frame(height: 25.0)
                        }
                        .background(Color("color_primary"))
                    }
                    
                    TopBar()
                    
                    MenuItemSidebar(selectitem: selectitem, selectMenuAlert: selectMenuAlert, showMenuFalse : {() -> Void in
                        showMenu.wrappedValue = false
                    })
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(.white)
                
                VStack{
                    Color("black_bg")
                }
                .frame(width: 2.0)
                
                VStack{
                    Color("transparent")
                }
                .frame(width: 70.0)
                .contentShape(Rectangle())
                .onTapGesture {
                    showMenu.wrappedValue = false
                }
            }
            .animation( Animation.easeInOut(duration: 0.4))
            
        }
        .edgesIgnoringSafeArea(.all)
        .frame(maxWidth: .infinity)
        
    }
}

struct MenuView_Previews: PreviewProvider {
    @State static private var na = 1
    @State static private var no = false
    @State static private var ni = 0
    @State static private var trr : UserResponse = UserResponse()
    //selectMenuAlert нажатие на элемент где реакция показ алерта
    
    static var previews: some View {
        MenuView(selectitem: $na, selectMenuAlert: $ni, showMenu: $no)
    }
}
