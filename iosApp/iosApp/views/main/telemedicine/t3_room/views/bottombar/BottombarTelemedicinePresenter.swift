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
    let MAX_LENGTH_SIMPLE_CLICK = 0.15
    static let MAX_DURATION_OF_ONE_AUDIO_MSG = 180 //
    static let MAX_DURATION_OF_ONE_VIDEO_MSG = 30
    
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
    
    @Published var typeRecordBtn = Constants.MsgRoomType.REC_AUD
    @Published var isShowRecordBtn = true
    @Published var isButtonRecordPressed = false
    {
        didSet {
            if(isButtonRecordPressed){
                self.textFild = "0.00"
                if(typeRecordBtn == Constants.MsgRoomType.REC_AUD){
                    self.startRecordAudio()
                }else{
                    self.startRecordVideo()
                }
            }else{
                self.textFild = ""
                if(typeRecordBtn == Constants.MsgRoomType.REC_AUD){
                    self.stopRecordAudio()
                }else{
                    self.stopRecordVideo()
                }
            }
        }
    }
    
    let workWithFiles = WorkWithFiles()
    
    var pathToFileRecord : URL? = nil
    var recordVideoPad: RecordVideoPad
    
    
    init(item: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>, listener: BottombarTelemedicineListener, recordVideoPad: RecordVideoPad){
        self.itemRecord = item
        self.listener = listener
        self.recordVideoPad = recordVideoPad
    
        self.recordVideoPad.bottomBarListener = BottomBarListener(sendVideoMsg: {(i: String) -> Void in
            self.listener?.sendVideoMsg(i)
        }, errorMsg: {(i: String, j: String) -> Void in
            self.listener?.showAlertMsg(i,j)
        })
    }
    
    func selectFileFromPhotoLibrary(){
        self.listener?.selectFileFromPhotoLibrary()
    }
    func selectFileFromOtherPlace(){
        self.listener?.selectFileFromOtherPlace()
    }
    
    func clickSendMsg(){
        let tmp = self.textFild
        
        if(tmp.count != 0){
            self.textFild = ""
            self.listener?.sendMsg(tmp)
        }
    }
    
    var lastClickRecord: Date? = nil
    var isPresCurrent: Bool = false
    func setIsButtonPressed(_ isPres: Bool){
        if(self.isPresCurrent != isPres){
            //print(">>>>>-2 setIsButtonPressed \(isPres)")
            if(isPres){
                self.lastClickRecord = MDate.getCurrentDate()
                self.asincProcessingStateRecordBtn()
            }else{
                let currDateTpm = MDate.getCurrentDate()
                if(self.lastClickRecord != nil && (currDateTpm.timeIntervalSince1970 - self.lastClickRecord!.timeIntervalSince1970 ) >  MAX_LENGTH_SIMPLE_CLICK){
                    self.isButtonRecordPressed = isPres
                }
                self.lastClickRecord = nil
            }
            self.isPresCurrent = isPres
        }
    }
    func asincProcessingStateRecordBtn(){
        DispatchQueue.main.asyncAfter(deadline: .now() + MAX_LENGTH_SIMPLE_CLICK) {
            //print(">>>>>0 lastClickRecord == nil \(self.lastClickRecord == nil)")
            if(self.lastClickRecord != nil){
                if(self.isPresCurrent == true){
                    //print(">>>>>1 lastClickRecord != nil")
                    self.isButtonRecordPressed = self.isPresCurrent
                }
            }else{
                if(self.isPresCurrent == false){
                    self.nextRecordType()
                    //print(">>>>>2 nextRecordType \(self.typeRecordBtn.rawValue)")
                }
            }
        }
    }
    
    func cancelRecord(){
        if(typeRecordBtn == Constants.MsgRoomType.REC_AUD){
            cancelRecordAudio()
        }else{
            cancelRecordVideo()
        }
    }
    
 
    func startRecordAudio(){
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
               // self.selectedShow = TVShow(name: i)
                self.listener?.showAlertMsg("",i)
            }, filePath: self.pathToFileRecord!)
        }
    }
    func stopRecordAudio(){
//        if(self.pointTimerStarted == nil){
//            return
//        }
        
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
    func cancelRecordAudio(){
        AudioRecorHandler.shared.finishRecording()
        if(pathToFileRecord != nil){
            self.workWithFiles.deleteFileFromDocumentsByURL(pathToFileRecord!)
            self.pathToFileRecord = nil
        }
        //self.selectedShow = TVShow(name: "Отменено")
        self.listener?.showAlertMsg("", "Отменено" )
    }
    
    
    
    func startRecordVideo(){
        DispatchQueue.main.async {
            self.pointTimerStarted = MDate.getCurrentDate()
            self.startRepeatShowTimer()
            
            let idRoom: String = String(Int(self.itemRecord!.wrappedValue!.idRoom!))
            let nameFile = self.workWithFiles.getNewNameForNewFile(idRoom, "mp4")
            if(nameFile != nil){
                self.pathToFileRecord = self.workWithFiles.getDocumentsDirectory()?.appendingPathComponent(nameFile!)
                
                if(self.pathToFileRecord == nil){
                    return
                }
                
                self.recordVideoPad.startRecord(pathToFileRecord: self.pathToFileRecord!)
            }
            
        }

    }
    func stopRecordVideo(){
//        if(self.pointTimerStarted == nil){
//            return
//        }
        
        self.stopRepeatShowTimer()
        
        if(self.isButtonRecordPressed == true){
            self.isButtonRecordPressed = false
        }
        
        self.recordVideoPad.stopRecord()
        self.pathToFileRecord = nil
    }

    
    func cancelRecordVideo(){
        self.recordVideoPad.cancel()
        
        self.pathToFileRecord = nil
        self.listener?.showAlertMsg("","Отменено")
    }
    
    
    var pointTimerStarted: Date? = nil
    func startRepeatShowTimer(){
        if(pointTimerStarted == nil){
            
            if(typeRecordBtn == Constants.MsgRoomType.REC_AUD){
                self.stopRecordAudio()
            }else{
                self.stopRecordVideo()
            }
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
        
        if((typeRecordBtn == Constants.MsgRoomType.REC_AUD && timeHasPassedSec >= BottombarTelemedicinePresenter.MAX_DURATION_OF_ONE_AUDIO_MSG) ||
           (typeRecordBtn == Constants.MsgRoomType.VIDEO && timeHasPassedSec >= BottombarTelemedicinePresenter.MAX_DURATION_OF_ONE_VIDEO_MSG)){
            pointTimerStarted = nil
           
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
    
    func nextRecordType(){
        if(self.typeRecordBtn == Constants.MsgRoomType.REC_AUD){
            self.typeRecordBtn = Constants.MsgRoomType.VIDEO
        }else{
            self.typeRecordBtn = Constants.MsgRoomType.REC_AUD
        }
    }
}
