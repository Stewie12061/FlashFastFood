package com.example.flashfastfood.Data;

import com.google.android.gms.common.internal.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Chat {

    private String sender, receiver, message, dateTimeSend;
    private Boolean isDelete;

    public Chat(){}

    public Chat(String sender, String receiver, String message, String dateTimeSend, Boolean isDelete) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.dateTimeSend = dateTimeSend;
        this.isDelete = isDelete;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTimeSend() {
        return dateTimeSend;
    }

    public void setDateTimeSend(String dateTimeSend) {
        this.dateTimeSend = dateTimeSend;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}
