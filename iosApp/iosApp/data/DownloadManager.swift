//
//  DownloadManager.swift
//  CallMed_ios
//
//  Created by Mihail on 09.12.2022.
//

import Foundation
import SwiftUI
import shared

class DownloadManager {
    var isDownloading = false
    var resultUiImage : ((UIImage) -> Void)? = nil
    
    init(){}
    
    init(_ imagePathString : String, resultUiImage : ((UIImage) -> Void)?){  //простая загрузка картинок и показ в UIImage
        self.resultUiImage = resultUiImage!
        
        let nameFile = getNameFile(imagePathString)
        
        if(nameFile != nil && !nameFile.isEmpty){
            let res1 = checkFile(nameFile)
            
            if res1 != nil{
                DispatchQueue.main.async {
                    self.resultUiImage?(res1!)
                }
            }else{
                downloadFileForImage(imagePathString, nameFile)
            }
        }
    }
    
    //url path to name file
    func getNameFile(_ imagePathServerString : String) -> String{
    
        var st1: String
        if(imagePathServerString.firstIndex(of: "&") != nil){
            st1 = String(imagePathServerString[..<imagePathServerString.firstIndex(of: "&")!])
        }else{
            st1 = imagePathServerString
        }
        
        let st2 = String(st1[st1.range(of: "path=")!.lowerBound...])
        let st3 = String(st2[st2.index(st2.startIndex, offsetBy: 5)...])
        return st3
    }
    func getExpansionFromFileName(_ fName: String) -> String{
        let st2 = String(fName[fName.range(of: ".")!.lowerBound...])
        let st3 = String(st2[st2.index(st2.startIndex, offsetBy: 1)...])
        return st3
    }
    func getExpansionFromFilePath(_ fPath: String) -> String{
        let s1 = fPath.lastIndex(of: ".")
        let st1 = String(fPath[s1!...])
        let st13 = String(st1[st1.index(st1.startIndex, offsetBy: 1)...])
    
        return st13
    }
    
