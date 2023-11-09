//
//  TimetableDocPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 12.12.2022.
//

import Foundation
import shared

class TimetableDocPresenter: ObservableObject{
    @Published var showDialogLoading: Bool = false
    @Published var isShowAlertStandart: StandartAlertData? = nil
    @Published var showEmptyView = false
    
    let sdk: NetworkManagerIos
    let sdkKMM: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var sharePreferenses : SharedPreferenses
    //let netConnection = NetMonitor.shared
    
    @Published var currentDateResponce : DateResponse.DateItem? = nil
    
    var selectDate : String = ""
    var msgCalendar : String = ""
    @Published var scheduleForDate : [VisitResponse.VisitItem] = []
    @Published var scheduleAll : [VisitResponse.VisitItem] = []
    
    @Published var selectBrtanch : SettingsAllBranchHospitalItem? = nil
    var listBranch : [SettingsAllBranchHospitalItem] = []
    
    @Published var docMonthTitleDate = ""
    @Published var arrMonth : [Date] = []
    @Published var raspSotrMont : [AllRaspSotrItem] = []
    @Published var docMonthHourWork = "0"
    @Published var docMonthError = ""
    
    
    init(){
        sdk=NetworkManagerIos()
        sharePreferenses = SharedPreferenses()
        //netConnection.startMonitoring()
        
        
        let currentDate = Date.now
        let dateFormatterGet = DateFormatter()
        dateFormatterGet.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
        dateFormatterGet.dateFormat = "LLLL yyyy"
        docMonthTitleDate = dateFormatterGet.string(from: currentDate)
        
        let previousMonth = Calendar.current.date(byAdding: .month, value: -1, to: Date())
        let nextMonth = Calendar.current.date(byAdding: .month, value: 1, to: Date())
        
        
        self.arrMonth.append(previousMonth!)
        self.arrMonth.append(currentDate)
        self.arrMonth.append(nextMonth!)
        
        self.findAllRaspSotr( currentDate, true)
    }
    
    func findAllRaspSotr(_ date: Date, _ isRequestNext: Bool ){
        if(sharePreferenses.currentUserInfo == nil){
            return
        }
        
        self.showDialogLoading = true
        
        let dateFormatterGet = DateFormatter()
        dateFormatterGet.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
        dateFormatterGet.dateFormat = "MM.yyyy"
        let strDate = dateFormatterGet.string(from: date)
        
        let idDoc = String(sharePreferenses.currentDocInfo?.id_doctor ?? 0)
        let dbName = sharePreferenses.currentCenterInfo?.db_name ?? ""
        let accessToc = sharePreferenses.currentUserInfo?.token ?? ""
        
        sdk.findAllRaspSotr(date: strDate, idDoc: idDoc, dbName: dbName,accessToken:accessToc, responseF: {(r: [AllRaspSotrItem]) -> Void in
            DispatchQueue.main.async {
                if(r.count>1 || r[0].naim_filial != nil){
                    
                    var tmpArr = r
                    
                    let dateFormatterGet = DateFormatter()
                    dateFormatterGet.dateFormat = "dd.MM.yyyy"
                    
                    tmpArr.sort { (s1 : AllRaspSotrItem, s2 : AllRaspSotrItem) -> Bool in
                        dateFormatterGet.date(from: s1.data!)! < dateFormatterGet.date(from: s2.data!)!
                    }
                    
                    self.raspSotrMont = tmpArr
                    self.sumWorkHour(r)
                    
                }else{
                    self.docMonthHourWork = "0"
                    self.raspSotrMont = []
                }
                
                self.docMonthError = ""
                
                
                if(isRequestNext == true){
                    self.getgetBranchByIdDoc()
                }else{
                    self.showDialogLoading = false
                }
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("TimetableDocPresenter/findAllRaspSotr \(e)")
                self.docMonthError = "Не удалось загрузить информацию на месяц"
                self.raspSotrMont = []
                self.docMonthHourWork = "0"
                
                if(isRequestNext == true){
                    self.getgetBranchByIdDoc()
                }else{
                    self.showDialogLoading = false
                }
            }
        })
    }
    
    func sumWorkHour(_ raspSotrMont : [AllRaspSotrItem]){
        var sum: CLong = 0
        
        for i in raspSotrMont{
            if(i.start == i.end){
                continue
            }
            
            let dateFormatterGet = DateFormatter()
            dateFormatterGet.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
            dateFormatterGet.dateFormat = "HH:mm"
            
            let dStart = Int(dateFormatterGet.date(from: i.start!)!.timeIntervalSince1970)
            let dStop = Int(dateFormatterGet.date(from: i.end!)!.timeIntervalSince1970)
            
            var rez = dStop - dStart
             
            if(i.obed_start?.count == 5 && i.obed_end?.count == 5){
                let odStart = Int(dateFormatterGet.date(from: i.obed_start!)!.timeIntervalSince1970)
                let odStop = Int(dateFormatterGet.date(from: i.obed_end!)!.timeIntervalSince1970)
                rez -= odStop - odStart
            }
            
            sum += rez
        }
        
        let sum22 = Float(sum)/(60*60)
        let roundedValue = round(sum22 * 10) / 10.0
        
        
        self.docMonthHourWork = String(roundedValue)
        
    }
    
