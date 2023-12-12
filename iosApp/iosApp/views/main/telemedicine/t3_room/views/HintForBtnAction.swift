//
//  HintForBtnAction.swift
//  iosApp
//
//  Created by Михаил Хари on 11.12.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct HintForBtnAction: View {
    var hintForActionBtnSend: String?
    
    var body: some View {
        if(self.hintForActionBtnSend != nil && !self.hintForActionBtnSend!.isEmpty){
            VStack{
                //Spacer()
                HStack{
                    Spacer()
                    HStack{
                        Text(self.hintForActionBtnSend ?? "")
                            .foregroundColor(Color.white)
                            .font(.system(size: 14))
                    }
                    .padding(8)
                    .background(Color("black_bg50"))
                    .cornerRadius(6)
                }
            }
        }
    }
}

struct HintForBtnAction_Previews: PreviewProvider {
    //@State static var hintForActionBtnSend: String? = ""
    
    static var previews: some View {
        HintForBtnAction(hintForActionBtnSend: "")
    }
}
