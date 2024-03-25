package com.example.forum;

import android.net.ParseException;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommentDetail extends AppCompatActivity {
    public String user_name;
    public String comment_time;
    public String comment_content;
    public String parent_user_name;
    public int user_id;
    public int comment_id;
    public int parent_comment_id;
    public CommentDetail(String user_name,String comment_time,String comment_content,String parent_user_name,int user_id,int comment_id,int parent_comment_id)
    {
        this.user_name = user_name;
        this.comment_time = convertTimeFormat(comment_time);
        this.comment_content = comment_content;
        this.parent_user_name = parent_user_name;
        this.user_id = user_id;
        this.comment_id = comment_id;
        this.parent_comment_id = parent_comment_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public int getParent_comment_id() {
        return parent_comment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public String getComment_time() {
        return comment_time;
    }

    public String getParent_user_name() {
        return parent_user_name;
    }

    public String getUser_name() {
        return user_name;
    }
    private String convertTimeFormat(String isoTime) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = isoFormat.parse(isoTime);
            return simpleFormat.format(date);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
