package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Dev1 on 2016-11-30.
 */
@IgnoreExtraProperties
public class LikeModel {

    public String otherAuth;
    public String stampTime;
    public long stump;

    public LikeModel(){

    }
    public LikeModel(String otherAuth, String stampTime, long stump) {
        this.otherAuth = otherAuth;
        this.stampTime = stampTime;
        this.stump = stump;
    }
}
