package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 29/09/16.
 */
public class ProductionOrderEntry {
    public String ProductionDate;
    public String ExpiryDate;
    public String QtyToConsume;
    public String LotNo;
    public String SerialNo;
    public int index;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;

    public ProductionOrderEntry(int indexNo, String productionDate, String expiryDate, String qtyToConsume, String lotNo, String serialNo, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded){
        index = indexNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToConsume = qtyToConsume;
        LotNo = lotNo;
        SerialNo = serialNo;

        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}
