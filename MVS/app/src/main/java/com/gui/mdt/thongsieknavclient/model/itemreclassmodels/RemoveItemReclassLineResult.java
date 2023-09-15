package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

/**
 * Created by user on 2/22/2017.
 */

public class RemoveItemReclassLineResult extends BaseResult{
    private int ReclassHeaderLineNo;

    public int getReclassHeaderLineNo() {
        return ReclassHeaderLineNo;
    }

    public void setReclassHeaderLineNo(int reclassHeaderLineNo) {
        ReclassHeaderLineNo = reclassHeaderLineNo;
    }
}
