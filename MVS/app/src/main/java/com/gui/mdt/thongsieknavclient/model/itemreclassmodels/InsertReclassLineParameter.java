package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/29/2016.
 */

public class InsertReclassLineParameter {

    private String UserCompany;
    private String UserName;
    private String Password;

    private int ReclassHeaderLineNo;
    private String DocumentNo;
    private String ItemNo;
    private String ItemVariant;
    private float Quantity;
    private String ItemUom;
    private String BinCode;
    private String NewBinCode;

    public InsertReclassLineParameter(String userCompany, String userName, String password, int reclassHeaderLineNo, String documentNo, String itemNo, String itemVariant, float quantity, String itemUom, String binCode, String newBinCode) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ReclassHeaderLineNo = reclassHeaderLineNo;
        DocumentNo = documentNo;
        ItemNo = itemNo;
        ItemVariant = itemVariant;
        Quantity = quantity;
        ItemUom = itemUom;
        BinCode = binCode;
        NewBinCode = newBinCode;
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

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
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

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getItemUom() {
        return ItemUom;
    }

    public void setItemUom(String itemUom) {
        ItemUom = itemUom;
    }

    public String getBinCode() {
        return BinCode;
    }

    public void setBinCode(String binCode) {
        BinCode = binCode;
    }

    public String getNewBinCode() {
        return NewBinCode;
    }

    public void setNewBinCode(String newBinCode) {
        NewBinCode = newBinCode;
    }
}
