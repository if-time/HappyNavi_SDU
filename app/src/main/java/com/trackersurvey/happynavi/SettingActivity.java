package com.trackersurvey.happynavi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.bean.FileInfo;
import com.trackersurvey.broadcastreceiver.InitApkBroadCastReceiver;
import com.trackersurvey.http.DownloadUpdateApp;
import com.trackersurvey.http.LogoutRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.httpconnection.PostCheckVersion;
import com.trackersurvey.service.AppUpdateService;
import com.trackersurvey.service.DownloadApkService;
import com.trackersurvey.service.DownloadService;
import com.trackersurvey.service.LocationService;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.CustomDialog;
import com.trackersurvey.util.DataCleanManager;
import com.trackersurvey.util.ToastUtil;
import com.trackersurvey.util.UrlHeader;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView          titleTv;
    private TextView          titleRightTv;
    private TextView          cacheSizeTv;
    private SharedPreferences sp;

    private int               checkedItem = 0;
    private SharedPreferences languageSelected;     // 默认 0 为 zh ； 1 为 en

    private String versionCode = null;

    private ProgressDialog proDialog = null;

    private Intent updateService = null;

    private Context mContext;

    private static final int REQUEST_CODE_WRITE_STORAGE = 1002;
    private static final int REQUEST_CODE_UNKNOWN_APP = 2001;

    private String mSavePath = null;
    private String mDownloadUrl = null;

    private             String                            fileName             = null;
    private             String                            token                = null;

    private DownloadApkService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadBinder = (DownloadApkService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    // 监听APK安装
    private IntentFilter             intentFilter;
    private InitApkBroadCastReceiver initApkBroadCastReceiver;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(initApkBroadCastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        languageSelected = getSharedPreferences("languageSet", MODE_PRIVATE);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        titleTv = (TextView) findViewById(R.id.title_text);
        titleTv.setText(getResources().getString(R.string.item_settings));
        titleRightTv = (TextView) findViewById(R.id.title_right_text);
        titleRightTv.setVisibility(View.GONE);
        cacheSizeTv = (TextView) findViewById(R.id.cache_size);
        try {
            cacheSizeTv.setText(DataCleanManager.getCacheSize(new File(Common.APPLICATION_DIR)));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LinearLayout checkupdateLayout = (LinearLayout) findViewById(R.id.ll_checkupdate);
        LinearLayout parameterLayout = (LinearLayout) findViewById(R.id.parameter_setting_layout);
        LinearLayout languageLayout = (LinearLayout) findViewById(R.id.select_language_layout);
        LinearLayout clearCacheLayout = (LinearLayout) findViewById(R.id.clear_cache_layout);
        LinearLayout backgroundRunLayout = (LinearLayout) findViewById(R.id.background_run_layout);
        Button logoutBtn = (Button) findViewById(R.id.logout_btn);
        checkupdateLayout.setOnClickListener(this);
        parameterLayout.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        backgroundRunLayout.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        versionCode = Common.getAppVersionCode(getApplicationContext());

        checkedItem = Integer.parseInt(languageSelected.getString("language", "0"));

        if (ContextCompat.checkSelfPermission(SettingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initData();

        Intent intent = new Intent(this, DownloadApkService.class);
        startService(intent); // 启动服务
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务

        if (proDialog == null){
            proDialog = new ProgressDialog(this);
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        initApkBroadCastReceiver = new InitApkBroadCastReceiver();
        registerReceiver(initApkBroadCastReceiver, intentFilter);
    }

    private void initData() {
        mContext = this;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_checkupdate:

                Common.showDialog(proDialog, getResources().getString(R.string.tip), getResources().getString(R.string.tips_updatedlgmsg));

                DownloadUpdateApp downloadUpdateApp = new DownloadUpdateApp(sp.getString("token", ""), versionCode);
                downloadUpdateApp.requestHttpData(new ResponseData() {
                    @Override
                    public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                        if (isSuccess) {
                            final FileInfo fileInfo = (FileInfo) responseObject;

                            Common.fileInfo = new FileInfo(fileInfo.getVersionid(), fileInfo.getVersioncode(),
                                    fileInfo.getVersionname(), fileInfo.getDownloadurl(), fileInfo.getVersiondesc(), 0 ,0);
                            token = sp.getString("token", "");
                            fileName = fileInfo.getDownloadurl().substring(9);
                            mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "微足迹.apk";
                            mDownloadUrl = UrlHeader.BASE_URL_NEW + fileInfo.getDownloadurl() + "?token=" + token;

                            ToastUtil.show(getApplicationContext(), getResources().getString(R.string.tips_gotodownnewapk));

                            Log.i("downloadUpdateApp", "onResponseData: " + fileInfo.toString());
                            if (code.equals("0")) {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       CustomDialog.Builder builder = new CustomDialog.Builder(SettingActivity.this);
                                       builder.setTitle(getResources().getString(R.string.tips_updatedlg_tle));
                                       builder.setMessage(getResources().getString(R.string.tips_updatedlg_msg1) + "\n"
                                               + getResources().getString(R.string.tips_updatedlg_msg2) + fileInfo.getVersioncode() + "\n"
                                               + getResources().getString(R.string.tips_updatedlg_msg5) + fileInfo.getVersiondesc() + "\n"
                                               + getResources().getString(R.string.tips_updatedlg_msg6));
                                       builder.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {

                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                               proDialog.dismiss();
                                           }
                                       });
                                       builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                               proDialog.dismiss();
                                               // 通知Service开始下载
                                               downloadAPK();
//                                               downloadBinder.startDownload(mDownloadUrl);

                                               Common.isUpdationg = true;
                                               ToastUtil.show(getApplicationContext(), getResources().getString(R.string.tips_gotodownnewapk));

                                           }
                                       });
                                       builder.create().show();
                                   }
                               });
                                Log.i("downloadUpdateApp1", "onResponseData: " + fileInfo.toString());
                            }
                            if (code.equals("311")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Common.dismissDialog(proDialog);
                                        ToastUtil.show(SettingActivity.this, getResources().getString(R.string.tips_update_alreadynew));
                                    }
                                });
                            }
                        }
                    }
                });
                break;
            case R.id.parameter_setting_layout:
                Intent intent = new Intent(this, SetParameterActivity.class);
                startActivity(intent);
                break;
            case R.id.select_language_layout:
                final String[] items = {getResources().getString(R.string.language_chinese),
                        getResources().getString(R.string.language_english)};
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(getResources().getString(R.string.language_title));
                builder2.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0: // 中文
                                dialog.dismiss();
                                SharedPreferences preferences1 = getSharedPreferences("language", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences1.edit();
                                editor.putString("language", "zh");
                                editor.apply();

                                SharedPreferences.Editor editor1 = languageSelected.edit();
                                editor1.putString("language", "0");
                                editor1.apply();
                                finish();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                                        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(LaunchIntent);
                                    }
                                }, 1);
                                break;
                            case 1: // 英文
                                dialog.dismiss();

                                //                                EventBus.getDefault().post("EVENT_REFRESH_LANGUAGE");
                                SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.putString("language", "en");
                                editor2.apply();

                                SharedPreferences.Editor editor3 = languageSelected.edit();
                                editor3.putString("language", "1");
                                editor3.apply();
                                finish();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                                        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(LaunchIntent);
                                    }
                                }, 1);
                                break;
                            default:
                                break;
                        }
                        checkedItem = which;
                    }
                });
                builder2.create().show(); // 创建对话框并显示
                break;
            case R.id.clear_cache_layout:
                CustomDialog.Builder builder = new CustomDialog.Builder(SettingActivity.this);
                builder.setTitle(getResources().getString(R.string.tip));
                builder.setMessage(getResources().getString(R.string.tips_clean));
                builder.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        DataCleanManager.cleanApplicationData(getApplicationContext(),
                                Common.LOG_PATH, Common.PHOTO_PATH,
                                Common.GROUPHEAD_PATH, Common.CACHEPHOTO_PATH,
                                Common.DOWNLOAD_APP_PATH);
                        ToastUtil.show(SettingActivity.this, getResources().getString(R.string.tips_cleanok));
                        try {
                            cacheSizeTv.setText(DataCleanManager.getCacheSize(new File(Common.APPLICATION_DIR)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.background_run_layout:
                if (Common.isNetConnected) {
                    startActivity(new Intent(this, BGRunningGuideActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.tips_netdisconnect), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logout_btn:
                String msg = getResources().getString(R.string.exitdlg1);
                if (Common.isRecording) {
                    msg = getResources().getString(R.string.exitdlg2);
                }
                CustomDialog.Builder builder_logout = new CustomDialog.Builder(this);
                builder_logout.setTitle(getResources().getString(R.string.exit));
                builder_logout.setMessage(msg);
                builder_logout.setNegativeButton(getResources().getString(R.string.cancl), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder_logout.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //                        Common.sendOffline(Common.getDeviceId(getApplicationContext()),getApplication());
                        //                        //Common.userId="0";
                        //                        Common.layerid_main=0;
                        LogoutRequest logoutRequest = new LogoutRequest(sp.getString("token", ""));
                        logoutRequest.requestHttpData(new ResponseData() {
                            @Override
                            public void onResponseData(boolean isSuccess, String code, Object responseObject, String msg) throws IOException {
                                if (isSuccess) {
                                    if (code.equals("0")) {
                                        SharedPreferences.Editor loginEditor = sp.edit();
                                        loginEditor.putString("token", "");
                                        loginEditor.putInt("userID", 0);
                                        loginEditor.putString("userPhone", "");
                                        loginEditor.putString("birthDate", "");
                                        loginEditor.putString("headurl", "");
                                        loginEditor.putString("mobilePhone", "");
                                        loginEditor.putString("nickname", "");
                                        loginEditor.putString("realName", "");
                                        loginEditor.putString("city", "");
                                        loginEditor.putString("workPlace", "");
                                        loginEditor.putString("education", "");
                                        loginEditor.putString("income", "");
                                        loginEditor.putString("occupation", "");
                                        loginEditor.putString("marriage", "");
                                        loginEditor.putString("childCount", "");
                                        loginEditor.putInt("sex", 0);
                                        loginEditor.apply();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                        // 这个Activity之前只有MainActivity
                        ActivityCollector.finishActivity("MainActivity");
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //intent.setClass(UserInfoActivity.this, SplashActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
                builder_logout.create().show();
                break;
        }
    }

    private void set(String lauType) {
        // 本地语言设置
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        configuration.locale = Locale.ENGLISH;
        resources.updateConfiguration(configuration, dm);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UNKNOWN_APP) {
            Log.e("dongsiyuaninstall",resultCode+"");
            downloadAPK();
        }
    }

    private void downloadAPK() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
//                AppUpdateService.start(mContext, mSavePath, mDownloadUrl);//安装应用的逻辑(写自己的就可以)
                downloadBinder.startDownload(mDownloadUrl);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP);
            }
        } else {
//            AppUpdateService.start(mContext, mSavePath, mDownloadUrl);
            downloadBinder.startDownload(mDownloadUrl);
        }
    }
}
