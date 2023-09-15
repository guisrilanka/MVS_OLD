package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 19/08/16.
 */
public class PurchaseOrderLineLotEntriesResultData {
    private int EntryNo;
    private String ExpireDate;
    private int LineNo;
    private String LotNo;
    private String ProductionDate;
    private Float ReceivedQuantity;
    private String SerialNo;

    public PurchaseOrderLineLotEntriesResultData(int entryNo, String expireDate, int lineNo, String lotNo, String productionDate, Float receivedQuantity, String serialNo) {
        EntryNo = entryNo;
        ExpireDate = expireDate;
        LineNo = lineNo;
        LotNo = lotNo;
        ProductionDate = productionDate;
        ReceivedQuantity = receivedQuantity;
        SerialNo = serialNo;
    }

    public int getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(int entryNo) {
        EntryNo = entryNo;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public Float getReceivedQuantity() {
        return ReceivedQuantity;
    }

    public void setReceivedQuantity(Float receivedQuantity) {
        ReceivedQuantity = receivedQuantity;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }
}
