package com.trackersurvey.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.trackersurvey.happynavi.OnePxActivity;

public class OnePxService extends Service {
    public OnePxService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    BroadcastReceiver myBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("trans", "screen off");
                Intent activity = new Intent(context, OnePxActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activity);
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("trans", "screen on");
                Intent broadcast = new Intent("FinishActivity");
                context.sendBroadcast(broadcast);
            }
        }
    };

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // 动态启动广播接收器
        IntentFilter screenStatus = new IntentFilter();
        screenStatus.addAction(Intent.ACTION_SCREEN_OFF);
        screenStatus.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(myBroadcast, screenStatus);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcast);
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), OnePxService.class);
        startService(intent);
    }
}
