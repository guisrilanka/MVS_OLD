package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;



import com.gui.mdt.thongsieknavclient.model.BaseResult;
import java.util.List;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassLineLotEntriesResult extends BaseResult {
    private List<ItemReclassLineLotEntriesResultData> ItemReclassLineLotEntriesResultData;

    public List<ItemReclassLineLotEntriesResultData> getmItemReclassLineLotEntriesResultData() {

        if(ItemReclassLineLotEntriesResultData == null)
        {
            return null;
        }else {
            return ItemReclassLineLotEntriesResultData;
        }
    }

    public void setmItemReclassLineLotEntriesResultData(List<ItemReclassLineLotEntriesResultData> mItemReclassLineLotEntriesResultData) {
        ItemReclassLineLotEntriesResultData = mItemReclassLineLotEntriesResultData;
    }
}
