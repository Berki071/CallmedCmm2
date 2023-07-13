//
//  SettingsAllBranchHospitalItem.swift
//  CallMed_ios
//
//  Created by Mihail on 12.12.2022.
//

import Foundation

struct SettingsAllBranchHospitalItem: Decodable,Encodable,Hashable{
    var id_filial : Int?
    var naim_filial : String?
    
    var isFavorite = false
    
    enum CodingKeys: CodingKey {
        case id_filial
        case naim_filial
    }
}
