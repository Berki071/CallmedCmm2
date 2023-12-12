//
//  BottombarTelemedicinePresenter2.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared

class BottombarTelemedicinePresenter2: ObservableObject {
    
    @Published var isDisableChat: Bool = false
    var showBtnActionSendAnimation: DarwinBoolean
    
    init(item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?, showBtnActionSendAnimation: DarwinBoolean){
        self.showBtnActionSendAnimation = showBtnActionSendAnimation
        
        
        if(item == nil){
            self.isDisableChat = false
        }else{
            if(item!.status! == Constants.TelemedicineStatusRecord.active()){
                self.isDisableChat = false
            }else{
                self.isDisableChat = true
            }
        }
    }
    
}
