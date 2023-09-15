package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class WarehouseShipmentLineLotEntriesResult extends BaseResult{
    private List<WarehouseShipmentLineLotEntriesResultData> WarehouseShipmentLineLotEntriesResultData;

    public List<WarehouseShipmentLineLotEntriesResultData> getWarehouseShipmentLineLotEntriesResultData() {
        if(WarehouseShipmentLineLotEntriesResultData == null)
        {
            return null;
        }
        else
        {
            return this.WarehouseShipmentLineLotEntriesResultData;
        }

    }

    public void setWarehouseShipmentLineLotEntriesResultData(List<WarehouseShipmentLineLotEntriesResultData> warehouseShipmentLineLotEntriesResultData) {
        this.WarehouseShipmentLineLotEntriesResultData = warehouseShipmentLineLotEntriesResultData;
    }
}
