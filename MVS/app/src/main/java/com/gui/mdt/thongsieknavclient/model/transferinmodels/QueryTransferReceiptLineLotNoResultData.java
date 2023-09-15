package com.gui.mdt.thongsieknavclient.model.transferinmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

/**
 * Created by yeqim_000 on 07/10/16.
 */
public class QueryTransferReceiptLineLotNoResultData {
    private String SerialNo;
    private String LotNo;
    private String Quantity;
    private String ExpirationDate;
    private String ProductionDate;
    private int EntryNo;
    private String QuantityToHandleBase;

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
