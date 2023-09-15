package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class TransferReceiptEntry {

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

    public TransferReceiptEntry(int indexNo, String productionDate, String expiryDate, String qtyToReceive, String qtyToHandleBase, String lotAvailableQuantity, String lotNo, String serialNo, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded){
        index = indexNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToReceive = qtyToReceive;
        QtyToHandleBase = qtyToHandleBase;
        LotAvailableQuantity = lotAvailableQuantity;
        LotNo = lotNo;
        SerialNo = serialNo;

        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }

}
