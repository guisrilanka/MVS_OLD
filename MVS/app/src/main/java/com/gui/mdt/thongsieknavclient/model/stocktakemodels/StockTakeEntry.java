package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/28/2016.
 */

public class StockTakeEntry {

    public String ProductionDate;
    public String ExpiryDate;
    public String QtyToReceive;
    public String QtyToHandleBase;
    public String LotAvailableQuantity;
    public String LotNo;
    public String SerialNo;
    public int index;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;
    public boolean IsDoNotShowSystemQty;

    public StockTakeEntry(int indexNo, String productionDate, String expiryDate, String qtyToReceive, String qtyToHandleBase, String lotAvailableQuantity, String lotNo, String serialNo, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded, boolean isDoNotShowSystemQty) {
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToReceive = qtyToReceive;
        QtyToHandleBase = qtyToHandleBase;
        LotAvailableQuantity = lotAvailableQuantity;
        LotNo = lotNo;
        SerialNo = serialNo;
        index = indexNo;
        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
        IsDoNotShowSystemQty = isDoNotShowSystemQty;
    }
}
