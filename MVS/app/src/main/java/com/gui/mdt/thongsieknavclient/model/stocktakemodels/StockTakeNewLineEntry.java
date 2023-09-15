package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 1/26/2017.
 */

public class StockTakeNewLineEntry {

    public String EntryNo;
    public String ItemNo;
    public String LocCode;
    public String NewLocCode;
    public String BinCode;
    public String NewBinCode;
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

    public StockTakeNewLineEntry(String docNo, String itemNo, String locCode, String newLocCode, String binCode, String newBinCode, String productionDate, String expiryDate, String qtyToReceive, String lotNo, String serialNo, int index, boolean isScanned, int localEntryIndexNo, int serverEntryIndexNo, boolean isUploaded) {
        EntryNo = docNo;
        ItemNo = itemNo;
        LocCode = locCode;
        NewLocCode = newLocCode;
        BinCode = binCode;
        NewBinCode = newBinCode;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToReceive = qtyToReceive;
        LotNo = lotNo;
        SerialNo = serialNo;
        this.index = index;
        IsScanned = isScanned;
        LocalEntryIndexNo = localEntryIndexNo;
        ServerEntryIndexNo = serverEntryIndexNo;
        IsUploaded = isUploaded;
    }
}
