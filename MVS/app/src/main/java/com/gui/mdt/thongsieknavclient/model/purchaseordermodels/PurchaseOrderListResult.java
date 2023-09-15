package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 20/07/16.
 */
public class PurchaseOrderListResult extends BaseResult {
    private List<PurchaseOrderListItem> PurchaseOrderListResultData;

    public List<PurchaseOrderListItem> getPurchaseOrderList() {
        if(PurchaseOrderListResultData == null)
        {
            return null;
        }
        else
        {
            return this.PurchaseOrderListResultData;
        }

    }

    public void setPurchaseOrderList(List<PurchaseOrderListItem> purchaseOrderList) {
        this.PurchaseOrderListResultData = purchaseOrderList;
    }

}
