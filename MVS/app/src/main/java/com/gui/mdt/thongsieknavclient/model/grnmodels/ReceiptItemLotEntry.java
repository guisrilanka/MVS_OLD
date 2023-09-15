package com.gui.mdt.thongsieknavclient.model.grnmodels;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class ReceiptItemLotEntry {
    public String ProductionDate;
    public String ExpirationDate;
    public String LotNo;
    public String Quantity;
    public String SerialNo;

    public ReceiptItemLotEntry(String productionDate, String expirationDate, String lotNo, String quantity, String serialNo)
    {
        ProductionDate = productionDate;
        ExpirationDate = expirationDate;
        LotNo = lotNo;
        Quantity = quantity;
        SerialNo = serialNo;
    }
}
