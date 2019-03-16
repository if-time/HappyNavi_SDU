package com.trackersurvey.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.toolsfinal.StringUtils;

import com.trackersurvey.happynavi.BuildConfig;
import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.Common;

public class AppUpdateService extends Service {

    private Notification mNotification;
    /**
     * 保存的路径
     */
    private String       mSavePath;
    /**
     * 下载的Url
     */
    private String       mDownloadUrl;

    private int mOldProgress = 0;
    private int preProgress  = 0;

    private String TAG = "AppUpdateService";

    public AppUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mSavePath = intent.getStringExtra("save_path");
            mDownloadUrl = intent.getStringExtra("download_url");

            Log.e(TAG, mSavePath + "," + mDownloadUrl);
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    File saveFile = new File(mSavePath);
                    HttpRequest.download(mDownloadUrl, saveFile, new FileDownloadCallback() {

                        //开始下载
                        @Override
                        public void onStart() {
                            super.onStart();
                        }

                        //下载进度
                        @Override
                        public void onProgress(int progress, long networkSpeed) {
                            super.onProgress(progress, networkSpeed);
                            /**
                             * 这里的条件是为了避免Notification的频繁notify,因为Notification的频繁notify会导致
                             * 应用界面卡顿或卡死
                             */
                            //                    if (mOldProgress == 0 || (mOldProgress > 0 && (progress - mOldProgress > 10) || progress == 100)) {
                            //                        notifyUser(progress);
                            //                    }
                            //                            Log.i(TAG, "notifyUser: progress : " + progress);
                            int currProgress = (int) (100 * progress);
                            //防止界面卡死
                            if (preProgress < currProgress) {

                                if (progress % 5 == 0) {
                                    notifyUser(progress);
                                }

                            }
                            preProgress = currProgress;

                        }

                        //下载失败
                        @Override
                        public void onFailure() {
                            super.onFailure();
                            Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        //下载完成（下载成功）
                        @Override
                        public void onDone() {
                            super.onDone();
                            Toast.makeText(getBaseContext(), "下载成功", Toast.LENGTH_SHORT).show();
                            Common.isUpdationg = false;
                            //关闭Service
                            stopSelf();

                        }
                    });
                    stopSelf();
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;

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

        File saveFile = new File(mSavePath);
        Intent install = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            Log.i(TAG, "getContentIntent: saveFile : " + saveFile);
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", saveFile);//在AndroidManifest中的android:authorities值

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

    /**
     * @param context
     * @param savePath    保存到本地的路径
     * @param downloadUrl 下载的Url
     */
    public static void start(Context context, String savePath, String downloadUrl) {
        Intent intent = new Intent(context, AppUpdateService.class);
        intent.putExtra("save_path", savePath);
        intent.putExtra("download_url", downloadUrl);
        context.startService(intent);
        Log.i("dongsiyuastartServicen", "start: ");
    }
}
