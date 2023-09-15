package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/29/2016.
 */

public class QueryStockTakeAvailableLotNoParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private int StockTakeDetailLineNo;

    public QueryStockTakeAvailableLotNoParameter(String userCompany, String userName, String password, int stockTakeDetailLineNo) {
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

    public int getStockTakeDetailLineNo() {
        return StockTakeDetailLineNo;
    }

    public void setStockTakeDetailLineNo(int stockTakeDetailLineNo) {
        StockTakeDetailLineNo = stockTakeDetailLineNo;
    }
}
