package com.gui.mdt.thongsieknavclient.model.location;

/**
 * Created by user on 12/30/2016.
 */

public class LocationListParameter {
    private String UserCompany;
    private String UserName;
    private String Password;

    public LocationListParameter(String userCompany, String userName, String password) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
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
}
