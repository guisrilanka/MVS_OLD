package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GUISL-NB05 on 10/2/2017.
 */

public class ApiPostSalesOrderStatusUpdateParameter {

    String Key;
    String No;
    String Delivered;
    String UserName;
    String Password;
    String UserCompany;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getDelivered() {
        return Delivered;
    }

    public void setDelivered(String delivered) {
        Delivered = delivered;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }
}
