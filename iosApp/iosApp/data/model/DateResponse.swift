//
//  DateResponse.swift
//  CallMed_ios
//
//  Created by Mihail on 14.12.2022.
//

import Foundation

struct DateResponse: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : DateItem?
}
