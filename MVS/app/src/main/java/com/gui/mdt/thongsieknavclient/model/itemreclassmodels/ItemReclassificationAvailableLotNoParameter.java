package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassificationAvailableLotNoParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String ItemNo;
    private int ItemReclassLineNo;

    public ItemReclassificationAvailableLotNoParameter(String userCompany, String userName, String password, String itemNo, int itemReclassLineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ItemNo = itemNo;
        ItemReclassLineNo = itemReclassLineNo;
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

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public int getItemReclassLineNo() {
        return ItemReclassLineNo;
    }

    public void setItemReclassLineNo(int itemReclassLineNo) {
        ItemReclassLineNo = itemReclassLineNo;
    }
}
