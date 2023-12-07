//
//  VideoRecordingView.swift
//  iosApp
//
//  Created by Михаил Хари on 04.12.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import AVKit
import SwiftUI

//https://stackoverflow.com/questions/61141039/swiftui-recording-video-with-live-preview

struct MyPreviewVideoRecording: View {
    @StateObject var mainPresenter: MyPreviewVideoRecordingPresenter
    
    init(recordVideoPad: RecordVideoPad){
        _mainPresenter = StateObject(wrappedValue: MyPreviewVideoRecordingPresenter(recordVideoPad: recordVideoPad))
    }
    
    var body: some View {
        ZStack {
            GeometryReader { proxy in
                ZStack(alignment: .bottom){
                    VideoRecordingView(recordPath: self.$mainPresenter.recorVideoURL,
                                       erroMsg: {(i: String) -> Void in
                        DispatchQueue.main.async {
                            self.mainPresenter.recordVideoPad.showError("",i)
                        }
                    }, aspectRatioM: {(asp: Double) -> Void in
                        self.mainPresenter.setAspectRatioOfVideo(asp)
                    }, fileCreated: {(url: URL) -> Void in
                        self.mainPresenter.recordVideoPad.fileWithViedeoCreated(url)
                    })
                    .frame(width: self.mainPresenter.widthVideoPlayer, height: self.mainPresenter.heightVideoPlayer)
                }
                .frame(width: self.mainPresenter.BASE_WIDHT_AND_HIGTH_VIDEO_VIEW, height: self.mainPresenter.BASE_WIDHT_AND_HIGTH_VIDEO_VIEW, alignment: .center)
            }
            .frame(width: self.mainPresenter.BASE_WIDHT_AND_HIGTH_VIDEO_VIEW,  height: self.mainPresenter.BASE_WIDHT_AND_HIGTH_VIDEO_VIEW, alignment: .center)
            .cornerRadius(self.mainPresenter.BASE_CORNER_RADIUS)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color("black_bg65").edgesIgnoringSafeArea(.all))
        .padding(.bottom, 53.0)
    }
}

class MyPreviewVideoRecordingPresenter: ObservableObject{
    var aspectRatioOfVideo = 1.5   //Double(height / width)
    
    let BASE_WIDHT_AND_HIGTH_VIDEO_VIEW: CGFloat = 320
    let BASE_CORNER_RADIUS: CGFloat = 160
    
    @Published var heightVideoPlayer: CGFloat
    @Published var widthVideoPlayer: CGFloat
    @Published var recorVideoURL: URL?

    
    var recordVideoPad: RecordVideoPad  //recordPath !=nil начинается запись, == nil заканчивается
    
    init(recordVideoPad: RecordVideoPad){
        self.recordVideoPad = recordVideoPad
        
        heightVideoPlayer = BASE_WIDHT_AND_HIGTH_VIDEO_VIEW * 1.5
        widthVideoPlayer = BASE_WIDHT_AND_HIGTH_VIDEO_VIEW
        
        self.recorVideoURL = self.recordVideoPad.recorVideoURL2
        self.recordVideoPad.previewViderRListener = PreviewVideoRListener(setUrl: {(i: URL?) -> Void in
            self.recorVideoURL = i
        })
    }
    
    func setAspectRatioOfVideo(_ value: Double){
        self.aspectRatioOfVideo = value
        self.calculateViewVideoSize()
    }
    
    private func calculateViewVideoSize(){
        
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
}


struct VideoRecordingView: UIViewRepresentable {
    @Binding var recordPath: URL?   //есть recordPath начинается запись, == nil заканчивается
//    {
//        willSet{
//            print(">>>>>>> VideoRecordingView will recordPath =  \(recordPath)")
//        }
//        didSet{
//            print(">>>>>>> VideoRecordingView did recordPath =  \(recordPath)")
//        }
//    }
    
    var erroMsg: ((String) -> Void)? = nil
    var aspectRatioM: ((Double) -> Void)? = nil
    var fileCreated: ((URL) -> Void)? = nil

    
    func makeUIView(context: UIViewRepresentableContext<VideoRecordingView>) -> PreviewView {
        let recordingView2 = PreviewView(aspectRatioM: {(y: Double) -> Void in
            self.aspectRatioM?(y)
        })
        
        recordingView2.erroMsg = {(i : String) -> Void in
            self.erroMsg?(i)
        }
        recordingView2.fileCreated = {(i : URL) -> Void in
            self.fileCreated?(i)
        }
        
        return recordingView2
    }
    
