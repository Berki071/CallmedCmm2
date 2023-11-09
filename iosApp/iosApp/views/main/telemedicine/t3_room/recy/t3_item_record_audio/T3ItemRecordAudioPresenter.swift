//
//  T3ItemRecordAudioPresenter.swift
//  iosApp
//
//  Created by Михаил Хари on 17.08.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import AVFoundation
import UIKit


class T3ItemRecordAudioPresenter : ObservableObject {
    var item: MessageRoomResponse.MessageRoomItem
    var idRoom: String
    var showAlert: ((String, String) -> Void)
    
    var audioP: AudioPlayerHandler? = nil
    @Published var isShowPlayIamge = true
    @Published var duration = "0:00"
    var progressBarMax : Int = 0  //millsec
    @Published var progressBarValue = 0.0 // как часть от 100%
    
    var isStartCheckPlayback = false
    
    var urlAudio: URL?
    
    @Published var isShowLoad = false
    
    var loaderFileForChat: LoaderFileForChat = LoaderFileForChat()
    
    
    init(item: MessageRoomResponse.MessageRoomItem, idRoom: String, showAlert: @escaping  (String, String) -> Void){
        self.item = item
        self.idRoom = idRoom
        self.showAlert = showAlert
        
        self.getDurationFile()
        
        if(item.isShowLoading){
            isShowLoad = true
        }else{
            isShowLoad = false
        }
        
        if(item.text != nil && !item.text!.isEmpty){
            self.getFile()
        }else{
            isShowLoad = true
            loadFile(item)
        }
    }
    
    func loadFile(_ item: MessageRoomResponse.MessageRoomItem){
        loaderFileForChat.load(item: item, processingFileComplete: {(i: MessageRoomResponse.MessageRoomItem) -> Void in
            RealmDb.shared.updateItemMessageText(item: item)
            self.getFile()
            self.isShowLoad = false
            
        }, errorBack: {(i: MessageRoomResponse.MessageRoomItem, title: String, text: String) -> Void in
            self.isShowLoad = false
            self.showAlert(title,text)
        }, idRoom: self.idRoom)
    }
    
    
    func activateProximitySensor() {
        UIDevice.current.isProximityMonitoringEnabled = true
    }
    func deactivateProximitySensor() {
        UIDevice.current.isProximityMonitoringEnabled = false
    }
    
    func getFile(){
        let fileName = item.text!
        let path = getDocumentsDirectory()
        
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "wav" }  // во внутренню папку все сохраняютсяв пнг
            
            for i in allFilesPngURL {
                if i.absoluteString.contains(fileName){
                    self.urlAudio = i
                }
            }
            
        } catch {
            LoggingTree.INSTANCE.e("T3ItemImg/getImage", error)
        }
    }
    func getDurationFile(){
        if(self.urlAudio == nil){
            return
        }
        Task {
            let asset = AVURLAsset(url: urlAudio!, options: nil)
            // Returns a CMTime value.
            let duration = try await asset.load(.duration)
            let t1 =  duration.seconds
            
            let allSec = Int.init(t1)
           
            let timeStr = self.durationSecToStringFormatted(allSec)
            
            DispatchQueue.main.async {
                self.progressBarMax = Int.init(t1 * 1000)
                self.duration = timeStr
            }

        }
    }
    
    func durationSecToStringFormatted(_ allSec: Int) -> String {
        let sec = allSec % 60
        let min = String(Int.init(allSec / 60))
        
        let strSec: String
        
        if(sec < 10){
            strSec = "0\(String(sec))"
        }else{
            strSec = String(sec)
        }
        
        return "\(min):\(strSec)"
    }
    
    var proximityState: Bool = false // по умолчанию false
    func clickPlay(){
       // print(">>>> clickPlay \(item.text)")
        
        getFile()
        
        if(self.urlAudio == nil){
            return
        }
        
        proximityState = false
        
        AudioPlayerHandler.shared.fileUrl = self.urlAudio!
        AudioPlayerHandler.shared.listenrIsPlaying = {(i: Bool) -> Void in
            self.isShowPlayIamge = !i
            
            if(self.isShowPlayIamge){
                self.stopRepeatCheckPlayback()
            }
        }
        AudioPlayerHandler.shared.playAudio()

        self.isStartCheckPlayback = true
        self.startRepeatCheckPlayback()
        
    }
    func clickStop(){
        AudioPlayerHandler.shared.stopAudio()
        AudioPlayerHandler.shared.listenrIsPlaying = nil
        self.stopRepeatCheckPlayback()
    }
    
    func startRepeatCheckPlayback(){
        if(!self.isStartCheckPlayback){
            return 
        }
        
        activateProximitySensor()
        
        let allMillSec = AudioPlayerHandler.shared.getCurrentTimeMillSec()
        let durationMillSec = AudioPlayerHandler.shared.getDurationMillSec()
        
        let timeStr = self.durationSecToStringFormatted(allMillSec/1000)
        
        self.duration = timeStr
        self.progressBarValue = (Double(allMillSec) * 100) / Double(durationMillSec)
        
        //print(">>>> \(duration) \(String(progressBarValue))")
        
        if(!AudioPlayerHandler.shared.myAudioPlayer.isPlaying){
            AudioPlayerHandler.shared.stopAudio()
        }

 
        let proxStateTmp = UIDevice.current.proximityState
        if(proxStateTmp != self.proximityState){
            self.proximityState = proxStateTmp
            if(proxStateTmp){
                AudioPlayerHandler.shared.configureAudioSessionToEarSpeaker()
            }else{
                AudioPlayerHandler.shared.configureAudioSessionToSpeaker()
            }
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            self.startRepeatCheckPlayback()
        }
    }
    func stopRepeatCheckPlayback(){
        deactivateProximitySensor()
        
        self.isStartCheckPlayback = false
        self.progressBarValue = 0.0
        getDurationFile()
        AudioPlayerHandler.shared.configureAudioSessionToSpeaker()
        //self.activateProximitySensor(false)
    }
    
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
    

    
}
