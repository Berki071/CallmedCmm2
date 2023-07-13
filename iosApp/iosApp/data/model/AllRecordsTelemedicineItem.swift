////
////  AllRecordsTelemedicineItem.swift
////  CallMed_ios
////
////  Created by Mihail on 08.06.2023.
////
//
//import Foundation
//
//struct AllRecordsTelemedicineItem: Decodable,Encodable,Hashable {
//    
//    var server_key: String?
//    var data_server: String?
//    var id_room: Int?
//    var status: String?
//    var id_kl: Int?
//    var id_filial: Int?
//    var specialty: String?
//    var full_name_kl: String?
//    var dr_kl: String?
//    var komment_kl: String?
//    var fcm_kl: String?
//    var tm_id: Int?
//    var tm_name: String?
//    var tm_type: String?
//    var tm_price: Int?
//    var tm_time_for_tm: Int?
//    var time_start_after_pay: Int?
//    var data_start: String?
//    var data_end: String?
//    var data_pay: String?
//    var status_pay: String?
//    var about: String?
//    var about_full: String?
//    var notif_24: String?
//    var notif_12: String?
//    var notif_4: String?
//    var notif_1: String?
//    
//    var isShowNewMsgIco: Bool? = false  //не надо джейсонить
//    
//    private enum CodingKeys: String, CodingKey {
//        case server_key, data_server, id_room, status, id_kl, id_filial, specialty, full_name_kl, dr_kl, komment_kl, fcm_kl, tm_id, tm_name, tm_type, tm_price, tm_time_for_tm, time_start_after_pay, data_start, data_end, data_pay, status_pay, about, about_full, notif_24, notif_12, notif_4, notif_1
//    }
//}