    func getgetBranchByIdDoc(){
        if(sharePreferenses.currentUserInfo == nil){
            return
        }
        
        self.showDialogLoading = true
        
        let idDoc = String(sharePreferenses.currentDocInfo?.id_doctor ?? 0)
        let dbName = sharePreferenses.currentCenterInfo?.db_name ?? ""
        let accessToc = sharePreferenses.currentUserInfo?.token ?? ""
        
        sdk.getAllHospitalBranchForDoc(idDoc: idDoc, dbName: dbName,accessToken:accessToc, responseF: {(r: [SettingsAllBranchHospitalItem]) -> Void in
            DispatchQueue.main.async {
                if(r.count>1 || r[0].naim_filial != nil){
                    
                    var list = r
                    list = r.sorted { $0.naim_filial!.compare($1.naim_filial!) == .orderedAscending }
                    self.listBranch = list
                    
                    var cuerrentFBranch = self.sharePreferenses.doctorSelectBranch
                    if(cuerrentFBranch == nil || cuerrentFBranch!.isEmpty){
                        cuerrentFBranch = String( list[0].id_filial ?? 0)
                    }
                    list.forEach{i in
                        let strN = String(i.id_filial!)
                        if(strN == cuerrentFBranch){
                            self.selectBrtanch = i
                            return
                        }
                    }
                    
                    if(self.selectBrtanch == nil){
                        
                        self.selectBrtanch = list[0]
                        
                    }
                    
                    self.selectNeqBranch()
                }else{
                    self.showDialogLoading = false
                    self.showStandartAlert("Ошибка!","Не удалось загрузить информацию о филиалах")
                }
            }
        }, errorM: {(e: String) -> Void in
            DispatchQueue.main.async {
                LoggingTree.INSTANCE.e("TimetableDocPresenter/getgetBranchByIdDoc \(e)")
                self.showDialogLoading = false
                self.showStandartAlert("Ошибка!","Не удалось загрузить информацию о филиалах")
            }
        })
    }
    
    
    
    func selectNeqBranch(){
        if(selectBrtanch != nil){
            //self.selectBrtanch = item
            self.getDateAndCallAllReception(branch: selectBrtanch!.id_filial!)
        }else{
            self.showDialogLoading = false
            self.isShowAlertStandart = StandartAlertData(titel: "Ошибка", text: "Что-то пошло не так.", isShowCansel: false ,  someFuncOk: {() -> Void in
                self.isShowAlertStandart = nil
            }, someFuncCancel: {() -> Void in})
        }
    }
    
    func getDateAndCallAllReception(branch: Int){
       
        self.showDialogLoading = true
        
        sdkKMM.currentDateApiCall( completionHandler: { response, error in
            if let res : DateResponse = response {
                DispatchQueue.main.async {
                    self.currentDateResponce = res.response
                    self.getAllReceptionDoc(branch, res.response!.today!)
                }
            } else {
                DispatchQueue.main.async {
                    if let t=error{
                        LoggingTree.INSTANCE.e("TimetableDocPresenter/getDateAndCallAllReception \(t)")
                    }
                    
                    self.showDialogLoading = false
                }
            }
        })
    }
    
    func getAllReceptionDoc(_ branch: Int, _ date: String){
        if(sharePreferenses.currentUserInfo == nil){
            return
        }
        
        self.showDialogLoading = true
        
        let brString = String(branch)
        
        let idDoc = String(sharePreferenses.currentDocInfo?.id_doctor ?? 0)
        let dbName = sharePreferenses.currentCenterInfo?.db_name ?? ""
        let accessToken = sharePreferenses.currentUserInfo?.token ?? ""
        
        sdkKMM.getAllReceptionApiCall(branch: brString, dateMonday: date,
                                      h_Auth: accessToken, h_dbName: dbName,  h_idDoc: idDoc,  completionHandler: { response, error in
            if let res : VisitResponse = response {
                
                DispatchQueue.main.async {
                    self.showDialogLoading = false
                    
                    if(res.response.count > 1 || res.response[0].date != nil){
                        
                        self.showEmptyView = false
                        self.scheduleAll = res.response
                        
                        if(!self.selectDate.isEmpty){
                            self.selectDate(self.selectDate)
                        }
                    }else{
                        self.showEmptyView = true
                        self.scheduleAll = []
                        
                    }
                    
                    if(date == self.currentDateResponce!.today){
                        self.selectDate(date)
                    }
                }
            } else {
                DispatchQueue.main.async {
                    if let t=error{
                        LoggingTree.INSTANCE.e("TimetableDocPresenter/getAllReceptionDoc \(t)")
                    }
                    
                    self.showDialogLoading = false
                }
            }
        }
        )
    }
    
    
    func selectDate(_ date: String){
        self.selectDate = date
        
        if(scheduleAll.count == 0){
            scheduleForDate = []
        }else{
            var tmpList : [VisitResponse.VisitItem] = []
            scheduleAll.forEach{i in
                if(i.date == date){
                    tmpList.append(i)
                }
            }

            scheduleForDate = tmpList
        }
    }
    
    func selectNewMonth(date: Date){
        let dateFormatterGet = DateFormatter()
        dateFormatterGet.locale = Locale.init(identifier: Locale.preferredLanguages.first!)
        dateFormatterGet.dateFormat = "LLLL yyyy"
        docMonthTitleDate = dateFormatterGet.string(from: date)
        
        self.findAllRaspSotr(date, false)
    }
    
    func checkShowDocMonthError(){
        if(!self.docMonthError.isEmpty){
            self.showStandartAlert("Ошибка!",self.docMonthError)
        }
    }
    
    func clickLoadNewWeek(_ dateMonday : String){
        currentDateResponce?.lastMonday = dateMonday
        self.getAllReceptionDoc(selectBrtanch!.id_filial!, dateMonday)
    }
    
    func showStandartAlert(_ title: String, _ text: String){
        self.isShowAlertStandart = StandartAlertData(titel: title, text: text, isShowCansel: false ,  someFuncOk: {() -> Void in
            self.isShowAlertStandart = nil
        }, someFuncCancel: {() -> Void in})
    }
    
}
