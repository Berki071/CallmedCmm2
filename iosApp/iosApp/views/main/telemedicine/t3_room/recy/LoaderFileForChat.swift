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
        
        if(newFileName == nil){
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
    
}
