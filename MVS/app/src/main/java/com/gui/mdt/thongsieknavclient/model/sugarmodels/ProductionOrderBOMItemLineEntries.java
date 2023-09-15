package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import com.orm.SugarRecord;

/**
 * Created by yeqim_000 on 27/09/16.
 */
public class ProductionOrderBOMItemLineEntries extends SugarRecord{
    private String ProductionOrderNo;
    private String ItemNo;
    private int LineNo;

    private String ProductionDate;
    private String ExpiryDate;
    private String QtyToConsume;
    private String LotNumber;
    private String BarcodeSerialNo;
    private String SerialNo;

    private int EntryIndexNo;
    private boolean IsUploaded;
    private Long TimeStamp;

    public ProductionOrderBOMItemLineEntries()
    {

    }

    public ProductionOrderBOMItemLineEntries(String productionOrderNo, String itemNo, int lineNo, String productionDate, String expiryDate, String qtyToConsume, String lotNumber, String barcodeSerialNo, String serialNo, int entryIndexNo, boolean isUploaded, Long timeStamp) {
        ProductionOrderNo = productionOrderNo;
        ItemNo = itemNo;
        LineNo = lineNo;
        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToConsume = qtyToConsume;
        LotNumber = lotNumber;
        BarcodeSerialNo = barcodeSerialNo;
        SerialNo = serialNo;
        EntryIndexNo = entryIndexNo;
        IsUploaded = isUploaded;
        TimeStamp = timeStamp;
    }

    public String getProductionOrderNo() {
        return ProductionOrderNo;
    }

    public void setProductionOrderNo(String productionOrderNo) {
        ProductionOrderNo = productionOrderNo;
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

    public String getQtyToConsume() {
        return QtyToConsume;
    }

    public void setQtyToConsume(String qtyToConsume) {
        QtyToConsume = qtyToConsume;
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
