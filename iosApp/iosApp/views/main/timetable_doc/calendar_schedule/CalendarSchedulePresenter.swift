//
//  CalendarSchedulePresenter.swift
//  iosApp
//
//  Created by Михаил Хари on 20.07.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared


class CalendarSchedulePresenter: ObservableObject{
    var listSchedule: Binding<[VisitResponse.VisitItem]>
    
    var currentDateResponce : Binding<DateResponse.DateItem?>
    var clickNextWeek: ((String) -> Void)
    var selectdedDate : ((String,String) -> Void)
    
    @Published var title = "desember, 2022"
    @Published var day1 : DayWeekItemViewData? = nil
    @Published var day2 : DayWeekItemViewData? = nil
    @Published var day3 : DayWeekItemViewData? = nil
    @Published var day4 : DayWeekItemViewData? = nil
    @Published var day5 : DayWeekItemViewData? = nil
    @Published var day6 : DayWeekItemViewData? = nil
    @Published var day7 : DayWeekItemViewData? = nil
    
    @Published var hindSelectDay: String  = ""
    
    init(currentDateResponce : Binding<DateResponse.DateItem?>, listSchedule: Binding<[VisitResponse.VisitItem]>, clickNextWeek: @escaping ((String) -> Void), selectdedDate: @escaping ((String,String) -> Void)){
        
        self.currentDateResponce = currentDateResponce
        self.listSchedule = listSchedule
        self.clickNextWeek = clickNextWeek
        self.selectdedDate = selectdedDate

        initTitle()
    }
    
    func  initTitle() {
        if currentDateResponce.wrappedValue == nil {
            title = ""
            return
        }
        
        let strDate = currentDateResponce.wrappedValue!.lastMonday == "1" ? currentDateResponce.wrappedValue!.today! : currentDateResponce.wrappedValue!.lastMonday!
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        let date = dateFormatter.date(from: strDate)!
        dateFormatter.dateFormat = MDate.DATE_FORMAT_LLLL_YYYY
        dateFormatter.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
        title = dateFormatter.string(from: date).uppercased()
        
        initItems()
        
    }
    
    func initItems(){
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        
        let todayDate = dateFormatter.date(from: currentDateResponce.wrappedValue!.today!)!
        
        var d1Str = currentDateResponce.wrappedValue!.lastMonday == "1" ? currentDateResponce.wrappedValue!.today! : currentDateResponce.wrappedValue!.lastMonday!
        var d1Date = dateFormatter.date(from: d1Str)!
        
       // var listItemsDay = [day1,day2,day3,day4,day5,day6,day7]
     
        day1 = self.initItems2(d1Date,todayDate, d1Str)
        d1Date.addTimeInterval(TimeInterval(60*60*24))
        d1Str=dateFormatter.string(from: d1Date)
        
        day2 = self.initItems2(d1Date,todayDate, d1Str)
        d1Date.addTimeInterval(TimeInterval(60*60*24))
        d1Str=dateFormatter.string(from: d1Date)
        
        day3 = self.initItems2(d1Date,todayDate, d1Str)
        d1Date.addTimeInterval(TimeInterval(60*60*24))
        d1Str=dateFormatter.string(from: d1Date)
        
        day4 = self.initItems2(d1Date,todayDate, d1Str)
        d1Date.addTimeInterval(TimeInterval(60*60*24))
        d1Str=dateFormatter.string(from: d1Date)
        
        day5 = self.initItems2(d1Date,todayDate, d1Str)
        d1Date.addTimeInterval(TimeInterval(60*60*24))
        d1Str=dateFormatter.string(from: d1Date)
        
        day6 = self.initItems2(d1Date,todayDate, d1Str)
        d1Date.addTimeInterval(TimeInterval(60*60*24))
        d1Str=dateFormatter.string(from: d1Date)
        
        day7 = self.initItems2(d1Date,todayDate, d1Str)
    }
    
