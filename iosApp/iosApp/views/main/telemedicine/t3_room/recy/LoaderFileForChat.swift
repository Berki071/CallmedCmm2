//
//  LoaderFileForChat.swift
//  iosApp
//
//  Created by Михаил Хари on 08.11.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class LoaderFileForChat{
    
    var tryLoadCounter = 0
    func load(item: MessageRoomResponse.MessageRoomItem, processingFileComplete: @escaping ((MessageRoomResponse.MessageRoomItem) -> Void), errorBack: @escaping ((MessageRoomResponse.MessageRoomItem, String, String) -> Void), idRoom: String){
        let sdk: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
        var sharePreferenses = SharedPreferenses()
        
        let apiKey = String.init(sharePreferenses.currentUserInfo!.token!)
        let h_dbName = sharePreferenses.currentCenterInfo!.db_name!
        let idDoc = String(Int.init(sharePreferenses.currentDocInfo!.id_doctor!))
        
        let idMsg = String(Int.init(truncating: item.idMessage!))
        
        sdk.getFileForMessageRoom(idMessage: idMsg,
                                    h_Auth: apiKey, h_dbName: h_dbName, h_idDoc: idDoc, completionHandler: { response, error in
            if let res : FileForMsgResponse = response {
                if(res.response[0].data_file == nil){
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        if(self.tryLoadCounter < 10){
                            self.load(item: item, processingFileComplete: processingFileComplete, errorBack: errorBack, idRoom: idRoom)
                            self.tryLoadCounter = self.tryLoadCounter + 1
                        }else{
                            LoggingTree.INSTANCE.e("LoaderFileForChat/load количество попыток загрузки исчерпано")
                            errorBack(item, "Ошибка!", "Не удалось создать файл для сохранения")
                        }
                    }
                    return
                }
                self.tryLoadCounter = 0
                
                item.text = res.response[0].data_file
                self.processingOnImageOrFile(item, processingFileComplete, errorBack, idRoom)
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("LoaderFileForChat/load", t)
                }
                errorBack(item, "Ошибка!", "Не удалось загрузить файл")
                              
            }
        })
    }
    
    func processingOnImageOrFile(_ item: MessageRoomResponse.MessageRoomItem, _ processingFileComplete: ((MessageRoomResponse.MessageRoomItem) -> Void), _ errorBack: ((MessageRoomResponse.MessageRoomItem, String, String) -> Void), _ idRoom: String){
        
        let workWithFiles = WorkWithFiles()
        
        var ext: String
        if(item.type == Constants.MsgRoomType.IMG()){
            ext = "png"
        }else if(item.type == Constants.MsgRoomType.FILE()){
            ext = "pdf"
        }else if(item.type == Constants.MsgRoomType.REC_AUD()){
            ext = "wav"
        }else{
            ext = "pdf"
        }
        
        //String(Int.init(truncating: recordTItem!.idRoom!))
        let newFileName = workWithFiles.getNewNameForNewFile(idRoom, ext, String(Int.init(truncating: item.idMessage!)))
        
        if(newFileName == nil || item.text == nil){
            item.text = "null"
            errorBack(item, "Ошибка!", "Не удалось создать файл для сохранения")
        }else{
            let tmp = workWithFiles.base64ToFile(item.text!, newFileName!, errorListener: {(i:String, j:String) in
                //self.showAlet(i, j)
            })
            
            if (!tmp){
                errorBack(item, "Ошибка!", "Не удалось скопировать файл для сохранения")
            }else{
                item.text = newFileName
                processingFileComplete(item)
            }
        }
    }
    
    
    func loadVideo(item: MessageRoomResponse.MessageRoomItem, processingFileComplete: @escaping ((MessageRoomResponse.MessageRoomItem) -> Void), errorBack: @escaping ((MessageRoomResponse.MessageRoomItem, String, String) -> Void), idRoom: String){
        let sdk: NetworkManagerIos = NetworkManagerIos()
        let sharePreferenses = SharedPreferenses()
        let workWithFiles = WorkWithFiles()
        
        let pathDir = getDocumentsDirectory()
        let newFileName = workWithFiles.getNewNameForNewFile(idRoom, "mp4", String(Int.init(truncating: item.idMessage!)))
        
        let apiKey = String.init(sharePreferenses.currentUserInfo!.token!)
        
        if(newFileName == nil || item.text == nil){
            item.text = "null"
            errorBack(item, "Ошибка!", "Не удалось создать файл для сохранения")
        }else{
            let destinationUrl = pathDir?.appendingPathComponent(newFileName!)
            
            if let destinationUrl = pathDir?.appendingPathComponent(newFileName!) {
                sdk.loadVideoFile(urlStr: "\(item.text!)&token=\(apiKey)", savedURL: destinationUrl, responseF: {() -> Void in
                    self.convertingToFileVideo(newFileName!, destinationUrl, item, processingFileComplete, errorBack)
                }, errorM: {(e: String) -> Void in
                    LoggingTree.INSTANCE.e("LoaderFileForChat/loadStatMkb \(e)")
                    errorBack(item, "Ошибка!", "Не удалось загрузить файл")
                })
            }else{
                item.text = "null"
                errorBack(item, "Ошибка2!", "Не удалось создать файл для сохранения")
            }
        }
    }
    
    func convertingToFileVideo(_ newFileName: String ,_ destinationUrl: URL, _ item: MessageRoomResponse.MessageRoomItem, _ processingFileComplete: @escaping ((MessageRoomResponse.MessageRoomItem) -> Void), _ errorBack: @escaping ((MessageRoomResponse.MessageRoomItem, String, String) -> Void)){
        
        if FileManager.default.fileExists(atPath: destinationUrl.path){
            
            let b64Str: String? = self.gerStringFromFile(destinationUrl, item, errorBack)
            
            if(b64Str == nil){
                errorBack(item, "Ошибка2!", "Файла не существует")
                return
            }
            
            //let strB64 = String(decoding: b64Data!, as: UTF8.self)
            
            var decodedData: Data? = Data(base64Encoded: b64Str!, options: .ignoreUnknownCharacters)
            
            if(decodedData == nil){
                errorBack(item, "Ошибка!", "Что-то пошло не так.")
                return
            }
            
            let text = ""
            do {
                try text.write(to: destinationUrl, atomically: false, encoding: .utf8)
            } catch {
                print(error)
            }
            
            try? decodedData?.write(to: destinationUrl)
            
            item.text = newFileName
            
            processingFileComplete(item)
            
        }else{
            LoggingTree.INSTANCE.e("LoaderFileForChat/convertingToFileVideo destinationUrl not exist")
            errorBack(item, "Ошибка!", "Файла не существует")
        }
    }
    func gerStringFromFile(_ destinationUrl: URL, _ item: MessageRoomResponse.MessageRoomItem, _ errorBack: @escaping ((MessageRoomResponse.MessageRoomItem, String, String) -> Void)) -> String?{
        var string: String? = nil
        do {
           // data = try? Data(contentsOf: destinationUrl)
            string = try String(contentsOf: destinationUrl)
        } catch {
            errorBack(item, "Ошибка!", "Не удалось создать файл для сохранения \((error.localizedDescription))")
        }
        
        return  string
    }
    
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
}
