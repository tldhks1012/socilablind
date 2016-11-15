package com.kkkhhh.socialblinddate.Model;

import android.provider.ContactsContract;

/**
 * Created by Dev1 on 2016-10-31.
 */

public class Content {
    public String __cID;
    public String __uID;
    public String __content;
    public String __cPicture;
    public String __createData;
    public String __updateData;
    public String __sub;

    public Content(String __cID, String __uID, String __sub,String __content, String __cPicture, String __createData, String __updateData) {
        this.__cID = __cID;
        this.__uID = __uID;
        this.__content = __content;
        this.__cPicture = __cPicture;
        this.__createData = __createData;
        this.__updateData = __updateData;
        this.__sub = __sub;
    }

    public String get__cID() {
        return __cID;
    }

    public void set__cID(String __cID) {
        this.__cID = __cID;
    }

    public String get__uID() {
        return __uID;
    }

    public void set__uID(String __uID) {
        this.__uID = __uID;
    }

    public String get__content() {
        return __content;
    }

    public void set__content(String __content) {
        this.__content = __content;
    }

    public String get__cPicture() {
        return __cPicture;
    }

    public void set__cPicture(String __cPicture) {
        this.__cPicture = __cPicture;
    }

    public String get__createData() {
        return __createData;
    }

    public void set__createData(String __createData) {
        this.__createData = __createData;
    }

    public String get__updateData() {
        return __updateData;
    }

    public void set__updateData(String __updateData) {
        this.__updateData = __updateData;
    }

    public String get__sub() {
        return __sub;
    }

    public void set__sub(String __sub) {
        this.__sub = __sub;
    }
}
