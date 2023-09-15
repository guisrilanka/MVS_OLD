package com.gui.mdt.thongsieknavclient.model.productionordermodels;

/**
 * Created by yeqim_000 on 28/09/16.
 */
public class ProductionOrderLineLotEntriesResultData {
    private int EntryNo;
    private String ExpireDate;
    private int LineNo;
    private String LotNo;
    private String ProductionDate;
    private Float QuantityToShip;
    private String SerialNo;

    public ProductionOrderLineLotEntriesResultData(int entryNo, String expireDate, int lineNo, String lotNo, String productionDate, Float quantityToShip, String serialNo) {
        EntryNo = entryNo;
        ExpireDate = expireDate;
        LineNo = lineNo;
        LotNo = lotNo;
        ProductionDate = productionDate;
        QuantityToShip = quantityToShip;
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

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public Float getQuantityToShip() {
        return QuantityToShip;
    }

    public void setQuantityToShip(Float quantityToShip) {
        QuantityToShip = quantityToShip;
    }
}
