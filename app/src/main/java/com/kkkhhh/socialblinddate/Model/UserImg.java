package com.kkkhhh.socialblinddate.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Dev1 on 2016-11-08.
 */
@IgnoreExtraProperties
public class UserImg {
    public String _uImage1;
    public String _uImage2;
    public String _uImage3;
    public String _uImage4;
    public String _uImage5;
    public String _uImage6;

    public UserImg() {

    }

    public UserImg(String _uImage1, String _uImage2, String _uImage3, String _uImage4, String _uImage5, String _uImage6) {
        this._uImage1 = _uImage1;
        this._uImage2 = _uImage2;
        this._uImage3 = _uImage3;
        this._uImage4 = _uImage4;
        this._uImage5 = _uImage5;
        this._uImage6 = _uImage6;
    }


}
