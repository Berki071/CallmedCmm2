package com.medhelp.callmed2.data.network

object LocalEndPoint {
    //const val BASE_URL = "https://oneclick.tmweb.ru/medhelp_main/v1/"
    const val BASE_URL = "http://188.225.25.133/medhelp_main/v1/"


    const val CURRENT_DATE = BASE_URL + "date"
    const val LOGIN = BASE_URL + "login/doctor"
    const val CENTER = BASE_URL + "centres/{id_center}"
    const val SEND_LOG_TO_SERVER = BASE_URL + "LogDataInsert/{" + NetworkManager.ID_USER + "}/{" + NetworkManager.ID_CENTER + "}/{" +
            NetworkManager.ID_BRANCH + "}/{" + NetworkManager.TYPE + "}/sotr/{" + NetworkManager.VERSION_CODE + "}"

}