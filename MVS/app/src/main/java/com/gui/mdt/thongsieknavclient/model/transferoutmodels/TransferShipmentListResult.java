package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 31/08/16.
 */
public class TransferShipmentListResult extends BaseResult {

    private List<TransferShipmentListResultData> TransferShipmentListResultData;

    public List<TransferShipmentListResultData> getTransferShipmentListResultList() {
        if(TransferShipmentListResultData == null)
        {
            return null;
        }
        else
        {
            return this.TransferShipmentListResultData;
        }

    }

    public void setTransferShipmentListResultList(List<TransferShipmentListResultData> transferShipmentListResultData) {
        this.TransferShipmentListResultData = transferShipmentListResultData;
    }
}
