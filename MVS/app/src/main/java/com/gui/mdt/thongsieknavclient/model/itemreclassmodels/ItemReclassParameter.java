package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/29/2016.
 */

public class ItemReclassParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private int ReclassHeaderLineNo;
    private String FilterItemNo;
    private String FilterItemDescription;

    public ItemReclassParameter(String userCompany, String userName, String password, int reclassHeaderLineNo, String filterItemNo, String filterItemDescription) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ReclassHeaderLineNo = reclassHeaderLineNo;
        FilterItemNo = filterItemNo;
        FilterItemDescription = filterItemDescription;
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

    public String getFilterItemNo() {
        return FilterItemNo;
    }

    public void setFilterItemNo(String filterItemNo) {
        FilterItemNo = filterItemNo;
    }

    public String getFilterItemDescription() {
        return FilterItemDescription;
    }

    public void setFilterItemDescription(String filterItemDescription) {
        FilterItemDescription = filterItemDescription;
    }
}
