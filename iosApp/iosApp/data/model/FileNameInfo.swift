//
//  FileNameInfo.swift
//  iosApp
//
//  Created by Михаил Хари on 07.09.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

class FileNameInfo : Decodable,Encodable{
    var uuid: String
    
    var pref: String
    var date: String
    var nameSpec: String
    var nameUser: String
    
    
    init(pref: String, date: String, nameSpec: String, nameUser: String){
        
        uuid = UUID().uuidString
        self.pref = pref
        self.date = date
        self.nameSpec = nameSpec
        self.nameUser = nameUser
    }
    
    func getFileName() -> String {
        if(pref == Constants.ConclusionZacPDF){
            return  "\(pref)_\(date)_\(uuid).pdf"
        }else if(pref == Constants.ConclusionZacHtml){
            return  "\(pref)_\(date)_\(uuid).html"
        }else{
            let ext = getExpansionFromFileName(self.nameSpec)
            let dateEd = date.replacingOccurrences(of: ".", with: "_")
            return  "\(pref)_\(dateEd)_\(uuid).\(ext)"
        }
    }
    
    
    func getExpansionFromFileName(_ fName: String) -> String{
        let st2 = String(fName[fName.range(of: ".")!.lowerBound...])
        let st3 = String(st2[st2.index(st2.startIndex, offsetBy: 1)...])
        return st3
    }
}
