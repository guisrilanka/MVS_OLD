package com.gui.mdt.thongsieknavclient.model.transferinmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 05/09/16.
 */
public class TransferReceiptListResult extends BaseResult{

    private List<TransferReceiptListResultData> TransferReceiptListResultData;

    public List<TransferReceiptListResultData> getTransferReceiptListResultData() {
        if(TransferReceiptListResultData == null)
        {
            return null;
        }
        else
        {
            return this.TransferReceiptListResultData;
        }

    }

    public void setTransferReceiptListResultData(List<TransferReceiptListResultData> transferReceiptListResultData) {
        this.TransferReceiptListResultData = transferReceiptListResultData;
    }
}
