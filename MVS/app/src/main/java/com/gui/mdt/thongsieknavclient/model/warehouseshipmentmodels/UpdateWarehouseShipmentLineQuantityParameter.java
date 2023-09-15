package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

/**
 * Created by yeqim_000 on 13/09/16.
 */
public class UpdateWarehouseShipmentLineQuantityParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String WarehouseShipmentNo;
    private int LineNo;
    private Float QuantityToShip;

    public UpdateWarehouseShipmentLineQuantityParameter(String userCompany, String userName, String password, String warehouseShipmentNo, int lineNo, Float quantityToShip) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        WarehouseShipmentNo = warehouseShipmentNo;
        LineNo = lineNo;
        QuantityToShip = quantityToShip;
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

    public Float getQuantityToShip() {
        return QuantityToShip;
    }

    public void setQuantityToShip(Float quantityToShip) {
        QuantityToShip = quantityToShip;
    }
}
