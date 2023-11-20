//
//  T1ListOfEntriesItemPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 19.06.2023.
//

import Foundation
import SwiftUI
import shared

class T1ListOfEntriesItemPresenter : ObservableObject {
    @Published var iuImageLogo : UIImage =  UIImage(named: "person_pin")!
    
    @Published var isShowTimeLeft = false
    var timerTimeStop : TimeInterval = 0
    @Published var showTimeTimer : String = ""
    @Published var titleTimer = "Время до завершения: "
    
    
    //let timeForNoty30Min: TimeInterval = 60*30
    var timeForNoty24And0Hour: TimeInterval = 60*60*24
    var timeForNoty12And0Hour: TimeInterval = 60*60*12
    var timeForNoty4And0Hour: TimeInterval = 60*60*4
    let timeForNoty1And0Hour: TimeInterval = 60*60
    let timeForNoty5Min: TimeInterval = 60*5
    
    var sharePreferenses : SharedPreferenses
    //var centerResponse : CenterResponse
 
    var item : AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
    var listener : T1ListOfEntriesItemListener? = nil
    
    
//    init(item : Binding<AllRecordsTelemedicineItem>, listener : T1ListOfEntriesItemListener?){
//        self.item = item
//        self.listener = listener
//
//        sharePreferenses = SharedPreferenses()
//        //centerResponse = sharePreferenses.centerInfo ?? CenterResponse()
//    
//        self.processingShowTimeLeft()
//    }
    
    init(item : AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem, listener : T1ListOfEntriesItemListener?){
        self.item = item
        self.listener = listener

        sharePreferenses = SharedPreferenses()
        //centerResponse = sharePreferenses.centerInfo ?? CenterResponse()
    
        self.processingShowTimeLeft()
    }
    
    func processingShowTimeLeft() {
        if(item == nil){
            self.isShowTimeLeft = false
        }
        
        if(item.status == Constants.TelemedicineStatusRecord.active()){
            self.checkTimerActive()
        }else if(item.status == Constants.TelemedicineStatusRecord.wait() && item.statusPay == "true"){
            self.checkTimerWait()
        }else{
            self.isShowTimeLeft = false
        }
    }
    
