//
//  AllRaspSotrResponse.swift
//  CallMed_ios
//
//  Created by Mihail on 20.01.2023.
//

import Foundation

struct AllRaspSotrResponse : Decodable,Encodable {
    var error : Bool?
    var message : String?
    var response : [AllRaspSotrItem]?
}
