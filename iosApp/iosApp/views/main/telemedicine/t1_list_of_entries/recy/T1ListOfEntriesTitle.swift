//
//  T1ListOfEntriesTitle.swift
//  CallMed_ios
//
//  Created by Mihail on 19.06.2023.
//

import SwiftUI
import shared

struct T1ListOfEntriesItemListener{
    let showInfo: (AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) -> Void
    let goToRoom: (AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) -> Void
    let closeTm: (AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem) -> Void
    let sendNotyReminder: (AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem, String, String) -> Void
}

struct T1ListOfEntriesTitle: View {
    @Binding var item : DataForListOfEntriesRecy
    var listener: T1ListOfEntriesItemListener? = nil
    
    @State var isShowDop: Bool
    
    init(item : Binding<DataForListOfEntriesRecy>, listener : T1ListOfEntriesItemListener?){
        self._item = item
        
//        let tmp1 = item.wrappedValue.items.count
//        let tmp2 = item.wrappedValue.items[0].status
        
        self.isShowDop = item.wrappedValue.isShowDop
        self.listener = listener
    }
    
    var body: some View {
    
        
        VStack(spacing:0){
            HStack{
                Text(item.title)
                    .multilineTextAlignment(.leading)
                    .padding(8.0)
                    .font(.system(size: 16))
                
                Spacer()
                
                if(isShowDop){
                    Image("arrow_up")
                        .padding(8.0)
                        .imageScale(.large)
                        .foregroundColor(Color("textSideMenu"))
                }else{
                    Image("arrow_down")
                        .padding(8.0)
                        .imageScale(.large)
                        .foregroundColor(Color("textSideMenu"))
                }
            }
            .frame(maxWidth: .infinity, minHeight: 44.0)
            .background(Color("black_bg3"))
            .cornerRadius(8)
            .padding(8.0)
            .onTapGesture{
                isShowDop = !isShowDop
            }
            
            if(isShowDop){
                ForEach(self.item.items, id: \.self) { item in
                    T1ListOfEntriesItem(item: item, listener: listener)
                    
                }
            }
        }
    }
}

struct T1ListOfEntriesItem_Previews: PreviewProvider {
    @State static private var item = DataForListOfEntriesRecy(id: 1 ,title: "nil", items: [])
    
    static var previews: some View {
        T1ListOfEntriesTitle(item: $item, listener: nil)
    }
}
