package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.model.LoginModel;
import com.trackersurvey.util.DESUtil;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zh931 on 2018/5/9.
 */

public class LoginRequest extends HttpUtil {

//    private String userID = "";
    private String loginName = "";
    private String password = "";
    private String marshmallowMacAddress;
    private String mobilePlatform = "1";

    public LoginRequest(String loginName, String password, String marshmallowMacAddress) {
        this.loginName = loginName;
        this.password = password;
        this.marshmallowMacAddress = marshmallowMacAddress;
    }

    @Override
    public String getUrl() {
        return UrlHeader.LOGIN_URL_NEW;
    }

//    @Override
//    public RequestBody parameter() {
//        RequestBody requestBody = new FormBody.Builder()
//                .add("userID", userID)
//                .add("password", password)
//                .add("deviceId", deviceId)
//                .build();
//        return requestBody;
//    }


    @Override
    public RequestBody parameter() {
        Log.i("HttpUtil", "loginName"+loginName+"password"+password);
        RequestBody requestBody = new FormBody.Builder()
                .add("loginName", loginName)
                .add("password", password)
                .add("macAddress", marshmallowMacAddress)
                .add("MobilePlatform", mobilePlatform)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        try {
            String result = DESUtil.decrypt(obj);
            Log.i("LoginRequestdecrypt", "decrypt result : " + result);
            JSONObject jsonObject = new JSONObject(result);
            LoginModel model = new LoginModel(jsonObject);
            response.responseObject = model;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
