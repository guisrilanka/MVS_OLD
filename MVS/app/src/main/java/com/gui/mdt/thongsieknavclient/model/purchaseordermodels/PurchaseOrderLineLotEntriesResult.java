package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 19/08/16.
 */
public class PurchaseOrderLineLotEntriesResult extends BaseResult {
    private List<PurchaseOrderLineLotEntriesResultData> PurchaseOrderLineLotEntriesResultData;

    public List<PurchaseOrderLineLotEntriesResultData> getPurchaseOrderLineLotEntriesResultData() {
        if(PurchaseOrderLineLotEntriesResultData == null)
        {
            return null;
        }
        else
        {
            return this.PurchaseOrderLineLotEntriesResultData;
        }

    }

    public void setPurchaseOrderLineLotEntriesResultData(List<PurchaseOrderLineLotEntriesResultData> purchaseOrderLineLotEntriesResultData) {
        this.PurchaseOrderLineLotEntriesResultData = purchaseOrderLineLotEntriesResultData;
    }
}
