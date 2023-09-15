package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/29/2016.
 */

public class QueryItemReclassItemResult{

    private String UserCompany;
    private String UserName;
    private String Password;

    private String ItemNo;
    private String SourceID;
    private String BatchName;
    private int SourceSubType;
    private int SourceType;
    private int SourceRefNo;
    private int SourceProductionOrderLine;
    private boolean IsItemTrackingRequired;
    private boolean IsItemLotTrackingRequired;
    private boolean IsItemExpireDateRequired;
    private boolean IsItemProductionDateRequired;
    private boolean IsItemSNRequired;
    private boolean IsItemLotNoAutoAssigned;

    public QueryItemReclassItemResult(String userCompany, String userName, String password, String itemNo, String sourceID, String batchName, int sourceSubType, int sourceType, int sourceRefNo, int sourceProductionOrderLine, boolean isItemTrackingRequired, boolean isItemLotTrackingRequired, boolean isItemExpireDateRequired, boolean isItemProductionDateRequired, boolean isItemSNRequired, boolean isItemLotNoAutoAssigned) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        ItemNo = itemNo;
        SourceID = sourceID;
        BatchName = batchName;
        SourceSubType = sourceSubType;
        SourceType = sourceType;
        SourceRefNo = sourceRefNo;
        SourceProductionOrderLine = sourceProductionOrderLine;
        IsItemTrackingRequired = isItemTrackingRequired;
        IsItemLotTrackingRequired = isItemLotTrackingRequired;
        IsItemExpireDateRequired = isItemExpireDateRequired;
        IsItemProductionDateRequired = isItemProductionDateRequired;
        IsItemSNRequired = isItemSNRequired;
        IsItemLotNoAutoAssigned = isItemLotNoAutoAssigned;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getSourceID() {
        return SourceID;
    }

    public void setSourceID(String sourceID) {
        SourceID = sourceID;
    }

    public String getBatchName() {
        return BatchName;
    }

    public void setBatchName(String batchName) {
        BatchName = batchName;
    }

    public int getSourceSubType() {
        return SourceSubType;
    }

    public void setSourceSubType(int sourceSubType) {
        SourceSubType = sourceSubType;
    }

    public int getSourceType() {
        return SourceType;
    }

    public void setSourceType(int sourceType) {
        SourceType = sourceType;
    }

    public int getSourceRefNo() {
        return SourceRefNo;
    }

    public void setSourceRefNo(int sourceRefNo) {
        SourceRefNo = sourceRefNo;
    }

    public int getSourceProductionOrderLine() {
        return SourceProductionOrderLine;
    }

    public void setSourceProductionOrderLine(int sourceProductionOrderLine) {
        SourceProductionOrderLine = sourceProductionOrderLine;
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

    public boolean isItemLotNoAutoAssigned() {
        return IsItemLotNoAutoAssigned;
    }

    public void setItemLotNoAutoAssigned(boolean itemLotNoAutoAssigned) {
        IsItemLotNoAutoAssigned = itemLotNoAutoAssigned;
    }
}
