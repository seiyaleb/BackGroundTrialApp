package com.backgroundapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {

    private Timer timer;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        //通知チャネルを作成
        String id = "NOTIFICATION_ID";
        String name = getString(R.string.notification_channel_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id,name,importance);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        //10秒ごとに定期実行
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //アラームを通知する
                notify_alarm();
            }
        },0,10000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //アラームを通知する
    public void notify_alarm() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainService.this,"NOTIFICATION_ID");
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setContentTitle(getString(R.string.notification_title));
        builder.setContentText(getString(R.string.notification_text));
        Intent intent = new Intent(MainService.this,MainActivity.class);
        intent.putExtra("fromNotification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainService.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,notification);
    }
}