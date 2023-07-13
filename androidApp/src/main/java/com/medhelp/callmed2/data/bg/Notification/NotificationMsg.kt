package com.medhelp.callmed2.data.bg.Notification

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.medhelp.callmed2.data.pref.PreferencesManager
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class NotificationMsg(
    private val context: Context,
    private val notificationManager: NotificationManager?
) {
    private val preferencesManager: PreferencesManager
    private val CHANNEL_ID = "STREAM_DEFAULT"
    private var notificationId = 1
        private get() {
            field++
            if (field > 1000) field = 1
            return field
        }

    // private int icon;
    var GROUP_KEY_WORK_EMAIL = "com.medhelp.medhelp.Medelp.Msg"

    init {
        preferencesManager = PreferencesManager(context)

        // icon= R.mipmap.sotr_icon;
    }

//    fun showNotificationMessage(list: List<MessageFromServer>, currentRoom: Long) {
//        for (msg in list) {
//            if (msg.idRoom == currentRoom) continue
//            val cd = CompositeDisposable()
//            cd.add(realmManager
//                .getInfoAboutOneDoc(msg.idRoom)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ responce: InfoAboutKL ->
//                    showMsg(responce, msg)
//                    cd.dispose()
//                }
//                ) { throwable: Throwable? ->
//                    Timber.e(
//                        getMessageForError(
//                            throwable,
//                            "ShowNotification/showNotificationMessage "
//                        )
//                    )
//                    cd.dispose()
//                }
//            )
//        }
//    }

//    private fun showMsg(info: InfoAboutKL, msg: MessageFromServer) {
//
//        //для поддержки версии 8 и выше
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create the NotificationChannel, but only on API 26+ because
//            // the NotificationChannel class is new and not in the support library
//            val name: CharSequence = "channel_name"
//            val description = "channel_description"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance)
//            channel.description = description
//            assert(notificationManager != null)
//            notificationManager!!.createNotificationChannel(channel)
//        }
//        val center = preferencesManager.centerInfo!!.title
//        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val intent = Intent(context, RoomActivity::class.java)
//        intent.putExtra(InfoAboutKL::class.java.canonicalName, info)
//        val pendingIntent3 =
//            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        val msgNew: String
//        msgNew = if (msg.type == MainUtils.TEXT) {
//            msg.msg
//        } else if (msg.type == MainUtils.IMAGE) {
//            "Изображение"
//        } else {
//            "Файл"
//        }
//        val mBuilder = NotificationCompat.Builder(
//            context, CHANNEL_ID
//        )
//            .setSmallIcon(Constants.ICON_FOR_NOTIFICATION)
//            .setContentTitle(center)
//            .setContentText(Html.fromHtml("<b>" + info.name + "</b>: " + msgNew))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText(Html.fromHtml("<b>" + info.name + "</b>: " + msgNew))
//            )
//            .setContentIntent(pendingIntent3)
//            .setAutoCancel(true) //удаляет нотификацию после нажатия на нее
//            .setGroup(GROUP_KEY_WORK_EMAIL)
//            .setSound(sound)
//        assert(notificationManager != null)
//        val notiGroup = NotificationCompat.Builder(
//            context, CHANNEL_ID
//        )
//            .setSmallIcon(Constants.ICON_FOR_NOTIFICATION)
//            .setGroup(GROUP_KEY_WORK_EMAIL)
//            .setGroupSummary(true)
//            .build()
//        notificationManager!!.notify(notificationId, mBuilder.build())
//        notificationManager.notify(NOTI_GROUP_CHAT, notiGroup)
//    }

    private fun getBitmap(path: String): Bitmap? {
        var bmp: Bitmap? = null
        try {
            val url = URL(path + "&token=" + preferencesManager.accessToken)
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmp
    }

    companion object {
        const val BTN_CONFIRM = "btnConfirm"
        const val BTN_REFUSE = "btnRefuse"
        const val KEY_CLICK_BTN = "KEY_CLICK_BTN"
        const val KEY_NOTIFICATION = "KEY_NOTIFICATION"
        const val KEY_RECORD = "KEY_RECORD"
        const val KEY_ID_NOTIFICATION = "KEY_ID_NOTIFICATION"
        const val ID_RECORD = "ID_RECORD"
        const val ID_BRANCH = "ID_BRANCH"
        const val ID_USER = "ID_USER"
        const val ID_ROOM = "ID_ROOM"
        const val NOTI_GROUP_CHAT = -100
    }
}