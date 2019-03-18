package com.trackersurvey.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.trackersurvey.event.NetworkChangeEvent;
import com.trackersurvey.util.NetUtils;

import org.greenrobot.eventbus.EventBus;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkConnectChanged";
    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断当前的网络连接状态是否可用
        boolean isConnected = NetUtils.isConnected(context);
        Log.d(TAG, "onReceive: 当前网络 " + isConnected);
        EventBus.getDefault().post(new NetworkChangeEvent(isConnected));
    }
}