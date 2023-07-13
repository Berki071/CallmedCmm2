//
//  AllRaspSotrItem.swift
//  CallMed_ios
//
//  Created by Mihail on 20.01.2023.
//

import Foundation

struct AllRaspSotrItem: Decodable,Encodable,Hashable, Identifiable{
    let id = UUID()
    var data : String?
    var start : String?
    var end : String?
    var obed_start : String?
    var obed_end : String?
    var naim_filial : String?
}
