//
//  MDate.swift
//  iosApp
//
//  Created by Михаил Хари on 20.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

class MDate{
    public static let DATE_FORMAT_HHmmss_ddMMyyyy = "HH:mm:ss dd.MM.yyyy"
    public static let DATE_FORMAT_ddMMyyyy_HHmmss = "dd.MM.yyyy HH:mm:ss"
    public static let DATE_FORMAT_HHmm = "HH:mm"
    public static let DATE_FORMAT_HHmmss = "HH:mm:ss"
    public static let DATE_FORMAT_HHmm_ddMMyyyy = "HH:mm dd.MM.yyyy"
    public static let DATE_FORMAT_ddMMyyyy = "dd.MM.yyyy"
    public static let DATE_FORMAT_dd_MMMM_yyyy = "dd MMMM yyyy"
    public static let DATE_FORMAT_ddMMyy = "dd.MM.yy"
    public static let DATE_FORMAT_MMMM_YYYY = "MMMM, yyyy"
    public static let DATE_FORMAT_LLLL_YYYY = "LLLL, yyyy"
    public static let DATE_FORMAT_yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss"
    
//    public static let DATE_FORMAT_MMMM = "MMMM"
//    public static let DATE_FORMAT_YYYY = "yyyy"LLLL
    
    static func getCurrentDate() -> Date{
        // дата по умолчанию будет в нулевом поясе, для локальной Date().localDate()
        //при конвертации stringToDate она будет в  0 поясе дате
        return Date()
    }
    static func getCurrentDate(_ format: String) -> String{
        let date = getCurrentDate()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: date)
    }
    static func getCurrentDate1970() -> Double{
        // дата по умолчанию будет в нулевом поясе, для локальной Date().localDate()
        //при конвертации stringToDate она будет в  0 поясе дате
        return Date().timeIntervalSince1970
    }

    static func dateToString(_ date: Date, _ format: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: date)
    }
    
    static func stringToDate(_ strDate: String, _ format: String ) ->Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        
        return dateFormatter.date(from: strDate)!
    }
    
    static func curentTimeDate() -> Date {
        let curTimeStr = dateToString(getCurrentDate(), MDate.DATE_FORMAT_HHmm)
        
        return stringToDate(curTimeStr, MDate.DATE_FORMAT_HHmm)
    }
    
    static func currentDateClearString() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = DATE_FORMAT_ddMMyyyy
        
        return dateFormatter.string(from: getCurrentDate())
    }
    
    static func getNextDateFromCurrentInDaysString(_ countDays: Int) -> String{
        let modifiedDate : Date = Calendar.current.date(byAdding: .day, value: countDays, to: getCurrentDate())!
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = DATE_FORMAT_ddMMyyyy
        
        return dateFormatter.string(from: modifiedDate)
    }
    
    static func datePlasTimeInterval(_ date: Date, _ timeIntervalMin: Int) -> Date {
        let minutes: TimeInterval = TimeInterval(timeIntervalMin*60)
        return date + minutes
    }
    
    static func getNewFormatString(_ strDate: String, _ formatFrom: String, _ formatTo: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = formatFrom
        let dateTmp = dateFormatter.date(from: strDate)!
        
        dateFormatter.dateFormat = formatTo
        return dateFormatter.string(from: dateTmp)
    }
}
