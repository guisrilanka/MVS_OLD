package com.gui.mdt.thongsieknavclient.model.productionordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class PasteGroupProductionOrderBomListResult extends BaseResult {
    private List<ProductionOrderBomListResultData> PasteGroupProductionOrderBomResultData;

    public List<ProductionOrderBomListResultData> getProductionOrderBomResultList() {
        if(PasteGroupProductionOrderBomResultData == null)
        {
            return null;
        }
        else
        {
            return this.PasteGroupProductionOrderBomResultData;
        }

    }

    public void setProductionOrderBomResultList(List<ProductionOrderBomListResultData> productionOrderBomListResultData) {
        this.PasteGroupProductionOrderBomResultData = productionOrderBomListResultData;
    }
}
