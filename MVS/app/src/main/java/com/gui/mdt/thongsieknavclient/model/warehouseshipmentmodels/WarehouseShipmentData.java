package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentData {
    private String WarehouseShipmentNo;
    private String DriverCode;
    private String ShipmentDate;

    private List<WarehouseShipmentLineData> LinesData;

    public List<WarehouseShipmentLineData> getLinesDataList() {
        if(LinesData == null)
        {
            return new ArrayList<WarehouseShipmentLineData>();
        }
        else
        {
            return this.LinesData;
        }
    }

    public void setLinesDataList(List<WarehouseShipmentLineData> linesData) {
        this.LinesData = linesData;
    }

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
