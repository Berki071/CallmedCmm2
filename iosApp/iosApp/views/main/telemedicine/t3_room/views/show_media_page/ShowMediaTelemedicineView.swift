//
//  ShowMediaTelemedicineView.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct ShowMediaTelemedicineView: View {
    var itemRecord: Binding<AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem?>
    var clickBack: () -> Void
    
    @State var isShowBigImageOrFile: ShowImagesFilesItemData? = nil
    
    @State private var selectedTab: Int = 0
    let tabs: [Tab] = [
        .init(icon: nil, title: "Изображения"),
        .init(icon: nil, title: "Документы")
    ]
    
    var body: some View {
        if(self.isShowBigImageOrFile != nil){
            ShowImagePage(itemForShowBigImage4: self.$isShowBigImageOrFile)
        }else{
            ZStack{
                VStack(spacing: 0){
                    MyToolBar(title1: "Медиа", clickHumburger: {() -> Void in   //44.0
                        self.clickBack()
                    }, isShowImageFreeLine: false)
                    GeometryReader { geo in
                        VStack(spacing: 0) {
                            // Tabs
                            Tabs(tabs: tabs, geoWidth: geo.size.width, selectedTab: $selectedTab)
                            // Views
                            TabView(selection: $selectedTab,
                                    content: {
                                ShowImagesView(item: itemRecord, showBigImglistener: {(i: ShowImagesFilesItemData) -> Void in
                                    self.isShowBigImageOrFile = i
                                })
                                .tag(0)
                                ShowFilesView(item: itemRecord, showBigFileListener: {(i: ShowImagesFilesItemData) -> Void in
                                    self.isShowBigImageOrFile = i
                                })
                                .tag(1)
                            })
                            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                        }
                    }
                    
                }
            }
        }
    }
}

struct ShowMediaTelemedicineView_Previews: PreviewProvider {
    @State static var item: AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem? = AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem()
    
    static var previews: some View {
        ShowMediaTelemedicineView(itemRecord: $item, clickBack: {() -> Void in })
    }
}
