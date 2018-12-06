package com.kelybro.android.model;

/**
 * Created by Nick Bapu on 15-08-2018.
 */

public class ModelNotificationDetail {

    String Title;
    String Message;
    String Date;
    String Type;
    String client_code;

    public ModelNotificationDetail(String title, String message, String date, String type, String client_code) {
        Title = title;
        Message = message;
        Date = date;
        Type = type;
        this.client_code = client_code;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getClient_code() {
        return client_code;
    }

    public void setClient_code(String client_code) {
        this.client_code = client_code;
    }
}
