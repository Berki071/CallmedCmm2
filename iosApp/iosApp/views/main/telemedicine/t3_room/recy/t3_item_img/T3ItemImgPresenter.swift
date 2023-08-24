//
//  T3ItemPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI
import shared

class T3ItemImgPresenter: ObservableObject {
    var item: MessageRoomItem
    
    @Published var iuImageLogo : UIImage? = nil
    
    init(item: MessageRoomItem){
        self.item = item
        
        self.getImage()
    }
    
    
    
    func getImage(){
        let fileName = item.text!
        
        let path = getDocumentsDirectory()
        
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(at: path!, includingPropertiesForKeys: nil)
            let allFilesPngURL = directoryContents.filter{ $0.pathExtension == "png" }  // во внутренню папку все сохраняютсяв пнг
            
            for i in allFilesPngURL {
                if i.absoluteString.contains(fileName){
                    self.urlToUIImage(i)
                    return
                }
            }
            
        } catch {
            LoggingTree.INSTANCE.e("T3ItemImg/getImage", error)
        }
    }
    
    func urlToUIImage(_ urlF: URL){
        do {
            var imageData : Data? = try Data(contentsOf: urlF)
            
            guard let dataOfImage = imageData else { return }
            guard let image = UIImage(data: dataOfImage) else { return }
            self.iuImageLogo = image
            let tttt0 = image == nil
            let tttt = self.iuImageLogo == nil
            
            //print(">>>>>>!!!>>>>> \(dataOfImage.count)")
        } catch {
            //print(error.localizedDescription)
        }
                    
    }
    
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }
}
