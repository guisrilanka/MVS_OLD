package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by GUI-NB03 on 2017-09-18.
 */

public class StockTransferRequest {

    String key;
    String No;
    String DriverCode;
    String StockTransferDate;
    String DocumentDate;
    String StockTransferType;//Stock Transfer In or Stock Transfer Out
    String TotalQuantity;
    String NoOfItems;
    String Status;
    String Transferred;
    String CreatedBy;
    String CreatedDateTime;
    String LastModifiedBy;
    String LastModifiedDateTime;
    String LastTransferredBy;
    String LastTransferredDateTime;

    //helper props
    boolean isConfirmedSt;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getStockTransferDate() {
        return StockTransferDate;
    }

    public void setStockTransferDate(String stockTransferDate) {
        StockTransferDate = stockTransferDate;
    }

    public String getDocumentDate() {
        return DocumentDate;
    }

    public void setDocumentDate(String documentDate) {
        DocumentDate = documentDate;
    }

    public String getStockTransferType() {
        return StockTransferType;
    }

    public void setStockTransferType(String stockTransferType) {
        StockTransferType = stockTransferType;
    }

    public String getTotalQuantity() {
        return TotalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        TotalQuantity = totalQuantity;
    }

    public String getNoOfItems() {
        return NoOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        NoOfItems = noOfItems;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTransferred() {
        return Transferred;
    }

    public void setTransferred(String transferred) {
        Transferred = transferred;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDateTime() {
        return CreatedDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        CreatedDateTime = createdDateTime;
    }

    public String getLastModifiedBy() {
        return LastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        LastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedDateTime() {
        return LastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        LastModifiedDateTime = lastModifiedDateTime;
    }

    public String getLastTransferredBy() {
        return LastTransferredBy;
    }

    public void setLastTransferredBy(String lastTransferredBy) {
        LastTransferredBy = lastTransferredBy;
    }

    public String getLastTransferredDateTime() {
        return LastTransferredDateTime;
    }

    public void setLastTransferredDateTime(String lastTransferredDateTime) {
        LastTransferredDateTime = lastTransferredDateTime;
    }

    public boolean isConfirmedSt() {
        return isConfirmedSt;
    }

    public void setConfirmedSt(boolean confirmedSt) {
        isConfirmedSt = confirmedSt;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static StockTransferRequest fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, StockTransferRequest.class);
    }
}
