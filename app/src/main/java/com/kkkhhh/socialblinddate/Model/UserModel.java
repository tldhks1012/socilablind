package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev1 on 2016-10-28.
 */
@IgnoreExtraProperties
public class UserModel  {
    public String _uID;
    public String _uEmail;
    public String  _uUpdateData;
    public String _uNickname;
    public String _uAge;
    public String _uLocal;
    public String _uGender;
    public String _uImage1;
    public String _uImage2;
    public String _uImage3;
    public String _uImage4;
    public String _uImage5;
    public String _uImage6;

    public UserModel(){

    }
    public UserModel(String _uID, String _uEmail, String _uUpdateData) {
        this._uID = _uID;
        this._uEmail = _uEmail;
        this._uUpdateData=_uUpdateData;
    }

    public UserModel(String _uNickname, String _uAge, String _uLocal, String _uGender) {
        this._uNickname = _uNickname;
        this._uAge = _uAge;
        this._uLocal = _uLocal;
        this._uGender = _uGender;
    }

    public UserModel(String _uImage1, String _uImage2, String _uImage3, String _uImage4, String _uImage5, String _uImage6) {
        this._uImage1 = _uImage1;
        this._uImage2 = _uImage2;
        this._uImage3 = _uImage3;
        this._uImage4 = _uImage4;
        this._uImage5 = _uImage5;
        this._uImage6 = _uImage6;
    }


}
