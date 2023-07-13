//
//  DateItem.swift
//  CallMed_ios
//
//  Created by Mihail on 14.12.2022.
//

import Foundation

struct DateItem: Decodable,Encodable{
    var today : String?
    var week_day : String?
    var last_monday : String?
}
