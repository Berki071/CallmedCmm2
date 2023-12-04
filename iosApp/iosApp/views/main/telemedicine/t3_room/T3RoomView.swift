//
//  T3RoomView.swift
//  CallMed_ios
//
//  Created by Mihail on 08.06.2023.
//

import SwiftUI
import shared
//import UIKit

struct FlippedUpsideDown: ViewModifier {
   func body(content: Content) -> some View {
    content
      .rotationEffect(.radians(.pi))
      .scaleEffect(x: -1, y: 1, anchor: .center)
   }
}
extension View{
   func flippedUpsideDown() -> some View{
     self.modifier(FlippedUpsideDown())
   }
}

struct T3RoomView: View {
    @StateObject var mainPresenter: T3RoomPresenter
    //@ObservedObject var mainPresenter: T3RoomPresenter
    var clickBack: () -> Void
    
    @State private var showSheet = false    //ImagePicker bool для показа в sheet
    @State var fileName = "no file chosen"  //fileImporter
    @State var openFile = false             //fileImporter bool для показа
    
    
//    func activateProximitySensor() {
//        UIDevice.current.isProximityMonitoringEnabled = true
//    }
//    func deactivateProximitySensor() {
//        UIDevice.current.isProximityMonitoringEnabled = false
//    }
    
//    @State var itemTmp: AllRecordsTelemedicineItem? = AllRecordsTelemedicineItem(server_key: "1", data_server:"2", id_room: 3, status: "4", id_kl: 5, id_filial: 6, specialty: "7", full_name_kl: "8", dr_kl: "9", komment_kl: "10", fcm_kl: "11", tmId: 12, tm_name: "13", tm_type: "14", tm_price: 15, tm_time_for_tm: 16, timeStartAfterPay: 17, dataStart: "18", data_end: "19", dataPay: "20", status_pay: "21", about: "22", about_full: "23", notif_24: "24", notif_12: "25", notif_4: "26", notif_1: "27")
    
    init(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem, clickBack: @escaping () -> Void){
        //print(">>>>>>> T3RoomView.init \(item.idRoom)")
        
        self.clickBack = clickBack
        _mainPresenter = StateObject(wrappedValue:T3RoomPresenter(item: item))
        //mainPresenter = T3RoomPresenter(item: item)
        
       /// UIDevice.current.isProximityMonitoringEnabled = true
    }
    init(idRoom: String, idTm: String, clickBack: @escaping () -> Void){
        //print(">>>>>>> T3RoomView.init \(idRoom)")
        
        self.clickBack = clickBack
        _mainPresenter = StateObject(wrappedValue:T3RoomPresenter(idRoom: idRoom, idTm: idTm ))
        
       // UIDevice.current.isProximityMonitoringEnabled = true
    }
    
