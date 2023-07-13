//
//  TimetableDocMonthView.swift
//  CallMed_ios
//
//  Created by Mihail on 19.01.2023.
//

import SwiftUI

struct TimetableDocMonthView: View {
    @ObservedObject var mainPresenter : TimetableDocPresenter
    @State var isShowMonthPicker = false
    
    init(@ObservedObject mainPresenter : TimetableDocPresenter){
        self.mainPresenter = mainPresenter
        self.mainPresenter.checkShowDocMonthError()
    }
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                VStack(spacing: 0){
                    Spacer()
                    Text(mainPresenter.docMonthTitleDate.uppercased())
                        .underline()
                    Spacer()
                    VStack { Divider().frame(height: 1.0).background(Color.gray) }
                }
                .frame(height: 44.0)
                .contentShape(Rectangle())
                .onTapGesture {
                    self.isShowMonthPicker = true
                }

                
                VStack(spacing: 0){
                    HStack(spacing: 2){
                        Spacer()
                            .frame(width: 0)
                        
                        Text( "Дата")
                            .font(.system(size: 16))
                            .fontWeight(.bold)
                            .frame(width: 85.0)
                        
                        HStack{ Divider().frame(width: 1.0).background(Color.gray) }
                        
                        HStack(spacing: 2){
                            Text("Раб. время")
                                .fontWeight(.bold)
                                .font(.system(size: 16))
                        }
                        .frame(width: 105.0)
                        
                        HStack{ Divider().frame(width: 1.0).background(Color.gray) }
                        
                        Spacer()
                        Text("Филиал")
                            .fontWeight(.bold)
                            .font(.system(size: 16))
                        Spacer()
                    }
                    .padding(.trailing)
                    .frame(height: 40.0)
                    .background(Color("textSideMenu10"))
                    
                    VStack { Divider().frame(height: 1.0).background(Color.gray) }
                }
                
                ScrollView {
                    VStack(spacing: 0){
                        ForEach(self.mainPresenter.raspSotrMont) { item in
                            TimetableDocMonthItem(item: item)
                                .listRowInsets(EdgeInsets())
                        }
                        
                        VStack(spacing: 0){
                            HStack(spacing: 2){
                                Spacer()
                                    .frame(width: 8.0)
                                    
                                Text( "Итого часов: ")
                                    .font(.system(size: 16))
                                    .fontWeight(.bold)
                                   
                                Text(self.mainPresenter.docMonthHourWork)
                                    .fontWeight(.bold)
                                    .font(.system(size: 16))
                            
                                Spacer()
                                    //.frame(width: 8.0)
                            }
                            .padding(.trailing)
                            .frame(height: 40.0)
                            
                            VStack { Divider().frame(height: 1.0).background(Color.gray) }
                        }
                        .background(Color("black_bg3"))
                    }
                }
                .frame(height: .infinity)
            
                
               
            }
            
            if(self.isShowMonthPicker){
                ThreeMonthPickerDialog(mainPresenter: mainPresenter, isShowMonthPicker: $isShowMonthPicker)
            }
        }
    }
}

struct TimetableDocMonthView_Previews: PreviewProvider {
    static var previews: some View {
        TimetableDocMonthView(mainPresenter: TimetableDocPresenter())
    }
}
