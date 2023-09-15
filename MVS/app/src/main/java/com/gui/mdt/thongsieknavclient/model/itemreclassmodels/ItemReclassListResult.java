package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;



import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by user on 1/4/2017.
 */

public class ItemReclassListResult extends BaseResult {

    private List<ItemReclassListResultData> ItemReclassListResultData;

    public List<ItemReclassListResultData> getItemReclassListResultData() {

        if(ItemReclassListResultData == null)
        {
            return null;
        }else  {
            return this.ItemReclassListResultData;
        }
    }

    public void setItemReclassListResultData(List<ItemReclassListResultData> itemReclassListResultData) {
        this.ItemReclassListResultData = itemReclassListResultData;
    }

//    public List<ItemReclassLocationListResultData> getItemReclassificationListResultList() {
//        if(ItemReclassListResultData == null)
//        {
//            return null;
//        }
//        else
//        {
//            return this.ItemReclassListResultData;
//        }
//
//    }
//
//    public void setItemReclassificationListResultList(List<ItemReclassLocationListResultData> ItemReclassLocationListResultData) {
//        this.ItemReclassListResultData = ItemReclassLocationListResultData;
//    }
}
