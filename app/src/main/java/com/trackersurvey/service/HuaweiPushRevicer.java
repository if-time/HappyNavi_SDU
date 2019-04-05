package com.trackersurvey.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;
import com.trackersurvey.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用需要创建一个子类继承com.huawei.hms.support.api.push.PushReceiver，
 * 实现onToken，onPushState ，onPushMsg，onEvent，这几个抽象方法，用来接收token返回，push连接状态，透传消息和通知栏点击事件处理。
 * onToken 调用getToken方法后，获取服务端返回的token结果，返回token以及belongId
 * onPushState 调用getPushState方法后，获取push连接状态的查询结果
 * onPushMsg 推送消息下来时会自动回调onPushMsg方法实现应用透传消息处理。本接口必须被实现。 在开发者网站上发送push消息分为通知和透传消息
 * 通知为直接在通知栏收到通知，通过点击可以打开网页，应用 或者富媒体，不会收到onPushMsg消息
 * 透传消息不会展示在通知栏，应用会收到onPushMsg
 * onEvent 该方法会在设置标签、点击打开通知栏消息、点击通知栏上的按钮之后被调用。由业务决定是否调用该函数。
 */
public class HuaweiPushRevicer extends PushReceiver {
    public static final String TAG = "HuaweiPushRevicer";

    public static final String ACTION_UPDATEUI = "action.updateUI";
    public static final String ACTION_TOKEN    = "action.updateToken";

    private static       List<IPushCallback> pushCallbacks = new ArrayList<IPushCallback>();
    private static final Object              CALLBACK_LOCK = new Object();

    public interface IPushCallback {
        void onReceive(Intent intent);
    }

    public static void registerPushCallback(IPushCallback callback) {
        synchronized (CALLBACK_LOCK) {
            pushCallbacks.add(callback);
        }
    }

    public static void unRegisterPushCallback(IPushCallback callback) {
        synchronized (CALLBACK_LOCK) {
            pushCallbacks.remove(callback);
        }
    }

    @Override
    public void onToken(Context context, String tokenIn, Bundle extras) {

        Log.i(TAG, "连接到华为推送服务器，token=" + tokenIn);

        String belongId = extras.getString("belongId");

        Intent intent = new Intent();
        intent.setAction(ACTION_TOKEN);
        intent.putExtra(ACTION_TOKEN, tokenIn);
        callBack(intent);

        intent = new Intent();
        intent.setAction(ACTION_UPDATEUI);
        intent.putExtra("log", "belongId is:" + belongId + " Token is:" + tokenIn);
        callBack(intent);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            //CP可以自己解析消息内容，然后做相应的处理 | CP can parse message content on its own, and then do the appropriate processing
            String content = new String(msg, "UTF-8");
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATEUI);
            intent.putExtra("log", "Receive a push pass message with the message:" + content);
            callBack(intent);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATEUI);
            intent.putExtra("log", "Receive push pass message, exception:" + e.getMessage());
            callBack(intent);
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATEUI);

        int notifyId = 0;
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
        }

        String message = extras.getString(BOUND_KEY.pushMsgKey);
        intent.putExtra("log", "Received event,notifyId:" + notifyId + " msg:" + message);
        callBack(intent);
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {

        Log.i(TAG, "是否连接到华为推送服务器：" + (pushState ? "connected" : "disconnected"));

        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATEUI);
        intent.putExtra("log", "The Push connection status is:" + pushState);
        callBack(intent);
    }

    private static void callBack(Intent intent) {
        synchronized (CALLBACK_LOCK) {
            for (IPushCallback callback : pushCallbacks) {
                if (callback != null) {
                    callback.onReceive(intent);
                }
            }
        }
    }
}
