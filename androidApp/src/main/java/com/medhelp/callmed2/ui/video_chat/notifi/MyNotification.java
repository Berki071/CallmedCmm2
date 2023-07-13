package com.medhelp.callmed2.ui.video_chat.notifi;//package com.medhelp.callmed2.ui.video_chat.notifi;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import androidx.core.app.NotificationCompat;
//
//import com.medhelp.callmed2.R;
//import com.medhelp.callmed2.bg.Constants;
//import com.medhelp.callmed2.data.model.VisitItem2;
//
//public class MyNotification {
//
//    private Context context;
//    private String companionid;
//    private NotificationManager notificationManager;
//    private String callerName;
//
//    public static final String CHANNEL_ID="STREAM_DEFAULT";
//    public static final int ID_NOTI_FOR_VIDEO_CALL=1;
//    private String GROUP_KEY_Reminder= "com.medhelp.medhelp.videochaaa2";
//
//    public MyNotification(Context context, String companionid, String callerName)
//    {
//        this.context=context;
//        this.companionid=companionid;
//        this.callerName=callerName;
//        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        showNotificationWait();
//    }
//
//    public MyNotification(Context context, VisitItem2 data)
//    {
//        this.context=context;
//        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        showNotificationInvite(data);
//    }
//    @SuppressWarnings("LaunchActivityFromNotification")
//    private void showNotificationWait() {
//
//        Intent intBtnOpen = new Intent(context, NotificationButtonListener.class);
//        intBtnOpen.putExtra("companionid",companionid);
//        intBtnOpen.setAction(NotificationButtonListener.ACTION_GO_TO_VIDEO);
//        PendingIntent pendingMyOpen = PendingIntent.getBroadcast(context, 1, intBtnOpen, PendingIntent.FLAG_IMMUTABLE);
//
//        Intent intBtnClose=new Intent(context,NotificationButtonListener.class);
//        intBtnClose.setAction(NotificationButtonListener.ACTION_CLOSE);
//        intBtnClose.putExtra("companionid",companionid);
//        PendingIntent pendingMyClose = PendingIntent.getBroadcast(context, 1, intBtnClose, PendingIntent.FLAG_IMMUTABLE);
//
//        //для поддержки версии 8 и выше
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "channel_name";
//            String description = "channel_description";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
//                .setSmallIcon(Constants.ICON_FOR_NOTIFICATION)
//                .setContentTitle("Входящий звонок")
//                .setContentText(callerName)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                //.setStyle(new NotificationCompat.BigTextStyle().bigText("Необходимо перейти на страницу видеозвонка и ожидать подключение врача"))
//                .addAction(R.drawable.ic_done_green_500_36dp,"Ответить",pendingMyOpen)
//                .addAction(R.drawable.ic_close_red_600_18dp,"Отклонить",pendingMyClose)
//                .setOngoing(true)   //нельзя смахнуть
//                .setGroup(GROUP_KEY_Reminder);
//
//        assert notificationManager != null;
//
//        notificationManager.notify(ID_NOTI_FOR_VIDEO_CALL, mBuilder.build());
//    }
//
//    @SuppressWarnings("LaunchActivityFromNotification")
//    private void showNotificationInvite(VisitItem2 data)
//    {
//        Intent intBtnOpen = new Intent(context, NotificationButtonListener.class);
//        intBtnOpen.putExtra(VisitItem2.class.getCanonicalName(),data);
//        intBtnOpen.setAction(NotificationButtonListener.ACTION_GO_TO_VIDEO_WAIT);
//        PendingIntent pendingMyOpen = PendingIntent.getBroadcast(context, 1, intBtnOpen, PendingIntent.FLAG_IMMUTABLE);
//
//        Intent intBtnClose=new Intent(context,NotificationButtonListener.class);
//        intBtnClose.setAction(NotificationButtonListener.ACTION_CLOSE_NOTY_ONLY);
//        PendingIntent pendingMyClose = PendingIntent.getBroadcast(context, 1, intBtnClose, PendingIntent.FLAG_IMMUTABLE);
//
//
//        //для поддержки версии 8 и выше
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "channel_name";
//            String description = "channel_description";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
//                .setSmallIcon(Constants.ICON_FOR_NOTIFICATION)
//                .setContentTitle("Входящий звонок")
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("Необходимо перейти на страницу видеозвонка и ожидать подключение врача"))
//                .addAction(R.drawable.ic_done_green_500_36dp,"Перейти",pendingMyOpen)
//                .addAction(R.drawable.ic_close_red_600_18dp,"Отмена",pendingMyClose)
//                .setOngoing(true)   //нельзя смахнуть
//                .setGroup(GROUP_KEY_Reminder);
//
//        assert notificationManager != null;
//
//        notificationManager.notify(ID_NOTI_FOR_VIDEO_CALL, mBuilder.build());
//    }
//
//
//}
