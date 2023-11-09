//
//  T3ItemPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared

class T3ItemImgPresenter: ObservableObject {
    var item: MessageRoomResponse.MessageRoomItem
    var idRoom: String
    var showAlert: ((String, String) -> Void)
    
    @Published var iuImageLogo : UIImage? = nil
    
    @Published var isShowLoad = false
    
    var loaderFileForChat: LoaderFileForChat = LoaderFileForChat()
    
    init(item: MessageRoomResponse.MessageRoomItem, idRoom: String, showAlert: @escaping  (String, String) -> Void){
        self.item = item
        self.idRoom = idRoom
        self.showAlert = showAlert
        
        if(item.isShowLoading){
            isShowLoad = true
        }else{
            isShowLoad = false
        }
        
        if(item.text != nil && !item.text!.isEmpty){
            self.getImage()
        }else{
            isShowLoad = true
            loadFile(item)
        }
    }
    
    func loadFile(_ item: MessageRoomResponse.MessageRoomItem){
        loaderFileForChat.load(item: item, processingFileComplete: {(i: MessageRoomResponse.MessageRoomItem) -> Void in
            RealmDb.shared.updateItemMessageText(item: item)
            self.getImage()
            self.isShowLoad = false
            
        }, errorBack: {(i: MessageRoomResponse.MessageRoomItem, title: String, text: String) -> Void in
            self.isShowLoad = false
            self.showAlert(title,text)
        }, idRoom: self.idRoom)
    }
    
    
    func getImage(){
        let fileName = item.text!
        
        let path = getDocumentsDirectory()
        
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "png" }  // во внутренню папку все сохраняютсяв пнг
            
            for i in allFilesPngURL {
                if i.absoluteString.contains(fileName){
                    self.urlToUIImage(i)
                    return
                }
            }
            
        } catch {
            LoggingTree.INSTANCE.e("T3ItemImg/getImage", error)
        }
    }
    
    func urlToUIImage(_ urlF: URL){
        do {
            var imageData : Data? = try Data(contentsOf: urlF)
            
            guard let dataOfImage = imageData else { return }
            guard let image = UIImage(data: dataOfImage) else { return }
            self.iuImageLogo = image
            let tttt0 = image == nil
            let tttt = self.iuImageLogo == nil
            
            //print(">>>>>>!!!>>>>> \(dataOfImage.count)")
        } catch {
            //print(error.localizedDescription)
        }
                    
    }
    
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
    
    
  
}
