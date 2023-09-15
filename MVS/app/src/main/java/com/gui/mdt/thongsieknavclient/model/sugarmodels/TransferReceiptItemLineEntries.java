package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import com.orm.SugarRecord;

/**
 * Created by yeqim_000 on 07/09/16.
 */
public class TransferReceiptItemLineEntries extends SugarRecord {
    private String TransferNo;
    private String ItemNo;
    private int LineNo;

    private String ProductionDate;
    private String ExpiryDate;
    private String QtyToReceive;
    private String LotNumber;
    private String BarcodeSerialNo;
    private String SerialNo;

    private int EntryIndexNo;
    private boolean IsUploaded;
    private Long TimeStamp;

    public TransferReceiptItemLineEntries()
    {
    }

    public TransferReceiptItemLineEntries(String transferNo, String itemNo, int lineNo, String productionDate, String expiryDate, String qtyToReceive, String lotNumber, String barcodeSerialNo, String serialNo, int entryIndexNo, boolean isUploaded, Long timeStamp) {
        TransferNo = transferNo;
        ItemNo = itemNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToReceive = qtyToReceive;
        LotNumber = lotNumber;
        BarcodeSerialNo = barcodeSerialNo;
        SerialNo = serialNo;
        EntryIndexNo = entryIndexNo;
        IsUploaded = isUploaded;
        TimeStamp = timeStamp;
    }

    public String getTransferNo() {
        return TransferNo;
    }

    public void setTransferNo(String transferNo) {
        TransferNo = transferNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
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

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getQtyToReceive() {
        return QtyToReceive;
    }

    public void setQtyToReceive(String qtyToReceive) {
        QtyToReceive = qtyToReceive;
    }

    public String getLotNumber() {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber) {
        LotNumber = lotNumber;
    }

    public String getBarcodeSerialNo() {
        return BarcodeSerialNo;
    }

    public void setBarcodeSerialNo(String barcodeSerialNo) {
        BarcodeSerialNo = barcodeSerialNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public int getEntryIndexNo() {
        return EntryIndexNo;
    }

    public void setEntryIndexNo(int entryIndexNo) {
        EntryIndexNo = entryIndexNo;
    }

    public boolean isUploaded() {
        return IsUploaded;
    }

    public void setUploaded(boolean uploaded) {
        IsUploaded = uploaded;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
