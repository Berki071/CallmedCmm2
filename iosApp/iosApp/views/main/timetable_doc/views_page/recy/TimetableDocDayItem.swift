//
//  TimetableDocItem.swift
//  CallMed_ios
//
//  Created by Mihail on 14.12.2022.
//

import SwiftUI

struct TimetableDocDayItem: View {
    @State var showDopIngo = false
    
    var item : VisitItem
    
    var body: some View {
        ZStack{
            let isRecord = item.naim != nil && !item.naim!.isEmpty
            let colorbg = isRecord ? Color("bg_record") : Color.black
       
            
            VStack(spacing: 0){
                HStack(spacing: 0){
                    let title = isRecord ? item.getFullName() : "Нет записи"
                    
                    Text(item.time ?? "")
                        .font(.headline)
                        .foregroundColor(Color("dark_blue"))
                        .frame(width: 60.0)
                        //.background(Color.red)
                    
                    Spacer()
                        .frame(width: 6.0)
                    
                    if(isRecord){
                        Text(title)
                            .font(.headline)
                            .foregroundColor(colorbg)
                    }else{
                        Text(title)
                            .font(.system(size: 16))
                            .foregroundColor(Color.gray)
                    }
                    Spacer()
                }
                .padding(.all, 6.0)
                
                if(self.showDopIngo && item.naim != nil && !item.naim!.isEmpty){
                    VStack(spacing: 0){
                        HStack{
                            Text("Услуга:")
                                .fontWeight(.bold)
                            Text(item.naim ?? "")
                            Spacer()
                        }
                       
                        if(item.komment != nil && !item.komment!.isEmpty){
                            HStack{
                                Text("Комментарий:")
                                    .fontWeight(.bold)
                                Text(item.komment!)
                                Spacer()
                            }
                        }
                    }
                    .padding([.leading, .bottom, .trailing])
                }
            }
            .onTapGesture {
                self.showDopIngo = !showDopIngo
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: 0)
                .stroke(Color("black_bg3"), lineWidth: 1)
        )
    }
    
    
}

struct TimetableDocItem_Previews: PreviewProvider {
    static var previews: some View {
        TimetableDocDayItem(item: VisitItem("18:00","Ivanov", "Ivan", "Ivanovich", "priem tatata tata", "ku ku epta"))
    }
}
