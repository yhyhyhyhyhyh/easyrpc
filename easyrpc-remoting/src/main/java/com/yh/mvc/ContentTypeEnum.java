package com.yh.mvc;

public enum ContentTypeEnum {
    FORM_DATA("form-data"),
    X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_JSON("application/json");
    private String val;

    private ContentTypeEnum(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
