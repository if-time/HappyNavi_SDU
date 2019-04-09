package com.trackersurvey.litepal.pointofinterestdata;

import org.litepal.crud.LitePalSupport;

public class PartnerNum extends LitePalSupport {
    private int key;
    private String value;

    public PartnerNum() {
    }

    public PartnerNum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
