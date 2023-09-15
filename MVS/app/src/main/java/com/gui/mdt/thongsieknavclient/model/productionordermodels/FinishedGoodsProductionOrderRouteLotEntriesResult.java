package com.gui.mdt.thongsieknavclient.model.productionordermodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by yeqim_000 on 06/10/16.
 */
public class FinishedGoodsProductionOrderRouteLotEntriesResult extends BaseResult{
    private List<FinishedGoodsProductionOrderRouteLotEntriesResultData> FinishedGoodsProductionOrderRouteLotEntriesResultData;

    public List<FinishedGoodsProductionOrderRouteLotEntriesResultData> getFinishedGoodsProductionOrderRouteLotEntriesResultData() {
        if(FinishedGoodsProductionOrderRouteLotEntriesResultData == null)
        {
            return null;
        }
        else
        {
            return this.FinishedGoodsProductionOrderRouteLotEntriesResultData;
        }

    }

    public void setFinishedGoodsProductionOrderRouteLotEntriesResultData(List<FinishedGoodsProductionOrderRouteLotEntriesResultData> finishedGoodsProductionOrderRouteLotEntriesResultData) {
        this.FinishedGoodsProductionOrderRouteLotEntriesResultData = finishedGoodsProductionOrderRouteLotEntriesResultData;
    }
}
