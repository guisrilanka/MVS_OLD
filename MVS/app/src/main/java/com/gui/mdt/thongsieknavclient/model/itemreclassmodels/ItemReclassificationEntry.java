package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassificationEntry {

    public String ProductionDate;
    public String ExpiryDate;
    public String QtyToShip;
    public String LotNo;
    public String SerialNo;
    public int Index;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;

    public ItemReclassificationEntry(String productionDate, String expiryDate, String qtyToShip, String lotNo, String serialNo, int index, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded) {
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToShip = qtyToShip;
        LotNo = lotNo;
        SerialNo = serialNo;
        Index = index;
        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}

