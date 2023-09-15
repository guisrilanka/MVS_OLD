package com.gui.mdt.thongsieknavclient.model.productionordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 28/09/16.
 */
public class FinishedGoodsProductionOrderListResult extends BaseResult {
    private List<FinishedGoodsProductionOrderListResultData> PasteGroupProductionOrderListResultData;

    public List<FinishedGoodsProductionOrderListResultData> getProductionOrderListResultData() {
        if(PasteGroupProductionOrderListResultData == null)
        {
            return null;
        }
        else
        {
            return this.PasteGroupProductionOrderListResultData;
        }

    }

    public void setProductionOrderListResultData(List<FinishedGoodsProductionOrderListResultData> finishedGoodsProductionOrderListResultData) {
        this.PasteGroupProductionOrderListResultData = finishedGoodsProductionOrderListResultData;
    }
}
