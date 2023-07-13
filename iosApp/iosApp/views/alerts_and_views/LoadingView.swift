//
//  LoadingView.swift
//  CallMed_ios
//
//  Created by Mihail on 07.12.2022.
//

import SwiftUI

struct LoadingView: View {
    var body: some View {
        ZStack{
            Color("black_bg")
            ProgressView("Данные обновляются, подождите…")
                .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
                .foregroundColor(.white)
        }
        .ignoresSafeArea()
    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView()
    }
}
