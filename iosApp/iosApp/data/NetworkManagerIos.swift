//
//  NetworkManagerIos.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

//import Foundation
import UIKit

let apiKey = "AAAA2UBtySo:APA91bGOxg0DNY9Ojz-BD0d4bUr-GukFBdvCtivWVjqZ8ppEHtl-BIwmINKD3R_"

class NetworkManagerIos{
    
    func doLoginApiCall(_ login:String?, _ pass:String?, responseF: @escaping (([UserResponse]) -> Void), errorM: @escaping ((String) -> Void) ){
        // Create the session object
        let session = URLSession.shared
        
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_main/v1/login/doctor") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "POST" //set http method as POST
        
        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!
        components.queryItems = [
            URLQueryItem(name: "username", value: login!),
            URLQueryItem(name: "password", value: pass!)
        ]
        let query = components.url!.query
        // Set parameters here. Replace with your own.
        request.httpBody = Data(query!.utf8)
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(apiKey, forHTTPHeaderField: "Authorization")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error login/doctor"
                errorM(str)
                return
            }
            
            let prespData = try? JSONDecoder().decode(UserList.self, from: data)
            
           // let tttr = String(data: data, encoding: .utf8)
            
            if let prespData = prespData {
                responseF(prespData.response!)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    func sendLogToServer(idUser: String, idCenter: String, idBranch: String, type: String, log: String , versionCode: String,
                         responseF: @escaping ((Bool) -> Void), errorM: @escaping ((String) -> Void)){
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_main/v1/LogDataInsert/\(idUser)/\(idCenter)/\(idBranch)/\(type)/sotr/\(versionCode)") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "POST" //set http method as POST
        
        //body value
        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!
        components.queryItems = [
            URLQueryItem(name: "log", value: log),
        ]
        let query = components.url!.query
        request.httpBody = Data(query!.utf8)
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(apiKey, forHTTPHeaderField: "Authorization")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error LogDataInsert/"
                errorM(str)
                return
            }
            
            responseF(true)
        })
        
        // Run the task
        webTask.resume()
    }
    
    func getCenterApiCall(idCenter: String,
                          responseF: @escaping (([CenterItem]) -> Void), errorM: @escaping ((String) -> Void)){
        
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_main/v1/centres/\(idCenter)") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue(apiKey, forHTTPHeaderField: "Authorization")
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error centres/"
                errorM(str)
                return
            }
            
            //let tttr = String(data: data, encoding: .utf8)
            let respData = try? JSONDecoder().decode(CenterResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response!)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    func getDoctorById(dbName: String, accessToken: String, docId: String,
                       responseF: @escaping ((DoctorItem) -> Void), errorM: @escaping ((String) -> Void)){
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/v1/sotr_info_doc/\(docId)") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(dbName, forHTTPHeaderField: "db_name")
        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
        request.setValue(docId, forHTTPHeaderField: "id_sotr")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error centres/"
                errorM(str)
                return
            }
            
           // let tttr = String(data: data, encoding: .utf8)
            let respData = try? JSONDecoder().decode(DoctorResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response![0])
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    func getAllHospitalBranchForDoc(idDoc: String, dbName: String, accessToken: String,
                                    responseF: @escaping (([SettingsAllBranchHospitalItem]) -> Void), errorM: @escaping ((String) -> Void)){
        
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/v1/FilialByIdSotr/\(idDoc)") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(dbName, forHTTPHeaderField: "db_name")
        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
        request.setValue(idDoc, forHTTPHeaderField: "id_sotr")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                var str = error?.localizedDescription ?? "error centres/"
                str += " ,error._code: " + String(error?._code ?? -333)
                if let response = response {
                    str += " response.description: "
                    str += response.description
                }
                errorM(str)
                return
            }
            
            // let tttr = String(data: data, encoding: .utf8)
            let respData = try? JSONDecoder().decode(SettingsAllBranchHospitalResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response!)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    func getCurrentDateApiCall(responseF: @escaping ((DateItem) -> Void), errorM: @escaping ((String) -> Void)){
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_main/v1/date") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(apiKey, forHTTPHeaderField: "Authorization")
  
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error date/"
                errorM(str)
                return
            }
            
            // let tttr = String(data: data, encoding: .utf8)
            let respData = try? JSONDecoder().decode(DateResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response!)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    
    func getAllReceptionApiCall(branch: String, date: String, idDoc: String, dbName: String, accessToken: String,
                                responseF: @escaping (([VisitItem]) -> Void), errorM: @escaping ((String) -> Void)){
        
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/v1/scheduleFull/doctor/\(idDoc)/\(date)/\(branch)") else {return}
        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(dbName, forHTTPHeaderField: "db_name")
        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
        request.setValue(idDoc, forHTTPHeaderField: "id_sotr")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                var str = error?.localizedDescription ?? "error scheduleFull/doctor/"
                str += " ,error._code: " + String(error?._code ?? -333)
                
                if let response = response {
                    str += " response.description: "
                    str += response.description
                }
                errorM(str)
                return
            }
            
            // let tttr = String(data: data, encoding: .utf8)
            let respData = try? JSONDecoder().decode(VisitResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response!)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    func updateFcmToken(fbToken: String, idDoc: String, dbName: String, accessToken: String,
                        responseF: @escaping ((SimpleResponseString) -> Void), errorM: @escaping ((String) -> Void)){
        
        let session = URLSession.shared
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/v1/UpdateFCMdoctor/\(idDoc)/\(fbToken)") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(dbName, forHTTPHeaderField: "db_name")
        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
        request.setValue(idDoc, forHTTPHeaderField: "id_sotr")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error scheduleFull/doctor/"
                errorM(str)
                return
            }

            let respData = try? JSONDecoder().decode(SimpleResponseString.self, from: data)
            
            if let prespData = respData {
                responseF(prespData)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    func findAllRaspSotr(date: String, idDoc: String, dbName: String, accessToken: String,
                         responseF: @escaping (([AllRaspSotrItem]) -> Void), errorM: @escaping ((String) -> Void)){
        
        let session = URLSession.shared
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/v1/FindAllRaspSotr/\(idDoc)/\(date)") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "GET" //set http method
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(dbName, forHTTPHeaderField: "db_name")
        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
        request.setValue(idDoc, forHTTPHeaderField: "id_sotr")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                var str = error?.localizedDescription ?? "error scheduleFull/doctor/"
                if let response = response {
                    str += " response.description: "
                    str += response.description
                }
                errorM(str)
                return
            }

            let respData = try? JSONDecoder().decode(AllRaspSotrResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response!)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
    
    func loadStatMkb(dateFrom: String, dateTo: String, idDoc: String, dbName: String, accessToken: String,
                     responseF: @escaping (([String]?) -> Void), errorM: @escaping ((String) -> Void)){
        
        let session = URLSession.shared
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/v1/load_stat_mkb/\(dateFrom)/\(dateTo)/\(idDoc)") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "GET" //set http method
     
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        request.setValue(dbName, forHTTPHeaderField: "db_name")
        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
        request.setValue(idDoc, forHTTPHeaderField: "id_sotr")
        
        // Create a task using the session object, to run and return completion handler
        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
            guard let data = data, error == nil else {
                let str = error?.localizedDescription ?? "error scheduleFull/doctor/"
                errorM(str)
                return
            }

            let respData = try? JSONDecoder().decode(LoadStatMkbResponse.self, from: data)
            
            if let prespData = respData {
                responseF(prespData.response)
            }
            else{
                let tttr = String(data: data, encoding: .utf8)
                errorM("pars errpr \(String(describing: tttr))")
            }
        })
        
        // Run the task
        webTask.resume()
    }
    
