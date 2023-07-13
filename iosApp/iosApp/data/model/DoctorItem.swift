//
//  DoctorItem.swift
//  CallMed_ios
//
//  Created by Mihail on 09.12.2022.
//

import Foundation

struct DoctorItem: Decodable,Encodable{
    var id_doctor : Int?
    var full_name : String?
    var id_spec : String?
    var stag : String?
    var specialty : String?
    var dop_info : String?
    var image_url : String?
}
