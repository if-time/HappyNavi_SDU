package com.trackersurvey.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.trackersurvey.happynavi.LoginActivity;
import com.trackersurvey.happynavi.SplashActivity;
import com.trackersurvey.util.SystemUtils;

/** 监听系统广播，复活进程
 *  开机广播
 *  配合华为push服务重启app失效
 */

public class KeepAliveReceiver extends BroadcastReceiver {
    private static final String TAG = "AliveBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"AliveBroadcastReceiver---->接收到的系统广播："+action);
        if(SystemUtils.isAPPALive(context, "com.trackersurvey.happynavi")){
            Log.i(TAG,"AliveBroadcastReceiver---->APP还是活着的");
            return;
        }
        Intent intentAlive = new Intent(context, LoginActivity.class);
        intentAlive.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentAlive);
        Log.i(TAG,"AliveBroadcastReceiver---->复活进程(APP)");
    }
}