    var timeLeftInSecForActive : Double? = nil
    func checkTimerActive(){
//        self.titleTimer = "Время до завершения: "
//
//        let currentTimePhone = MDate.getCurrentDate()
//        let currentTimeServerLong = MDate.stringToDate(item.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//        let differenceCurrentTime = currentTimeServerLong.timeIntervalSince(currentTimePhone)
//
//        let dateStartLong = MDate.stringToDate(item.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//        let dateEndLong = MDate.datePlasTimeInterval(dateStartLong, Int.init(item.tmTimeForTm!))
//
//        if(dateEndLong <= currentTimeServerLong){
//            self.isShowTimeLeft = false
//        }else{
//            self.isShowTimeLeft = true
//
//            //время остановиться в телефонном времени
//            timerTimeStop = dateEndLong.timeIntervalSince1970 - differenceCurrentTime
//
//            self.startTimerActive()
//        }
        self.titleTimer = "Время до завершения: "
        
        let currentTimeServerLong = MDate.stringToDate(item.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
        
        let dateStartLong = MDate.stringToDate(item.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
        let dateEndLong = MDate.datePlasTimeInterval(dateStartLong, Int.init(truncating: item.tmTimeForTm!))
        
        if(dateEndLong <= currentTimeServerLong){
            LoggingTree.INSTANCE.d("T1ListOfEntriesItemPresenter closeTm dataServer:\(item.dataServer!), dataStart:\(item.dataStart!), tmTimeForTm:\(item.tmTimeForTm!), tmId:\(item.tmId)")
            
            self.isShowTimeLeft = false
            self.listener?.closeTm(item)
        }else{
            let tmp: Double = dateEndLong.timeIntervalSince1970 - currentTimeServerLong.timeIntervalSince1970
          
            if(tmp > 0){
                self.isShowTimeLeft = true
                self.timeLeftInSecForActive = tmp
                self.startTimerActive()
            }else{
                LoggingTree.INSTANCE.d("T1ListOfEntriesItemPresenter2 closeTm dataServer:\(item.dataServer!), dataStart:\(item.dataStart!), tmTimeForTm:\(item.tmTimeForTm!), tmId:\(item.tmId)")
                
                self.isShowTimeLeft = false
                self.listener?.closeTm(item)
            }
        }
    }
    
    func startTimerActive(){
        
        if(self.timeLeftInSecForActive == nil || self.timeLeftInSecForActive! <= 0){
            LoggingTree.INSTANCE.d("T1ListOfEntriesItemPresenter startTimerActive closeTm dataServer:\(item.dataServer!), dataStart:\(item.dataStart!), tmTimeForTm:\(item.tmTimeForTm!), tmId:\(item.tmId)")
            
            self.isShowTimeLeft = false
            self.listener?.closeTm(item)
            return
        }
        DispatchQueue.global(qos: .background).async {
            self.processinStartTimerActive()
        }
        
        //1 sec step
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.97) {
            self.startTimerActive()
        }
        
        
//        let currentTimePhone = MDate.getCurrentDate()
//
//        if(currentTimePhone.timeIntervalSince1970 >= timerTimeStop){
//            self.isShowTimeLeft = false
//            self.listener?.closeTm(item)
//            return
//        }
//
//        let timeLeft = timerTimeStop-currentTimePhone.timeIntervalSince1970
//
//        let sec = Int(timeLeft.truncatingRemainder(dividingBy: 60))
//        let minAll = timeLeft / 60
//        let min = Int(minAll.truncatingRemainder(dividingBy: 60))
//        let hour = Int(minAll / 60)
//
//        var str = ""
//        if(hour != 0){
//            str += String.init(hour) + ":"
//        }
//        var minStr = String.init(min)
//        if(minStr.count == 1){
//            minStr = "0"+minStr
//        }
//
//        str += minStr + ":"
//
//        var secStr = String.init(sec)
//        if(secStr.count == 1){
//            secStr = "0"+secStr
//        }
//
//        str += secStr
//
//        showTimeTimer = str
//
//        //1 sec step
//        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
//            self.startTimerActive()
//        }
    }
    func processinStartTimerActive(){
        DispatchQueue.main.async {
            self.timeLeftInSecForActive =  self.timeLeftInSecForActive! - 1
        }
        
        let timeLeft = self.timeLeftInSecForActive!  //in sec
         
        let sec = Int(timeLeft.truncatingRemainder(dividingBy: 60))
        let minAll = timeLeft / 60
        let min = Int(minAll.truncatingRemainder(dividingBy: 60))
        let hour = Int(minAll / 60)
        
        var str = ""
        if(hour != 0){
            str += String.init(hour) + ":"
        }
        var minStr = String.init(min)
        if(minStr.count == 1){
            minStr = "0"+minStr
        }
        
        str += minStr + ":"
        
        var secStr = String.init(sec)
        if(secStr.count == 1){
            secStr = "0"+secStr
        }
        
        str += secStr
        
        DispatchQueue.main.async {
            self.showTimeTimer = str
        }
    }
    
    func checkTimerWait() {
        self.titleTimer = "Начнется в течение: "
        self.isShowTimeLeft = true
        
        let currentTimePhone = MDate.getCurrentDate()
       // let tmp1 = item.wrappedValue.dataServer!
        
        let currentTimeServerLong = MDate.stringToDate(item.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
        let differenceCurrentTime = currentTimeServerLong.timeIntervalSince(currentTimePhone) // -25200.759554982185    25200 это секунды
        
        let dateStartLong = MDate.stringToDate(item.dataPay!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
        
       // let tmp22 = Int.init(truncating: item.wrappedValue.timeStartAfterPay!)
        let dateEndLong = MDate.datePlasTimeInterval(dateStartLong, Int.init(item.timeStartAfterPay!))
        
        
        if(dateEndLong <= currentTimeServerLong){
            self.titleTimer = "Начнется в ближайшее время"
            self.showTimeTimer = ""
        }else{
            if(dateEndLong != nil && currentTimeServerLong != nil){
                timerTimeStop = dateEndLong.timeIntervalSince1970 - differenceCurrentTime //differenceCurrentTime для приведения к времени устройства
            }
            
            startTimerWait()
        }
    }
    func startTimerWait(){
        do {
            let currentTimePhone = MDate.getCurrentDate()
            
            if(currentTimePhone.timeIntervalSince1970 >= timerTimeStop){
                self.titleTimer = "Начнется в ближайшее время"
                self.showTimeTimer = ""
                return
            }
            
//            if(item == nil ){
//                print(">>>> sds ")
//            }
//            if(item.wrappedValue == nil){
//                print(">>>> sds ")
//            }
//            if(item.wrappedValue.status == Constants.TelemedicineStatusRecord.complete()){
//                print(">>>> sds ")
//            }
//            
            let timeLeft = timerTimeStop-currentTimePhone.timeIntervalSince1970
            
            let sec = Int(timeLeft.truncatingRemainder(dividingBy: 60))
            let minAll = timeLeft / 60
            let min = Int(minAll.truncatingRemainder(dividingBy: 60))
            let hour = Int(minAll / 60)
            
            var str = ""
            if(hour != 0){
                str += String.init(hour) + ":"
            }
            var minStr = String.init(min)
            
            if(minStr.count == 1){
                minStr = "0"+minStr
            }
            
            str += minStr + ":"
            
            var secStr = String.init(sec)
            if(secStr.count == 1){
                secStr = "0"+secStr
            }
            
            str += secStr
            
            self.titleTimer = "Начнется в течение: "
            showTimeTimer = str
            
//            let tmp1 = timeLeft                   // магия![!! вызывает краш когда item в вейте и переходишь в архив !!!!!!
//            let tmp2 = timeForNoty24And0Hour
//            let tmp3 = timeForNoty24And0Hour - timeForNoty5Min
//            let tmp4 = item.notif_24
//            let tmp5 = (item.notif_24 == "false")
            
            if(timeLeft > (timeForNoty24And0Hour - timeForNoty5Min) && timeLeft < (timeForNoty24And0Hour) &&  item.notif_24 == "false"){
                item.notif_24 = "true"
                listener?.sendNotyReminder(item, "До начала телемедицины осталось меньше 24 часов","notif_24")
            }
            if(timeLeft > (timeForNoty12And0Hour - timeForNoty5Min) && timeLeft < (timeForNoty12And0Hour) &&  item.notif_12 == "false"){
                item.notif_12 = "true"
                listener?.sendNotyReminder(item, "До начала телемедицины осталось меньше 12 часов","notif_12")
            }
            if(timeLeft > (timeForNoty4And0Hour - timeForNoty5Min) && timeLeft < (timeForNoty4And0Hour)  &&  item.notif_4 == "false"){
                item.notif_4 = "true"
                listener?.sendNotyReminder(item, "До начала телемедицины осталось меньше 4 часов","notif_4")
            }
            if(timeLeft > (timeForNoty1And0Hour - timeForNoty5Min) && timeLeft < (timeForNoty1And0Hour)  &&  item.notif_1 == "false"){
                item.notif_1 = "true"
                listener?.sendNotyReminder(item, "До начала телемедицины осталось меньше 30 минут","notif_1")
            }
            
        
            //1 sec step
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                self.startTimerWait()
            }
            
        } catch {
            print (">>>>>>!!>>> \(error)")
        }
    }
    
    func stringToInitials(str : String) -> String{
        let s1 = String(str[...str.index(str.startIndex, offsetBy: 0)])
        
        let tt = str.range(of: " ")
        if(tt != nil){
            let s2 = String(str[str.range(of: " ")!.lowerBound...])
            let s3 = String(s2[s2.index(s2.startIndex, offsetBy: 1)...])
            if(s3.isEmpty){
                return s1
            }else{
                let s4 = String(s3[...s3.index(s3.startIndex, offsetBy: 0)])
                return s1+s4
            }
        }else {
            return s1
        }
    }
    func getColor(status : String) -> Color{
        if(status == Constants.TelemedicineStatusRecord.active()){
            return Color("color_primary")
        }else if(status == Constants.TelemedicineStatusRecord.wait()){
            return Color("light_green_pressed")
        }else if(status == Constants.TelemedicineStatusRecord.complete()){
            return Color("red")
        }else{
            return Color("text_gray")
        }
    }
    
    
    func dateTimeToDate(_ dt: String) -> String {
        let d1 = MDate.stringToDate(dt, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
        let d2 = MDate.dateToString(d1, MDate.DATE_FORMAT_ddMMyyyy)
        return d2
    }
}
