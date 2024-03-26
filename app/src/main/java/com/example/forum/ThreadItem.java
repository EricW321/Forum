package com.example.forum;

public class ThreadItem {
    private String title;
    private String userName;
    private String threadTime;

    private int thread_id;

    public ThreadItem(String title, String userName, String threadTime, int thread_id) {
        this.title = title;
        this.userName = userName;
        this.threadTime = threadTime;
        this.thread_id=thread_id;
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
    public int getThread_id(){return thread_id;}
}
