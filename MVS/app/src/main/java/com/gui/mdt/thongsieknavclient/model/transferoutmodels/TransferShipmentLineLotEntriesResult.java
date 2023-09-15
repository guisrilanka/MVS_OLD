package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class TransferShipmentLineLotEntriesResult extends BaseResult {
    private List<TransferShipmentLineLotEntriesResultData> TransferShipmentLineLotEntriesResultData;

    public List<TransferShipmentLineLotEntriesResultData> getTransferShipmentLineLotEntriesResultData() {
        if(TransferShipmentLineLotEntriesResultData == null)
        {
            return null;
        }
        else
        {
            return this.TransferShipmentLineLotEntriesResultData;
        }

    }

    public void setTransferShipmentLineLotEntriesResultData(List<TransferShipmentLineLotEntriesResultData> transferShipmentLineLotEntriesResultData) {
        this.TransferShipmentLineLotEntriesResultData = transferShipmentLineLotEntriesResultData;
    }
}
