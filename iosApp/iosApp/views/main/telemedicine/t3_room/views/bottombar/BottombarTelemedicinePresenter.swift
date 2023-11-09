//
//  BottombarTelemedicinePresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared
import AVFoundation

class BottombarTelemedicinePresenter: ObservableObject {
    
    
    var itemRecord: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>? = nil
    var listener: BottombarTelemedicineListener? = nil
    
    @Published var textFild: String = ""{
        willSet {
            if(newValue.length > 0){
                if(isButtonRecordPressed){
                    isShowRecordBtn = true
                }else{
                    isShowRecordBtn = false
                }
            }else{
                isShowRecordBtn = true
            }
        }
    }
    @Published var isShowRecordBtn = true
    @Published var isButtonRecordPressed = false
    {
        didSet {
            if(isButtonRecordPressed){
                self.textFild = "0.00"
                self.startRecord()
            }else{
                self.textFild = ""
                self.stopRecord()
            }
        }
    }
    @Published var selectedShow: TVShow?
    
    let workWithFiles = WorkWithFiles()
    
    
    init(item: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>, listener: BottombarTelemedicineListener){
        self.itemRecord = item
        self.listener = listener
    }
    
    func selectFileFromPhotoLibrary(){
        self.listener?.selectFileFromPhotoLibrary()
    }
    func selectFileFromOtherPlace(){
        self.listener?.selectFileFromOtherPlace()
    }
    
    func clickSendMsg(){
        let tmp = self.textFild
        //print(">>>>>>!!!>>>>>> onTapGesture \(tmp)")
        
        if(tmp.count != 0){
            self.textFild = ""
            self.listener?.sendMsg(tmp)
        }
    }
    
    func setIsButtonPressed(_ isPres: Bool){
        if(self.isButtonRecordPressed != isPres){
            self.isButtonRecordPressed = isPres
            //print(">>>>> onTapGesture \(isPres)")
        }
    }
    func cancelRecord(){
        AudioRecorHandler.shared.finishRecording()
        if(pathToFileRecord != nil){
            self.workWithFiles.deleteFileFromDocumentsByURL(pathToFileRecord!)
            self.pathToFileRecord = nil
        }
        
        self.selectedShow = TVShow(name: "Отменено")
        
    }
    
    
    var pathToFileRecord : URL? = nil
    func startRecord(){
        DispatchQueue.main.async {
            self.pointTimerStarted = MDate.getCurrentDate()
            self.startRepeatShowTimer()
            
            let idRoom: String = String(Int(self.itemRecord!.wrappedValue!.idRoom!))
            let nameFile = self.workWithFiles.getNewNameForNewFile(idRoom, "wav")
            if(nameFile != nil){
                self.pathToFileRecord = self.workWithFiles.getDocumentsDirectory()?.appendingPathComponent(nameFile!)
            }
            
            if(self.pathToFileRecord == nil){
                return
            }
            
            AudioRecorHandler.shared.clickRecord(failMsg: {(i: String) -> Void in
                self.selectedShow = TVShow(name: i)
            }, filePath: self.pathToFileRecord!)
        }
    }
    func stopRecord(){
        if(self.pointTimerStarted == nil){
            return
        }
        
        AudioRecorHandler.shared.finishRecording()
        
        self.stopRepeatShowTimer()
        
        if(self.isButtonRecordPressed == true){
            self.isButtonRecordPressed = false
        }
        
        
        if(pathToFileRecord != nil){
            
            Task {
                let asset = AVURLAsset(url: pathToFileRecord!, options: nil)
                // Returns a CMTime value.
                let duration = try await asset.load(.duration)
                let t1 =  duration.seconds
                
                let allSec = Int.init(t1)
               
                DispatchQueue.main.async {
                    if(allSec < 1){
                        self.workWithFiles.deleteFileFromDocumentsByURL(self.pathToFileRecord!)
                        self.pathToFileRecord = nil
                    }else{
                        let tmp = self.pathToFileRecord!.lastPathComponent
                        self.listener?.sendRecordMsg(self.pathToFileRecord!.lastPathComponent)
                        self.pathToFileRecord = nil
                    }
                }

            }
        }
    }
    
    
    var pointTimerStarted: Date? = nil
    func startRepeatShowTimer(){
        if(pointTimerStarted == nil){
            self.stopRecord()
            return
        }
        
        let curTime = MDate.getCurrentDate()
        let timeHasPassedSecTI = curTime.timeIntervalSince1970 - pointTimerStarted!.timeIntervalSince1970
        let timeHasPassedSec: Int
        if(timeHasPassedSecTI == 0){
            timeHasPassedSec = 0
        }else{
            timeHasPassedSec = Int(timeHasPassedSecTI)
        }
        
        if(timeHasPassedSec >= Constants.MAX_DURATION_OF_ONE_AUDIO_MSG){
            pointTimerStarted = nil
            return
        }
        
        let min = String(timeHasPassedSec / 60)
        let secInt = timeHasPassedSec % 60
        let sec: String
        if(secInt<10){
            sec = "0\(secInt)"
        }else{
            sec = String(secInt)
        }
        
        self.textFild = "\(min):\(sec)"
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            self.startRepeatShowTimer()
        }
    }
    func stopRepeatShowTimer(){
        self.pointTimerStarted = nil
    }
}
