package com.gui.mdt.thongsieknavclient.model.productionordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 28/09/16.
 */
public class PasteGroupProductionOrderLineLotEntriesResult extends BaseResult{

    private List<ProductionOrderLineLotEntriesResultData> PasteGroupProductionOrderLotEntriesResultData;

    public List<ProductionOrderLineLotEntriesResultData> getPasteGroupProductionOrderLineLotEntriesResultData() {
        if(PasteGroupProductionOrderLotEntriesResultData == null)
        {
            return null;
        }
        else
        {
            return this.PasteGroupProductionOrderLotEntriesResultData;
        }

    }

    public void setPasteGroupProductionOrderLineLotEntriesResultData(List<ProductionOrderLineLotEntriesResultData> productionOrderLineLotEntriesResultData) {
        this.PasteGroupProductionOrderLotEntriesResultData = productionOrderLineLotEntriesResultData;
    }
}
