//
//  AudioRecorHandler.swift
//  iosApp
//
//  Created by Михаил Хари on 23.08.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import AVFoundation

class AudioRecorHandler: NSObject, ObservableObject, AVAudioRecorderDelegate {
    static let shared = AudioRecorHandler()
    private override init() {
        super.init()
      // myAudioPlayer.delegate = self
    }
    
    var recordingSession: AVAudioSession = AVAudioSession.sharedInstance()
    var audioRecorder: AVAudioRecorder? = nil
    
    
    func clickRecord(failMsg: @escaping ((String) -> Void), filePath: URL){
        do {
            try recordingSession.setCategory(.playAndRecord, mode: .default)
            try recordingSession.setActive(true)
            
            recordingSession.requestRecordPermission() { [unowned self] allowed in
                DispatchQueue.main.async {
                    if allowed {
                        self.startRecord(filePath)
                    } else {
                        // failed to record!
                        failMsg("Необходимо разрешение на запись звука")
                    }
                }
            }
        } catch {
            LoggingTree.INSTANCE.e("AudioRecorHandler/clickRecord \(error)")
        }
    }
    
    func startRecord(_ filePath: URL){
       // let audioFilename = getDocumentsDirectory().appendingPathComponent("recording.m4a")

        let settings = [
            AVFormatIDKey: Int(kAudioFormatLinearPCM),
            AVSampleRateKey: 8000,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.medium.rawValue
        ]

        do {
            audioRecorder = try AVAudioRecorder(url: filePath, settings: settings)
            audioRecorder?.delegate = self
            audioRecorder?.record()

        } catch {
            finishRecording()
        }
    }
    
    func finishRecording() {
        audioRecorder?.stop()
        audioRecorder = nil
    }
    
    //если система грохнула запись
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        if !flag {
            finishRecording()
        }
    }

}
