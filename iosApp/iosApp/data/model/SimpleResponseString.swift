//
//  SimpleResponseString.swift
//  CallMed_ios
//
//  Created by Mihail on 17.01.2023.
//

import Foundation

struct SimpleResponseString: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : String?
}
