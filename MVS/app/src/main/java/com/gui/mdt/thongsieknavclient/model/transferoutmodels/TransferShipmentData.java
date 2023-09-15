package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 01/09/16.
 */
public class TransferShipmentData {
    private String TransferNo;
    private String TransferFrom;
    private String TransferTo;
    private String ShipmentDate;

    private List<TransferShipmentLineData> LinesData;

    public List<TransferShipmentLineData> getLinesDataList() {
        if(LinesData == null)
        {
            return new ArrayList<TransferShipmentLineData>();
        }
        else
        {
            return this.LinesData;
        }

    }

    public void setLinesDataList(List<TransferShipmentLineData> linesData) {
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
