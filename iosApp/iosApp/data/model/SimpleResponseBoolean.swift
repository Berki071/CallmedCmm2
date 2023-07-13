//
//  SimpleResponseBoolean.swift
//  CallMed_ios
//
//  Created by Mihail on 06.12.2022.
//

import Foundation

struct SimpleResponseBoolean: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : Bool?
}
