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
    
//    func doLoginApiCall(_ login:String?, _ pass:String?, responseF: @escaping (([UserResponse]) -> Void), errorM: @escaping ((String) -> Void) ){
//        // Create the session object
//        let session = URLSession.shared
//
//        // Create url object
//        guard let url = URL(string: "http://188.225.25.133/medhelp_main/v1/login/doctor") else {return}
//        // Create the URLRequest object using the url object
//        var request = URLRequest(url: url)
//        // Set the request method. Important Do not set any other headers, like Content-Type
//        request.httpMethod = "POST" //set http method as POST
//
//        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!
//        components.queryItems = [
//            URLQueryItem(name: "username", value: login!),
//            URLQueryItem(name: "password", value: pass!)
//        ]
//        let query = components.url!.query
//        // Set parameters here. Replace with your own.
//        request.httpBody = Data(query!.utf8)
//
//        //HTTP Headers
//        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
//        request.setValue(apiKey, forHTTPHeaderField: "Authorization")
//
//        // Create a task using the session object, to run and return completion handler
//        let webTask = session.dataTask(with: request, completionHandler: {data, response, error in
//            guard let data = data, error == nil else {
//                let str = error?.localizedDescription ?? "error login/doctor"
//                errorM(str)
//                return
//            }
//
//            let prespData = try? JSONDecoder().decode(UserList.self, from: data)
//
//           // let tttr = String(data: data, encoding: .utf8)
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
    

    func loadVideoFile(urlStr: String, savedURL: URL,
                       responseF: @escaping (() -> Void), errorM: @escaping ((String) -> Void)){
        
        guard let url = URL(string: urlStr) else {return}
     
        let downloadTask = URLSession.shared.downloadTask(with: url) {
            urlOrNil, responseOrNil, errorOrNil in
            // check for and handle errors:
            // * errorOrNil should be nil
            // * responseOrNil should be an HTTPURLResponse with statusCode in 200..<299
            
            guard let fileURL = urlOrNil else { return }
            do {
//                let b64Data: Data? = self.gerStringFromFile(fileURL)
//                var strB64: String = ""
//                if(b64Data != nil){
//                    strB64 = String(decoding: b64Data!, as: UTF8.self)
//                }
                
                try FileManager.default.moveItem(at: fileURL, to: savedURL)
                
//                let b64Data2: Data? = self.gerStringFromFile(savedURL)
//                var strB642: String = ""
//                if(b64Data2 != nil){
//                    strB642 = String(decoding: b64Data2!, as: UTF8.self)
//                }
//
                responseF()
            } catch {
                errorM("file error: \(error)")
            }
        }
        downloadTask.resume()
    }
    
    func gerStringFromFile(_ destinationUrl: URL) -> Data?{
        var data: Data? = nil
        do {
            data = try? Data(contentsOf: destinationUrl)
            //string = try String(contentsOf: destinationUrl)
        } catch {
            //errorBack(item, "Ошибка!", "Не удалось создать файл для сохранения \((error.localizedDescription))")
        }
        
        return  data
    }


}
