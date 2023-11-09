//
//  ToolbarTelemedicineRoomPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared

class ToolbarTelemedicineRoomPresenter: ObservableObject {
    var itemRecord: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>
    var listener: ToolbarTelemedicineRoomListener
    
    @Published var iuImageLogo : UIImage =  UIImage(named: "sh_doc")!
   
    @Published var isShowTimer : Bool = false
    var timerTimeStop : TimeInterval = 0
    @Published var showTimeTimer : String = ""
    
    var sharePreferenses : SharedPreferenses = SharedPreferenses()

  
    
//    init(){
//      
//        self.listener = ToolbarTelemedicineRoomListener(goToMedia: {() -> Void in }, closeTm: {() -> Void in }, clickBack: {() -> Void in }, clickStartReception:  {() -> Void in },
//                                                          clickCompleteReception: {() -> Void in })
//
//    }
    
    init(item: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>, listener: ToolbarTelemedicineRoomListener){
        self.itemRecord = item
        self.listener = listener
        
        //self.loadIco()
        self.checkTimer()
    }
    
//    func loadIco(){
//        let currentUserInfo = sharePreferenses.currentUserInfo
//        
//
//    }
    
    func goToMedia() {
        listener.goToMedia()
    }
    func showAnalizes(){
        listener.showAnalizes()
    }
    func showConclusions(){
        listener.showConclusions()
    }
    
    var timeLeftInSecForActive : Double? = nil
    func checkTimer(){
        if(itemRecord.wrappedValue == nil){
            isShowTimer = false
            return
        }
        
        let tmp1 = itemRecord.wrappedValue!.dataStart
        let tmp2 = itemRecord.wrappedValue!.status
        
        if(itemRecord.wrappedValue!.dataStart != nil && itemRecord.wrappedValue!.status! == Constants.TelemedicineStatusRecord.active()){
            let currentTimeServerLong = MDate.stringToDate(itemRecord.wrappedValue!.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            
            let dateStartLong = MDate.stringToDate(itemRecord.wrappedValue!.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            let dateEndLong = MDate.datePlasTimeInterval(dateStartLong, Int.init(truncating: itemRecord.wrappedValue!.tmTimeForTm!))
            
            if(dateEndLong <= currentTimeServerLong){
                self.isShowTimer = false
            }else{
                let tmp: Double = dateEndLong.timeIntervalSince1970 - currentTimeServerLong.timeIntervalSince1970
              
                if(tmp > 0){
                    self.isShowTimer = true
                    self.timeLeftInSecForActive = tmp
                    self.startTimerActive()
                }else{
                    self.isShowTimer = false
                    self.listener.closeTm()
                   
                }
            }
        }else{
            isShowTimer = false
        }
        
        
//        if(itemRecord.wrappedValue == nil){
//            isShowTimer = false
//            return
//        }
//
//        let tmp1 = itemRecord.wrappedValue!.dataStart
//        let tmp2 = itemRecord.wrappedValue!.status
//
//        if(itemRecord.wrappedValue!.dataStart != nil && itemRecord.wrappedValue!.status! == Constants.TelemedicineStatusRecord.active()){
//            let currentTimePhone = MDate.getCurrentDate()
//            let currentTimeServerLong = MDate.stringToDate(itemRecord.wrappedValue!.dataServer!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//            let differenceCurrentTime = currentTimeServerLong.timeIntervalSince(currentTimePhone)
//
//            let dateStartLong = MDate.stringToDate(itemRecord.wrappedValue!.dataStart!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//            let dateEndLong = MDate.datePlasTimeInterval(dateStartLong, Int.init(itemRecord.wrappedValue!.tmTimeForTm!))
//
//            if(dateEndLong <= currentTimeServerLong){
//                self.isShowTimer = false
//            }else{
//                self.isShowTimer = true
//
//                //время остановиться в телефонном времени
//                timerTimeStop = dateEndLong.timeIntervalSince1970 - differenceCurrentTime
//
//                self.startTimerActive()
//            }
//
//        }else{
//            isShowTimer = false
//        }
    }
    func startTimerActive(){
        if(self.timeLeftInSecForActive == nil || self.timeLeftInSecForActive! <= 0){
            self.isShowTimer = false
            self.listener.closeTm()
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
//            self.isShowTimer = false
//            self.listener.closeTm()
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
//        self.showTimeTimer = str
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
    
    func clickStartReception(){
        listener.clickStartReception()
    }
    func clickCompleteReception(){
        listener.clickCompleteReception()
    }
    
    func stringToInitials(str : String) -> String{
        if(str.isEmpty){
            return ""
        }
        
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
}
