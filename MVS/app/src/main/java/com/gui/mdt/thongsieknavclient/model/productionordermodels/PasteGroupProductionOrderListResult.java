package com.gui.mdt.thongsieknavclient.model.productionordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 13/09/16.
 */
public class PasteGroupProductionOrderListResult extends BaseResult {
    private List<PasteGroupProductionOrderListResultData> PasteGroupProductionOrderListResultData;

    public List<PasteGroupProductionOrderListResultData> getProductionOrderListResultList() {
        if(PasteGroupProductionOrderListResultData == null)
        {
            return null;
        }
        else
        {
            return this.PasteGroupProductionOrderListResultData;
        }

    }

    public void setProductionOrderListResultList(List<PasteGroupProductionOrderListResultData> pasteGroupProductionOrderListResultData) {
        this.PasteGroupProductionOrderListResultData = pasteGroupProductionOrderListResultData;
    }
}
