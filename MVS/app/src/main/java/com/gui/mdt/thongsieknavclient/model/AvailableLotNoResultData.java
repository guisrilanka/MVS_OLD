package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by yeqim_000 on 06/09/16.
 */
public class AvailableLotNoResultData {
    private String SerialNo;
    private String LotNo;
    private String AvailableQuantity;
    private String ExpirationDate;
    private String ProductionDate;

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

    public String getAvailableQuantity() {
        return AvailableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        AvailableQuantity = availableQuantity;
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
}
