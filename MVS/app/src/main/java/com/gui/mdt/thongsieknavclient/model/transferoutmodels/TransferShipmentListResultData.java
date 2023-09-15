package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

/**
 * Created by yeqim_000 on 31/08/16.
 */
public class TransferShipmentListResultData {
    private String TransferNo;
    private String TransferFrom;
    private String TransferTo;
    private String ShipmentDate;

    public String getTransferNo() {
        return TransferNo;
    }

    public void setTransferNo(String transferNo) {
        TransferNo = transferNo;
    }

    public String getTransferFrom() {
        return TransferFrom;
    }

    public void setTransferFrom(String transferFrom) {
        TransferFrom = transferFrom;
    }

    public String getTransferTo() {
        return TransferTo;
    }

    public void setTransferTo(String transferTo) {
        TransferTo = transferTo;
    }

    public String getShipmentDate() {
        return ShipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        ShipmentDate = shipmentDate;
    }
}
