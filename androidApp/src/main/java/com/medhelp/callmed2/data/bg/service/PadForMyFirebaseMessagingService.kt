package com.medhelp.callmed2.data.bg.service

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.bg.Notification.SimpleNotificationForFCM

object PadForMyFirebaseMessagingService {
    var showIdRoom: String? = null
    var listener: MyFirebaseMessagingService.MyFirebaseMessagingServiceListener? = null

    fun onMessageReceived(appContext: Context, remoteMessage: RemoteMessage){
        //проверина в сервисе на не нулевую инфу

        val typeMsg: String? = remoteMessage.data["type_message"]
        typeMsg?.let{

            val idRoom = remoteMessage.data["idRoom"]
            val idTm = remoteMessage.data["idTm"]

            if (idRoom != null && idTm != null) {

                //Log.wtf("NotyLogMy","2 нотификация проверки на ноль данных пройдены")

                if(idRoom!=showIdRoom && typeMsg== Constants.TelemedicineNotificationType.MESSAGE.fullName) {
                    //Log.wtf("NotyLogMy","3 нотификация мессадж "+typeMsg)
                    val simpleNotificationForFCM = SimpleNotificationForFCM(appContext, appContext.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE)
                            as NotificationManager)

                    simpleNotificationForFCM.showDataTelemedicine(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!, idRoom, idTm,typeMsg)
                }

                if(typeMsg == Constants.TelemedicineNotificationType.PAY.fullName){
                    //Log.wtf("NotyLogMy","4 нотификация оплата "+typeMsg)
                    val simpleNotificationForFCM = SimpleNotificationForFCM(appContext, appContext.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE
                    ) as NotificationManager)

                    simpleNotificationForFCM.showDataTelemedicine(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!, idRoom, idTm,typeMsg)
                    listener?.updateRecordInfo()
                }
            }
        }
    }
}