//
//  ShowFilesView.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct ShowFilesView: View {
    
    @ObservedObject var mainPresenter: ShowFilesPresenter
    var showBigFileListener : (ShowImagesFilesItemData) -> Void
    
    init(item: Binding<AllRecordsTelemedicineItem?>, showBigFileListener: @escaping (ShowImagesFilesItemData) -> Void ){
        mainPresenter = ShowFilesPresenter(item: item)
        self.showBigFileListener = showBigFileListener
    }
    
    var body: some View {
        ZStack {
            VStack{
                List(self.mainPresenter.arrayFilesUrl) {
                    if($0.url == nil){
                        ShowImagesItemDate(item: $0)
                            .listRowSeparator(.hidden)
                            .listRowInsets(EdgeInsets(top: 0, leading: -1, bottom: 0, trailing: 0))
                    }else{
                        ShowFileItem(item: $0, listener: ShowImagesItemListener(simpleClick: {(i: ShowImagesFilesItemData) -> Void in
                            self.showBigFileListener(i)
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

struct ShowFilesView_Previews: PreviewProvider {
    @State static var item: AllRecordsTelemedicineItem? = AllRecordsTelemedicineItem()
    
    static var previews: some View {
        ShowFilesView(item: $item, showBigFileListener: {(ShowImagesFilesItemData) -> Void in})
    }
}
