//
//  DataForListOfEntriesRecy.swift
//  CallMed_ios
//
//  Created by Mihail on 08.06.2023.
//

import Foundation
import shared

struct DataForListOfEntriesRecy: Identifiable {
    var id: Int
    
    var title: String
    var items: [AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem]
    
    var isShowDop = false
}
