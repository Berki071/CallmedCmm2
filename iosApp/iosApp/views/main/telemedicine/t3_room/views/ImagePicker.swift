//
//  ImagePicker.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import Foundation
import shared

struct ImagePickerListener{
    var error: () -> Void
    var imageCreated: (String) -> Void
}

struct ImagePicker: UIViewControllerRepresentable {
    @Environment(\.presentationMode) private var presentationMode
    var sourceType: UIImagePickerController.SourceType = .photoLibrary
    var item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?
    var listener : ImagePickerListener
    
    var sharePreferenses : SharedPreferenses = SharedPreferenses()

    func makeUIViewController(context: UIViewControllerRepresentableContext<ImagePicker>) -> UIImagePickerController {

        let imagePicker = UIImagePickerController()
        imagePicker.allowsEditing = false
        imagePicker.sourceType = sourceType
        imagePicker.delegate = context.coordinator


        return imagePicker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: UIViewControllerRepresentableContext<ImagePicker>) {

    }

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    
    func saveUIImage(_ imgIn: UIImage){
        let idCenter: String = String(Int.init(truncating: self.sharePreferenses.currentCenterInfo!.idCenter ?? -1))
        let idRoom: String = String(Int.init(truncating: item!.idRoom!))
        let curentTimeMils = String(MDate.getCurrentDate1970())
        
        let path = getDocumentsDirectory()
        let fileName = "\(Constants.PREFIX_NAME_FILE)_\(idCenter)_\(idRoom)_\(curentTimeMils).png"
        
        let workWithFile = WorkWithFiles()
        let newImage = workWithFile.lightenTheImage(imgIn)
        
        if let filePath = path?.appendingPathComponent(fileName) {
            // Save image.
            do {
                try newImage.pngData()?.write(to: filePath, options: .atomic)
               
            } catch {
                print(">>> error")
            }
            
            self.listener.imageCreated(fileName)

        }
    }
    func getDocumentsDirectory() -> URL? {
        // find all possible documents directories for this user
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        // just send back the first one, which ought to be the only one
        return paths[0]
    }

    final class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

        var parent: ImagePicker

        init(_ parent: ImagePicker) {
            self.parent = parent
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {

            if let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
                parent.saveUIImage(image)
            }
            
            parent.presentationMode.wrappedValue.dismiss()
        }

    }
}
