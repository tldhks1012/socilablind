package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dev1 on 2016-11-21.
 */

public class FriendModel {
    public String _uID;
    public String _uNickname;
    public String _uAge;
    public String _uLocal;
    public String _uGender;
    public String _uImage1;

    public FriendModel(String _uID, String _uNickname, String _uAge, String _uLocal, String _uGender, String _uImage1) {
        this._uID = _uID;
        this._uNickname = _uNickname;
        this._uAge = _uAge;
        this._uLocal = _uLocal;
        this._uGender = _uGender;
        this._uImage1 = _uImage1;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_uID", _uID);
        result.put("_uNickname", _uNickname);
        result.put("_uAge", _uAge);
        result.put("_uLocal", _uLocal);
        result.put("_uGender", _uGender);
        result.put("_uImage1", _uImage1);
        return result;
    }
}
