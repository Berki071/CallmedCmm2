//
//  ShowImagePresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI

class ShowImagePresenter : ObservableObject{
    @Published var image:UIImage? = nil
    @Published var pdf : Data? = nil
    @Published var showDialogLoading: Bool = false
    
    let sharePreferenses : SharedPreferenses
    
    var telemendItemData: ShowImagesFilesItemData? = nil
    var url: URL? = nil
    
    
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
        if(self.telemendItemData == nil && self.url == nil){
            return
        }
        
        var urlString = ""
       if(self.telemendItemData != nil){
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
