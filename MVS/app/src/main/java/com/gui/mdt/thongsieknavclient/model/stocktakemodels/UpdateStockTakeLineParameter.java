package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/29/2016.
 */

public class UpdateStockTakeLineParameter {

    private String UserCompany;
    private String Password;
    private String UserName;

    private int StockTakeDetailLineNo;
    private float Quantity;

    public UpdateStockTakeLineParameter(String userCompany, String userName,String password, int stockTakeDetailLineNo, float quantity) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        StockTakeDetailLineNo = stockTakeDetailLineNo;
        Quantity = quantity;
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

    public int getStockTakeDetailLineNo() {
        return StockTakeDetailLineNo;
    }

    public void setStockTakeDetailLineNo(int stockTakeDetailLineNo) {
        StockTakeDetailLineNo = stockTakeDetailLineNo;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }
}
