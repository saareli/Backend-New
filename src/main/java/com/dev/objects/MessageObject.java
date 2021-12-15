package com.dev.objects;
import java.util.Date;

public class MessageObject {
    int id;
    int senderId;
    String senderName;
    int receiverId;
    String title;
    String content;
    String sendTime;
    String readTime;

       public MessageObject(){
    }


    public MessageObject(int senderId, int receiverId, String title, String content,
                         String sendTime, String readTime , String senderName, int id) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.sendTime = sendTime;
        this.readTime = readTime;
        this.senderName = senderName;
        this.id = id;
    }

    public MessageObject(MessageObject message){
        this.senderId = message.senderId;
        this.senderName = message.senderName;
        this.receiverId = message.receiverId;
        this.title = message.title;
        this.content = message.content;
        this.sendTime = message.sendTime;
        this.readTime = message.readTime;
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }
}