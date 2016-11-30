package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dev1 on 2016-11-08.
 */
@IgnoreExtraProperties
public class Post {
    public String uid;
    public String userProfileImg;
    public String title;
    public String body;
    public String img1;
    public String stampTime;
    public String local;
    public String gender;
    public String age;
    public String postKey;
    public long stump;


    public Post() {

    }

    public Post(String uid, String userProfileImg,String title, String body,String img1,String local,String gender,String age,String stampTime,String postKey,long stump) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.img1=img1;
        this.local=local;
        this.gender=gender;
        this.age=age;
        this.userProfileImg=userProfileImg;
        this.stampTime=stampTime;
        this.postKey=postKey;
        this.stump=stump;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("userProfileImg", userProfileImg);
        result.put("title", title);
        result.put("body", body);
        result.put("img1", img1);
        result.put("local", local);
        result.put("gender", gender);
        result.put("age", age);
        result.put("stampTime",stampTime);
        result.put("postKey",postKey);
        result.put("stump",stump);
        return result;
    }
}
