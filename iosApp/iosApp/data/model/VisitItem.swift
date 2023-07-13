//
//  VisitItem.swift
//  CallMed_ios
//
//  Created by Mihail on 14.12.2022.
//

import Foundation

struct VisitItem: Decodable,Encodable,Hashable {
    var kab : String?
    var date : String?
    var time : String?
    var fam_kl : String?
    var name_kl : String?
    var otch_kl : String?
    var komment : String?
    var id_client : Int?
    var naim : String?
    var start: String?
    var end: String?
    
    init(){}
    
    init(_ time: String, _ fam_kl: String, _ name_kl: String, _ otch_kl: String, _ naim: String){
        self.time = time
        self.fam_kl=fam_kl
        self.name_kl=name_kl
        self.otch_kl=otch_kl
        self.naim=naim
    }
    
    init(_ time: String, _ fam_kl: String, _ name_kl: String, _ otch_kl: String, _ naim: String, _ komment: String){
        self.time = time
        self.fam_kl=fam_kl
        self.name_kl=name_kl
        self.otch_kl=otch_kl
        self.naim=naim
        self.komment=komment
    }
    
    func getFullName() -> String {
        return "\(fam_kl!) \(name_kl!) \(otch_kl!)"
    }
}
