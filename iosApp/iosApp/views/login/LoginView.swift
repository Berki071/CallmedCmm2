//
//  LoginView.swift
//  CallMed_ios
//
//  Created by Mihail on 05.12.2022.
//

import SwiftUI
import AnyFormatKit
import AnyFormatKitSwiftUI


let lightGreyColor = Color(red: 239.0/255.0, green: 243.0/255.0, blue: 244.0/255.0, opacity: 1.0)

struct LoginView: View {
    @ObservedObject var loginPresenter = LoginPresenter()
    
    var body: some View {
        if(self.loginPresenter.nextPage != ""){
            MainView(startPage: -1)
        }else{
            ZStack{
                VStack{
                    Spacer()
                        .frame(height: 60.0)
                    
                    Image("sotr_ic_launcher")
                        .resizable(resizingMode: .stretch)
                    //.background(Color("color_primary"))
                        .frame(width: 75.0, height: 75.0)
                    
                    Text("Введите учетные данные")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(Color("color_primary"))
                        .padding(.top, 16.0)
                    
                    
                    FormatStartTextField(
                        unformattedText: $loginPresenter.username,
                        formatter: PlaceholderTextInputFormatter(textPattern: "+7(***) *** ** **", patternSymbol: "*")
                    )
                    .keyboardType(.numberPad)
                    .padding()
                    .padding(.horizontal, 16.0)
                    .background(lightGreyColor)
                    .cornerRadius(5.0)
                    
                    
                    SecureField("Пароль", text: $loginPresenter.password)
                        .padding()
                        .padding(.horizontal, 16.0)
                        .background(lightGreyColor)
                        .cornerRadius(5.0)
                        .keyboardType(.numberPad)
                    
                    
                    HStack {
                        Toggle(isOn: $loginPresenter.togleState) {}
                            .labelsHidden()
                        Text("Запомнить пароль")
                            .foregroundColor(Color("black_bg"))
                        Spacer()
                    }
                    .padding(.top, 16.0)
                    
                    HStack{
                        Text("Войти")
                            .foregroundColor(.white)
                            .padding(.all, 9)
                    }
                    .frame(maxWidth: .infinity)
                    .background(Color("color_primary"))
                    .cornerRadius(5.0)
                    .onTapGesture{
                        self.loginPresenter.onLoginClick()
                    }
                    
                    Spacer()
                    
                }
                .padding(/*@START_MENU_TOKEN@*/.all, 16.0/*@END_MENU_TOKEN@*/)
                .onTapGesture {
                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                }
                
                if(self.loginPresenter.isShowAlertStandart != nil){
                    StandartAlert(dataOb: loginPresenter.isShowAlertStandart!)
                }
                
                if(self.loginPresenter.showLoading == true){
                    LoadingView()
                }
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
