package com.example.forum;

public class ThreadItem {
    private String title;
    private String userName;
    private String threadTime;

    public ThreadItem(String title, String userName, String threadTime) {
        this.title = title;
        this.userName = userName;
        this.threadTime = threadTime;
    }

    public String getTitle() {
        return title;
    }

    public String getUserName() {
        return userName;
    }

    public String getThreadTime() {
        return threadTime;
    }
}
