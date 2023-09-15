package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/28/2016.
 */

public class StockTakeEntryLineLotEntriesResultData {

    private int EntryNo;
    private int LineNo;
    private String ProductionDate;
    private String ExpireDate;
    private String LotNo;
    private String SerialNo;
    private float SystemCalculatedQty;
    private float UserEnterQty;
    private boolean DoNotShowSystemQty;

    public StockTakeEntryLineLotEntriesResultData(int entryNo, int lineNo, String productionDate, String expireDate, String lotNo, String serialNo, float systemCalculatedQty, float userEnterQty, boolean doNotShowSystemQty) {
        EntryNo = entryNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpireDate = expireDate;
        LotNo = lotNo;
        SerialNo = serialNo;
        SystemCalculatedQty = systemCalculatedQty;
        UserEnterQty = userEnterQty;
        DoNotShowSystemQty = doNotShowSystemQty;
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

    public float getSystemCalculatedQty() {
        return SystemCalculatedQty;
    }

    public void setSystemCalculatedQty(float systemCalculatedQty) {
        SystemCalculatedQty = systemCalculatedQty;
    }

    public float getUserEnterQty() {
        return UserEnterQty;
    }

    public void setUserEnterQty(float userEnterQty) {
        UserEnterQty = userEnterQty;
    }

    public boolean isDoNotShowSystemQty() {
        return DoNotShowSystemQty;
    }

    public void setDoNotShowSystemQty(boolean doNotShowSystemQty) {
        DoNotShowSystemQty = doNotShowSystemQty;
    }
}
