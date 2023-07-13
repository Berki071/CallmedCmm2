package com.medhelp.callmed2.data.bg.service

import android.app.NotificationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.bg.Notification.SimpleNotificationForFCM


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //если открыта страница то она обновиться без уведомлений

        val handler = Handler(Looper.getMainLooper())
        handler.post {

            //Log.wtf("NotyLogMy","1 нотификация получена")
            if (remoteMessage.notification != null) {
                if (remoteMessage.data.size > 0) {
                    // т.к. нет возможности сделать изменить class на object FirebaseMessagingService
                    PadForMyFirebaseMessagingService.onMessageReceived(applicationContext ,remoteMessage)

//                    else{
//                        val type_message = remoteMessage.data["type_message"]
//                        val id_kl = remoteMessage.data["id_kl"]
//                        val id_filial = remoteMessage.data["id_filial"]
//
//                        if (type_message != null && id_kl != null && id_filial != null) {
//                            val simpleNotificationForFCM = SimpleNotificationForFCM(applicationContext, getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//                            simpleNotificationForFCM.showData(
//                                remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!, type_message, id_kl, id_filial
//                            )
//                        }
//                    }
                } else if (remoteMessage.getNotification() != null) {
                    //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    val simpleNotificationForFCM = SimpleNotificationForFCM(applicationContext, getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                    simpleNotificationForFCM.showDataTelemedicine(
                        remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }


    private var countFcmSend=0
    private fun sendRegistrationToServer(token: String) {
//        val prefManager = PreferencesManager(applicationContext)
//        val networkManager = NetworkManager()
//        val list = prefManager.usersLogin
//        if (list == null || list.size == 0) {
//            return
//        }
//
//        val mainScope = MainScope()
//
//        for (i in list) {
//            mainScope.launch {
//                kotlin.runCatching {
//                    networkManager.sendFcmId(i.idUser.toString(), i.idBranch.toString(), token, prefManager.currentUserInfo!!.apiKey!!, prefManager.centerInfo!!.db_name!!,prefManager.currentUserInfo!!.idUser.toString(),prefManager.currentUserInfo!!.idBranch.toString())
//                }
//                    .onSuccess {
//                        mainScope.cancel()
//                    }.onFailure {
//                        Timber.tag("my").e(getMessageForError(it, "MyFirebaseMessagingService\$sendFcmToken"))
//                        mainScope.cancel()
//                    }
//            }
//        }
    }

    interface MyFirebaseMessagingServiceListener{
        fun updateRecordInfo()
    }
}