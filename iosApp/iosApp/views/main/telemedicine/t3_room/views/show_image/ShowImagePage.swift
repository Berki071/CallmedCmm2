//
//  ShowImagePage.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI

struct ShowImagePage: View {
    var itemForShowBigImage: Binding<AnaliseResponseIos?>?
    var itemForShowBigImage2: Binding<DataClassForElectronicRecyIos?>?
    var itemForShowBigImage4: Binding<ShowImagesFilesItemData?>?
    var itemForShowBigImage5: Binding<URL?>?
    
    @ObservedObject var presenter: ShowImagePresenter
    
    init(itemForShowBigImage: Binding<AnaliseResponseIos?>){
        self.itemForShowBigImage = itemForShowBigImage
        itemForShowBigImage2 = nil
        itemForShowBigImage4 = nil
        itemForShowBigImage5 = nil
        presenter = ShowImagePresenter(analiseResponseIos: itemForShowBigImage.wrappedValue)
        
    }
    
    init(itemForShowBigImage2: Binding<DataClassForElectronicRecyIos?>){
        itemForShowBigImage = nil
        self.itemForShowBigImage2 = itemForShowBigImage2
        itemForShowBigImage4 = nil
        itemForShowBigImage5 = nil
        presenter = ShowImagePresenter(dataClassForElectronicRecy: itemForShowBigImage2.wrappedValue)
    }
    
    init(itemForShowBigImage4: Binding<ShowImagesFilesItemData?>){ //telemed
        itemForShowBigImage = nil
        itemForShowBigImage2 = nil
        self.itemForShowBigImage4 = itemForShowBigImage4
        itemForShowBigImage5 = nil
        presenter = ShowImagePresenter(telemendItemData: itemForShowBigImage4.wrappedValue)
    }
    
    init(itemForShowBigImage5: Binding<URL?>){ //telemed
        itemForShowBigImage = nil
        itemForShowBigImage2 = nil
        itemForShowBigImage4 = nil
        self.itemForShowBigImage5 = itemForShowBigImage5
        presenter = ShowImagePresenter(url: itemForShowBigImage5.wrappedValue)
    }
    
    var body: some View {
        ZStack{
            VStack{
                if(self.presenter.image != nil ){
                    ZoomableScrollView {
                        Image(uiImage: self.presenter.image!)
                            .resizable()
                            .scaledToFit()
                    }
                }
                
                if(self.presenter.pdf != nil){
                    PDFKitRepresentedView(self.presenter.pdf!)
                }
                if(self.presenter.html != nil){
                    WebView(url: self.presenter.html!)
                        .ignoresSafeArea()
                        .navigationTitle("Sarunw")
                        .navigationBarTitleDisplayMode(.inline)
                }
            }
            .padding(.top, 44.0)
            .frame(maxWidth: .infinity)
            .background(.white)
            
            VStack(spacing: 0){
                if(self.itemForShowBigImage == nil && self.itemForShowBigImage2 == nil){
                    MyToolBar(title1: "", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                        itemForShowBigImage?.wrappedValue = nil
                        itemForShowBigImage2?.wrappedValue = nil
                        itemForShowBigImage4?.wrappedValue = nil
                        itemForShowBigImage5?.wrappedValue = nil
                    }, strSerch: nil, isShowImageFreeLine: false, isShowShareBtn: {() -> Void in
                        self.presenter.shareF()
                    })
                }else{
                    MyToolBar(title1: "", isShowSearchBtn: false, clickHumburger: {() -> Void in   //44.0
                        itemForShowBigImage?.wrappedValue = nil
                        itemForShowBigImage2?.wrappedValue = nil
                        itemForShowBigImage4?.wrappedValue = nil
                        itemForShowBigImage5?.wrappedValue = nil
                    }, strSerch: nil, isShowImageFreeLine: false, isShowShareBtn: nil)
                }
                
                
                Spacer()
            }
            
 
            if(self.presenter.showDialogLoading == true){
                LoadingView()
            }
        }
    }
}

struct ShowIamgePage_Previews: PreviewProvider {
    @State static private var tmp2: URL? = nil
    
    static var previews: some View {
        ShowImagePage(itemForShowBigImage5: $tmp2)
    }
}

