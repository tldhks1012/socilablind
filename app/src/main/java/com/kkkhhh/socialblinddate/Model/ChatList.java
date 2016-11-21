package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dev1 on 2016-11-18.
 */
@IgnoreExtraProperties
public class ChatList {
    public String chatKey;
    public String partnerID;
    public String uID;



    public ChatList() {

    }

    public ChatList(String chatKey, String partnerID,String uID) {
        this.chatKey=chatKey;
        this.partnerID=partnerID;
        this.uID=uID;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("chatKey", chatKey);
        result.put("partnerID", partnerID);
        result.put("uID", uID);
        return result;
    }
}