//    func findRaspTomorrow(date: String,
//                          idDoc: String, dbName: String, accessToken: String,
//                     responseF: @escaping (([ScheduleTomorrowItem]) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/FindRaspTomorrow/\(idDoc)/\(date)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(dbName, forHTTPHeaderField: "db_name")
//        request.setValue(accessToken, forHTTPHeaderField: "Authorization")
//        request.setValue(idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error FindRaspTomorrow/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(ScheduleTomorrowResponse.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response!)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
    
    
//    func getAllRecordsTelemedicine(type: String,
//                                   h_Auth: String, h_dbName: String, h_idDoc: String,
//                                   responseF: @escaping (([AllRecordsTelemedicineItem]?) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/showTMbyDoc/\(h_idDoc)/\(type)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error showTMbyDoc/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(AllRecordsTelemedicineResponse.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    
//    func closeRecordTelemedicine(idRoom: String, idTm: String,
//                                 h_Auth: String, h_dbName: String, h_idDoc: String,
//                                 responseF: @escaping ((Bool?) -> Void), errorM: @escaping ((String) -> Void)) {
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/CloseTMDoc/\(idRoom)/\(idTm)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error CloseTMDoc/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(SimpleResponseBoolean.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    
//    func areThereAnyNewTelemedicineMsg (h_Auth: String, h_dbName: String, h_idDoc: String,
//                                        responseF: @escaping (([HasPacChatsItem]?) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/Has_sotr_chats/\(h_idDoc)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error Has_sotr_chats/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(HasPacChatsResponse.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    
//    
//    func updateTelemedicineReminderDocAboutRecord(type: String, idTm: String,
//                                                  h_Auth: String, h_dbName: String, h_idDoc: String,
//                                                  responseF: @escaping ((Bool?) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/UpdateNotifDoctorForTMbyDoc/\(type)/\(idTm)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error UpdateNotifDoctorForTMbyDoc/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(SimpleResponseBoolean.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//        
//    }
//    
//    
//    func getMessagesRoom(idRoom: String, idLastMsg: String,
//                         h_Auth: String, h_dbName: String, h_idDoc: String,
//                         responseF: @escaping (([MessageRoomItem]?) -> Void), errorM: @escaping ((String) -> Void)) {
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/LoadAllMessagesSOTR/\(idRoom)/\(idLastMsg)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error LoadAllMessagesSOTR/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(MessageRoomResponse.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    func sendMessageFromRoom(idRoom: String, idTm: String, idUser: String, typeMsg: String, text: String,
//                             h_Auth: String, h_dbName: String, h_idDoc: String, h_idFilial: String,
//                            responseF: @escaping (([SendMessageFromRoomItem]?) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        // Create the session object
//        //let session = URLSession.shared
//        let config = URLSessionConfiguration.default
//        config.timeoutIntervalForRequest = TimeInterval(60)
//        config.timeoutIntervalForResource = TimeInterval(60)
//        let session = URLSession(configuration: config)
//        
//        
//        // Create url object
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/SendNewMessageToChatSOTR/\(idRoom)/\(idTm)/\(h_idDoc)/\(h_idFilial)/\(idUser)/\(typeMsg)") else {return}
//        // Create the URLRequest object using the url object
//        var request = URLRequest(url: url)
//        // Set the request method. Important Do not set any other headers, like Content-Type
//        request.httpMethod = "POST" //set http method as POST
//        
//    
//        
//        //body value
//        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!
//        components.queryItems = [
//            URLQueryItem(name: "message", value: text),
//        ]
//        let query = components.url!.query
//        request.httpBody = Data(query!.utf8)
//        
////        let postData = "message=\(text)".data(using: .utf8)
////        request.httpBody = postData
////        var params: [String : String] = [:]
////        params["message"] = text
////        let tmp1 = self.jsonToString(params)
////        let postData = tmp1.data(using: .utf8)
////        request.httpBody = postData
//        
//        
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error SendNewMessageToChatSOTR/"
//                errorM(str)
//                return
//            }
//            
//            let prespData = try? JSONDecoder().decode(SendMessageFromRoomResponse.self, from: data)
//        
//            if let prespData = prespData {
//                responseF(prespData.response!)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    func jsonToString(_ json: [String: Any]) -> String{
//        do {
//            let data1 = try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.prettyPrinted)
//            let convertedString = String(data: data1, encoding: String.Encoding.utf8) as NSString? ?? ""
//            //debugPrint(convertedString)
//            return convertedString as String
//        } catch let myJSONError {
//            //debugPrint(myJSONError)
//            return ""
//        }
//    }
//
//    
//    func deleteMessageFromServer(idMsg: String,
//                                 h_Auth: String, h_dbName: String, h_idKl: String,
//                                 responseF: @escaping ((Bool?) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/ChatDeleteMessageSOTR/\(idMsg)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idKl, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error ChatDeleteMessageSOTR/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(SimpleResponseBoolean.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    
//    func toActiveRecordTelemedicine(idRoom: String, idTm: String,
//                                    h_Auth: String, h_dbName: String, h_idDoc: String,
//                                    responseF: @escaping ((Bool?) -> Void), errorM: @escaping ((String) -> Void)) {
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/ActiveTMbySotr/\(idRoom)/\(idTm)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error ActiveTMbySotr/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(SimpleResponseBoolean.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    
//    func getSelectRecordsTelemedicine (idRoom: String, idTm: String,
//                                       h_Auth: String, h_dbName: String, h_idDoc: String,
//                                       responseF: @escaping (([AllRecordsTelemedicineItem]?) -> Void), errorM: @escaping ((String) -> Void)){
//        
//        let session = URLSession.shared
//        guard let url = URL(string: "https://oneclick.tmweb.ru/medhelp_client/v1/showTMbyIDtm/\(idRoom)/\(idTm)") else {return}
//        var request = URLRequest(url: url)
//        request.httpMethod = "GET" //set http method
//     
//        //HTTP Headers
//        request.setValue(h_dbName, forHTTPHeaderField: "db_name")
//        request.setValue(h_Auth, forHTTPHeaderField: "Authorization")
//        request.setValue(h_idDoc, forHTTPHeaderField: "id_sotr")
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error showTMbyIDtm/"
//                errorM(str)
//                return
//            }
//
//            let respData = try? JSONDecoder().decode(AllRecordsTelemedicineResponse.self, from: data)
//            
//            if let prespData = respData {
//                responseF(prespData.response)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
//    
//    func sendMsgFCM (json: String, serverKey: String, senderId: String,
//                     responseF: @escaping ((FCMResponse?) -> Void), errorM: @escaping ((String) -> Void)) {
//        // Create the session object
//        let session = URLSession.shared
//        // Create url object
//        guard let url = URL(string: "https://fcm.googleapis.com/fcm/send") else {return}
//        // Create the URLRequest object using the url object
//        var request = URLRequest(url: url)
//        // Set the request method. Important Do not set any other headers, like Content-Type
//        request.httpMethod = "POST" //set http method as POST
//        
//        //HTTP Headers
//        request.setValue("key=\(serverKey)", forHTTPHeaderField: "Authorization")
//        request.setValue("id=\(senderId)", forHTTPHeaderField: "Sender")
//        
//        //body value
//        // Set parameters here. Replace with your own.
//        let postData = json.data(using: .utf8)
//        request.httpBody = postData
//        
//        
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error fcm.googleapis.com/fcm/send"
//                errorM(str)
//                return
//            }
//            
//            let prespData = try? JSONDecoder().decode(FCMResponse.self, from: data)
//        
//            if let prespData = prespData {
//                responseF(prespData)
//            }
//            else{
//                let tttr = String(data: data, encoding: .utf8)
//                errorM("pars errpr \(String(describing: tttr))")
//            }
//        })
//        
//        // Run the task
//        webTask.resume()
//    }
}
