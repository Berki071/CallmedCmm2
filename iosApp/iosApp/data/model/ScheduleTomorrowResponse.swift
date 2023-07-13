//
//  ScheduleTomorrowResponse.swift
//  CallMed_ios
//
//  Created by Mihail on 04.05.2023.
//

import Foundation

struct ScheduleTomorrowResponse: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : [ScheduleTomorrowItem]?
}
