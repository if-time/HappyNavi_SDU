package com.trackersurvey.litepal.pointofinterestdata;

import org.litepal.crud.LitePalSupport;

public class Relation extends LitePalSupport {
    private int key;
    private String value;

    public Relation() {
    }

    public Relation(int key, String value) {
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
