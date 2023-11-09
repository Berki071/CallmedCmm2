//
//  T3ItemFilePresenter.swift
//  iosApp
//
//  Created by Михаил Хари on 09.11.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class T3ItemFilePresenter: ObservableObject {
    var item: MessageRoomResponse.MessageRoomItem
    var idRoom: String
    var showAlert: ((String, String) -> Void)
    
    @Published var isShowLoad = false
    @Published var nameF = ""
    
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
            nameF = item.text!
        }else{
            isShowLoad = true
            loadFile(item)
        }

    }
    
    func loadFile(_ item: MessageRoomResponse.MessageRoomItem){
        loaderFileForChat.load(item: item, processingFileComplete: {(i: MessageRoomResponse.MessageRoomItem) -> Void in
            RealmDb.shared.updateItemMessageText(item: item)
            self.nameF = item.text!
            self.isShowLoad = false
            
        }, errorBack: {(i: MessageRoomResponse.MessageRoomItem, title: String, text: String) -> Void in
            self.isShowLoad = false
            self.showAlert(title,text)
        }, idRoom: self.idRoom)
    }
    
    
}
