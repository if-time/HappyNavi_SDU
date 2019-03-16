/*
 * @Title FileInfo.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description��
 * @author Yann
 * @date 2015-8-7 ����10:13:28
 * @version 1.0
 */
package com.trackersurvey.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 文件信息
 */
public class FileInfo implements Serializable {
    private int    versionid;
    private String versioncode;
    private String versionname;
    private String downloadurl;
    private String versiondesc;
    private int length;
    private int finished;

    public FileInfo(int versionid, String versioncode, String versionname, String downloadurl, String versiondesc, int length, int finished) {
        this.versionid = versionid;
        this.versioncode = versioncode;
        this.versionname = versionname;
        this.downloadurl = downloadurl;
        this.versiondesc = versiondesc;
        this.length = length;
        this.finished = finished;
    }

    public FileInfo(JSONObject jsonObject) {
        try {
            versionid = jsonObject.getInt("versionid");
            versioncode = jsonObject.getString("versioncode");
            versionname = jsonObject.getString("version");
            downloadurl = jsonObject.getString("downloadurl");
            versiondesc = jsonObject.getString("versiondesc");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getVersionid() {
        return versionid;
    }

    public void setVersionid(int versionid) {
        this.versionid = versionid;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getVersiondesc() {
        return versiondesc;
    }

    public void setVersiondesc(String versiondesc) {
        this.versiondesc = versiondesc;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "versionid=" + versionid +
                ", versioncode='" + versioncode + '\'' +
                ", versionname='" + versionname + '\'' +
                ", downloadurl='" + downloadurl + '\'' +
                ", versiondesc='" + versiondesc + '\'' +
                '}';
    }
}
