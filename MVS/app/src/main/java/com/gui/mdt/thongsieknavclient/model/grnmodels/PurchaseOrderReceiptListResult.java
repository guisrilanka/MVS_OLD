package com.gui.mdt.thongsieknavclient.model.grnmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 22/08/16.
 */
public class PurchaseOrderReceiptListResult extends BaseResult {
    private List<PurchaseOrderReceiptListItem> PurchaseOrderReceiptListResultData;

    public List<PurchaseOrderReceiptListItem> getPurchaseOrderReceiptList() {
        if(PurchaseOrderReceiptListResultData == null)
        {
            return null;
        }
        else
        {
            return this.PurchaseOrderReceiptListResultData;
        }

    }

    public void setPurchaseOrderReceiptList(List<PurchaseOrderReceiptListItem> purchaseOrderReceiptList) {
        this.PurchaseOrderReceiptListResultData = purchaseOrderReceiptList;
    }
}