    func updateUIView(_ uiViewController: PreviewView, context: UIViewRepresentableContext<VideoRecordingView>) {
       // print(">>>>>>> VideoRecordingView updateUIView \(recordPath)")
        
        if(recordPath == nil){
            uiViewController.stopRecording()
        }else{
            uiViewController.recordPath = recordPath!
            uiViewController.startRecording()
        }
    }
}

extension PreviewView: AVCaptureFileOutputRecordingDelegate{
    func fileOutput(_ output: AVCaptureFileOutput, didFinishRecordingTo outputFileURL: URL, from connections: [AVCaptureConnection], error: Error?) {
        //print(">>>>>>> fileOutput \(outputFileURL.absoluteString)")
        self.fileCreated?(outputFileURL)
    
    }

}

class PreviewView: UIView {
    var recordPath: URL?
    var erroMsg: ((String) -> Void)? = nil
    var fileCreated: ((URL) -> Void)? = nil
    

    private var captureSession: AVCaptureSession?
    let videoFileOutput = AVCaptureMovieFileOutput()
    var recordingDelegate:AVCaptureFileOutputRecordingDelegate!
    
    var lastAction: LastAction? = nil
    var errorExist: String? = nil
    
    init(aspectRatioM: ((Double) -> Void)) {
        super.init(frame: .zero)
    
        var allowedAccess = false
        let blocker = DispatchGroup()
        blocker.enter()
        AVCaptureDevice.requestAccess(for: .video) { flag in
            allowedAccess = flag
            blocker.leave()
        }
        blocker.wait()
        
        if !allowedAccess {
            //print(">>>>>>> !!! NO ACCESS TO CAMERA")
            errorExist = "NO ACCESS TO CAMERA"
            //self.erroMsg?("NO ACCESS TO CAMERA")
            return
        }
        
        // setup session
        let session = AVCaptureSession()
        session.beginConfiguration()
        
        let videoDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back)
        guard videoDevice != nil, let videoDeviceInput = try? AVCaptureDeviceInput(device: videoDevice!), session.canAddInput(videoDeviceInput) else {
            errorExist = "NO CAMERA DETECTED"
            return
        }
       
        
        guard let audioDevice = AVCaptureDevice.default(for: AVMediaType.audio) else {
            errorExist = "Default audio device is unavailable."
            return
        }
        let audioInput = try? AVCaptureDeviceInput(device: audioDevice)
        
        session.sessionPreset = AVCaptureSession.Preset.medium
        session.addInput(videoDeviceInput)
        if audioInput != nil && session.canAddInput(audioInput!) {
            session.addInput(audioInput!)
        } else {
            errorExist = "Couldn't add audio device input to the session."
            return
        }
        session.commitConfiguration()
        self.captureSession = session
        
        let tmp1: AVCaptureDeviceInput? = captureSession?.inputs.first as? AVCaptureDeviceInput
        if(tmp1 != nil){
            let dims : CMVideoDimensions = CMVideoFormatDescriptionGetDimensions(tmp1!.device.activeFormat.formatDescription)
            let r: Double = Double(dims.width)/Double(dims.height) // разрешения как на боку приходит поэтому не height/width
            aspectRatioM(r)
        }
        
        //print(">>>>>>> PreviewView init")
    }
    
    override class var layerClass: AnyClass {
        AVCaptureVideoPreviewLayer.self
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    var videoPreviewLayer: AVCaptureVideoPreviewLayer {
        return layer as! AVCaptureVideoPreviewLayer
    }
    
    override func didMoveToSuperview() {
        //print(">>>>>>> didMoveToSuperview")
        
        super.didMoveToSuperview()
        recordingDelegate = self
        
        stopRecording()
        videoFileOutput.stopRecording()
        
        if nil != self.superview {
            self.videoPreviewLayer.session = self.captureSession
            self.videoPreviewLayer.videoGravity = .resizeAspect
           
            self.captureSession?.startRunning()
            
            //print(">>>>>>> didMoveToSuperview != ")
            
        } else {
            self.captureSession?.stopRunning()
            //print(">>>>>>> didMoveToSuperview == ")
        }
    }
    
    
    func startRecording(){
        if errorExist != nil {
            self.erroMsg?(errorExist!)
            return
        }
        
        if(recordPath == nil){
            return
        }
        
        if(lastAction == LastAction.START){
            return
        }
        lastAction = LastAction.START
        
        captureSession?.addOutput(videoFileOutput)
        videoFileOutput.startRecording(to: recordPath!, recordingDelegate: recordingDelegate)
        
        //print(">>>>>>> 🟢 startRecording \(videoFileOutput.isRecording) \(captureSession == nil)")
    }
    
    func stopRecording(){
        if(lastAction == LastAction.STOP){
            return
        }
        lastAction = LastAction.STOP
        
        videoFileOutput.stopRecording()
        self.captureSession?.stopRunning()

        //print(">>>>>>> 🔴 stopRecording \(videoFileOutput.isRecording)")
    }
    
    enum LastAction{
        case START
        case STOP
    }
}


