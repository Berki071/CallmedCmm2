//
//  WorkWithFiles.swift
//  CallMed_ios
//
//  Created by Mihail on 20.06.2023.
//

import Foundation
import SwiftUI
import shared

class WorkWithFiles {
    
    // директория документов Приложения
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
    
    //для сохранения файла в директории приложеня
    func saveFileByUrl(url: URL, saveName: String, errorListener: (String, String) -> Void, successfully: (String) -> Void){
        do{
            // Start accessing a security-scoped resource.
            guard url.startAccessingSecurityScopedResource() else {
                LoggingTree.INSTANCE.e("WorkWithFiles/saveFileByUrl startAccessingSecurityScopedResource err \(url.absoluteString)" )
                errorListener("Ошибка!", "Что-то пошло не так.")
                return
            }
            
            let pathDir = getDocumentsDirectory()
            
            if let filePath = pathDir?.appendingPathComponent(saveName) {
                
                let fileManager = FileManager.default
                try fileManager.copyItem(at: url, to: filePath)
                // Make sure you release the security-scoped resource when you finish.
                do { url.stopAccessingSecurityScopedResource() }
                
                successfully(filePath.lastPathComponent)
            }else{
                LoggingTree.INSTANCE.e("WorkWithFiles/saveFileByUrl filePath == nil" )
                errorListener("Ошибка!", "Что-то пошло не так.")
            }
            
            
        } catch {
            // Make sure you release the security-scoped resource when you finish.
            defer { url.stopAccessingSecurityScopedResource() }
            
            LoggingTree.INSTANCE.e("WorkWithFiles/saveFileByUrl 0 \(error.localizedDescription)" )
            
            errorListener("Ошибка!", "Что-то пошло не так.")
            //print(">>>>>!!!>>> \(error.localizedDescription)")
        }
    }
    
    //для сохранения картинки в директории приложеня
    func saveImageByUrl(url: URL, saveName: String, errorListener: (String, String) -> Void, successfully: (String) -> Void) {
        // перевод UIImage и сохранение в папку приложения в нижном виде
        
        do {
            // Start accessing a security-scoped resource.
            guard url.startAccessingSecurityScopedResource() else {
                LoggingTree.INSTANCE.e("WorkWithFiles/saveImageByUrl startAccessingSecurityScopedResource err \(url.absoluteString)" )
                errorListener("Ошибка!", "Что-то пошло не так.")
                return
            }
            
            // to UIImage
            var imageData : Data? = try Data(contentsOf: url)
            
            guard let dataOfImage = imageData else {
                LoggingTree.INSTANCE.e("WorkWithFiles/saveImageByUrl imageData == nil \(url.absoluteString)" )
                errorListener("Ошибка!", "Что-то пошло не так.")
                return }
            guard let uiImageTmpTTT = UIImage(data: dataOfImage) else {
                LoggingTree.INSTANCE.e("WorkWithFiles/saveImageByUrl dataOfImage == nil \(url.absoluteString)" )
                errorListener("Ошибка!", "Что-то пошло не так.")
                return }
            
            let uiImageTmp = self.lightenTheImage(uiImageTmpTTT)
            
            // Make sure you release the security-scoped resource when you finish.
            defer { url.stopAccessingSecurityScopedResource() }
            
            let pathDir = self.getDocumentsDirectory()
            
            if let filePath = pathDir?.appendingPathComponent(saveName) {
                // Save image.
                do {
                    try uiImageTmp.pngData()?.write(to: filePath, options: .atomic)
                    
                    successfully(filePath.lastPathComponent)
                } catch {
                    LoggingTree.INSTANCE.e("WorkWithFiles/saveImageByUrl Save image to dir error \(url.absoluteString)" )
                    errorListener("Ошибка!", "Что-то пошло не так.")
                    return
                }
            }else{
                LoggingTree.INSTANCE.e("WorkWithFiles/saveImageByUrl filePath == nil" )
                errorListener("Ошибка!", "Что-то пошло не так.")
                return
            }
            
            // show 
            //self.image = uiImageTmp
            
        } catch {
            LoggingTree.INSTANCE.e("WorkWithFiles/saveImageByUrl 0 \(error.localizedDescription)" )
            errorListener("Ошибка!", "Что-то пошло не так.")
            // print(error.localizedDescription)
        }
    }
    
