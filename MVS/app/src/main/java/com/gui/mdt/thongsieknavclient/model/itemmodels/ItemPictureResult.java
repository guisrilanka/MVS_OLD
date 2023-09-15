package com.gui.mdt.thongsieknavclient.model.itemmodels;



import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by user on 12/30/2016.
 */

public class ItemPictureResult extends BaseResult {
    private List<ItemPictureResultData> ItemPictureResultData;

    public List<ItemPictureResultData> getItemPictureResultData() {
        if (ItemPictureResultData == null) {
            return null;
        } else {
            return this.ItemPictureResultData;
        }
    }

    public void setItemPictureResultData(List<ItemPictureResultData> itemPictureResultData) {
        ItemPictureResultData = itemPictureResultData;
    }
}
