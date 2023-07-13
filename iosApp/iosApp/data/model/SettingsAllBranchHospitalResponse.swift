//
//  SettingsAllBranchHospitalResponse.swift
//  CallMed_ios
//
//  Created by Mihail on 12.12.2022.
//

import Foundation

struct SettingsAllBranchHospitalResponse: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : [SettingsAllBranchHospitalItem]?
}
