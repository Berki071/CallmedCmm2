//
//  ShowImagesView.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct ShowImagesFilesItemData: Identifiable{
    let id = UUID()
    var date: Date
    var url: URL?
}

struct ShowImagesItemListener {
    var simpleClick: (ShowImagesFilesItemData) -> Void
    var longClick: (ShowImagesFilesItemData) -> Void
}

struct ShowImagesView: View {
    @ObservedObject var mainPresenter: ShowImagesPresenter
    
    var showBigImglistener : (ShowImagesFilesItemData) -> Void
    
    init(item: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>, showBigImglistener: @escaping  (ShowImagesFilesItemData) -> Void){
        mainPresenter = ShowImagesPresenter(item: item)
        self.showBigImglistener = showBigImglistener
    }
    
    var body: some View {
        ZStack {
            VStack{
                List(self.mainPresenter.arrayImagePng) {
                    if($0.url == nil){
                        ShowImagesItemDate(item: $0)
                            .listRowSeparator(.hidden)
                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                    }else{
                        ShowImagesItemImg(item: $0, listener: ShowImagesItemListener(simpleClick: {(i: ShowImagesFilesItemData) -> Void in
                            self.showBigImglistener(i)
                        }, longClick: {(i: ShowImagesFilesItemData) -> Void in
                            self.mainPresenter.deleteItemQuestion(i)
                        }))
                            .listRowSeparator(.hidden)
                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                    }
                    
                }
                .listStyle(PlainListStyle())
            }
            .frame(maxWidth: .infinity)
            .background(.white)
            
            
            if(self.mainPresenter.isShowAlertStandart != nil){
                StandartAlert(dataOb: mainPresenter.isShowAlertStandart!)
            }
        }
    }
}

struct Demo1View_Previews: PreviewProvider {
    @State static var item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem? = AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem()

    static var previews: some View {
        ShowImagesView(item: $item, showBigImglistener: {(ShowImagesItemData) -> Void in})
    }
}
