package com.medhelp.callmed2.data.network

object CenterEndPoint {
    //const val BASE_URL = "https://oneclick.tmweb.ru/medhelp_client/v1/"
    const val BASE_URL = "http://188.225.25.133/medhelp_client/v1/"

    const val GET_ALL_IP_LIST = BASE_URL + "IPcallcentr" //-
    const val GET_CLIENT_BD_LIST =
        BASE_URL + "BDcallcentr/{" + NetworkManager.DATE + "}/{" + NetworkManager.IP_ADRESS + "}" //-
    const val SET_CALL_STATUS =
        BASE_URL + "updateBDcallcentr/{" + NetworkManager.ID_CALL + "}/{" + NetworkManager.STATUS + "}" //-
    const val SEND_PHONE_NUMBER =
        BASE_URL + "insertNewCallcentr/{" + NetworkManager.DATE + "}/{" + NetworkManager.PHONE + "}/{" + NetworkManager.IP_ADRESS + "}"
//    const val ALL_ROOM = BASE_URL + "ChatAllRoomsSOTR/{" + NetworkManager.ID_USER + "}" //-
//    const val EXTERNAL_MSG = BASE_URL + "ChatNewTextSOTR/{" + NetworkManager.ID_USER + "}" //-
//    const val SEND_MSG =
//        BASE_URL + "ChatInsertNewTextDoc/{" + NetworkManager.ID_ROOM + "}/{" + NetworkManager.MESSAGE + "}/sotr/{" + NetworkManager.TYPE + "}" //-
    const val BRANCH_DOC = BASE_URL + "FilialByIdSotr/{" + NetworkManager.ID_USER + "}" //-
    const val TIME_TABLE_DOC =
        BASE_URL + "scheduleFull/doctor/{" + NetworkManager.ID_USER + "}/{" + NetworkManager.DATE + "}/{" + NetworkManager.ID_BRANCH + "}" //-
    const val SCHEDULE_TOMORROW =
        BASE_URL + "scheduleTomorrow/{" + NetworkManager.ID_USER + "}" //-
    const val DOCTOR_BY_ID = BASE_URL + "sotr_info_doc/{id_doctor}" //-
    const val CATEGORY = BASE_URL + "specialtyDOC"
    const val PRICE = BASE_URL + "servicesDOC"
    const val GER_FILIAL_BY_ID_SERVICES =
        BASE_URL + "FilialByIdYslDOC/{" + NetworkManager.ID_SERVICE + "}"
    const val SCHEDULE_SERVICE =
        BASE_URL + "scheduleDOC/service/{" + NetworkManager.ID_SERVICE + "}/{" + NetworkManager.DATE + "}/{" + NetworkManager.ADM_TIME + "}/{" + NetworkManager.ID_BRANCH + "}"
    const val FIND_CLIENT_BY_FAM =
        BASE_URL + "FindKlByFam/{" + NetworkManager.SURNAME + "}/{" + NetworkManager.ID_BRANCH + "}"
    const val FIND_CLIENT_BY_HONE =
        BASE_URL + "FindKlBySot/{" + NetworkManager.PHONE + "}/{" + NetworkManager.ID_BRANCH + "}"
    const val USER_IN_NEW_BRANCH =
        BASE_URL + "SmenaLikeFilialDOC/{" + NetworkManager.ID_USER + "}/{" + NetworkManager.ID_BRANCH + "}/{" + NetworkManager.ID_BRANCH_NEW + "}"
    const val RECORD_TO_THE_DOCTOR =
        BASE_URL + "recordDOC/{" + NetworkManager.ID_DOCTOR + "}/{" + NetworkManager.DATE + "}/{" + NetworkManager.TIME + "}/{" + NetworkManager.ID_USER + "}/{" + NetworkManager.ID_SPEC + "}" +
                "/{" + NetworkManager.ID_SERVICE + "}/{" + NetworkManager.DURATION + "}/{" + NetworkManager.ID_BRANCH + "}"
    const val SEND_NEW_USER =
        (BASE_URL + "CreateNewClient/{" + NetworkManager.ID_BRANCH + "}/{" + NetworkManager.SURNAME + "}/{" + NetworkManager.USERNAME + "}/{" + NetworkManager.PATRONYMIC
                + "}/{" + NetworkManager.PHONE + "}")
    const val SCHEDULE_DOC_FOR_VIDEO_CALL =
        BASE_URL + "scheduleVideoCall/{" + NetworkManager.ID_DOC + "}/{" + NetworkManager.DATE + "}"
    const val FIND_NEW_SYNC = BASE_URL + "find_new_sync_session/{" + NetworkManager.ID_DOC + "}"
    const val SEND_IMG_FOR_SCANNER =
        BASE_URL + "insert_data_in_sync_session/{" + NetworkManager.ID_SYNC + "}"
    const val CHANGE_STATUS_SYNC =
        BASE_URL + "change_status_sync_session/{" + NetworkManager.ID_SYNC + "}/{" + NetworkManager.STATUS + "}"
//    const val UPDATE_FCM_DOCTOR =
//        BASE_URL + "UpdateFCMdoctor/{" + NetworkManager.ID_DOC + "}/{" + NetworkManager.FB_TOKEN + "}"
    const val GET_ALL_RASP_SOTR =
        BASE_URL + "FindAllRaspSotr/{" + NetworkManager.ID_DOC + "}/{" + NetworkManager.DATE + "}"
    const val LOAD_START_MKB =
        BASE_URL + "load_stat_mkb/{" + NetworkManager.DATE_FROM + "}/{" + NetworkManager.DATE_TO + "}/{" + NetworkManager.ID_DOC + "}"
}