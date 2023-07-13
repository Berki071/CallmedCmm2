//
//  DownloadManager.swift
//  CallMed_ios
//
//  Created by Mihail on 09.12.2022.
//

import Foundation
import SwiftUI

class DownloadImgManager {
    var isDownloading = false
    var resultUiImage : ((UIImage) -> Void)? = nil
    
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
    
}