    var body: some View {
        
        //let _ = Self._printChanges()
        
        if(self.mainPresenter.isShowMediaView == true){
            ShowMediaTelemedicineView(itemRecord: self.$mainPresenter.recordTItem, clickBack: {() -> Void in
                self.mainPresenter.isShowMediaView = false
            })
            
        }else if(self.mainPresenter.isShowBigImageOrFile != nil){
            ShowImagePage(itemForShowBigImage5: self.$mainPresenter.isShowBigImageOrFile)
            
        }else if(self.mainPresenter.isShowAnalizesView == true && self.mainPresenter.recordTItem != nil){
            AnaliseResultPage(clickBack: {() -> Void in
                self.mainPresenter.isShowAnalizesView = false
            }, recordTItem: self.mainPresenter.recordTItem!)
        }else if(self.mainPresenter.isShowConclusionsView == true){
            ElectronicConclusionsPage(clickBack: {() -> Void in
                self.mainPresenter.isShowConclusionsView = false
            }, recordTItem: self.mainPresenter.recordTItem!)
        }else{
            ZStack{
                VStack{
                    ScrollViewReader { proxy in
                        VStack{
                            if(self.mainPresenter.recyList.count > 0){
                                List(self.mainPresenter.recyList, id: \.idMessage) { i in
                                    
                                    if(i.type == Constants.MsgRoomType.DATE()){
                                        T3ItemDate(item: i)
                                            .listRowSeparator(.hidden)
                                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                            .id(Int.init(truncating: i.idMessage!))
                                            .flippedUpsideDown()
                                        
                                    }else if(i.type  == Constants.MsgRoomType.TARIFF()){
                                        T3ItemTariff(item: i)
                                            .listRowSeparator(.hidden)
                                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                            .id(Int.init(truncating: i.idMessage!))
                                            .flippedUpsideDown()
                                    }else if(i.type  == Constants.MsgRoomType.TEXT()){
                                        T3ItemMsg(item: i, clickRemuveItem: {(k: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.clickRemuveItem(k)
                                        })
                                        .listRowSeparator(.hidden)
                                        .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                        .id(i.idMessage!)
                                        .flippedUpsideDown()
                                    }else if(i.type  == Constants.MsgRoomType.IMG()){
                                        T3ItemImg(item: i, showBigImage: {(j: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.showBigImage(j)
                                        }, clickRemuveItem: {(k: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.clickRemuveItem(k)
                                        }, idRoom: self.mainPresenter.getIdRoom(), showAlert: {(i: String, j: String) -> Void in
                                            self.mainPresenter.showAlet(i,j)
                                        })
                                        .listRowSeparator(.hidden)
                                        .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                        .id(Int.init(truncating: i.idMessage!))
                                        .flippedUpsideDown()
                                    }else if(i.type  == Constants.MsgRoomType.FILE()){
                                        T3ItemFile(item: i, showBigDoc: {(j: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.showBigImage(j)
                                        }, clickRemuveItem: {(k: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.clickRemuveItem(k)
                                        }, idRoom: self.mainPresenter.getIdRoom(), showAlert: {(i: String, j: String) -> Void in
                                            self.mainPresenter.showAlet(i,j)}
                                        )
                                        .listRowSeparator(.hidden)
                                        .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                        .id(Int.init(truncating: i.idMessage!))
                                        .flippedUpsideDown()
                                    }else if(i.type  == Constants.MsgRoomType.REC_AUD()){
                                        T3ItemRecordAudio(item: i, clickRemuveItem: {(k: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.clickRemuveItem(k)
                                        }, idRoom: self.mainPresenter.getIdRoom(), showAlert: {(i: String, j: String) -> Void in
                                            self.mainPresenter.showAlet(i,j)}
                                        )
                                        .listRowSeparator(.hidden)
                                        .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                        .id(Int.init(truncating: i.idMessage!))
                                        .flippedUpsideDown()
                                    }else if(i.type  == Constants.MsgRoomType.VIDEO()){
                                        T3ItemVideo(item: i, clickRemuveItem: {(k: MessageRoomResponse.MessageRoomItem) -> Void in
                                            self.mainPresenter.clickRemuveItem(k)
                                        }, idRoom: self.mainPresenter.getIdRoom(), showAlert: {(i: String, j: String) -> Void in
                                            self.mainPresenter.showAlet(i,j)}
                                        )
                                        .listRowSeparator(.hidden)
                                        .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                        .id(Int.init(truncating: i.idMessage!))
                                        .flippedUpsideDown()
                                    }else{
                                        Text("")
                                            .listRowSeparator(.hidden)
                                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                                            .font(.system(size: 1))
                                           // .id(18881412322155)
                                            .frame(height: 1)
                                        
                                    }
                                }
                                .id(UUID())
                                .listStyle(PlainListStyle())
                                .flippedUpsideDown()
                                .onTapGesture {
                                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                                }
                            }else{
                                Spacer()
                            }
                            
                        }
                        .frame(maxWidth: .infinity)
                        .background(.white)
                    }
                    
                    BottombarTelemedicine(item: self.$mainPresenter.recordTItem, listener: BottombarTelemedicineListener(sendMsg: {(msg: String) -> Void in
                        self.mainPresenter.sendMessage(msg)
                    }, sendRecordMsg: {(fileName: String) -> Void in
                        self.mainPresenter.createRecordMsg(fileName)
                    }, showAlertMsg: {(i: String, j: String) -> Void in
                        self.mainPresenter.showAlet(i,j)
                    }, makePhoto: {() -> Void in
                        self.mainPresenter.isStartCamera = true
                        self.showSheet = true
                        
                    }, selectFileFromPhotoLibrary: {() -> Void in
                        self.mainPresenter.isStartCamera = false
                        self.showSheet = true
                    }, selectFileFromOtherPlace: {() -> Void in
                        self.openFile.toggle()
                    }))
                }
                .padding(.top, 48.0)
            
                
                //48.0
                ToolbarTelemedicineRoom(item: self.$mainPresenter.recordTItem, listener: ToolbarTelemedicineRoomListener(goToMedia: {() -> Void in
                //ToolbarTelemedicineRoom(item: self.$itemTmp, listener: ToolbarTelemedicineRoomListener(goToMedia: {() -> Void in
                    self.mainPresenter.isShowMediaView = true
                }, closeTm: {() -> Void in
                    self.mainPresenter.closeRecordTelemedicine(self.mainPresenter.recordTItem!)
                }, clickBack: {() -> Void in
                    self.mainPresenter.isStoppedT3View = true
                    PadForMyFirebaseMessagingService.shared.showIdRoom = nil
                    PadForMyFirebaseMessagingService.shared.listenerT3 = nil
                    self.clickBack()
                }, clickStartReception: {() -> Void in
                    self.mainPresenter.clickStartReception()
                }, clickCompleteReception: {() -> Void in
                    self.mainPresenter.clickCompleteReception()
                }, showAnalizes: {() -> Void in
                    self.mainPresenter.isShowAnalizesView = true
                }, showConclusions: {() -> Void in
                    self.mainPresenter.isShowConclusionsView = true
                }))
                
                if(self.mainPresenter.isShowAlertStandart != nil){
                    StandartAlert(dataOb: mainPresenter.isShowAlertStandart!)
                }
                
                ZStack{
                    if(self.mainPresenter.showDialogLoading == true){
                        LoadingView()
                        //.opacity(self.mainPresenter.showDialogLoading == true ? 1 : 0)
                    }
                }

                
            }
            .sheet(isPresented: $showSheet) {
                //sheet новая вьюха поверх имеющихся со скрытием свайпом вниз
                if(self.mainPresenter.isStartCamera){
                    ImagePicker(sourceType: .camera, item: self.$mainPresenter.recordTItem, listener: ImagePickerListener(error: {() -> Void in
                        self.mainPresenter.showAlet("","Что-то пошло не так.")
                    }, imageCreated: {(i: String) -> Void in
                        self.mainPresenter.createImageMsg(i)
                    }) )
                }else{
                    ImagePicker(sourceType: .photoLibrary, item: self.$mainPresenter.recordTItem, listener: ImagePickerListener(error: {() -> Void in
                        self.mainPresenter.showAlet("","Что-то пошло не так.")
                    }, imageCreated: {(i: String) -> Void in
                        self.mainPresenter.createImageMsg(i)
                    }) )
                }
            }
//            .onAppear() {
//                 self.activateProximitySensor()
             .onDisappear() {
                 AudioPlayerHandler.shared.stopAudio()
                 //self.deactivateProximitySensor()
             }
            .fileImporter( isPresented: $openFile, allowedContentTypes: [.image,.pdf], allowsMultipleSelection: false, onCompletion: {
                (Result) in
                
                do{
                    let fileUrl = try Result.get().first
                    self.fileName = fileUrl?.lastPathComponent ?? "file not available"
                    
                    guard  let URLString = fileUrl?.absoluteString.lowercased() else {
                        LoggingTree.INSTANCE.e("T3RoomView/fileImporter URLString == nil :\(fileName)" )
                        return
                    }
                    
                    if (fileUrl != nil && (URLString.hasSuffix("jpg") || URLString.hasSuffix("jpeg") || URLString.hasSuffix("png"))) {
                        self.mainPresenter.saveImageByUrl(fileUrl!)
                    }else if(fileUrl != nil && (URLString.hasSuffix("pdf"))){
                        self.mainPresenter.saveFileByUrl(fileUrl!)
                    }else {
                        LoggingTree.INSTANCE.e("T3RoomView/fileImporter неизвестное расширение :\(fileName) \(fileUrl == nil)" )
                    }
                    
                }
                
                
                
                catch{
                    LoggingTree.INSTANCE.e("T3RoomView/fileImporter error reading file \(error.localizedDescription)" )
                    //print("error reading file \(error.localizedDescription)")
                }
                
            })

        }
    }
    

}

struct T3RoomView_Previews: PreviewProvider {
    static var previews: some View {
        T3RoomView(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem(), clickBack: {() -> Void in } )
    }
}
