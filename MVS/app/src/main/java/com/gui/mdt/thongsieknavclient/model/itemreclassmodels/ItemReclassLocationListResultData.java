package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassLocationListResultData {

    private int ReclassHeaderLineNo;
    private String DocNo;
    private String PostDate;
    private String LocationCode;
    private String NewLocationCode;
    private String Creator;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private String BinCode;
    private String NewBinCode;
    private boolean ShowBinCode;

    public ItemReclassLocationListResultData(int reclassHeaderLineNo, String docNo, String postDate, String locationCode, String newLocationCode, String creator, boolean isItemTrackingRequired, boolean isItemLotTrackingRequired, boolean isItemExpireDateRequired, boolean isItemProductionDateRequired, boolean isItemSNRequired, String binCode, String newBinCode, boolean showBinCode) {
        ReclassHeaderLineNo = reclassHeaderLineNo;
        DocNo = docNo;
        PostDate = postDate;
        LocationCode = locationCode;
        NewLocationCode = newLocationCode;
        Creator = creator;
        IsItemTrackingRequired = isItemTrackingRequired;
        IsItemLotTrackingRequired = isItemLotTrackingRequired;
        IsItemExpireDateRequired = isItemExpireDateRequired;
        IsItemProductionDateRequired = isItemProductionDateRequired;
        IsItemSNRequired = isItemSNRequired;
        BinCode = binCode;
        NewBinCode = newBinCode;
        ShowBinCode = showBinCode;
    }

    public int getReclassHeaderLineNo() {
        return ReclassHeaderLineNo;
    }

    public void setReclassHeaderLineNo(int reclassHeaderLineNo) {
        ReclassHeaderLineNo = reclassHeaderLineNo;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getPostDate() {
        return PostDate;
    }

    public void setPostDate(String postDate) {
        PostDate = postDate;
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

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
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

    public boolean isShowBinCode() {
        return ShowBinCode;
    }

    public void setShowBinCode(boolean showBinCode) {
        ShowBinCode = showBinCode;
    }
}
