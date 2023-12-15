//
//  TimetableDocItem.swift
//  CallMed_ios
//
//  Created by Mihail on 14.12.2022.
//

import SwiftUI
import shared

struct TimetableDocDayItem: View {
    private let INDIVIDUAL_DB_NAME = "msoli"
    
    @State var showDopIngo = false
    
    var dbName: String?
    var item : VisitResponse.VisitItem
    var appointmentConfirm: (VisitResponse.VisitItem) -> Void
    
    var body: some View {
        ZStack{
            let isRecord = item.naim != nil && !item.naim!.isEmpty
            //let colorbg = isRecord ? Color("bg_record") : Color.black
            
       
            
            VStack(spacing: 0){
                HStack(spacing: 0){
                    let title = isRecord ? item.fullName : "Нет записи"
                    
                    Text(item.time ?? "")
                        .font(.headline)
                        .foregroundColor(Color("dark_blue"))
                        .frame(width: 60.0)
                        //.background(Color.red)
                    
                    Spacer()
                        .frame(width: 6.0)
                    
                    if(isRecord){
                        if(dbName != nil && dbName! == INDIVIDUAL_DB_NAME){
                            if(item.statPriem == "p"){
                                Text(title)
                                    .font(.headline)
                                    .foregroundColor(Color("bg_record"))
                            }else{
                                Text(title)
                                    .font(.headline)
                                    .foregroundColor(Color("red"))
                            }
                        }else{
                            Text(title)
                                .font(.headline)
                                .foregroundColor(Color("bg_record"))
                        }
                        
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
            .contextMenu {
                if(dbName != nil && dbName! == INDIVIDUAL_DB_NAME){
                    if(item.statPriem != "p"){
                        menuItems
                    }
                }
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: 0)
                .stroke(Color("black_bg3"), lineWidth: 1)
        )
    }
    
    
    var menuItems: some View {
         Group {
             Button("Подтвердить прием", action: {self.appointmentConfirm(self.item)})
         }
     }
    
}

struct TimetableDocItem_Previews: PreviewProvider {
    static var previews: some View {
        TimetableDocDayItem(item: VisitResponse.VisitItem(time:"18:00", am_kl: "Ivanov", name_kl:"Ivan", otch_kl:"Ivanovich", naim:"priem tatata tata", komment:"ku ku epta"),
                            appointmentConfirm: {(VisitItem) -> Void in })
    }
}
