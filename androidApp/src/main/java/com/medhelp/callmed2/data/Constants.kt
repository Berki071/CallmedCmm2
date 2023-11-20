package com.medhelp.callmed2.data

import com.medhelp.callmed2.R

object Constants {
    val ICON_FOR_NOTIFICATION = R.mipmap.sotr_icon
    const val NOTIFICATION_ID_TIMETABLE_DOC = 1111
    const val PERMISSION_REQUEST_READ_PHONE_STATE = 130
    const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 132
    const val KEY_FOR_INTENT_POINTER_TO_PAGE = "POINTER_TO_PAGE"

    const val KEY_FOR_OPEN_PHOTO = 341
    const val KEY_FOR_OPEN_CAMERA = 1

    const val TYPE_DOP_VIDEO_CALL = "videoсall"
    const val MIN_TIME_BEFORE_VIDEO_CALL = 10 //минут, до того как можно будет запустить видеочат от начала приема

    const val SCENARIO_NON = "non"
    const val SCENARIO_VIDEO = "video"
    const val SCENARIO_INCOMING_WAIT = "wait"
    const val REQUEST_CODE_FOR_UPDATE_APP = 156

    const val SENDER_ID_FCM_PATIENT = "933088839978"

    enum class TelemedicineStatusRecord{
        wait, active, complete
    }

    enum class TelemedicineNotificationType(val fullName: String) {
        MESSAGE("Медицинский помощник.Сотрудник.Телемедицина.Сообщение"), PAY("Медицинский помощник.Сотрудник.Телемедицина.Оплата"),
        START_APPOINTMENT("Медицинский помощник.Сотрудник.Телемедицина.ПриемНачался"), END_APPOINTMENT("Медицинский помощник.Сотрудник.Телемедицина.ПриемОкончился")
    }

    enum class TelemedicineChangeStatusAppointment(val fullName: String) {
        START("Начат прием. Войдите в приложение или нажмите на уведомление "), END("Прием окончен")
    }
}