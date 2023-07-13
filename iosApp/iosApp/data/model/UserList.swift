//
//  UserList.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import Foundation

struct UserList: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : [UserResponse]?
}
