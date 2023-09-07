//
//  ShowImagePresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared

class ShowImagePresenter : ObservableObject{
    @Published var image:UIImage? = nil
    @Published var pdf : Data? = nil
    @Published var html : URL? = nil
    
    @Published var showDialogLoading: Bool = false
    
    let dm = DownloadManager()
    let sharePreferenses : SharedPreferenses
    
    var analiseResponseElectronic: AnaliseResponse? = nil
    var resultZakl2ItemElectronic: ResultZakl2Item? = nil
    var analiseResponseIos: AnaliseResponseIos? = nil
    var telemendItemData: ShowImagesFilesItemData? = nil
    var url: URL? = nil
    
    init(analiseResponseIos: AnaliseResponseIos?){
        sharePreferenses = SharedPreferenses()
        
        self.analiseResponseIos = analiseResponseIos
        
        if(analiseResponseIos != nil ){
            
            let name = dm.getNameFile(analiseResponseIos!.linkToPDF!)
            let exp = dm.getExpansionFromFileName(name)
            
            self.showLoading(true)
            let urlString = analiseResponseIos!.pathToFile
            guard let url = URL(string: urlString) else { return }
            let task = URLSession.shared.dataTask(with: url) { data, response, error in
                guard let data = data else { return }
                DispatchQueue.main.async {
                    
                    if(exp == "jpg" || exp == "jpeg"){
                        let photo = UIImage.init(data: data)
                        self.image = photo!
                        
                    }else if(exp == "pdf"){
                        self.pdf = data
                    }
                    
                    self.showLoading(false)
                }
            }
            task.resume()
            
        }
    }
    
    init(dataClassForElectronicRecy: DataClassForElectronicRecyIos?){
        sharePreferenses = SharedPreferenses()
        
        if(dataClassForElectronicRecy?.item is AnaliseResponse){
            analiseResponseElectronic = dataClassForElectronicRecy?.item as? AnaliseResponse
        }else{
            resultZakl2ItemElectronic = dataClassForElectronicRecy?.item as? ResultZakl2Item
        }
        
        if(analiseResponseElectronic != nil ){
            
            let name = dm.getNameFile(analiseResponseElectronic!.linkToPDF!)
            let exp = dm.getExpansionFromFileName(name)
            
            self.showLoading(true)
            let urlString = analiseResponseElectronic!.pathToFile
            guard let url = URL(string: urlString) else { return }
            let task = URLSession.shared.dataTask(with: url) { data, response, error in
                guard let data = data else { return }
                DispatchQueue.main.async {
                    
                    if(exp == "jpg" || exp == "jpeg"){
                        let photo = UIImage.init(data: data)
                        self.image = photo!
                        
                    }else if(exp == "pdf"){
                        self.pdf = data
                    }
                    
                    self.showLoading(false)
                }
            }
            task.resume()
            
        }else if(resultZakl2ItemElectronic != nil){
            
            let puthToFile = resultZakl2ItemElectronic!.pathToFile
            let exp = dm.getExpansionFromFilePath(puthToFile)
            
            self.showLoading(true)
            let urlString = resultZakl2ItemElectronic!.pathToFile
            guard let url = URL(string: urlString) else { return }
            
            if(exp == "html"){
                self.html = url
                //self.url = url
                self.showLoading(false)
                return
            }
            
            let task = URLSession.shared.dataTask(with: url) { data, response, error in
                guard let data = data else {
                    DispatchQueue.main.async {
                        self.showLoading(false)
                    }
                    return
                }
                DispatchQueue.main.async {
                    
                    if(exp == "jpg" || exp == "jpeg"){
                        let photo = UIImage.init(data: data)
                        self.image = photo!
                        
                    }else if(exp == "pdf"){
                        self.pdf = data
                    }
                    
                    self.showLoading(false)
                }
            }
            task.resume()
        }
    }
    
    
    init(telemendItemData: ShowImagesFilesItemData?){ //telemed img
        sharePreferenses = SharedPreferenses()
        
        self.telemendItemData = telemendItemData

        if(telemendItemData != nil){
            let ext = telemendItemData!.url!.absoluteString.lowercased()
            
            if(ext.hasSuffix("pdf")){
                
                let task = URLSession.shared.dataTask(with: telemendItemData!.url!) { data, response, error in
                    guard let data = data else { return }
                    DispatchQueue.main.async {
                        self.pdf = data
                    }
                }
                task.resume()
                
            }else{
                do {
                    var imageData : Data? = try Data(contentsOf: telemendItemData!.url!)
                    
                    guard let dataOfImage = imageData else { return }
                    guard let image = UIImage(data: dataOfImage) else { return }
                    self.image = image
                    //print(">>>>>>!!!>>>>> \(dataOfImage.count)")
                } catch {
                    print(error.localizedDescription)
                }
            }
        }
    }
    
    init(url: URL?){ //telemed img
        sharePreferenses = SharedPreferenses()
        
        self.url = url

        if(url != nil){
            let ext = url!.absoluteString.lowercased()
            
            if(ext.hasSuffix("pdf")){
                
                let task = URLSession.shared.dataTask(with: url!) { data, response, error in
                    guard let data = data else { return }
                    DispatchQueue.main.async {
                        self.pdf = data
                    }
                }
                task.resume()
                
            }else{
                do {
                    var imageData : Data? = try Data(contentsOf: url!)
                    
                    guard let dataOfImage = imageData else { return }
                    guard let image = UIImage(data: dataOfImage) else { return }
                    self.image = image
                    
                } catch {
                    print(error.localizedDescription)
                }
            }
        }
    }
    
    func showLoading(_ isShow : Bool){
        if(isShow){
            self.showDialogLoading = true
        }else{
            self.showDialogLoading = false
        }
    }
    
    func shareF(){
        if(self.analiseResponseIos == nil && analiseResponseElectronic == nil && resultZakl2ItemElectronic == nil && self.telemendItemData == nil && self.url == nil){
            return
        }
        
        var urlString = ""
        if(analiseResponseIos != nil ){
            urlString = analiseResponseIos!.pathToFile
        }else if(analiseResponseElectronic != nil){
            urlString = analiseResponseElectronic!.pathToFile
        }else if (resultZakl2ItemElectronic != nil){
            urlString = resultZakl2ItemElectronic!.pathToFile
        }else if(self.telemendItemData != nil){
            urlString = self.telemendItemData!.url!.absoluteString
        }else if(self.url != nil){
            urlString = self.url!.absoluteString
        }
        
        
        guard let urlF = URL(string: urlString) else { return }
        
        let fileURL = NSURL(fileURLWithPath: urlF.absoluteString)
        // Create the Array which includes the files you want to share
        var filesToShare = [Any]()
        // Add the path of the file to the Array
        filesToShare.append(fileURL)
        
//        // Make the activityViewContoller which shows the share-view
//        let activityViewController = UIActivityViewController(activityItems: filesToShare, applicationActivities: nil)
//        // Show the share-view
//        self.present(activityViewController, animated: true, completion: nil)
//
        
        
        guard let source = UIApplication.shared.windows.last?.rootViewController else {
            return
        }
        let vc = UIActivityViewController(activityItems: filesToShare,applicationActivities: nil)
        vc.excludedActivityTypes = nil
        vc.popoverPresentationController?.sourceView = source.view
        source.present(vc, animated: true)
    }
    
}
