//
//  LoadingView.swift
//  CallMed_ios
//
//  Created by Mihail on 07.12.2022.
//

import SwiftUI

//, Equatable
struct LoadingView: View{
    var body: some View {
        ZStack{
            Color("black_bg")
            ProgressView("Данные обновляются, подождите…")
                .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
                .foregroundColor(.white)
        }
        .ignoresSafeArea()
    }
    
//    static func == (lhs: LoadingView, rhs: LoadingView) -> Bool {
//        // << return yes on view properties which identifies that the
//        // view is equal and should not be refreshed (ie. `body` is not rebuilt)
//        return true
//    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView()
    }
}
