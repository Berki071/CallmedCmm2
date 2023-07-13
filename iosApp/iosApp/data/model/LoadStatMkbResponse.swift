//
//  LoadStatMkbResponse.swift
//  CallMed_ios
//
//  Created by Mihail on 26.01.2023.
//

import Foundation

struct LoadStatMkbResponse: Decodable,Encodable {
    var error : Bool?
    var message : String?
    var response : [String]?
}
