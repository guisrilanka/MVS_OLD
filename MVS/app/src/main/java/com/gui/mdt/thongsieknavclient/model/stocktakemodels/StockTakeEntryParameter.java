package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 2/3/2017.
 */

public class StockTakeEntryParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private int StockTakeHeaderLineNo;
    private String FilterItemNo;
    private String FilterItemDescription;

    public StockTakeEntryParameter(String userCompany, String userName, String password, int stockTakeHeaderLineNo, String filterItemNo, String filterItemDescription) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        StockTakeHeaderLineNo = stockTakeHeaderLineNo;
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

    public int getStockTakeHeaderLineNo() {
        return StockTakeHeaderLineNo;
    }

    public void setStockTakeHeaderLineNo(int stockTakeHeaderLineNo) {
        StockTakeHeaderLineNo = stockTakeHeaderLineNo;
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
