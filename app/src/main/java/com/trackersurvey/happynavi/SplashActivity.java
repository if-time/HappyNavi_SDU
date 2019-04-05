package com.trackersurvey.happynavi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.huawei.hms.support.api.push.TokenResult;
import com.trackersurvey.com.huawei.android.hms.agent.HMSAgent;
import com.trackersurvey.com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.trackersurvey.com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.trackersurvey.service.HuaweiPushRevicer;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.ToastUtil;

import static com.trackersurvey.service.HuaweiPushRevicer.ACTION_TOKEN;
import static com.trackersurvey.service.HuaweiPushRevicer.ACTION_UPDATEUI;


public class SplashActivity extends BaseActivity implements HuaweiPushRevicer.IPushCallback  {

    private ImageView iv_start;
    protected boolean _active = true;
    protected int _splashTime = 5000;
    private ImageView iv_version;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        iv_start = (ImageView) findViewById(R.id.iv_start);
        //iv_version = (ImageView) findViewById(R.id.iv_version);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(Common.getAppVersionName(SplashActivity.this));
//		Handler x = new Handler();
//		x.postDelayed(new splashhandler(), 2000);

        /**
         * SDK连接HMS
         */
        HMSAgent.connect(this, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                ToastUtil.show(SplashActivity.this,"HMS connect end:" + rst);
            }
        });

        getToken();

        registerBroadcast();

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000); // 动画持续时间
        iv_start.startAnimation(animation);
        //iv_version.setAnimation(animation);
        tv_version.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                iv_start.setBackgroundResource(R.mipmap.splash_page);
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

    private void registerBroadcast() {
        HuaweiPushRevicer.registerPushCallback(this);
    }


    /**
     * 获取token
     */
    private void getToken() {
//        ToastUtil.show(SplashActivity.this,"get token: begin");
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rst) {
//                ToastUtil.show(SplashActivity.this,"get token: end" + rst);
            }
        });
    }

    private static String token = null;

    @Override
    public void onReceive(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            if (b != null && ACTION_TOKEN.equals(action)) {
                token = b.getString(ACTION_TOKEN);
//                Log.i("onReceive get token: ", "onReceive: " + token);
            } else if (b != null && ACTION_UPDATEUI.equals(action)) {
                String log = b.getString("log");
//                ToastUtil.show(SplashActivity.this,"onReceive get token: end" + log);
            }
        }
    }
}
