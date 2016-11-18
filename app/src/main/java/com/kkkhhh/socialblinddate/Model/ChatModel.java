package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dev1 on 2016-11-18.
 */
@IgnoreExtraProperties
public class ChatModel {
    public String uID;
    public String body;
    public String nickName;
    public String timeStamp;

    public ChatModel(){

    }

    public ChatModel(String uID, String body, String nickName, String timeStamp) {
        this.uID = uID;
        this.body = body;
        this.nickName = nickName;
        this.timeStamp = timeStamp;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uID", uID);
        result.put("body", body);
        result.put("nickName", nickName);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
