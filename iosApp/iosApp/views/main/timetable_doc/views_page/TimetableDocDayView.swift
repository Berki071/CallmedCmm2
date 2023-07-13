//
//  TimetableDocDayView.swift
//  CallMed_ios
//
//  Created by Mihail on 19.01.2023.
//

import SwiftUI

struct TimetableDocDayView: View {
    
    @ObservedObject var mainPresenter : TimetableDocPresenter
    
    var body: some View {
        ScrollView {
            
            ZStack{
                
                VStack(spacing: 0){
              
                    CalendarScheduleView( currentDateResponce: self.$mainPresenter.currentDateResponce, listSchedule: self.$mainPresenter.scheduleAll, clickNextWeek: {(i: String) -> Void in
                        self.mainPresenter.selectDate = ""
                        self.mainPresenter.msgCalendar = ""
                        self.mainPresenter.scheduleForDate = []
                        self.mainPresenter.scheduleAll = []
                        self.mainPresenter.clickLoadNewWeek(i)
                    },selectdedDate: {(j: String, k: String) -> Void  in
                        self.mainPresenter.selectDate(j)
                        self.mainPresenter.msgCalendar = k
                    }, selectDay: self.mainPresenter.selectDate, msg: self.mainPresenter.msgCalendar
                    )
                    .padding(.top, 4.0)
                    
                    
                    if(self.mainPresenter.showEmptyView){
                        VStack(spacing: 0){
                            Spacer()
                            Image("sh_profile")
                            Spacer()
                                .frame(height: 30.0)
                            Text("Ничего не найдено, выберите другую неделю или филиал")
                                .multilineTextAlignment(.center)
                                .padding(/*@START_MENU_TOKEN@*/.all/*@END_MENU_TOKEN@*/)
                            Spacer()
                            Spacer()
                                .frame(height: 70.0)
                        }
                    }else{
                        ForEach(self.mainPresenter.scheduleForDate, id: \.self ){itemR in
                            TimetableDocDayItem(item: itemR)
                        }
                        Spacer()
                    }
                }
                .padding(.top, 50.0)
                .frame(width: nil)
                
                VStack(spacing: 0){
                    DropdownSelectorCategory(selectedOption: self.$mainPresenter.selectBrtanch , options: mainPresenter.listBranch, onOptionSelected : { option in
                        self.mainPresenter.msgCalendar = ""
                        self.mainPresenter.scheduleForDate = []
                        self.mainPresenter.scheduleAll = []
                        
                        let strN = String(option.id_filial!)
                        self.mainPresenter.sharePreferenses.doctorSelectBranch = strN
                        self.mainPresenter.selectNeqBranch()
                        
                    })
                    Spacer()
                }
            }
        }
        
    }
}

struct TimetableDocDayView_Previews: PreviewProvider {
    static var previews: some View {
        TimetableDocDayView(mainPresenter: TimetableDocPresenter())
    }
}
