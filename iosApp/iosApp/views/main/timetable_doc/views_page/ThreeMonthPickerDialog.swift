//
//  ThreeMonthPickerDialog.swift
//  CallMed_ios
//
//  Created by Mihail on 19.01.2023.
//

import SwiftUI

struct ThreeMonthPickerDialog: View {
    
    @ObservedObject var mainPresenter : TimetableDocPresenter
    var isShowMonthPicker: Binding<Bool>
    @State private var selectedTab: Int = 1
    
    let dateFormatterGet = DateFormatter()
    
    init(@ObservedObject mainPresenter: TimetableDocPresenter, isShowMonthPicker: Binding<Bool>) {
        self.mainPresenter = mainPresenter
        self.isShowMonthPicker = isShowMonthPicker
       
        initDateFormater()
    }
    
    var body: some View {
        
        ZStack{
            Color("black_bg")
            
            
            ZStack{
                VStack{
                    Spacer()
                        .frame(height: 12.0)
                    
                    Text("Выберите месяц")
                        .font(.system(size: 26))
                    
                    Picker("", selection: $selectedTab) {
                        Text(dateFormatterGet.string(from: self.mainPresenter.arrMonth[0]))
                            .tag(0)
                        Text(dateFormatterGet.string(from: self.mainPresenter.arrMonth[1]))
                            .tag(1)
                        Text(dateFormatterGet.string(from: self.mainPresenter.arrMonth[2]))
                            .tag(2)
                    }
                    .padding(/*@START_MENU_TOKEN@*/.all/*@END_MENU_TOKEN@*/)
                    .frame(height: 120.0)
                    .pickerStyle(WheelPickerStyle())
                    
                
                    HStack{
                        Button(action: {
                            self.isShowMonthPicker.wrappedValue = false
                        }) {
                            Text("Отмена")
                                .frame(minWidth: 100, maxWidth: .infinity, minHeight: 44, maxHeight: 44, alignment: .center)
                                .foregroundColor(Color.white)
                                .background(Color("color_primary"))
                                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                                .font(.system(size: 16))
                        }
                        
                        Button(action: {
                            self.isShowMonthPicker.wrappedValue = false
                            self.mainPresenter.selectNewMonth(date: self.mainPresenter.arrMonth[selectedTab])
                        }) {
                            Text("Выбрать")
                                .frame(minWidth: 100, maxWidth: .infinity, minHeight: 44, maxHeight: 44, alignment: .center)
                                .foregroundColor(Color.white)
                                .background(Color("color_primary"))
                                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                                .font(.system(size: 16))
                        }
                        
                    }
                    .padding([.leading, .bottom, .trailing])
                    .padding(.top, 12.0)
                    
                }
                
            }
            .background(Color.white)
            .cornerRadius(6)
            .padding([.leading, .trailing], 16.0)
        }
        .ignoresSafeArea()
    }
    
    func initDateFormater(){
        self.dateFormatterGet.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
        self.dateFormatterGet.dateFormat = "LLLL yyyy"
    }

}

struct ThreeMonthPickerDialog_Previews: PreviewProvider {
    @State static var isShowMonthPicker = false
    
    static var previews: some View {
        ThreeMonthPickerDialog(mainPresenter: TimetableDocPresenter(), isShowMonthPicker: $isShowMonthPicker)
    }
}
