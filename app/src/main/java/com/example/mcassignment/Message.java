package com.example.mcassignment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    private String senderId;
    private String message;
    private String timestamp;


    public Message(){}

    public Message( String senderId, String message, String timestamp){
        this.senderId=senderId;
        this.message=message;
        this.timestamp=timestamp;

    }

    public String getSenderId(){
        return senderId;
    }

    public String getMessage(){
        return message;
    }

    public  String getTimestamp(){
        return timestamp;
    }

    public void setSenderId(String senderId){
        this.senderId=senderId;
    }

    public void setMessage( String message){
        this.message=message;
    }

    public void setTimestamp( String timestamp){
        this.timestamp=timestamp;
    }
}
