//
//  DataClassForElectronicRecyIos.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class DataClassForElectronicRecyIos : Identifiable {
    let id = UUID()
    var item: DataClassForElectronicRecy
    
    init(item: DataClassForElectronicRecy){
        self.item = item
    }
}
