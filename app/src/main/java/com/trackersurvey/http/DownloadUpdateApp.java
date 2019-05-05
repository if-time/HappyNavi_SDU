package com.trackersurvey.http;

import android.util.Log;

import com.trackersurvey.bean.FileInfo;
import com.trackersurvey.bean.InterestMarkerData;
import com.trackersurvey.util.UrlHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DownloadUpdateApp extends HttpUtil {
    private String token;
    private String versionCode;

    public DownloadUpdateApp(String token, String versionCode) {
        this.token = token;
        this.versionCode = versionCode;
    }

    @Override
    public String getUrl() {
        return UrlHeader.DOWNLOAD_NEWSET_APP;
    }

    @Override
    public String baseUrl() {
        return "http://interface.hptracker.com:8090";
    }

    @Override
    public RequestBody parameter() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("versionCode", versionCode)
                .build();
        return requestBody;
    }

    @Override
    public HttpUtil handleData(String obj) {
        HttpUtil response = new HttpUtil();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(obj);
            Log.i("dongjsonObject", "handleData: " + jsonObject);
            FileInfo fileInfo = new FileInfo(jsonObject);
            response.responseObject = fileInfo;
            Log.i("dongjsonObject", "handleData: " + fileInfo.toString());
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return response;
    }
}
