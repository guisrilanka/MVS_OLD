package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/29/2016.
 */

public class InsertStockTakeParameter {
    private String UserCompany;
    private String Password;
    private String UserName;

    private int StockTakeHeaderLineNo;
    private String PostingDate;
    private String ItemNo;
    private String ItemVariant;
    private String LocationCode;
    private String BinCode;
    private String LotNo;
    private String SerialNo;
    private String ProductionDate;
    private String ExpirationDate;
    private float Quantity;
    private String Uom;

    public InsertStockTakeParameter(String userCompany, String userName, String password, int stockTakeHeaderLineNo, String postingDate, String itemNo, String itemVariant, String locationCode, String binCode, String lotNo, String serialNo, String productionDate, String expirationDate, float quantity, String uom) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        StockTakeHeaderLineNo = stockTakeHeaderLineNo;
        PostingDate = postingDate;
        ItemNo = itemNo;
        ItemVariant = itemVariant;
        LocationCode = locationCode;
        BinCode = binCode;
        LotNo = lotNo;
        SerialNo = serialNo;
        ProductionDate = productionDate;
        ExpirationDate = expirationDate;
        Quantity = quantity;
        Uom = uom;
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

    public int getStockTakeHeaderLineNo() {
        return StockTakeHeaderLineNo;
    }

    public void setStockTakeHeaderLineNo(int stockTakeHeaderLineNo) {
        StockTakeHeaderLineNo = stockTakeHeaderLineNo;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String postingDate) {
        PostingDate = postingDate;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getItemVariant() {
        return ItemVariant;
    }

    public void setItemVariant(String itemVariant) {
        ItemVariant = itemVariant;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getBinCode() {
        return BinCode;
    }

    public void setBinCode(String binCode) {
        BinCode = binCode;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }
}
