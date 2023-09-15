package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

import java.util.List;

/**
 * Created by user on 12/29/2016.
 */

public class ItemReclassData {

    private int ReclassHeaderLineNo;
    private String DocumentNo;
    private String PostingDate;
    private String LocationCode;
    private String NewLocationCode;
    private String CreatedBy;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;

    private String BinCode;
    private String NewBinCode;
    private boolean ShowBinCode;
    private boolean ShowNewBinCode;

    private List<ItemReclassLineData> LinesData;

    public List<ItemReclassLineData> getItemReclassLineDatasList() {
        if (LinesData == null) {
            return null;
        } else {
            return this.LinesData;
        }
    }

    public void setItemReclassLineDatasList(List<ItemReclassLineData> itemReclassLineDatas) {
        this.LinesData = itemReclassLineDatas;
    }

    public boolean isShowNewBinCode() {
        return ShowNewBinCode;
    }

    public void setShowNewBinCode(boolean showNewBinCode) {
        ShowNewBinCode = showNewBinCode;
    }

    public int getReclassHeaderLineNo() {
        return ReclassHeaderLineNo;
    }

    public void setReclassHeaderLineNo(int reclassHeaderLineNo) {
        ReclassHeaderLineNo = reclassHeaderLineNo;
    }

    public String getBinCode() {
        return BinCode;
    }

    public void setBinCode(String binCode) {
        BinCode = binCode;
    }

    public String getNewBinCode() {
        return NewBinCode;
    }

    public void setNewBinCode(String newBinCode) {
        NewBinCode = newBinCode;
    }

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String postingDate) {
        PostingDate = postingDate;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getNewLocationCode() {
        return NewLocationCode;
    }

    public void setNewLocationCode(String newLocationCode) {
        NewLocationCode = newLocationCode;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public boolean isItemTrackingRequired() {
        return IsItemTrackingRequired;
    }

    public void setItemTrackingRequired(boolean itemTrackingRequired) {
        IsItemTrackingRequired = itemTrackingRequired;
    }

    public boolean isItemLotTrackingRequired() {
        return IsItemLotTrackingRequired;
    }

    public void setItemLotTrackingRequired(boolean itemLotTrackingRequired) {
        IsItemLotTrackingRequired = itemLotTrackingRequired;
    }

    public boolean isItemExpireDateRequired() {
        return IsItemExpireDateRequired;
    }

    public void setItemExpireDateRequired(boolean itemExpireDateRequired) {
        IsItemExpireDateRequired = itemExpireDateRequired;
    }

    public boolean isItemProductionDateRequired() {
        return IsItemProductionDateRequired;
    }

    public void setItemProductionDateRequired(boolean itemProductionDateRequired) {
        IsItemProductionDateRequired = itemProductionDateRequired;
    }

    public boolean isItemSNRequired() {
        return IsItemSNRequired;
    }

    public void setItemSNRequired(boolean itemSNRequired) {
        IsItemSNRequired = itemSNRequired;
    }

    public boolean isShowBinCode() {
        return ShowBinCode;
    }

    public void setShowBinCode(boolean showBinCode) {
        ShowBinCode = showBinCode;
    }
}