    func deleteFileFromDocumentsByName(_ nameFile: String){
        let pathDir = self.getDocumentsDirectory()
        
        if let destinationUrl = pathDir?.appendingPathComponent(nameFile) {
            self.deleteFileFromDocumentsByURL(destinationUrl)
        }
    }
    func deleteFileFromDocumentsByURL(_ file: URL){
        try? FileManager.default.removeItem(at: file)
    }
    
    func base64ToFile(_ b64: String, _ saveName: String, errorListener: (String, String) -> Void) -> Bool {
        let pathDir = getDocumentsDirectory()
        
        if let destinationUrl = pathDir?.appendingPathComponent(saveName) {
            var decodedData: Data? = Data(base64Encoded: b64, options: .ignoreUnknownCharacters)
            
            if(decodedData == nil){
                errorListener("Ошибка!", "Что-то пошло не так.")
                return false
            }
            
            
            try? decodedData?.write(to: destinationUrl)
            return true
            
        }
        return false
    }
    
    func fileToBase64ByName(_ nameFile: String) -> String{
        let pathDir = self.getDocumentsDirectory()
        
        if let destinationUrl = pathDir?.appendingPathComponent(nameFile) {
            let fileData = try? Data.init(contentsOf: destinationUrl)
            if let fileData = fileData {
                let fileStream:String = fileData.base64EncodedString(options: NSData.Base64EncodingOptions.init(rawValue: 0))
                //let fileStream:String = fileData.base64EncodedString()
                return fileStream
            }else {
                LoggingTree.INSTANCE.e("WorkWithFiles/fileToBase64ByName  erro convert file to Base64 \(nameFile)" )
                return "error convert file"
            }
        }else{
            LoggingTree.INSTANCE.e("WorkWithFiles/fileToBase64ByName  erro convert file to URL \(nameFile)" )
            return "error convert file"
        }
    }
    
    func renameImageWithId(_ item: MessageRoomItem, _ idMessage: String ) -> String?{
        let pathDir = self.getDocumentsDirectory()
        if let uriFile = pathDir?.appendingPathComponent(item.text!) {
            //telemedicine_555_4_1680172330439.jpg тут это  item.text!
            
            let fName = item.text!
            let ext = self.getExtensionByUri(uriFile)
            
            let tmpInd = fName.range(of: ".", options: NSString.CompareOptions.backwards)
            
            
            let lastInd1 = fName.lastIndexOf(target: "_")! + 1
            let lastInd2 = fName.lastIndexOf(target: ".")!
            let range = lastInd1..<lastInd2
            let timeMillis = fName.substring(start: lastInd1, end: lastInd2)
            
            let newName = self.getNewNameForNewFile(item.idRoom!, ext!, idMessage, timeMillis)
            
            if let newNameTmp = newName , let newUriFile = pathDir?.appendingPathComponent(newNameTmp){
                let res =  self.copyFileByUri(uriFile, newUriFile)
                if (res){
                    self.deleteFileFromDocumentsByURL(uriFile)
                    
                    return newUriFile.lastPathComponent
                }else{
                    return nil
                }
            }
        }
        
        return nil
    }
    
    func copyFileByUri(_ odlURL: URL, _ newURL: URL) -> Bool {
        do {
            if FileManager.default.fileExists(atPath: newURL.path) {
                try FileManager.default.removeItem(at: newURL)
            }
            try FileManager.default.copyItem(at: odlURL, to: newURL)
        } catch (let error) {
            //print("Cannot copy item at \(odlURL) to \(newURL): \(error)")
            return false
        }
        return true
    }
    
    func getExtensionByUri(_ urlIn: URL) -> String? {
        let ext = urlIn.absoluteString.lowercased()
        if(ext.hasSuffix("pdf")){
            return "pdf"
        }else if (ext.hasSuffix("png")){
            return "png"
        }else if(ext.hasSuffix("wav")){
            return "wav"
        }else {
            return nil
        }
    }
    
