package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 06/10/16.
 */
public class ProductionOrderRouteLotEntry {
    public String ProductionDate;
    public String ExpiryDate;
    public String QtyProduced;
    public String LotNo;
    public String SerialNo;
    public int index;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;

    public ProductionOrderRouteLotEntry(int indexNo, String productionDate, String expiryDate, String qtyProduced, String lotNo, String serialNo, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded){
        index = indexNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyProduced = qtyProduced;
        LotNo = lotNo;
        SerialNo = serialNo;

        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}
