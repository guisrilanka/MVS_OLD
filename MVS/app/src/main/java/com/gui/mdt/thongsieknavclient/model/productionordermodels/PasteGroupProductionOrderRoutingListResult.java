package com.gui.mdt.thongsieknavclient.model.productionordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 15/09/16.
 */
public class PasteGroupProductionOrderRoutingListResult extends BaseResult {
    private List<PasteGroupProductionOrderRoutingListResultData> PasteGroupProductionOrderRouteResultData;

    public List<PasteGroupProductionOrderRoutingListResultData> getPasteGroupProductionOrderRoutingListResultList() {
        if(PasteGroupProductionOrderRouteResultData == null)
        {
            return null;
        }
        else
        {
            return this.PasteGroupProductionOrderRouteResultData;
        }

    }

    public void setPasteGroupProductionOrderRoutingListResultList(List<PasteGroupProductionOrderRoutingListResultData> pasteGroupProductionOrderRoutingListResultData) {
        this.PasteGroupProductionOrderRouteResultData = pasteGroupProductionOrderRoutingListResultData;
    }
}
