package com.trackersurvey.happynavi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.trackersurvey.event.NetworkChangeEvent;
import com.trackersurvey.util.ActivityCollector;
import com.trackersurvey.util.LanguageUtil;
import com.trackersurvey.util.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zh931 on 2018/6/7.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected boolean mCheckNetWork = true; //默认检查网络状态
    View                       mTipView;
    WindowManager              mWindowManager;
    WindowManager.LayoutParams mLayoutParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BaseActivity", getClass().getSimpleName());

        mContext = this;

        initTipView();//初始化提示View
        EventBus.getDefault().register(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityCollector.addActivity(this);
    }

    /**
     * 重写一个 ContextWrapper 类
     *  ContextWrapper 构造函数中必须包含一个真正的 Context 引用，
     * 同时 ContextWrapper 中提供了 attachBaseContext() 用于给 ContextWrapper 对象中指定真正的 Context 对象，
     * 调用 ContextWrapper 的方法都会被转向其所包含的真正的Context对象。
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("language", Context.MODE_PRIVATE);
        String selectedLanguage = preferences.getString("language", "");
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, selectedLanguage));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在无网络情况下打开APP时，系统不会发送网络状况变更的Intent，需要自己手动检查
        hasNetWork(NetUtils.isConnected(mContext));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        //当提示View被动态添加后直接关闭页面会导致该View内存溢出，所以需要在finish时移除
        if (mTipView != null && mTipView.getParent() != null) {
            mWindowManager.removeView(mTipView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityCollector.removeActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        hasNetWork(event.isConnected);
    }

    private void hasNetWork(boolean has) {
        if (isCheckNetWork()) {
            if (has) {
                if (mTipView != null && mTipView.getParent() != null) {
                    mWindowManager.removeView(mTipView);
                }
            } else {
                if (mTipView.getParent() == null) {
                    mWindowManager.addView(mTipView, mLayoutParams);
                }
            }
        }
    }

    public void setCheckNetWork(boolean checkNetWork) {
        mCheckNetWork = checkNetWork;
    }

    public boolean isCheckNetWork() {
        return mCheckNetWork;
    }

    private void initTipView() {
        LayoutInflater inflater = getLayoutInflater();
        mTipView = inflater.inflate(R.layout.layout_network_tip, null); //提示View布局
        mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        //使用非CENTER时，可以通过设置XY的值来改变View的位置
        mLayoutParams.gravity = Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
    }

//    @Override
//    public boolean moveTaskToBack(boolean nonRoot) {
//        return super.moveTaskToBack(true);
//    }
}
