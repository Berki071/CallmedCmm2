//
//  CalendarScheduleView.swift
//  iosApp
//
//  Created by Михаил Хари on 20.07.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct CalendarScheduleView: View {
    
    @ObservedObject var presenter: CalendarSchedulePresenter
    var selectdedDate : ((String,String) -> Void)
    
    init(currentDateResponce : Binding<DateItem?> ,listSchedule: Binding<[VisitItem]>,
         clickNextWeek: @escaping ((String) -> Void), selectdedDate : @escaping ((String,String) -> Void), selectDay: String, msg : String){
        
        self.selectdedDate = selectdedDate
        
        _presenter = ObservedObject(wrappedValue: CalendarSchedulePresenter(currentDateResponce: currentDateResponce, listSchedule: listSchedule, clickNextWeek: clickNextWeek, selectdedDate: selectdedDate))
        
        self.presenter.clickDay(selectDay)
        //self.presenter.hindSelectDay = msg
    }
    
    var body: some View {
        ZStack{
            VStack(spacing: 0){
                HStack{
                    if(self.presenter.checkIsShowBackDayArrow()){
                        Image(systemName: "arrowtriangle.left.fill")
                            .foregroundColor(.gray)
                            .padding(.horizontal, 16.0)
                            .padding(.vertical, 6.0)
                            .contentShape(Rectangle())
                            .onTapGesture {
                                self.presenter.clickArrowBack()
                            }
                    }else{
                        Image(systemName: "arrowtriangle.left.fill")
                            .foregroundColor(Color("black_bg25"))
                            .padding(.horizontal, 16.0)
                            .padding(.vertical, 6.0)
                    }
                    Spacer()
                    Text(self.presenter.title)
                    Spacer()
                    if(self.presenter.checkIsShowNextDayArrow()){
                    Image(systemName: "arrowtriangle.right.fill")
                            .foregroundColor(.gray)
                            .padding(.horizontal, 16.0)
                            .padding(.vertical, 6.0)
                            .contentShape(Rectangle())
                            .onTapGesture {
                                self.presenter.clickArrowForward()
                            }
                    }else{
                        Image(systemName: "arrowtriangle.right.fill")
                            .foregroundColor(Color("black_bg25"))
                            .padding(.horizontal, 16.0)
                            .padding(.vertical, 6.0)
                    }
                }
                .frame(height: 44.0)
                
                HStack(spacing: 4){
                 
                    DayWeekItemView(title: "Пн", day: self.$presenter.day1, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day1!))
                    })
                    DayWeekItemView(title: "Вт", day: self.$presenter.day2, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day2!))
                    })
                    DayWeekItemView(title: "Ср", day: self.$presenter.day3, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day3!))
                    })
                    DayWeekItemView(title: "Чт", day: self.$presenter.day4, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day4!))
                    })
                    DayWeekItemView(title: "Пт", day: self.$presenter.day5, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day5!))
                    })
                    DayWeekItemView(title: "Сб", day: self.$presenter.day6, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day6!))
                    })
                    DayWeekItemView(title: "Вс", day: self.$presenter.day7, clickItem: {(i: String) -> Void in
                        self.selectdedDate(i, self.presenter.getHintMsg(self.presenter.day7!))
                    })
                    
                }
                .padding(.horizontal, 5.0)
                
                if(!self.presenter.hindSelectDay.isEmpty){
                    HStack{
                        Spacer()
                        Text(self.presenter.hindSelectDay)
                            .font(.title3)
                            .padding(.vertical, 4.0)
                        Spacer()
                    }
                    .background(Color("textSideMenu10"))
                }
            }
        }
    }
}

struct CalendarScheduleView_Previews: PreviewProvider {
    
    @State static var cheTmp : DateItem? = nil
    @State static var cheduleAll : [VisitItem] = []
    
    static var previews: some View {
        CalendarScheduleView(currentDateResponce: $cheTmp, listSchedule: $cheduleAll, clickNextWeek: {(String) -> Void in },
                             selectdedDate: {(j: String, k: String) -> Void in }, selectDay: "", msg: "")
    }
}
