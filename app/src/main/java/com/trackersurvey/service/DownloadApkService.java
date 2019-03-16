package com.trackersurvey.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.trackersurvey.happynavi.BuildConfig;
import com.trackersurvey.happynavi.MainActivity;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.interfaces.DownloadListener;

import java.io.File;

public class DownloadApkService extends Service {

    private DownloadTask downloadTask;

    private String downloadUrl;

    private String TAG = "dong";

    private Notification mNotification;

    private DownloadListener listener = new DownloadListener() {

        @Override
        public void onProgress(int progress) {
            //            getNotificationManager().notify(1, getNotification("Downloading...", progress));
            Log.i(TAG, "onProgress: " + progress);
            notifyUser(progress);
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            //            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadApkService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            //            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadApkService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadApkService.this, "Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadApkService.this, "Canceled", Toast.LENGTH_SHORT).show();
        }

    };

    public DownloadApkService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public class DownloadBinder extends Binder {

        public void startDownload(String url) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                //                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadApkService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    // 取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadApkService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;

    private Notification getNotification(String title, int progress) {

        if (Build.VERSION.SDK_INT >= 26) {

            if (mNotificationChannel == null) {
                //创建 通知通道  channelid和channelname是必须的（自己命名就好）
                mNotificationChannel = new NotificationChannel("1",
                        "Channel1", NotificationManager.IMPORTANCE_HIGH);
                mNotificationChannel.enableLights(true);//是否在桌面icon右上角展示小红点
                mNotificationChannel.setLightColor(Color.GREEN);//小红点颜色
                mNotificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                getNotificationManager().createNotificationChannel(mNotificationChannel);
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }


    private void notifyUser(int progress) {

        if (Build.VERSION.SDK_INT >= 26) {

            if (mNotificationChannel == null) {
                //创建 通知通道
                mNotificationChannel = new NotificationChannel("H1", "Channel1", NotificationManager.IMPORTANCE_HIGH);
                mNotificationChannel.enableLights(true);//是否在桌面icon右上角展示小红点
                mNotificationChannel.setLightColor(Color.GREEN);//小红点颜色
                mNotificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                mNotificationManager.createNotificationChannel(mNotificationChannel);
            }

            int notificationId = 0x1234;
            Notification.Builder builder = new Notification.Builder(getApplicationContext(), "H1");
            builder.setOnlyAlertOnce(true);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("正在下载新版本，请稍后...")
                    .setAutoCancel(true);
            if (progress > 0 && progress <= 100) {
                builder.setProgress(100, progress, false);
            } else {
                builder.setProgress(0, 0, false);
            }
            Log.i(TAG, "notifyUser: progress : " + progress);
            builder.setContentIntent(progress >= 100 ? this.getContentIntent() :
                    PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));

            Notification notification = builder.build();
            mNotificationManager.notify(notificationId, notification);

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name));
            if (progress > 0 && progress <= 100) {
                builder.setProgress(100, progress, false);
            } else {
                builder.setProgress(0, 0, false);
            }
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            Log.i(TAG, "notifyUser: progress : " + progress);
            builder.setContentIntent(progress >= 100 ? this.getContentIntent() :
                    PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
            mNotification = builder.build();
            mNotificationManager.notify(0, mNotification);
        }
    }

    /**
     * 进入安装
     *
     * @return
     */
    private PendingIntent getContentIntent() {

        mNotificationManager.cancelAll();

        //移除通知栏
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationManager.deleteNotificationChannel("H1");
            Log.i(TAG, "getContentIntent: " + "移除通知栏");
        }

        File saveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/微足迹.apk");
        Intent install = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            Log.i(TAG, "getContentIntent: saveFile : " + saveFile);
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), "com.trackersurvey.happynavi.fileProvider", saveFile);//在AndroidManifest中的android:authorities值
            Log.i(TAG, "getContentIntent: " + apkUri.getPath());
            install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            install.setDataAndType(Uri.fromFile(saveFile), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, install, PendingIntent.FLAG_UPDATE_CURRENT);
        startActivity(install);
        return pendingIntent;
    }
}
