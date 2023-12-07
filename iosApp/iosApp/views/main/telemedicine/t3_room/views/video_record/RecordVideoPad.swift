//
//  RecordVideoPad.swift
//  iosApp
//
//  Created by Михаил Хари on 06.12.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import AVFoundation

struct BottomBarListener{
    var sendVideoMsg: (String) -> Void
    var errorMsg: (String, String) -> Void
}
struct PreviewVideoRListener{
    var setUrl: (URL?) -> Void
}
struct RoomViewListener{
    var setUrl: (URL?) -> Void
}

class RecordVideoPad{
    var previewViderRListener: PreviewVideoRListener? = nil
    var bottomBarListener: BottomBarListener? = nil
    var roomViewListener: RoomViewListener? = nil
    
    var currentAction: CurrentAction = CurrentAction.Stopped
    
    let workWithFiles = WorkWithFiles()
    
    var recorVideoURL2: URL? = nil{
        didSet{
            previewViderRListener?.setUrl(recorVideoURL2)
            roomViewListener?.setUrl(recorVideoURL2)
            //print(">>>>>>> sendUrl RecordVideoPad recorVideoURL2 url \(recorVideoURL2) previewViderRListener \(previewViderRListener==nil) roomViewListener \(roomViewListener==nil)")
        }
    }
    
        
    func fileWithViedeoCreated(_ url: URL){
        //print(">>>>>>> fileWithViedeoCreated \(currentAction)")
        
        if(currentAction == CurrentAction.Stopped){
            Task {
//                let fExist = FileManager.default.fileExists(atPath: url.path)
//                let size = self.fileSize(forURL: url)
//                print(">>>>>>> fileWithViedeoCreated fExist=\(fExist) size=\(size)")
                
                let asset = AVURLAsset(url: url, options: nil)
                // Returns a CMTime value.
                let duration = try await asset.load(.duration)
                let t1 =  duration.seconds
                let allSec = Int.init(t1)
                
                //print(">>>>>>> fileWithViedeoCreated allSec=\(t1)")
                
                DispatchQueue.main.async {
                    if(allSec < 1){
                        self.workWithFiles.deleteFileFromDocumentsByURL(url)
                    }else{
                        let tmp = url.lastPathComponent
                        self.bottomBarListener?.sendVideoMsg(url.lastPathComponent)
                    }
                }
                
            }
            
        }else if (currentAction == CurrentAction.Canceled){
            self.workWithFiles.deleteFileFromDocumentsByURL(url)
        }
    }
    func showError(_ title: String, _ msg: String){
        self.bottomBarListener?.errorMsg(title, msg)
    }
    
    func startRecord(pathToFileRecord : URL){
        self.currentAction = CurrentAction.Recording
        self.recorVideoURL2 = pathToFileRecord
    
        //print(">>>>>> sendUrl sendUrl startRecord tmp \(recorVideoURL2)  pathToFileRecord \(pathToFileRecord)")
        
    }
    func stopRecord(){
        if(self.currentAction == CurrentAction.Canceled){
            return
        }
        
        //print(">>>>>>> RecordVideoPad stopRecord")
        self.currentAction = CurrentAction.Stopped
        self.recorVideoURL2 = nil
    }
    func cancel(){
       // print(">>>>>>> RecordVideoPad cancel")
        self.currentAction = CurrentAction.Canceled
        self.recorVideoURL2 = nil
    }
    
    func fileSize(forURL url: Any) -> Double {
        var fileURL: URL?
        var fileSize: Double = 0.0
        if (url is URL) || (url is String)
        {
            if (url is URL) {
                fileURL = url as? URL
            }
            else {
                fileURL = URL(fileURLWithPath: url as! String)
            }
            var fileSizeValue = 0.0
            try? fileSizeValue = (fileURL?.resourceValues(forKeys: [URLResourceKey.fileSizeKey]).allValues.first?.value as! Double?)!
            if fileSizeValue > 0.0 {
                fileSize = (Double(fileSizeValue) / (1024 * 1024))
            }
        }
        return fileSize
    }
    
    enum CurrentAction {
        case Recording
        case Stopped
        case Canceled
    }
}
