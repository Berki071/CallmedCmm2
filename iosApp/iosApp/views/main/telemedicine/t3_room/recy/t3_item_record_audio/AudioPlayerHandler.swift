//
//  AudioHandler.swift
//  iosApp
//
//  Created by Михаил Хари on 18.08.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import AVFoundation
import UIKit

class AudioPlayerHandler: NSObject, ObservableObject, AVAudioPlayerDelegate {
    static let shared = AudioPlayerHandler()
    private override init() {
        super.init()
        myAudioPlayer.delegate = self
    }
    
    
    
    @Published var isPlaying: Bool = false {
        willSet {
            listenrIsPlaying?(newValue)
        }
    }
    
    var myAudioPlayer = AVAudioPlayer()
    let audioSession = AVAudioSession.sharedInstance()
    
    var fileUrl: URL?
    var listenrIsPlaying: ((Bool) -> Void)?{
        willSet {
            if(listenrIsPlaying != nil && myAudioPlayer.isPlaying){
                stopAudio()
            }
        }
    }
    
    func playAudio() {
        if(self.fileUrl == nil){
            return
        }
        
        isPlaying = true

        configureAudioSessionToSpeaker() //по умолчанию
        do {
            myAudioPlayer = try AVAudioPlayer(contentsOf: fileUrl!)
            myAudioPlayer.play()
        } catch {
            // couldn't load file :(
        }
        
    }
    func stopAudio(){
        isPlaying = false
        myAudioPlayer.stop()
    }
    func getCurrentTimeMillSec() -> Int {
        let t1: Int = Int(myAudioPlayer.currentTime * 1000)
        return t1
    }
    func getDurationMillSec() -> Int {
        let t1: Int = Int(myAudioPlayer.duration * 1000)
        return t1
    }

    func configureAudioSessionToSpeaker(){
        do {
            try audioSession.setCategory(AVAudioSession.Category.playAndRecord, mode: AVAudioSession.Mode.spokenAudio)
            try audioSession.overrideOutputAudioPort(AVAudioSession.PortOverride.speaker)
            try audioSession.setActive(true)

            //print("Successfully configured audio session (SPEAKER-Bottom).", "\nCurrent audio route: ",audioSession.currentRoute.outputs)
        } catch let error as NSError {
            //print("#configureAudioSessionToSpeaker Error \(error.localizedDescription)")
        }
    }

    func configureAudioSessionToEarSpeaker(){
        do { ///Audio Session: Set on Speaker
            try audioSession.setCategory(AVAudioSession.Category.playAndRecord, mode: AVAudioSession.Mode.videoChat)
            try audioSession.overrideOutputAudioPort(AVAudioSession.PortOverride.none)
            try audioSession.setActive(true)
            
            //print("Successfully configured audio session (EAR-Speaker).", "\nCurrent audio route: ",audioSession.currentRoute.outputs)
        }
        catch{
            //print("#configureAudioSessionToEarSpeaker Error \(error.localizedDescription)")
        }
    }
    
    
    
}
