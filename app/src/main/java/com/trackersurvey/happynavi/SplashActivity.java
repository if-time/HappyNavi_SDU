package com.trackersurvey.happynavi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.util.Common;

public class SplashActivity extends BaseActivity {

    private   ImageView         iv_start;
    protected boolean           _active     = true;
    protected int               _splashTime = 5000;
    private   ImageView         iv_version;
    private   TextView          tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Android应用第一次安装成功点击“打开”后Home键切出应用后再点击桌面图标返回导致应用重启问题
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色

        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        iv_start = (ImageView) findViewById(R.id.iv_start);
        //iv_version = (ImageView) findViewById(R.id.iv_version);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(Common.getAppVersionName(SplashActivity.this));
        //		Handler x = new Handler();
        //		x.postDelayed(new splashhandler(), 2000);

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000); // 动画持续时间
        iv_start.startAnimation(animation);
        //iv_version.setAnimation(animation);
        tv_version.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                iv_start.setBackgroundResource(R.mipmap.splash_page1);
                //iv_version.setBackgroundResource(R.drawable.version);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                // 动画结束后跳转到登录页面
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                finish();
            }
        });

    }
}
