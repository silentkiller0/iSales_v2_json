package com.iSales.remote.model;

/**
 * Created by netserve on 27/08/2018.
 */

public class SuccessProperty {
    private Integer code;
    private String token;
    private String entity;
    private String message;

    public SuccessProperty() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
