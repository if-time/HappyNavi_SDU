package com.trackersurvey.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.trackersurvey.happynavi.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class NotificationUtils {
    private static NotificationManager manager;
    private static NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) MyApplication.getInstance().getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Notification.Builder getNotificationBuilder(String title, String content, String channelId) {
        //大于8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //id随便指定
            NotificationChannel channel = new NotificationChannel(channelId, MyApplication.getInstance().getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//可否绕过，请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(1);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪光是的灯光颜色
            channel.canShowBadge();//桌面laucher消息角标
            channel.enableVibration(true);//是否允许震动
            channel.setSound(null, null);
            //channel.getAudioAttributes();//获取系统通知响铃声音配置
            channel.getGroup();//获取通知渠道组
            channel.setBypassDnd(true);//设置可以绕过，请勿打扰模式
            channel.setVibrationPattern(new long[]{100, 100, 200});//震动的模式，震3次，第一次100，第二次100，第三次200毫秒
            channel.shouldShowLights();//是否会闪光
            //通知管理者创建的渠道
            getManager().createNotificationChannel(channel);

        }
        return new Notification.Builder(MyApplication.getInstance()).setAutoCancel(true).setChannelId(channelId)
                .setContentTitle(title)
                .setContentText(content).setSmallIcon(R.mipmap.ic_launcher);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showNotification(String title, String content, int manageId, String channelId, int progress, int maxProgress) {
        final Notification.Builder builder = getNotificationBuilder(title,content,channelId);
       /* Intent intent = new Intent(this, SecondeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);*/
        builder.setOnlyAlertOnce(true);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setProgress(maxProgress, progress, false);
        builder.setWhen(System.currentTimeMillis());
        getManager().notify(manageId, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void cancleNotification(int manageId) {
        getManager().cancel(manageId);
    }
}

