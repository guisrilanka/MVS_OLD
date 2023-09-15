package com.gui.mdt.thongsieknavclient.model.location;

/**
 * Created by user on 12/30/2016.
 */

public class LocationBinListParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String FilterLocationCode;

    public LocationBinListParameter(String userCompany, String userName, String password, String filterLocationCode) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        FilterLocationCode = filterLocationCode;
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

    public String getFilterLocationCode() {
        return FilterLocationCode;
    }

    public void setFilterLocationCode(String filterLocationCode) {
        FilterLocationCode = filterLocationCode;
    }

}
