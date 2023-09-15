package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/28/2016.
 */

public class StockTakeAvailableLotNoParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private String ItemReclassLineNo;
    private int ItemNo;

    public StockTakeAvailableLotNoParameter(String userCompany, String userName, String password, String itemReclassLineNo, int itemNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ItemReclassLineNo = itemReclassLineNo;
        ItemNo = itemNo;
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

    public String getItemReclassLineNo() {
        return ItemReclassLineNo;
    }

    public void setItemReclassLineNo(String itemReclassLineNo) {
        ItemReclassLineNo = itemReclassLineNo;
    }

    public int getItemNo() {
        return ItemNo;
    }

    public void setItemNo(int itemNo) {
        ItemNo = itemNo;
    }
}
