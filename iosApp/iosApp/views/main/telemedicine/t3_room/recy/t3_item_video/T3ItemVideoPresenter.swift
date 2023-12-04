//
//  T3ItemVideoPresenter.swift
//  iosApp
//
//  Created by Михаил Хари on 28.11.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import AVFoundation

class T3ItemVideoPresenter  : ObservableObject{
    var item: MessageRoomResponse.MessageRoomItem
    var idRoom: String
    var showAlert: ((String, String) -> Void)
    
    @Published var duration = "0:00"
    
    var urlVideo: URL?
    
    @Published var isShowLoad = false
    var loaderFileForChat: LoaderFileForChat = LoaderFileForChat()
    
    @Published var player: AVPlayer? = nil
    
    let BASE_WIDHT_AND_HIGTH_VIDEO_VIEW: CGFloat = 280
    @Published var heightVideoPlayer: CGFloat = 280
    @Published var widthVideoPlayer: CGFloat = 280
    
    @Published var isShowTimer = false
    @Published var tiemerTime = "00:00"

    
    init(item: MessageRoomResponse.MessageRoomItem, idRoom: String, showAlert: @escaping  (String, String) -> Void){
//        if(self.item != nil && self.item.idMessage == item.idMessage){
//            return
//        }
       
        
        self.item = item
        self.idRoom = idRoom
        self.showAlert = showAlert
        
        if(item.isShowLoading){
            isShowLoad = true
        }else{
            isShowLoad = false
        }
        
        print(">>>>>>> T3ItemVideoPresenter.init \(item.idMessage)")
        
        if(item.text != nil && !item.text!.isEmpty &&  !item.text!.contains(s: "http")){
            self.getFile()
        }else{
            self.isShowLoad = true
            self.loadFile(item)
        }
        
        self.getDurationFile()
    }
    
    func getDurationFile(){
        if(self.urlVideo == nil){
            return
        }
        Task {
            let asset = AVURLAsset(url: urlVideo!, options: nil)
            // Returns a CMTime value.
            let duration = try await asset.load(.duration)
            let t1 =  duration.seconds
            
            let allSec = Int.init(t1)
           
            let timeStr = self.durationSecToStringFormatted(allSec)
            
            DispatchQueue.main.async {
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
    
    func getFile(){
        let fileName = item.text!
        let path = getDocumentsDirectory()
        
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "mp4" }  // во внутренню папку все сохраняютсяв пнг
            
            for i in allFilesPngURL {
                if i.absoluteString.contains(fileName){
                    self.urlVideo = i
                }
            }
            
            if(self.urlVideo != nil){
                
                let aspectRatioOfVideo = self.initAspectRatioOfVideo(with: self.urlVideo!)
                self.calculateViewVideoSize(aspectRatioOfVideo)
                
                DispatchQueue.main.async {
                    self.player = AVPlayer(url: self.urlVideo!)
                }
            }
            
        } catch {
            LoggingTree.INSTANCE.e("T3ItemVideoPresenter/getImage", error)
        }
    }
    func loadFile(_ item: MessageRoomResponse.MessageRoomItem){
        loaderFileForChat.loadVideo(item: item, processingFileComplete: {(i: MessageRoomResponse.MessageRoomItem) -> Void in
            RealmDb.shared.updateItemMessageText(item: item)
            self.getFile()
            DispatchQueue.main.async {
                self.isShowLoad = false
            }
            
        }, errorBack: {(i: MessageRoomResponse.MessageRoomItem, title: String, text: String) -> Void in
            DispatchQueue.main.async {
                self.isShowLoad = false
            }
            self.showAlert(title,text)
        }, idRoom: self.idRoom)
    }
    func initAspectRatioOfVideo(with fileURL: URL) -> Double {
      let resolution = resolutionForLocalVideo(url: fileURL)
      guard let width = resolution?.width, let height = resolution?.height else {
         return 0
      }
      return Double(height / width)
    }
    private func resolutionForLocalVideo(url: URL) -> CGSize? {
        guard let track = AVURLAsset(url: url).tracks(withMediaType: AVMediaType.video).first else { return nil }
        let size = track.naturalSize.applying(track.preferredTransform)
        return CGSize(width: abs(size.width), height: abs(size.height))
    }
    private func calculateViewVideoSize(_ aspectRatioOfVideo: Double){
        
        if(aspectRatioOfVideo > 1){
           // height biger
            let newHeight = BASE_WIDHT_AND_HIGTH_VIDEO_VIEW * aspectRatioOfVideo
            DispatchQueue.main.async {
                self.heightVideoPlayer = newHeight
            }
             
        }else if(aspectRatioOfVideo < 1){
            // width biger
            let newWidth = BASE_WIDHT_AND_HIGTH_VIDEO_VIEW / aspectRatioOfVideo
            DispatchQueue.main.async {
                self.widthVideoPlayer = newWidth
            }
            
        }
    
    }
    
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
    
    func cklickVideo(){
        if(isShowTimer){
            finishShowTimePlayback()
        }else{
            startShowTimePlayback()
        }
    }
    func finishShowTimePlayback(){
        self.player?.pause()
        self.player?.seek(to: .zero)
        self.isShowTimer = false
    }
    func startShowTimePlayback(){
        self.player?.play()
        self.tiemerTime = "0:00"
        self.isShowTimer = true
        repeatPartShowTimePlayback()
    }
    func repeatPartShowTimePlayback() {
        if(!self.isShowTimer){
            return
        }
        
        let currentTime = self.player?.currentTime().seconds ?? 0
        let durationTime = self.player?.currentItem?.duration.seconds ?? 0
        let timeLost = Int.init(durationTime - currentTime)
        //print(" >>>>>>>>>> time \(durationTime - currentTime)")
        let timeLostStr = (timeLost<10) ? "00:0\(String.init(timeLost))" : "00:\(String.init(timeLost))"
        
        self.tiemerTime = timeLostStr
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
            self.repeatPartShowTimePlayback()
        }
    }
    
}
