package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentListResultData {
    private String WarehouseShipmentNo;
    private String DriverCode;
    private String ShipmentDate;

    public String getWarehouseShipmentNo() {
        return WarehouseShipmentNo;
    }

    public void setWarehouseShipmentNo(String warehouseShipmentNo) {
        WarehouseShipmentNo = warehouseShipmentNo;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getShipmentDate() {
        return ShipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        ShipmentDate = shipmentDate;
    }
}
