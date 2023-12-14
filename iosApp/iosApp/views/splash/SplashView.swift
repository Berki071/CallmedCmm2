//
//  ContentView.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import SwiftUI

struct SplashView: View {
    @ObservedObject var splashPresenter = SplashPresenter()
    
    var body: some View {
        if(self.splashPresenter.nextPage != ""){
            if(self.splashPresenter.nextPage == "Login"){
                LoginView()
            }else{
                MainView(startPage: -1)
            }
        }else{
            ZStack{
                VStack {
                    Image("splash_sotr")
                        .resizable()
                        .scaledToFill()
                        .ignoresSafeArea()
                    //.aspectRatio(contentMode: .fit)
                }
                
                if(self.splashPresenter.isShowAlertStandart != nil){
                    StandartAlert(dataOb: splashPresenter.isShowAlertStandart!)
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        SplashView()
    }
}
