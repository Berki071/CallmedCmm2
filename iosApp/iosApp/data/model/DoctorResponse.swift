//
//  DoctorResponse.swift
//  CallMed_ios
//
//  Created by Mihail on 09.12.2022.
//

import Foundation

struct DoctorResponse: Decodable,Encodable{
    var error : Bool?
    var message : String?
    var response : [DoctorItem]?
}
