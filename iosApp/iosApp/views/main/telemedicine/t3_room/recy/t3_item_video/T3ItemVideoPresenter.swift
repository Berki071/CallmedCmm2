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
import SwiftUI

class T3ItemVideoPresenter: ObservableObject{
    var item: MessageRoomResponse.MessageRoomItem
    var idRoom: String
    var showAlert: ((String, String) -> Void)
    
    @Published var duration = "0:00"
    
    var urlVideo: URL?
    
    @Published var isShowLoad = false
    var loaderFileForChat: LoaderFileForChat = LoaderFileForChat()
    
    @Published var player: AVPlayer? = nil
    
    let BASE_WIDHT_AND_HIGTH_VIDEO_VIEW: CGFloat = 280
    
    @Published var heightVideoPlayer: CGFloat = 320
    @Published var widthVideoPlayer: CGFloat = 280
    
    
    @Published var isShowTimer = false
    @Published var tiemerTime = "00:00"
    
    @Published var iuImageLogo : UIImage? = nil

    
    init(item: MessageRoomResponse.MessageRoomItem, idRoom: String, showAlert: @escaping  (String, String) -> Void){
        self.item = item
        self.idRoom = idRoom
        self.showAlert = showAlert
        
        if(item.isShowLoading){
            isShowLoad = true
        }else{
            isShowLoad = false
        }
        
        if(item.text != nil && !item.text!.isEmpty &&  !item.text!.contains(s: "http")){
            self.getImgFromVideo()
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
    
    func getImgFromVideo(){
        let fileName = item.text!
        let path = getDocumentsDirectory()
        
        
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "mp4" }
            
            for i in allFilesPngURL {
                if i.absoluteString.contains(fileName){
                    self.urlVideo = i
                }
            }
            
            if(self.urlVideo != nil){
                if let thumbnailImage = getThumbnailImage(forUrl: self.urlVideo!) {
                    
                    let aspectRatioOfVideo = self.initAspectRatioOfVideo(with: self.urlVideo!)
                    self.calculateViewVideoSize(aspectRatioOfVideo)
                    
                    if let cachedImage = ImageCache.shared.get(forKey: fileName) {
                        self.iuImageLogo = cachedImage
                        //print(">>>>>>>! Img from Cash")
                    }else{
                        let imageView = UIImageView()
                        imageView.image = thumbnailImage
                        let uiImTmp = imageView.image?.rotate(radians: .pi/2)  // .pi/2=90
                        
                        if(uiImTmp != nil){
                            ImageCache.shared.set(uiImTmp!, forKey: fileName)
                        }
                        
                        self.iuImageLogo = uiImTmp
                        //print(">>>>>>>! Img from Video")
                        
                    }
                 }
            }
            
        } catch {
            LoggingTree.INSTANCE.e("T3ItemVideoPresenter/getImage", error)
        }
    }
    func getThumbnailImage(forUrl url: URL) -> UIImage? {
        let asset: AVAsset = AVAsset(url: url)
        let imageGenerator = AVAssetImageGenerator(asset: asset)

        do {
            let thumbnailImage = try imageGenerator.copyCGImage(at: CMTimeMake(value: 1, timescale: 60), actualTime: nil)
            return UIImage(cgImage: thumbnailImage)
        } catch let error {
            print(error)
        }

        return nil
    }
    
//    func getFile(){
//        let fileName = item.text!
//        let path = getDocumentsDirectory()
//        
//        do {
//            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
//            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "mp4" }
//            
//            for i in allFilesPngURL {
//                if i.absoluteString.contains(fileName){
//                    self.urlVideo = i
//                }
//            }
//            
//            if(self.urlVideo != nil){
//                
//                let aspectRatioOfVideo = self.initAspectRatioOfVideo(with: self.urlVideo!)
//                self.calculateViewVideoSize(aspectRatioOfVideo)
//                
//                DispatchQueue.main.async {
//                    self.player = AVPlayer(url: self.urlVideo!)
//                }
//            }
//            
//        } catch {
//            LoggingTree.INSTANCE.e("T3ItemVideoPresenter/getImage", error)
//        }
//    }
    func loadFile(_ item: MessageRoomResponse.MessageRoomItem){
        loaderFileForChat.loadVideo(item: item, processingFileComplete: {(i: MessageRoomResponse.MessageRoomItem) -> Void in
            RealmDb.shared.updateItemMessageText(item: item)
            self.getImgFromVideo()
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
            if(self.urlVideo == nil){
                return
            }
            
            startShowTimePlayback()
        }
    }
    func finishShowTimePlayback(){
        self.player?.pause()
        //self.player?.seek(to: .zero)
        self.player = nil
        self.isShowTimer = false
    }
    func startShowTimePlayback(){
        self.player = AVPlayer(url: self.urlVideo!)
        self.player?.play()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.tiemerTime = "0:00"
            self.isShowTimer = true
            self.repeatPartShowTimePlayback()
        }
    }
    func repeatPartShowTimePlayback() {
        if(!self.isShowTimer){
            return
        }
        
        if(self.player != nil){
            let currentTime: Double = self.player!.currentTime().seconds ?? 0
            let durationTime: Double  = self.player!.currentItem?.duration.seconds ?? 0
            
            if(durationTime > 0){
                let timeLost = Int.init(durationTime - currentTime)
                //print(" >>>>>>>>>> time \(durationTime - currentTime)")
                let timeLostStr = (timeLost<10) ? "00:0\(String.init(timeLost))" : "00:\(String.init(timeLost))"
                
                self.tiemerTime = timeLostStr
            }
        }
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
            self.repeatPartShowTimePlayback()
        }
    }
    
}

extension UIImage {
    func rotate(radians: Float) -> UIImage? {
        //radians: .pi/2=90
        
        var newSize = CGRect(origin: CGPoint.zero, size: self.size).applying(CGAffineTransform(rotationAngle: CGFloat(radians))).size
        // Trim off the extremely small float value to prevent core graphics from rounding it up
        newSize.width = floor(newSize.width)
        newSize.height = floor(newSize.height)
        
        UIGraphicsBeginImageContextWithOptions(newSize, false, self.scale)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        
        // Move origin to middle
        context.translateBy(x: newSize.width/2, y: newSize.height/2)
        // Rotate around middle
        context.rotate(by: CGFloat(radians))
        // Draw the image at its center
        self.draw(in: CGRect(x: -self.size.width/2, y: -self.size.height/2, width: self.size.width, height: self.size.height))
    
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage
    }
}
