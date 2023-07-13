//
//  Constants.swift
//  CallMed_ios
//
//  Created by Mihail on 08.06.2023.
//

import Foundation

class Constants{
    static let PREFIX_NAME_FILE = "telemedicine"
    static let SENDER_ID_FCM_PATIENT = "933088839978"
    
    enum WhatDataShow: String {
        case ARCHIVE, ACTIVE
        func callAsFunction() -> String {
            rawValue
        }
    }
    
    enum TelemedicineStatusRecord: String {
        case wait, active, complete
        func callAsFunction() -> String {
            rawValue
        }
    }
    
    enum MsgRoomType: String{
        case DATE, TARIFF, TEXT, IMG, FILE
        func callAsFunction() -> String {
            rawValue
        }
      }
    
    enum TelemedicineNotificationType: String {
        case MESSAGE = "Медицинский помощник.Сотрудник.Телемедицина.Сообщение", PAY = "Медицинский помощник.Сотрудник.Телемедицина.Оплата",
             START_APPOINTMENT = "Медицинский помощник.Сотрудник.Телемедицина.ПриемНачался", END_APPOINTMENT = "Медицинский помощник.Сотрудник.Телемедицина.ПриемОкончился"
    }
    
    enum TelemedicineChangeStatusAppointment: String {
        case START = "Начат прием. Войдите в приложение или нажмите на уведомление ", END = "Прием окончен"
    }
}
