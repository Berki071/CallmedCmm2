//
//  BottombarTelemedicine.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import UIKit
import shared

struct BottombarTelemedicineListener {
    var sendMsg: (String) -> Void
    var sendRecordMsg: (String) -> Void
    var showAlertMsg: (String, String) -> Void
    
    var makePhoto: () -> Void
    var selectFileFromPhotoLibrary: () -> Void
    var selectFileFromOtherPlace: () -> Void
}
struct TVShow: Identifiable {
    var id: String { name }
    let name: String
}


struct BottombarTelemedicine: View {
    //@ObservedObject var mainPresenter: BottombarTelemedicinePresenter
    @StateObject var mainPresenter: BottombarTelemedicinePresenter
    @ObservedObject var mainPresenter2: BottombarTelemedicinePresenter2
    

    init(item: Binding<AllRecordsTelemedicineItem?>, listener: BottombarTelemedicineListener){
        _mainPresenter = StateObject(wrappedValue: BottombarTelemedicinePresenter(item: item, listener: listener))
        mainPresenter2 = BottombarTelemedicinePresenter2(item: item)
        
    }
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                Divider()
                    .frame(height: 2.0)
                    .overlay(Color("lightGray"))
                
                HStack(spacing: 0){
                    Spacer()
                        .frame(width: 8.0)
                    
                    ZStack{
                        HStack{
                            TextField("Введите сообщение", text: self.$mainPresenter.textFild, axis: .vertical)
                        }
                        
                        
                        if(self.mainPresenter.isButtonRecordPressed){
                            HStack(spacing: 0){
                                Spacer()
                                Image("chevron_left")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(width: 20, height: 20)
                                    .foregroundColor(Color("semi_gray"))
                                
                                Text("Проведите, чтобы отменить")
                                    .foregroundColor(Color("semi_gray"))
                                    .font(.system(size: 12))
                                
                                Spacer()
                                    .frame(width: 14.0)
                            }
                        }
                    }
                
                    
                    if(self.mainPresenter.textFild == "" && !self.mainPresenter.isButtonRecordPressed){
                        Image("make_photo")
                            .resizable()
                            .foregroundColor(Color("semi_gray"))
                            .padding(13.0)
                            .frame( width: 54.0, height: 52.0)
                            .scaledToFit()
                            .onTapGesture {
                                self.mainPresenter.listener?.makePhoto()
                            }
                        
                        Menu {
                            Button("Весь телефон", action: self.mainPresenter.selectFileFromOtherPlace)
                            Button("Библиотека фотографий", action: self.mainPresenter.selectFileFromPhotoLibrary)
                          
                        } label: {
                            Label {} icon: {
                                Image("show_library")
                                    .resizable()
                                    .foregroundColor(Color("semi_gray"))
                                    //.padding(13.0)
                                    //.frame( width: 52.0, height: 52.0)
                            }
                            .ignoresSafeArea(.keyboard)
                            .padding(13.0)
                            .frame( width: 52.0, height: 52.0)
                            //.background(Color.green)
                        }
                        .frame( width: 52.0, height: 52.0)
                        //.background(Color.red)
                    }
                    
                    Spacer()
                        .frame(width: 5.0)
                      
                    if(self.mainPresenter.isShowRecordBtn){
                        Image("mic")
                            .resizable()
                            .foregroundColor(Color.white)
                            .padding(12.0)
                            //.padding(.leading , 6)
                            .frame( width: 44.0, height: 44.0)
                            .background(Color.green)
                            .clipShape(
                                RoundedRectangle(cornerRadius: 21)
                            )
                            .gesture(DragGesture(minimumDistance: 3.0, coordinateSpace: .local)
                                .onEnded { value in
                                    if value.translation.width < -190 && value.translation.height > -30 && value.translation.height < 30 {
                                        self.mainPresenter.cancelRecord()
                                        //print(">>>>> cancelRecord (left swipe) \(value.translation.width)")
                                    }
                                }
                            )
                            .simultaneousGesture(
                                DragGesture(minimumDistance: 0)
                                    .onChanged({ _ in
                                        self.mainPresenter.setIsButtonPressed(true)
                                        //print(">>>>> onTapGesture onChanged")
                                    })
                                    .onEnded({ _ in
                                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
                                            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                                        }
                                        
                                        self.mainPresenter.setIsButtonPressed(false)
                                        //print(">>>>> onTapGesture onEnded")
                                    })
                            )
                    }else{
                        
                        Image("send")
                            .resizable()
                            .foregroundColor(Color.white)
                            .padding(10.0)
                            .padding(.leading , 6)
                            .frame( width: 44.0, height: 44.0)
                            .background(Color.green)
                            .clipShape(
                                RoundedRectangle(cornerRadius: 21)
                            )
                            .onTapGesture {
                                
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
                                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                                }
                                
                                self.mainPresenter.clickSendMsg()
                            }
                    }
                    
                    
                    Spacer()
                        .frame(width: 5.0)
                    
                }
                .frame(minHeight: 52.0)
                //.background(Color.green)
            }
            .background(Color.white)
            
            if(self.mainPresenter2.isDisableChat){
                HStack{
                    Spacer()
                    Text("Чат неактивен")
                        .foregroundColor(Color.white)
                        .fontWeight(.bold)
                        .font(.system(size: 20))
                        .onTapGesture{
                            self.mainPresenter.listener?.showAlertMsg("Внимание!","Чат неактивен, отправка сообщений заблокирована")
                        }
                    Spacer()
                }
                .frame(height: 54.0)
                .background(Color("black_bg65"))
  
            }
            
        }
        .alert(item: self.$mainPresenter.selectedShow) { show in
              Alert(title: Text(""), message: Text(show.name), dismissButton: .default(Text("Ok")))
          }
    }
}

struct BottombarTelemedicine_Previews: PreviewProvider {
    @State static var item: AllRecordsTelemedicineItem? = AllRecordsTelemedicineItem()
    static let list = BottombarTelemedicineListener(sendMsg: {(String) -> Void in }, sendRecordMsg: {(String) -> Void in }, showAlertMsg: {(i: String, j: String) -> Void in}, makePhoto: {()->Void in }, selectFileFromPhotoLibrary: {()->Void in }, selectFileFromOtherPlace: {()->Void in })
    
    static var previews: some View {
        BottombarTelemedicine(item: $item, listener: list)
    }
}