    //oldName getNewUriForNewFile
    func getNewNameForNewFile(_ idRoom: String, _ extensionF: String, _ idMessage: String? = nil, _ timeMillis: String? = nil) -> String? {
        if(extensionF.isEmpty){
            return nil
        }
        
        let sharePreferenses = SharedPreferenses()
        let idCenter: String = String(sharePreferenses.currentUserInfo!.id_center!)
        
        var nameNewFile: String? = nil
        let curentTimeMils = String(MDate.getCurrentDate1970())
        if(idMessage != nil){
            nameNewFile = "\(Constants.PREFIX_NAME_FILE)_\(idCenter)_\(idRoom)_\(idMessage!)_\(timeMillis ?? curentTimeMils).\(extensionF)"
        }else{
            nameNewFile = Constants.PREFIX_NAME_FILE + "_" + idCenter + "_" + idRoom + "_" + (timeMillis ?? curentTimeMils) + "." + extensionF
        }
        
        return nameNewFile
    }
    
    
    func jsonToString(_ json: [String: Any]) -> String{
        do {
            let data1 = try JSONSerialization.data(withJSONObject: json, options: JSONSerialization.WritingOptions.prettyPrinted)
            let convertedString = String(data: data1, encoding: String.Encoding.utf8) as NSString? ?? ""
            //debugPrint(convertedString)
            return convertedString as String
        } catch let myJSONError {
            //debugPrint(myJSONError)
            return ""
        }
    }
    
    func lightenTheImage(_ inImage: UIImage) -> UIImage{
        //var tmpDataStart: Data? = inImage.pngData()
        let data1 = inImage.compress(to: 1400)
        
        let lightImgTmp: UIImage? = UIImage(data: data1)
        let ttt = lightImgTmp?.pngData()
        
        if(lightImgTmp == nil){
            return inImage
        }else{
            return lightImgTmp!
        }
    }
}



extension UIImage {
    func resized(withPercentage percentage: CGFloat, isOpaque: Bool = true) -> UIImage? {
        let canvas = CGSize(width: size.width * percentage, height: size.height * percentage)
        let format = imageRendererFormat
        format.opaque = isOpaque
        return UIGraphicsImageRenderer(size: canvas, format: format).image {
            _ in draw(in: CGRect(origin: .zero, size: canvas))
        }
    }

    func compress(to kb: Int, allowedMargin: CGFloat = 0.2) -> Data {
        let bytes = kb * 1024
        var compression: CGFloat = 1.0
        let step: CGFloat = 0.05
        var holderImage = self
        var complete = false
        while(!complete) {
            if let data = holderImage.jpegData(compressionQuality: 1.0) {
                let ratio = data.count / bytes
                if data.count < Int(CGFloat(bytes) * (1 + allowedMargin)) {
                    complete = true
                    return data
                } else {
                    let multiplier:CGFloat = CGFloat((ratio / 5) + 1)
                    compression -= (step * multiplier)
                }
            }
            
            guard let newImage = holderImage.resized(withPercentage: compression) else { break }
            holderImage = newImage
        }
        return Data()
    }
}


extension String {
    var length:Int {
        return self.count
    }

    func indexOf(target: String) -> Int? {

        let range = (self as NSString).range(of: target)

        guard range.toRange() != nil else {
            return nil
        }

        return range.location

    }
    func lastIndexOf(target: String) -> Int? {

        let range = (self as NSString).range(of: target, options: NSString.CompareOptions.backwards)

        guard range.toRange() != nil else {
            return nil
        }

        //return self.length - range.location - 1
        return range.location
    }
    func contains(s: String) -> Bool {
        return (self.range(of: s) != nil) ? true : false
    }
    
    func substring(start: Int, end : Int) -> String
    {
        let startIndex = self.index(self.startIndex, offsetBy: start)
        let endIndex = self.index(self.startIndex, offsetBy: end)
        return String(self[startIndex..<endIndex])
    }
}

