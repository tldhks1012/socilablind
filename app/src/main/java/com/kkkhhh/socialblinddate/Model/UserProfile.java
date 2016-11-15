package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Dev1 on 2016-11-08.
 */
@IgnoreExtraProperties
public class UserProfile {
    public String _uNickname;
    public String _uAge;
    public String _uLocal;
    public String _uGender;
    public UserProfile() {
    }

    public UserProfile(String _uNickname, String _uAge, String _uLocal, String _uGender) {
        this._uNickname = _uNickname;
        this._uAge = _uAge;
        this._uLocal = _uLocal;
        this._uGender = _uGender;
    }

}
