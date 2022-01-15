package com.backgroundapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {


    public NotificationWorker(
            @NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    //バックグラウンド処理
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {

        //アラームを通知する
        notify_alarm();

        return Result.success();
    }

    //アラームを通知する
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notify_alarm() {

        //通知チャネルを作成
        String id = "NOTIFICATION_ID";
        String name = getApplicationContext().getString(R.string.notification_channel_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id,name,importance);
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        //通知の送信
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"NOTIFICATION_ID");
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setContentTitle(getApplicationContext().getString(R.string.notification_title));
        builder.setContentText(getApplicationContext().getString(R.string.notification_text));
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("fromNotification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,notification);
    }
}
