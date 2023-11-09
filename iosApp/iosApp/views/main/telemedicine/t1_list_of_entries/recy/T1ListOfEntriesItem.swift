//
//  T1ListOfEntriesItem.swift
//  CallMed_ios
//
//  Created by Mihail on 19.06.2023.
//

import SwiftUI
import shared

struct T1ListOfEntriesItem: View {
    //@Binding var item : AllRecordsTelemedicineItem
    var item : AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
    var listener: T1ListOfEntriesItemListener? = nil
    
    @ObservedObject var presenter : T1ListOfEntriesItemPresenter
    
//    init(item : Binding<AllRecordsTelemedicineItem>, listener : T1ListOfEntriesItemListener?){
//        self._item = item
//        self.listener = listener
//        
//        presenter = T1ListOfEntriesItemPresenter(item: item, listener: listener)
//    }
    init(item : AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem, listener : T1ListOfEntriesItemListener?){
        self.item = item
        self.listener = listener
        
        presenter = T1ListOfEntriesItemPresenter(item: item, listener: listener)
    }
    
    var body: some View {
        
        ZStack{
            VStack(spacing: 0){
                if(item.status == Constants.TelemedicineStatusRecord.complete()){
                    Spacer()
                        .frame(height: 3.0)
                }
                
                VStack(spacing: 0){
                    HStack(spacing: 0){
                        VStack{
                            
                            VStack(alignment: .center){
                                Text(self.presenter.stringToInitials(str: self.presenter.item.fullNameKl!))
                                    .font(.system(size: 22))
                                    .foregroundColor(Color.white)
                            }
                            .frame(width: 50.0, height: 50.0)
                            .background(self.presenter.getColor(status: self.presenter.item.status!))
                            .cornerRadius(27)
                            .padding(.horizontal, 8)
                            
                            
                            Spacer()
                        }
                        
                        
                        VStack(spacing: 0){
                            HStack{
                                Text(item.fullNameKl!)
                                    .fontWeight(.bold)
                                Spacer()
                            }
                            .padding(.trailing, 8.0)
                            .padding(.bottom, 1.0)
                            
                            if(item.tmName != nil){
                                HStack{
                                    Text("Тариф: ")
                                        .fontWeight(.bold)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13)) +
                                    Text(item.tmName!)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13))
                                    
                                    Spacer()
                                }
                                .padding(.trailing, 8.0)
                                .padding(.vertical, 1.0)
                                
                            }
                            
                            if(item.dataStart != nil && item.status == Constants.TelemedicineStatusRecord.complete()){
                                HStack{
                                    Text("Дата: ")
                                        .fontWeight(.bold)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13)) +
                                    Text(self.presenter.dateTimeToDate(item.dataStart!))
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13))
                                    
                                    Spacer()
                                }
                                .padding(.trailing, 8.0)
                                .padding(.vertical, 1.0)
                                
                            }
                            
                            if(item.specialty != nil && item.status != Constants.TelemedicineStatusRecord.complete()){
                                HStack{
                                    Text("Специальность: ")
                                        .fontWeight(.bold)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13)) +
                                    Text(item.specialty!)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13))
                                    
                                    Spacer()
                                }
                                .padding(.trailing, 8.0)
                                .padding(.vertical, 1.0)
                            }
                            
                            if(self.presenter.isShowTimeLeft){
                                HStack{
                                    Image("circle.fill")
                                        .resizable(resizingMode: .stretch)
                                        .foregroundColor(Color("deepRed"))
                                        .frame(width: 8.0, height: 8.0)
                                    
                                    Text(self.presenter.titleTimer)
                                        .fontWeight(.bold)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13)) +
                                    Text(self.presenter.showTimeTimer)
                                        .foregroundColor(Color("text_gray"))
                                        .font(.system(size: 13))
                                    
                                    Spacer()
                                }
                                .padding(.trailing, 8.0)
                                .padding(.bottom, 8.0)
                            }
                        }
                        //.background(Color.green)
                        
                        ZStack{
                            if(!item.isShowNewMsgIco){
                                VStack{
                                    if(item.status != Constants.TelemedicineStatusRecord.complete()){
                                        Spacer()
                                        if(item.statusPay == "true"){
                                            Image("rubl")
                                                .resizable()
                                                .frame(width: 25.0, height: 25.0)
                                        }else{
                                            Image("rubl_red")
                                                .resizable()
                                                .frame(width: 25.0, height: 25.0)
                                        }
                                        Spacer()
                                    }
                                }
                                .padding(8.0)
                            }
                            
                            if(item.isShowNewMsgIco){
                                Image("circle.fill")
                                    .resizable(resizingMode: .stretch)
                                    .foregroundColor(Color("green_wa"))
                                    .frame(width: 44.0, height: 44.0)
                                    .padding(4.0)
                                
                                
                                Text("1")
                                    .foregroundColor(Color.white)
                                    .fontWeight(.bold)
                                    .font(.system(size: 22))
                                    .padding(4.0)
                            }
                            
                        }
                        //.background(Color.green)
                        
                    }
                    .padding(.vertical , 8)
                    .onTapGesture{
                        listener?.goToRoom(item)
                    }
                    
                }
                .overlay(
                    RoundedRectangle(cornerRadius: 6)
                        .stroke(Color("black_bg3"), lineWidth: 2)
                )
                .padding([.leading, .bottom, .trailing], 3.0)
            }
        }
    }
}

struct T1ListOfEntriesParentChild_Previews: PreviewProvider {
    //@State private static var item = AllRecordsTelemedicineItem()
    
    static var previews: some View {
        T1ListOfEntriesItem(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem(), listener: nil)
    }
}
