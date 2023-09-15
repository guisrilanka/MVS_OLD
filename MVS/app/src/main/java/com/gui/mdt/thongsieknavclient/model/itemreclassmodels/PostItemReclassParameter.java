package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class PostItemReclassParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private int ReclassHeaderLineNo;

    public PostItemReclassParameter(String userCompany, String userName, String password, int reclassHeaderLineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ReclassHeaderLineNo = reclassHeaderLineNo;
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

    public int getReclassHeaderLineNo() {
        return ReclassHeaderLineNo;
    }

    public void setReclassHeaderLineNo(int reclassHeaderLineNo) {
        ReclassHeaderLineNo = reclassHeaderLineNo;
    }
}
