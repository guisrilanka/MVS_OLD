package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 05/08/16.
 */
public class PurchaseReceiveEntry {
    public String ProductionDate;
    public String ExpiryDate;
    public String QtyToReceive;
    public String LotNo;
    public String SerialNo;
    public int index;
    public boolean IsScanned;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;

    public PurchaseReceiveEntry(int indexNo, String productionDate, String expiryDate, String qtyToReceive, String lotNo, String serialNo, boolean isScanned, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded){
        index = indexNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToReceive = qtyToReceive;
        LotNo = lotNo;
        SerialNo = serialNo;
        IsScanned = isScanned;

        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}