    func initItems2(_ d1Date: Date, _ todayDate: Date, _ d1Str: String) -> DayWeekItemViewData? {
        var day1 : DayWeekItemViewData? = nil
        
        if(d1Date < todayDate){
            day1 = DayWeekItemViewData(date: d1Str, type: TypeDayWeekItem.Hide, listScheduleOnDate: nil)
        }else{
           
            let list : [VisitResponse.VisitItem]? = scheduleListForDate(d1Str)
            var d1Type : TypeDayWeekItem
            
            //let calendarDateCurrent = Date()
            
            if list == nil {
                day1  = DayWeekItemViewData(date: d1Str, type: TypeDayWeekItem.Common, listScheduleOnDate: nil)
            }else{
                d1Type = TypeDayWeekItem.Green
                day1  = DayWeekItemViewData(date: d1Str, type: d1Type, listScheduleOnDate: list)
            }
        }
        
        return day1
    }
    
    
    func scheduleListForDate(_ date : String) -> [VisitResponse.VisitItem]? {
        if(listSchedule.wrappedValue.count == 0){
            return nil
        }
        
        var newList : [VisitResponse.VisitItem] = []
        
        listSchedule.wrappedValue.forEach{i in
            if(i.date == date){
                newList.append(i)
            }
        }
        
        return newList.count>0 ? newList : nil
    }
    
    
    func clickDay(_ day: String){
        if(day1 != nil){
            if(day1?.date == day){
                day1?.idSelect = true
                self.hindSelectDay = getHintMsg(day1!)
            }else{
                day1?.idSelect = false
            }
        }
        
        if(day2 != nil){
            if(day2?.date == day){
                day2?.idSelect = true
                self.hindSelectDay = getHintMsg(day2!)
            }else{
                day2?.idSelect = false
            }
        }
        
        if(day3 != nil){
            if(day3?.date == day){
                day3?.idSelect = true
                self.hindSelectDay = getHintMsg(day3!)
            }else{
                day3?.idSelect = false
            }
        }
        
        if(day4 != nil){
            if(day4?.date == day){
                day4?.idSelect = true
                self.hindSelectDay = getHintMsg(day4!)
            }else{
                day4?.idSelect = false
            }
        }
        
        if(day5 != nil){
            if(day5?.date == day){
                day5?.idSelect = true
                self.hindSelectDay = getHintMsg(day5!)
            }else{
                day5?.idSelect = false
            }
        }
        
        if(day6 != nil){
            if(day6?.date == day){
                day6?.idSelect = true
                self.hindSelectDay = getHintMsg(day6!)
            }else{
                day6?.idSelect = false
            }
        }
        
        if(day7 != nil){
            if(day7?.date == day){
                day7?.idSelect = true
                self.hindSelectDay = getHintMsg(day7!)
            }else{
                day7?.idSelect = false
            }
        }
    }
    
    func getHintMsg(_ day : DayWeekItemViewData) -> String {
        switch(day.type){
        case TypeDayWeekItem.Common: return "В данный день нет записей."
        case TypeDayWeekItem.Green: return "\(day.listScheduleOnDate![0].kab!) (\(day.listScheduleOnDate![0].start!) - \(day.listScheduleOnDate![0].end!))" ?? ""
        case TypeDayWeekItem.Yellow: return ""
        case TypeDayWeekItem.Red: return ""
        default: return "В данный день нет записей."
        }
    }
    
    func clickArrowBack(){
        let strDate = currentDateResponce.wrappedValue!.lastMonday == "1" ? currentDateResponce.wrappedValue!.today! : currentDateResponce.wrappedValue!.lastMonday!
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        let date = dateFormatter.date(from: strDate)!
        
        let modifiedDate : Date = Calendar.current.date(byAdding: .day, value: -7, to: date)!
        
        let newDateStr = dateFormatter.string(from: modifiedDate)
        
        self.clickNextWeek(newDateStr)
    }
    
    func clickArrowForward(){
        let strDate = currentDateResponce.wrappedValue!.lastMonday == "1" ? currentDateResponce.wrappedValue!.today! : currentDateResponce.wrappedValue!.lastMonday!
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        let date = dateFormatter.date(from: strDate)!
        
        let modifiedDate : Date = Calendar.current.date(byAdding: .day, value: 7, to: date)!
        
        let newDateStr = dateFormatter.string(from: modifiedDate)
        
        self.clickNextWeek(newDateStr)
    }
    
    func checkIsShowBackDayArrow() -> Bool {
        if(currentDateResponce.wrappedValue == nil){
            return false
        }
        
        let strDate = currentDateResponce.wrappedValue!.lastMonday == "1" ? currentDateResponce.wrappedValue!.today! : currentDateResponce.wrappedValue!.lastMonday!
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        let lMonday = dateFormatter.date(from: strDate)!
        
        let today = dateFormatter.date(from: currentDateResponce.wrappedValue!.today!)!
        
        return today < lMonday
    }
    
    func checkIsShowNextDayArrow() -> Bool {
        if(currentDateResponce.wrappedValue == nil){
            return false
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MDate.DATE_FORMAT_ddMMyyyy
        
        let strDateToday = currentDateResponce.wrappedValue!.today!
        let strDateTodayD = dateFormatter.date(from: strDateToday)!
        let dateTodayPlus28 : Date = Calendar.current.date(byAdding: .day, value: 29, to: strDateTodayD)!
        
        let strMonday = currentDateResponce.wrappedValue!.lastMonday == "1" ? currentDateResponce.wrappedValue!.today! : currentDateResponce.wrappedValue!.lastMonday!
        let strMondayD = dateFormatter.date(from: strMonday)!
        let strMondayPlus6 : Date = Calendar.current.date(byAdding: .day, value: 7, to: strMondayD)!
        
        return dateTodayPlus28 >= strMondayPlus6
    }
}
