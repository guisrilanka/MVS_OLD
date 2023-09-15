package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/29/2016.
 */

public class PostStockTakeParameter {
    private String UserCompany;
    private String Password;
    private String UserName;
    private String StockTakeDetailLineNo;

    public PostStockTakeParameter(String userCompany, String userName, String password, String stockTakeDetailLineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        StockTakeDetailLineNo = stockTakeDetailLineNo;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getStockTakeDetailLineNo() {
        return StockTakeDetailLineNo;
    }

    public void setStockTakeDetailLineNo(String stockTakeDetailLineNo) {
        StockTakeDetailLineNo = stockTakeDetailLineNo;
    }
}
