package com.gui.mdt.thongsieknavclient.model.transferinmodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class TransferReceiptData {

    private String TransferNo;
    private String TransferFrom;
    private String TransferTo;
    private String ShipmentDate;

    private List<TransferReceiptLineData> LinesData;

    public List<TransferReceiptLineData> getLinesDataList() {
        if(LinesData == null)
        {
            return new ArrayList<TransferReceiptLineData>();
        }
        else
        {
            return this.LinesData;
        }

    }

    public void setLinesDataList(List<TransferReceiptLineData> linesData) {
        this.LinesData = linesData;
    }

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
