//
//  ToolbarTelemedicineRoom.swift
//  CallMed_ios
//
//  Created by Mihail on 21.06.2023.
//

import SwiftUI
import shared

struct ToolbarTelemedicineRoomListener{
    var goToMedia: () -> Void
    var closeTm: () -> Void
    var clickBack: () -> Void
    var clickStartReception: () -> Void
    var clickCompleteReception: () -> Void
    
    var showAnalizes: () -> Void
    var showConclusions: () -> Void
}

struct ToolbarTelemedicineRoom: View {
    @ObservedObject var mainPresenter: ToolbarTelemedicineRoomPresenter
    
    init(item: Binding<AllRecordsTelemedicineItem?>, listener: ToolbarTelemedicineRoomListener){

        mainPresenter = ToolbarTelemedicineRoomPresenter(item: item, listener: listener)
       
    }

    var body: some View {
        ZStack{

            VStack{
                HStack(spacing: 0){
                    Button {
                        self.mainPresenter.listener.clickBack()
                    } label: {
                        Image(systemName: "arrow.left")
                            .foregroundColor(Color.white)
                            .padding(.leading, 12.0)
                            .imageScale(.large)
                    }


                    Spacer()

//                    Image(uiImage: self.mainPresenter.iuImageLogo)
//                        .resizable()
//                        .aspectRatio(contentMode: .fit)
//                        .padding(8.0)
//                        .frame(width: 45.0, height: 50.0)
                    
                    VStack(alignment: .center){
                        Text(self.mainPresenter.stringToInitials(str: self.mainPresenter.itemRecord.wrappedValue?.fullNameKl ?? ""))
                            .font(.system(size: 18))
                            .foregroundColor(Color.white)
                    }
                    .frame(width: 36.0, height: 36.0)
                    .background(Color("text_gray"))
                    .cornerRadius(18)
                    .padding(8)
                    

                    Text(self.mainPresenter.itemRecord.wrappedValue?.fullNameKl ?? "")
                        .font(.system(size: 16))
                        .foregroundColor(Color.white)

                    Spacer()

                    if(self.mainPresenter.isShowTimer){
                        Text(self.mainPresenter.showTimeTimer)
                            .foregroundColor(.white)
                            .font(.system(size: 15))
                            .padding(.horizontal, 9.0)
                            .padding(.vertical, 3.0)
                            .background(Color("deepRed"))
                            .clipShape(
                                RoundedRectangle(cornerRadius: 20)
                            )
                    }


                    if(self.mainPresenter.itemRecord.wrappedValue != nil){
                        Menu {
                            Button("Медиа", action: self.mainPresenter.goToMedia)

                            if(self.mainPresenter.itemRecord.wrappedValue!.status != Constants.TelemedicineStatusRecord.active()){
                                Button("Начать прием", action: self.mainPresenter.clickStartReception)
                            }else{
                                Button("Завершить прием", action: self.mainPresenter.clickCompleteReception)
                            }
                            
                            Button("Посмотреть анализы", action: self.mainPresenter.showAnalizes)
                            Button("Посмотреть заключения ", action: self.mainPresenter.showConclusions)

                        } label: {
                            Label("", systemImage: "ellipsis")
                                .foregroundColor(Color.white)
                                .rotationEffect(.degrees(-90))
                                .padding(.horizontal, 12.0)
                                .padding(.vertical, 8.0)
                        }
                        .frame(height: 48.0)
                    }
                }
                .frame(height: 48.0)
                .background(Color("color_primary"))

                Spacer()
            }

        }


    }
}

struct ToolbarTelemedicineRoom_Previews: PreviewProvider {
    @State static var item: AllRecordsTelemedicineItem? = AllRecordsTelemedicineItem()
    static let list = ToolbarTelemedicineRoomListener(goToMedia: {() -> Void in }, closeTm: {() -> Void in }, clickBack: {() -> Void in }, clickStartReception:  {() -> Void in },
                                                      clickCompleteReception: {() -> Void in }, showAnalizes: {() -> Void in }, showConclusions: {() -> Void in })

    static var previews: some View {
        ToolbarTelemedicineRoom(item: $item, listener: list)
    }
}
