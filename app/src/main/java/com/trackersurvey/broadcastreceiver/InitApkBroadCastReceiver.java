package com.trackersurvey.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class InitApkBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        File saveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/微足迹.apk");
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            saveFile.delete();
            Toast.makeText(context, "监听到系统广播添加", Toast.LENGTH_LONG).show();
            Log.i("ADDED", "onReceive: ");
        }

        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            saveFile.delete();
            Toast.makeText(context, "监听到系统广播移除", Toast.LENGTH_LONG).show();
            Log.i("REMOVED", "onReceive: ");
        }

        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            saveFile.delete();
            Toast.makeText(context, "监听到系统广播替换", Toast.LENGTH_LONG).show();
            Log.i("REPLACED", "onReceive: ");
        }
    }
}
