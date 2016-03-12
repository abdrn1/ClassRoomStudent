package com.abd.classroom1;

import android.graphics.Bitmap;

/**
 * Created by Abd on 3/7/2016.
 */
public class ChatMessageModel {

    String senderName;
    String recivers;
    String messageType;
    String simpleMessage;
    Bitmap image;
    boolean isSelf;



    public ChatMessageModel(){

    }

    public ChatMessageModel(String senderName, String recivers, String messageType, String simpleMessage, boolean isSelf) {
        this.senderName = senderName;
        this.recivers = recivers;
        this.messageType = messageType;
        this.simpleMessage = simpleMessage;
        this.isSelf = isSelf;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecivers() {
        return recivers;
    }

    public void setRecivers(String recivers) {
        this.recivers = recivers;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSimpleMessage() {
        return simpleMessage;
    }

    public void setSimpleMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }
}
