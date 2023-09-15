package com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels;

/**
 * Created by yeqim_000 on 16/09/16.
 */
public class WarehouseShipmentEntry {
    public String ProductionDate;
    public String ExpiryDate;
    public String QtyToScan;
    public String LotNo;
    public String SerialNo;
    public int index;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;

    public WarehouseShipmentEntry(int indexNo, String productionDate, String expiryDate, String qtyToScan, String lotNo, String serialNo, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded){
        index = indexNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToScan = qtyToScan;
        LotNo = lotNo;
        SerialNo = serialNo;

        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}
