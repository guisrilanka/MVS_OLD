package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GUI-NB03 on 2017-08-30.
 */

public class ApiGSTPostingSetupParameter {

    String VAT_Bus_Posting_Group;
    String VAT_Prod_Posting_Group;
    String UserName;
    String Password;
    String UserCompany;

    public String getVAT_Bus_Posting_Group() {
        return VAT_Bus_Posting_Group;
    }

    public void setVAT_Bus_Posting_Group(String VAT_Bus_Posting_Group) {
        this.VAT_Bus_Posting_Group = VAT_Bus_Posting_Group;
    }

    public String getVAT_Prod_Posting_Group() {
        return VAT_Prod_Posting_Group;
    }

    public void setVAT_Prod_Posting_Group(String VAT_Prod_Posting_Group) {
        this.VAT_Prod_Posting_Group = VAT_Prod_Posting_Group;
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
