package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by user on 10/26/2016.
 */

public class PasteGroupProductionOrderRouteLotEntriesResultData {
    private int EntryNo;
    private String ExpireDate;
    private int LineNo;
    private String OperationNo;
    private String LotNo;
    private String ProductionDate;
    private Float QuantityProduced;
    private String SerialNo;

    public PasteGroupProductionOrderRouteLotEntriesResultData(int entryNo, String expireDate, int lineNo, String operationNo, String lotNo, String productionDate, Float quantityProduced, String serialNo) {
        EntryNo = entryNo;
        ExpireDate = expireDate;
        LineNo = lineNo;
        OperationNo = operationNo;
        LotNo = lotNo;
        ProductionDate = productionDate;
        QuantityProduced = quantityProduced;
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

    public String getOperationNo() {
        return OperationNo;
    }

    public void setOperationNo(String operationNo) {
        OperationNo = operationNo;
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

    public Float getQuantityProduced() {
        return QuantityProduced;
    }

    public void setQuantityProduced(Float quantityProduced) {
        QuantityProduced = quantityProduced;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }
}
