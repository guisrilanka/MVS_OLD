package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class RemoveWarehouseShipmentLotEntryParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String WarehouseShipmentNo;
    private int LineNo;
    private int EntryNo;
    private String LotSerialNo;
    private float QuantityToDeduct;

    public RemoveWarehouseShipmentLotEntryParameter(String userCompany, String userName, String password, String warehouseShipmentNo, int lineNo, int entryNo, String lotSerialNo, float quantityToDeduct) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        WarehouseShipmentNo = warehouseShipmentNo;
        LineNo = lineNo;
        EntryNo = entryNo;
        LotSerialNo = lotSerialNo;
        QuantityToDeduct = quantityToDeduct;
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

    public String getWarehouseShipmentNo() {
        return WarehouseShipmentNo;
    }

    public void setWarehouseShipmentNo(String warehouseShipmentNo) {
        WarehouseShipmentNo = warehouseShipmentNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public int getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(int entryNo) {
        EntryNo = entryNo;
    }

    public String getLotSerialNo() {
        return LotSerialNo;
    }

    public void setLotSerialNo(String lotSerialNo) {
        LotSerialNo = lotSerialNo;
    }

    public float getQuantityToDeduct() {
        return QuantityToDeduct;
    }

    public void setQuantityToDeduct(float quantityToDeduct) {
        QuantityToDeduct = quantityToDeduct;
    }
}
