package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/28/2016.
 */

public class CreateItemReclassHeaderParameter {

    private String UserCompany;
    private String UserName;
    private String Password;

    private String DocumentNo;
    private String PostingDate;
    private String ItemCode;
    private String ItemVariant;
    private String CurrentLocation;
    private String NewLocation;
    private String CurrentBin;
    private String NewBin;
    private float Quantity;
    private String ItemUomCode;
    private String LotNo;
    private String SerialNo;
    private String ProductionDate;
    private String ExpireDate;


    public CreateItemReclassHeaderParameter(String userCompany, String userName, String password, String documentNo, String postingDate, String itemCode, String itemVariant, String currentLocation, String newLocation, String currentBin, String newBin, float quantity, String itemUomCode, String lotNo, String serialNo, String productionDate, String expireDate) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        DocumentNo = documentNo;
        PostingDate = postingDate;
        ItemCode = itemCode;
        ItemVariant = itemVariant;
        CurrentLocation = currentLocation;
        NewLocation = newLocation;
        CurrentBin = currentBin;
        NewBin = newBin;
        Quantity = quantity;
        ItemUomCode = itemUomCode;
        LotNo = lotNo;
        SerialNo = serialNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
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

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String postingDate) {
        PostingDate = postingDate;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemVariant() {
        return ItemVariant;
    }

    public void setItemVariant(String itemVariant) {
        ItemVariant = itemVariant;
    }

    public String getCurrentLocation() {
        return CurrentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        CurrentLocation = currentLocation;
    }

    public String getNewLocation() {
        return NewLocation;
    }

    public void setNewLocation(String newLocation) {
        NewLocation = newLocation;
    }

    public String getCurrentBin() {
        return CurrentBin;
    }

    public void setCurrentBin(String currentBin) {
        CurrentBin = currentBin;
    }

    public String getNewBin() {
        return NewBin;
    }

    public void setNewBin(String newBin) {
        NewBin = newBin;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getItemUomCode() {
        return ItemUomCode;
    }

    public void setItemUomCode(String itemUomCode) {
        ItemUomCode = itemUomCode;
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

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }
}
