//
//  BottombarTelemedicinePresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared

class BottombarTelemedicinePresenter: ObservableObject {
    
    
    var itemRecord: Binding<AllRecordsTelemedicineItem?>? = nil
    var listener: BottombarTelemedicineListener? = nil
    
    @Published var textFild: String = ""
 

    init(item: Binding<AllRecordsTelemedicineItem?>, listener: BottombarTelemedicineListener){
        self.itemRecord = item
        self.listener = listener
    }
    
    func selectFileFromPhotoLibrary(){
        self.listener?.selectFileFromPhotoLibrary()
    }
    func selectFileFromOtherPlace(){
        self.listener?.selectFileFromOtherPlace()
    }
    
    func clickSendMsg(){
        let tmp = self.textFild
        //print(">>>>>>!!!>>>>>> onTapGesture \(tmp)")
        
        if(tmp.count != 0){
            self.textFild = ""
            self.listener?.sendMsg(tmp)
        }
    }
}
