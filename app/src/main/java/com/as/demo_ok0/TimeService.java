package com.as.demo_ok0;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.as.demo_ok0.bean.EventTimeBean;
import com.as.demo_ok0.bean.TickBean;

import org.greenrobot.eventbus.EventBus;

/**
 * -----------------------------
 * Created by zqf on 2018/11/14.
 * ---------------------------
 */
public class TimeService extends Service {

    public static final String ACTION_START = "com.play.ACTION_START";
    public static final String ACTION_STOP = "com.play.ACTION_STOP";
    private CountDownTimer countDownTimer;
    public static boolean isstarting = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("xxx", "xxx", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "xxx")
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();

            startForeground(101, notification);

        } else {
            startForeground(1, new Notification());
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_START)) {
            isstarting = true;

            int time = intent.getIntExtra("time", 0);

            //todo 每次的time才会发来start的Action 所以这里每次让他重新new
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = null;

            countDownTimer = new CountDownTimer(time * 60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    EventBus.getDefault().postSticky(new EventTimeBean(0, new TickBean(millisUntilFinished)));
                }

                @Override
                public void onFinish() {
                    EventBus.getDefault().postSticky(new EventTimeBean(1));
                }

            }.start();

        } else if (TextUtils.equals(action, ACTION_STOP)) {

            isstarting = false;
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = null;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
