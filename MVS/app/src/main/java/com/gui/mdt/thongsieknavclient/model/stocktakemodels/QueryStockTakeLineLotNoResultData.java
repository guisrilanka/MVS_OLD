package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 2/21/2017.
 */

public class QueryStockTakeLineLotNoResultData {

    private String SerialNo;
    private String LotNo;
    private String Quantity;
    private String ExpirationDate;
    private String ProductionDate;
    private int EntryNo;
    private String QuantityToHandleBase;

    public QueryStockTakeLineLotNoResultData(String serialNo, String lotNo, String quantity, String expirationDate, String productionDate, int entryNo, String quantityToHandleBase) {
        SerialNo = serialNo;
        LotNo = lotNo;
        Quantity = quantity;
        ExpirationDate = expirationDate;
        ProductionDate = productionDate;
        EntryNo = entryNo;
        QuantityToHandleBase = quantityToHandleBase;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public int getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(int entryNo) {
        EntryNo = entryNo;
    }

    public String getQuantityToHandleBase() {
        return QuantityToHandleBase;
    }

    public void setQuantityToHandleBase(String quantityToHandleBase) {
        QuantityToHandleBase = quantityToHandleBase;
    }
}
