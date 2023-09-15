package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 2/8/2017.
 */

public class ItemReclassLineLotEntriesResultData {

    private int EntryNo;
    private int LineNo;
    private String ProductionDate;
    private String ExpireDate;
    private String LotNo;
    private String SerialNo;
    private float Quantity;

    public ItemReclassLineLotEntriesResultData(int entryNo, int lineNo, String productionDate, String expireDate, String lotNo, String serialNo, float quantity) {
        EntryNo = entryNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
        LotNo = lotNo;
        SerialNo = serialNo;
        Quantity = quantity;
    }

    public int getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(int entryNo) {
        EntryNo = entryNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }
}