    func checkFile(_ fileName : String) -> UIImage?{
        if fileName == nil || fileName.isEmpty {
            return nil
        }
        
        let docsUrl = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first

        let destinationUrl = docsUrl?.appendingPathComponent(fileName)
        if let destinationUrl = destinationUrl {
            if (FileManager().fileExists(atPath: destinationUrl.path)) {
                do {
                    let data = try! Data.init(contentsOf: destinationUrl)
                    let photo = UIImage.init(data: data)
                    
                    return photo
                } catch {
                    return nil
                }
            } else {
                return nil
            }
        } else {
            return nil
        }
    }
    
    
    func downloadFileForImage(_ imagePathServerString : String, _ fileName : String){
        
        isDownloading = true
        
        let docsUrl = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first
        let destinationUrl = docsUrl?.appendingPathComponent(fileName)
        
        if let destinationUrl = destinationUrl {
            
            if let urlString = imagePathServerString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), let url = URL(string: urlString) {
                
                let urlRequest = URLRequest(url: url)
                
                let dataTask = URLSession.shared.dataTask(with: urlRequest) { (data, response, error) in
                    if let error = error {
                        print("Request error: ", error)
                        self.isDownloading = false
                        return
                    }
                    
                    guard let response = response as? HTTPURLResponse else { return }
                    
                    if response.statusCode == 200 {
                        guard let data = data else {
                            self.isDownloading = false
                            return
                        }
                        
                        DispatchQueue.main.async {
              
                            let strBase64 : String = String(decoding: data, as: UTF8.self)
                            
                            let dataDecoded : NSData = NSData(base64Encoded: strBase64, options: NSData.Base64DecodingOptions(rawValue: 0))!
                            let decodedimage : UIImage = UIImage(data: dataDecoded as Data)!
                
                            if let data = decodedimage.pngData() {
                                try? data.write(to: destinationUrl)
                                let data = try! Data.init(contentsOf: destinationUrl)
                                let photo = UIImage.init(data: data)

                                DispatchQueue.main.async {
                                    self.resultUiImage?(photo!)
                                }
                            }
                        }
                        
                    }
                }
                dataTask.resume()
            }
        }
    }
    
    
    func checkFileForAnaliseResult(list: [AnaliseResponse], nameUserIn: String){
        let sharePreferenses : SharedPreferenses = SharedPreferenses()
        let nameUser = nameUserIn
        
        
        
        list.forEach{i in
            
            let fName = getNameFile(i.linkToPDF!)
            let searchI = sharePreferenses.getAnaliseFileName(pref: Constants.Analise, nameU: nameUser, date: i.getDateForZakl()!, spec: fName)
            
            let fileName : String
            if(searchI == nil){
                fileName = getNameFile(i.linkToPDF!)
            }else{
                fileName = searchI!.getFileName()
            }
            
            let docsUrl = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first

            let destinationUrl = docsUrl?.appendingPathComponent(fileName)
            if let destinationUrl = destinationUrl {
               
                
                if (FileManager().fileExists(atPath: destinationUrl.path)) {
                    do {
                        //print(">>>>> Analise \(destinationUrl.absoluteString)")
                        i.pathToFile = destinationUrl.absoluteString
                    } catch {
                        i.pathToFile = ""
                    }
                } else {
                    i.pathToFile = ""
                }
            } else {
                i.pathToFile = ""
            }
        }
    }
    
    func checkFilesForDataClassForElectronicRecy(list: [DataClassForElectronicRecyIos], nameUserIn: String){
        let sharePreferenses : SharedPreferenses = SharedPreferenses()
        let nameUser = nameUserIn
        
        list.forEach{j in
            let i = j.item
            
            let fileName : String
            if(i is AnaliseResponse){
                let itm = (i as! AnaliseResponse)
                let fName = getNameFile(itm.linkToPDF!)
                let searchI = sharePreferenses.getElectronicConclusions(pref: Constants.ConclusionAn, nameU: nameUser, date: itm.getDateForZakl()!, spec: fName)
                
                if(searchI == nil){
                    fileName = getNameFile(itm.linkToPDF!)
                }else{
                    fileName = searchI!.getFileName()
                }
                
            }else{
                let itm = (i as! ResultZakl2Item)
                let searchI = sharePreferenses.getElectronicConclusions(pref: Constants.ConclusionZacPDF, nameU: nameUser, date: itm.datePer!, spec: itm.nameSpec!)
                
                if(searchI == nil){
                    fileName = itm.getNameFileWithoutExtension(name: nameUserIn)
                }else{
                    fileName = searchI!.getFileName()
                }
            }
            
            let docsUrl = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first

            let destinationUrl = docsUrl?.appendingPathComponent(fileName)
            if let destinationUrl = destinationUrl {
                if (FileManager().fileExists(atPath: destinationUrl.path)) {
                    do {
                        //print(">>>>> Electronic \(destinationUrl.absoluteString)")
                        i.pathToFile = destinationUrl.absoluteString
                    } catch {
                        i.pathToFile = ""
                    }
                } else {
                    i.pathToFile = ""
                }
            } else {
                i.pathToFile = ""
            }
        }
    }
    
    
    func downloadFileForAnaliseResult(fName: String, imagePathServerString : String, apiKey: String, result: @escaping ((String?) -> ())){
        
       // let fileName = getNameFile(imagePathServerString)  расширение файла!!!!!!
        
        isDownloading = true
        
        let docsUrl = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first
        let destinationUrl = docsUrl?.appendingPathComponent(fName)
        
        if let destinationUrl = destinationUrl {
            
            if let urlString = imagePathServerString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), let url = URL(string: urlString+"&token=" + apiKey) {
                
                let urlRequest = URLRequest(url: url)
                
                let dataTask = URLSession.shared.dataTask(with: urlRequest) { (data, response, error) in
                    if let error = error {
                        print("Request error: ", error)
                        self.isDownloading = false
                        result(nil)
                        return
                    }
                    
                    guard let response = response as? HTTPURLResponse else { return }
                    
                    let tt = response.statusCode
                    
                    if response.statusCode == 200 {
                        guard let data = data else {
                            self.isDownloading = false
                            result(nil)
                            return
                        }
                        
                        DispatchQueue.main.async {
                            let exttention = self.getExpansionFromFileName(fName)
                            
                            if(exttention == "jpg" || exttention == "jpeg"){
                                
                                let strBase64 : String = String(decoding: data, as: UTF8.self)
                                
                                let dataDecoded : NSData = NSData(base64Encoded: strBase64, options: NSData.Base64DecodingOptions(rawValue: 0))!
                                let decodedimage : UIImage = UIImage(data: dataDecoded as Data)!
                                
                                if let data = decodedimage.pngData() {
                                    try? data.write(to: destinationUrl)
                                    //let data = try! Data.init(contentsOf: destinationUrl)
                                    //let photo = UIImage.init(data: data)
                                    
                                    DispatchQueue.main.async {
                                        result(destinationUrl.absoluteString)
                                    }
                                }
                            }else if(exttention == "pdf"){
                                let strBase64 : String = String(decoding: data, as: UTF8.self)
                                //var data = Data(base64Encoded: strBase64, options: .ignoreUnknownCharacters)
                                let data : NSData = NSData(base64Encoded: strBase64, options: NSData.Base64DecodingOptions(rawValue: 0))!
                                
                                
                                if(data != nil){
                                    try? data.write(to: destinationUrl)
                                    
                                    
                                    
                                    
                                    let destinationUrl2 = docsUrl?.appendingPathComponent(fName)
                                    if let destinationUrl2 = destinationUrl2 {
                                        if (FileManager().fileExists(atPath: destinationUrl2.path)) {
                                            do {
                                               // print(">>>>> "+destinationUrl.absoluteString)
                                            } catch {
                                                print("no")
                                            }
                                        } else {
                                            print("no")
                                        }
                                    } else {
                                        print("no")
                                    }
                                    
                                    
                                    
                                    
                                    DispatchQueue.main.async {
                                        result(destinationUrl.absoluteString)
                                    }
                                }else{
                                    result(nil)
                                }
                            }else{
                                result(nil)
                            }
                        }
                        
                    }else{
                        result(nil)
                    }
                }
                dataTask.resume()
            }else{
                result(nil)
            }
        }else{
            result(nil)
        }
    }
    
    func deleteFile(fileName: String, response: ((Bool) ->())){
        let docsUrl = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first

        let destinationUrl = docsUrl?.appendingPathComponent(fileName)
        if let destinationUrl = destinationUrl {
            
            //let tt = FileManager().fileExists(atPath: destinationUrl.path)
            
            guard FileManager().fileExists(atPath: destinationUrl.path) else {
                response(true)
                return
            }
            
            do {
                try FileManager().removeItem(atPath: destinationUrl.path)
                print("File deleted successfully")
                response(true)
            } catch let error {
                print("Error while deleting video file: ", error)
                response(false)
            }
        }else{
            response(false)
        }
    }
    
    func loadFile2ForElectronicConclusions(tmpI: FileNameInfo ,item : ResultZakl2Item, result: @escaping ((String?) -> Void), recordTItem: AllRecordsTelemedicineItem) throws{
        let sharePreferenses : SharedPreferenses = SharedPreferenses()
        let sdk=NetworkManagerCompatibleKMM()
        
        let apiKey = String.init(recordTItem.token_kl ?? "")
        let idUser = String(Int.init(truncating: recordTItem.idKl ?? 0))
        let idBranch = String(Int.init(truncating: recordTItem.idFilial ?? 0))
        let h_dbName = sharePreferenses.currentCenterInfo!.db_name ?? ""
        
        
        sdk.geDataResultZakl2(item: item, h_Auth: apiKey, h_dbName: h_dbName, h_idKl: idUser, h_idFilial:  idBranch,
                                  completionHandler: { response, error in
            if let res : LoadDataZaklAmbResponse = response {
                
                if(h_dbName == "tomograd_podolsk" && res.response[0].sotrSpec != nil && res.response[0].sotrSpec! == "мрт"){
                    tmpI.pref = Constants.ConclusionZacHtml
                    sharePreferenses.addElectronicConclusions(item: tmpI)
                    self.saveHtmlStringToFile(data: res.response[0],fileName: tmpI.getFileName(), item: item, result: result)
                    return
                }
                
                sharePreferenses.addElectronicConclusions(item: tmpI)
              
                let fioClient = String.init(recordTItem.fullNameKl ?? "")
                self.downloadPdfFromInternet(data: res.response[0],fileName: tmpI.getFileName(), item: item, result: result, fioClient: fioClient)
                
            } else {
                if let t=error{
                    LoggingTree.INSTANCE.e("DownloadManager/loadFile2", t)
                }
                
                result(nil)
            }
        })
    }
    
    func saveHtmlStringToFile(data: LoadDataZaklAmbItem, fileName: String, item: ResultZakl2Item, result: @escaping ((String?) -> ())){
        guard let directory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first else {
            result(nil)
            return
        }
        let destinationUrl =  directory.appendingPathComponent(fileName)
        let str = data.textHtml!
        
        do {
            try str.write(to: destinationUrl, atomically: true, encoding: String.Encoding.utf8)
            
            DispatchQueue.main.async {
                result(destinationUrl.path)
            }
        } catch {
            LoggingTree.INSTANCE.e("DownloadManager/saveHtmlStringToFile", error)
            // failed to write file – bad permissions, bad filename, missing permissions, or more likely it can't be converted to the encoding
            
            DispatchQueue.main.async {
                result(nil)
            }
        }
    }
    func downloadPdfFromInternet(data: LoadDataZaklAmbItem, fileName: String, item: ResultZakl2Item, result: @escaping ((String?) -> Void), fioClient: String){
        let sharePreferenses : SharedPreferenses = SharedPreferenses()
        
        // Create the session object
        let session = URLSession.shared
        // Create url object
        guard let url = URL(string: "http://188.225.25.133/medhelp_client/fpdf/report_ambkarti.php") else {return}

        // Create the URLRequest object using the url object
        var request = URLRequest(url: url)
        // Set the request method. Important Do not set any other headers, like Content-Type
        request.httpMethod = "POST" //set http method as POST
        
        //HTTP Headers
        request.setValue("oneclick.tmweb.ru", forHTTPHeaderField: "host")
        
        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!

        components.queryItems = [
            URLQueryItem(name: "data_priem", value: data.dataPriem!),
            URLQueryItem(name: "diagnoz", value: data.diagnoz!),
            URLQueryItem(name: "rekomend", value: data.rekomend!),
            URLQueryItem(name: "sotr", value: data.sotr!),
            URLQueryItem(name: "sotr_spec", value: data.sotrSpec!),
            URLQueryItem(name: "cons", value: data.cons!),
            URLQueryItem(name: "shapka", value: data.shapka!),
            URLQueryItem(name: "nom_amb", value: String(Int.init(truncating: data.nom_amb ?? 0))),
            URLQueryItem(name: "OOO", value: data.ooo!),
            URLQueryItem(name: "fiokl", value: fioClient)
        ]

        let query = components.url!.query
        
        request.httpBody = Data(query!.utf8)
        
        guard let directory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first else {
            result(nil)
            return
        }
        let destinationUrl =  directory.appendingPathComponent(fileName)
        
        let task = session.dataTask(with: request, completionHandler:
                  {
                      data, response, error in
                      if error == nil
                      {
                          if let response = response as? HTTPURLResponse
                          {
                              if response.statusCode == 200
                              {
                                  if let data = data
                                  {
                                      if let _ = try? data.write(to: destinationUrl, options: Data.WritingOptions.atomic)
                                      {
                                          DispatchQueue.main.async {
                                              result(destinationUrl.path)
                                          }
                                      }
                                      else
                                      {
                                          LoggingTree.INSTANCE.e("DownloadManager/downloadPdfFromInternet write to file error")
                                          result(nil)
                                      }
                                  }
                                  else
                                  {
                                      LoggingTree.INSTANCE.e("DownloadManager/downloadPdfFromInternet data==nil")
                                      result(nil)
                                  }
                              }else{
                                  LoggingTree.INSTANCE.e("DownloadManager/downloadPdfFromInternet statusCode != 200")
                                  result(nil)
                              }
                          }else{
                              LoggingTree.INSTANCE.e("DownloadManager/downloadPdfFromInternet response!=HTTPURLResponse")
                              result(nil)
                          }
                      }
                      else
                      {
                          LoggingTree.INSTANCE.e("DownloadManager/downloadPdfFromInternet error \(error)")
                          result(nil)
                      }
                  })
          
        // Run the task
        task.resume()
    }
    
}
