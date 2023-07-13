//
//  DayWeekItem.swift
//  iosApp
//
//  Created by Михаил Хари on 20.07.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

enum TypeDayWeekItem{
    case Hide
    case Green
    case Yellow
    case Red
    case Common
}

struct DayWeekItemViewData{
    let date : String
    var idSelect = false
    var type : TypeDayWeekItem
    let listScheduleOnDate :[VisitItem]?
}

struct DayWeekItemView: View {
    let  title: String
    @Binding var day : DayWeekItemViewData?
    var clickItem: ((String) -> Void)?
    
    var body: some View {
        ZStack{
            if (day == nil || day!.type == TypeDayWeekItem.Hide) {
                VStack(spacing: 0){
                    Text(" ")
                        .padding(.vertical, 4.0)
        
                    ZStack{
                        VStack(spacing: 0){
                            Text(" ")
                                .font(.body)
                                .padding(.vertical, 10.0)
                        }
                        .frame(maxWidth: .infinity)
                    
                        .cornerRadius(4)
                        .padding(4)
                    }
                }
                .padding(2)
                .contentShape(Rectangle())
                .onTapGesture {
                }
                
                
            }else{
                
                VStack(spacing: 0){
                    Text(title)
                        .padding(.vertical, 4.0)
                    //let _ = print(">>>> day \(day!.date) , idSelect= \(day!.idSelect) ")
                    ZStack{
                        if(day!.idSelect){
                            VStack(spacing: 0){
                                Spacer()
                                    .frame(height: 2.0)
                                Text(" ")
                                    .font(.body)
                                    .padding(.vertical, 10.0)
                                Spacer()
                                    .frame(height: 2.0)
                            }
                            .frame(maxWidth: .infinity)
                            .background(Color("color_primary"))
                            .cornerRadius(4)
                            .padding(2)
                        }
                        
                        
                        VStack(spacing: 0){
                            Text(strinctDate(day!.date))
                                .font(.body)
                                .foregroundColor(getTextColor())
                                .padding(.vertical, 10.0)
                        }
                        .frame(maxWidth: .infinity)
                        .background(getColor())
                        .cornerRadius(4)
                        .padding(4)
                    }
                }
                .padding(2)
                .contentShape(Rectangle())
                .onTapGesture {
                    clickItem?(day!.date)
                }
                
            }
        }
        .frame(maxWidth: .infinity)
        .overlay(
            RoundedRectangle(cornerRadius: 6)
                .stroke(Color("black_bg3"), lineWidth: 1)
        )
    }
    
    func strinctDate(_ date: String) ->  String {
        let dot = date.range(of: ".")!
        let st2 = String(date[...dot.lowerBound])
        let st3 = String(st2[...st2.index(st2.startIndex, offsetBy: 1)])
        return st3
    }
    
    func getColor() -> Color {
        switch(day!.type){
        case TypeDayWeekItem.Common: return Color("transparent")
        case TypeDayWeekItem.Green: return Color("light_green")
        case TypeDayWeekItem.Yellow: return Color("orange")
        case TypeDayWeekItem.Red: return Color("red")
        default: return Color("transparent")
        }
    }
    
    func getTextColor() -> Color {
        if(day!.idSelect){
            return Color(.white)
        }else{
            return Color(.black)
        }
    }
    
}

struct DayWeekItem_Previews: PreviewProvider {
    @State static var tmp : DayWeekItemViewData? = DayWeekItemViewData(date: "22.08.2023", idSelect: true, type: TypeDayWeekItem.Green, listScheduleOnDate: nil )
    
    static var previews: some View {
        DayWeekItemView(title: "Vt", day: $tmp, clickItem: nil)
    }
}
