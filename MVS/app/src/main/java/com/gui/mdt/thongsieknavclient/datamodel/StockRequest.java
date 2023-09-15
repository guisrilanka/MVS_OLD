package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by User on 10/24/2017.
 */

public class StockRequest {
    String No;
    String SelltoCustomerNo;
    String SelltoCustomerName;
    String ExternalDocumentNo;
    String SelltoPostCode;
    String SelltoCountryRegionCode;
    String SelltoContact;
    String BilltoCustomerNo;
    String BilltoName;
    String BilltoPostCode;
    String BilltoCountryRegionCode;
    String BilltoContact;
    String ShiptoCode;
    String ShiptoName;
    String ShiptoPostCode;
    String ShiptoCountryRegionCode;
    String ShiptoContact;
    String PostingDate;
    String OrderDate;
    String ShipmentDate;
    String LocationCode;
    String SalespersonCode;
    String DriverCode;
    String Status;


    Float AmountInclVAT;
    Float VATAmount;
    Boolean Transferred;
    String LastTransferredBy;
    String LastTransferredDateTime;
    String CreatedBy;
    String CreatedDateTime;
    String LastModifiedBy;
    String LastModifiedDateTime;
    boolean IsDownloaded;

    //additional fields
    boolean isConfirmedSr;
    String SellToCustomerAddress;
    Integer TotalQty;


    public Float getAmountInclVAT() {
        return AmountInclVAT;
    }

    public void setAmountInclVAT(Float amountInclVAT) {
        AmountInclVAT = amountInclVAT;
    }

    public Float getVATAmount() {
        return VATAmount;
    }

    public void setVATAmount(Float VATAmount) {
        this.VATAmount = VATAmount;
    }

    public Boolean getTransferred() {
        return Transferred;
    }

    public void setTransferred(Boolean transferred) {
        Transferred = transferred;
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

    public boolean isConfirmedSr() {
        return isConfirmedSr;
    }

    public void setConfirmedSr(boolean confirmedSr) {
        isConfirmedSr = confirmedSr;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getSelltoCustomerNo() {
        return SelltoCustomerNo;
    }

    public void setSelltoCustomerNo(String selltoCustomerNo) {
        SelltoCustomerNo = selltoCustomerNo;
    }

    public String getSelltoCustomerName() {
        return SelltoCustomerName;
    }

    public void setSelltoCustomerName(String selltoCustomerName) {
        SelltoCustomerName = selltoCustomerName;
    }

    public String getExternalDocumentNo() {
        return ExternalDocumentNo;
    }

    public void setExternalDocumentNo(String externalDocumentNo) {
        ExternalDocumentNo = externalDocumentNo;
    }

    public String getSelltoPostCode() {
        return SelltoPostCode;
    }

    public void setSelltoPostCode(String selltoPostCode) {
        SelltoPostCode = selltoPostCode;
    }

    public String getSelltoCountryRegionCode() {
        return SelltoCountryRegionCode;
    }

    public void setSelltoCountryRegionCode(String selltoCountryRegionCode) {
        SelltoCountryRegionCode = selltoCountryRegionCode;
    }

    public String getSelltoContact() {
        return SelltoContact;
    }

    public void setSelltoContact(String selltoContact) {
        SelltoContact = selltoContact;
    }

    public String getBilltoCustomerNo() {
        return BilltoCustomerNo;
    }

    public void setBilltoCustomerNo(String billtoCustomerNo) {
        BilltoCustomerNo = billtoCustomerNo;
    }

    public String getBilltoName() {
        return BilltoName;
    }

    public void setBilltoName(String billtoName) {
        BilltoName = billtoName;
    }

    public String getBilltoPostCode() {
        return BilltoPostCode;
    }

    public void setBilltoPostCode(String billtoPostCode) {
        BilltoPostCode = billtoPostCode;
    }

    public String getBilltoCountryRegionCode() {
        return BilltoCountryRegionCode;
    }

    public void setBilltoCountryRegionCode(String billtoCountryRegionCode) {
        BilltoCountryRegionCode = billtoCountryRegionCode;
    }

    public String getBilltoContact() {
        return BilltoContact;
    }

    public void setBilltoContact(String billtoContact) {
        BilltoContact = billtoContact;
    }

    public String getShiptoCode() {
        return ShiptoCode;
    }

    public void setShiptoCode(String shiptoCode) {
        ShiptoCode = shiptoCode;
    }

    public String getShiptoName() {
        return ShiptoName;
    }

    public void setShiptoName(String shiptoName) {
        ShiptoName = shiptoName;
    }

    public String getShiptoPostCode() {
        return ShiptoPostCode;
    }

    public void setShiptoPostCode(String shiptoPostCode) {
        ShiptoPostCode = shiptoPostCode;
    }

    public String getShiptoCountryRegionCode() {
        return ShiptoCountryRegionCode;
    }

    public void setShiptoCountryRegionCode(String shiptoCountryRegionCode) {
        ShiptoCountryRegionCode = shiptoCountryRegionCode;
    }

    public String getShiptoContact() {
        return ShiptoContact;
    }

    public void setShiptoContact(String shiptoContact) {
        ShiptoContact = shiptoContact;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String postingDate) {
        PostingDate = postingDate;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getShipmentDate() {
        return ShipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        ShipmentDate = shipmentDate;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getSalespersonCode() {
        return SalespersonCode;
    }

    public void setSalespersonCode(String salespersonCode) {
        SalespersonCode = salespersonCode;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getSellToCustomerAddress() {
        return SellToCustomerAddress;
    }

    public void setSellToCustomerAddress(String sellToCustomerAddress) {
        SellToCustomerAddress = sellToCustomerAddress;
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

    public boolean isDownloaded() {
        return IsDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        IsDownloaded = downloaded;
    }

    public Integer getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(Integer totalQty) {
        TotalQty = totalQty;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static StockRequest fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, StockRequest.class);
    }
}
