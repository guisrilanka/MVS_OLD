package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

/**
 * Created by yeqim_000 on 30/09/16.
 */
public class WarehouseShipmentAvailableLotNoParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String WarehouseShipmentNo;
    private int LineNo;

    public WarehouseShipmentAvailableLotNoParameter(String userCompany, String userName, String password, String warehouseShipmentNo, int lineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        WarehouseShipmentNo = warehouseShipmentNo;
        LineNo = lineNo;
    }
}
