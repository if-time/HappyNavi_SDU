package com.trackersurvey.happynavi;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trackersurvey.http.GetMsgCodeRequest;
import com.trackersurvey.http.RegisterRequest;
import com.trackersurvey.http.ResponseData;
import com.trackersurvey.util.MD5Util;

import java.io.IOException;
import java.util.List;

public class ResettingPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button getIdentifyCode;
    MyCountDownTimer myCountDownTimer;
    private EditText inputPhone;
    private EditText inputMessage;
    private EditText inputPassword;
    private EditText confirmPassword;
    private Button commit;

    private SharedPreferences sp;

    private String phonenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetting_password);
        // 隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        sp = getSharedPreferences("config", MODE_PRIVATE);//私有参数
        phonenum = sp.getString("lastInputID", "");
        inputPhone = (EditText) findViewById(R.id.input_resetting_phone);
        inputPhone.setText(phonenum);

        inputMessage = (EditText) findViewById(R.id.input_verify_code);
        inputPassword = (EditText) findViewById(R.id.input_resetting_password);
        confirmPassword = (EditText) findViewById(R.id.input_confirm_password);


        commit = (Button) findViewById(R.id.commit_button);
        commit.setOnClickListener(this);

        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        myCountDownTimer = new MyCountDownTimer(60000,1000);
        getIdentifyCode = (Button) findViewById(R.id.get_identify_code);
        getIdentifyCode.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_identify_code:
                Log.i("RegisterInputPassword", "获取");
                myCountDownTimer.start();
                GetMsgCodeRequest getMsgCodeRequest = new GetMsgCodeRequest(inputPhone.getText().toString());
                getMsgCodeRequest.requestHttpData(new ResponseData() {
                    @Override
                    public void onResponseData(boolean isSuccess, String code, Object responseObject, final String msg) throws IOException {
                        if (isSuccess) {
                            switch (code) {
                                case "0":
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ResettingPasswordActivity.this, "获取验证码成功！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case "12":
                                    // 手机号格式不正确,请使用中国大陆运营商正确手机号码!
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ResettingPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case "13":
                                    // 手机验证码发送失败,请稍后再试!
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ResettingPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
                        } else {
                            Toast.makeText(ResettingPasswordActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.commit_button:
                String uid = inputPhone.getText().toString();
                String pwd = inputPassword.getText().toString();
                String confirmPwd = confirmPassword.getText().toString();
                String yzm = inputMessage.getText().toString();
                String pwdMD5 = MD5Util.string2MD5(pwd);
                if (yzm.equals("")) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                } else if (pwd.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (!confirmPwd.equals(pwd)) {
                    Toast.makeText(this, "您两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterRequest registerRequest = new RegisterRequest(uid, pwdMD5.toUpperCase(), yzm);
                    registerRequest.requestHttpData(new ResponseData() {
                        public void onResponseData(boolean isSuccess, String code, Object responseObject, final String msg) throws IOException {
                            if (isSuccess) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ResettingPasswordActivity.this, "重置密码成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ResettingPasswordActivity.this, "重置密码失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    Log.i("Resetting", inputPhone.getText().toString() + "_" + inputPassword.getText().toString() + "_" + inputMessage.getText().toString());
                }
                break;

        }
    }

    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long millisUntilFinished) {
            //防止计时过程中重复点击
            getIdentifyCode.setClickable(false);
            getIdentifyCode.setText(millisUntilFinished/1000 + "秒" + "后重新获取");
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            getIdentifyCode.setText("重新获取验证码");
            //设置可点击
            getIdentifyCode.setClickable(true);
        }
    }
}
