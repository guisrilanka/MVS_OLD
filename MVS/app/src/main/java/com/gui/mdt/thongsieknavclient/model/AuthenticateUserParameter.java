package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 13/7/2016.
 */
public class AuthenticateUserParameter {
    public AuthenticateUserParameter(String userCompany, String userName, String password) {
        UserName = userName;
        Password = password;
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

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    private String UserName;
    private String Password;
    private String UserCompany;
}
