package com.trackersurvey.http;

import com.trackersurvey.util.UrlHeader;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ResettingPasswordRequest extends HttpUtil {
    private String loginName = "";
    private String password = "";
    private String code = "";

    public ResettingPasswordRequest(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }

    @Override
    public String getUrl() {
        return UrlHeader.UPDATEPWD_URL;
    }

    @Override
    public RequestBody parameter() {
//        String signature = HMAC_SHA1_Util.genHMAC(Common.secretKey+"_appkey"+Common._appkey
//                +"_timestamp"+_timestamp +"LoginName"+LoginName+"Password"+Password +Common.secretKey,
//                Common.secretKey);
        RequestBody requestBody = new FormBody.Builder()
                .add("loginName", loginName)
                .add("password", password)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        response.responseObject = obj;
        return response;
    }
}
