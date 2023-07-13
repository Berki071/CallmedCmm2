//
//  ShowImagesItemImgPresenter.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import Foundation
import SwiftUI

class ShowImagesItemImgPresenter: ObservableObject {
    
    @Published var image: UIImage? = nil
    
    var item: ShowImagesFilesItemData
    
    init(item: ShowImagesFilesItemData){
        self.item = item
        urlToUIImage()
    }
    
    func urlToUIImage(){
        do {
            var imageData : Data? = try Data(contentsOf: item.url!)
            
            guard let dataOfImage = imageData else { return }
            guard let image = UIImage(data: dataOfImage) else { return }
            self.image = image
            //print(">>>>>>!!!>>>>> \(dataOfImage.count)")
        } catch {
            print(error.localizedDescription)
        }
                    
    }
}
