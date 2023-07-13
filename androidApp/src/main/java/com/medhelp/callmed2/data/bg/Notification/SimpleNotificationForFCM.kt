package com.medhelp.callmed2.data.bg.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.medhelp.callmed2.R
import com.medhelp.callmed2.ui.splash.SplashActivity

class SimpleNotificationForFCM(private val context: Context, private val notificationManager: NotificationManager?) {
    var idNoti = 111
    private var sound: Uri? = null
    var pendingIntent: PendingIntent? = null
    private val GROUP_KEY_CHAT = "com.medhelp.medhelp.Medhelp.Msg.CHAT"

    fun showDataTelemedicine(title: String, msd: String, idRoom: String, idTm: String, type: String) {  // телемедицина
        Log.wtf("NotyLogMy","0,3 нотификация " + type)

        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(context, SplashActivity::class.java)
        intent.putExtra("idRoom", idRoom)
        intent.putExtra("idTm", idTm)
        intent.putExtra("type_message", type)
        val idNot = (idRoom+idTm).toInt()
        pendingIntent = PendingIntent.getActivity(context, idNot, intent, PendingIntent.FLAG_IMMUTABLE)

        val type2 = intent?.getStringExtra("type_message") ?: "null"
        Log.wtf("NotyLogMy","0,03 нотификация " + type2)

        showNoti(title, msd,idNot)
    }
//    fun showDataTelemedicine(title: String, msd: String, type_message: String?, id_kl: String?, id_filial: String?) {
//        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val intent = Intent(context, SplashActivity::class.java)
//        intent.putExtra("type_message", type_message)
//        intent.putExtra("id_kl", id_kl)
//        intent.putExtra("id_filial", id_filial)
//        pendingIntent =
//            PendingIntent.getActivity(context, idNoti, intent, PendingIntent.FLAG_IMMUTABLE)
//        showNoti(title, msd, idNoti)
//    }

    fun showDataTelemedicine(title: String, msd: String) {
        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(context, SplashActivity::class.java)
        pendingIntent =
            PendingIntent.getActivity(context, idNoti, intent, PendingIntent.FLAG_IMMUTABLE)
        showNoti(title, msd, idNoti)
    }

    fun showDataRasp(title: String, msd: String){
        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(context, SplashActivity::class.java)
        pendingIntent = PendingIntent.getActivity(context, idNoti, intent, PendingIntent.FLAG_IMMUTABLE)
        showNoti(title, msd, idNoti)
    }

    private fun showNoti(title: String, msg: String, idNoty: Int) {
        //для поддержки версии 8 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name: CharSequence = "channel_name"
            val description = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            assert(notificationManager != null)
            notificationManager!!.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(
            context, CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.sotr_icon)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml("<b>"+info.getName()+"</b>: "+ type)))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) //удаляет нотификацию после нажатия на нее
            .setGroup(GROUP_KEY_CHAT)
            .setSound(sound)
            .build()
        assert(notificationManager != null)
        notificationManager!!.notify(idNoty, mBuilder)
    }

    companion object {
        const val CHANNEL_ID = "STREAM_DEFAULT"
    }
}