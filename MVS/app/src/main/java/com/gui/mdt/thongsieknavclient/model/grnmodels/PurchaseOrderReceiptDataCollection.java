package com.gui.mdt.thongsieknavclient.model.grnmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class PurchaseOrderReceiptDataCollection extends BaseResult{
    private PurchaseOrderReceiptData PurchaseOrderReceiptData;

    public PurchaseOrderReceiptData getPurchaseOrderReceiptData() {
        if(PurchaseOrderReceiptData == null)
        {
            return null;
        }
        else
        {
            return this.PurchaseOrderReceiptData;
        }

    }

    public void setPurchaseOrderReceiptData(PurchaseOrderReceiptData purchaseOrderReceiptData) {
        this.PurchaseOrderReceiptData = purchaseOrderReceiptData;
    }

}
