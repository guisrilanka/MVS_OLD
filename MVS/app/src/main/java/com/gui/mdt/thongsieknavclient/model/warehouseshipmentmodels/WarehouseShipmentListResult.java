package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentListResult extends BaseResult{
    private List<WarehouseShipmentListResultData> WarehouseShipmentListResultData;

    public List<WarehouseShipmentListResultData> getWarehouseShipmentListResultData() {
        if(WarehouseShipmentListResultData == null)
        {
            return null;
        }
        else
        {
            return this.WarehouseShipmentListResultData;
        }

    }

    public void setWarehouseShipmentListResultData(List<WarehouseShipmentListResultData> warehouseShipmentListResultData) {
        this.WarehouseShipmentListResultData = warehouseShipmentListResultData;
    }
}
