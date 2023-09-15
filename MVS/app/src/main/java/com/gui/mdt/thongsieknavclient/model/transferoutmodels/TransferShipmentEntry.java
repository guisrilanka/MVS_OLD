package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

/**
 * Created by yeqim_000 on 02/09/16.
 */
public class TransferShipmentEntry {
    public String ProductionDate;
    public String ExpiryDate;
    public String QtyToShip;
    public String LotNo;
    public String SerialNo;
    public int index;

    public int LocalEntryIndexNo;
    public int ServerEntryIndexNo;
    public boolean IsUploaded;

    public TransferShipmentEntry(int indexNo, String productionDate, String expiryDate, String qtyToShip, String lotNo, String serialNo, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded){
        index = indexNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToShip = qtyToShip;
        LotNo = lotNo;
        SerialNo = serialNo;

        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}
