package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by nelin_000 on 07/26/2017.
 */

public class SalesOrder {

    /*private int id;
    private String key;
    private String salesOrderNo;
    private String customerNumber;
    private String shipToNumber;
    private String salepersonNumber;
    private Integer detailOrdinal;
    private String salesOrderDate;
    private String dueDate;
    private String purchaseOrderNumber;
    private Float totalAmtafterDiscount;
    private Float discountAmt;
    private Float taxPercentage;
    private Float totalAmtafterTax;
    private String driverCode;
    private String comment;
    private String createdBy;
    private String createdDateTime;
    private String lastModifiedBy;
    private String lastModifiedDateTime;
    private boolean transferred;
    private String lastTransferredBy;
    private String lastTransferredDateTime;*/

    int id;
    String Key;
    String No;
    String SelltoCustomerNo;
    String SelltoCustomerName;
    String SelltoAddress;
    String SelltoContactNo;
    String SelltoPostCode;
    String SelltoCity;
    String OrderDate;
    String DocumentDate;
    String RequestedDeliveryDate;
    String ShipmentDate;
    String ExternalDocumentNo;
    String SalespersonCode;
    String DriverCode;
    String Status;
    String CreatedFrom;
    String CreatedBy;
    String CreatedDateTime;
    String LastModifiedBy;
    String LastModifiedDateTime;
    float AmountExcludingVAT;
    float VATAmount;
    float AmountIncludingVAT;
    String DueDate;
    String comment;
    boolean transferred;
    String lastTransferredBy;
    String lastTransferredDateTime;
    String Comments;
    boolean Delivered;
    String module;
    int documentType;
    String SIDate;
    String SINo;
    String vatPercentage;


    public String getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(String vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    boolean IsDownloaded;
    boolean IsDeleted;

    //helper props
    boolean isConfirmedSo;
    Integer TotalBillQty;

    List<SalesOrderLine> lineItems;

    public List<SalesOrderLine> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<SalesOrderLine> lineItems) {
        this.lineItems = lineItems;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }
    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
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

    public String getSelltoAddress() {
        return SelltoAddress;
    }

    public void setSelltoAddress(String selltoAddress) {
        SelltoAddress = selltoAddress;
    }

    public String getSelltoContactNo() {
        return SelltoContactNo;
    }

    public void setSelltoContactNo(String selltoContactNo) {
        SelltoContactNo = selltoContactNo;
    }

    public String getSelltoPostCode() {
        return SelltoPostCode;
    }

    public void setSelltoPostCode(String selltoPostCode) {
        SelltoPostCode = selltoPostCode;
    }

    public String getSelltoCity() {
        return SelltoCity;
    }

    public void setSelltoCity(String selltoCity) {
        SelltoCity = selltoCity;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getDocumentDate() {
        return DocumentDate;
    }

    public void setDocumentDate(String documentDate) {
        DocumentDate = documentDate;
    }

    public String getRequestedDeliveryDate() {
        return RequestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(String requestedDeliveryDate) {
        RequestedDeliveryDate = requestedDeliveryDate;
    }

    public String getShipmentDate() {
        return ShipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        ShipmentDate = shipmentDate;
    }

    public String getExternalDocumentNo() {
        return ExternalDocumentNo;
    }

    public void setExternalDocumentNo(String externalDocumentNo) {
        ExternalDocumentNo = externalDocumentNo;
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

    public String getCreatedFrom() {
        return CreatedFrom;
    }

    public void setCreatedFrom(String createdFrom) {
        CreatedFrom = createdFrom;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String stringCreatedBy) {
        CreatedBy = stringCreatedBy;
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

    public float getAmountExcludingVAT() {
        return AmountExcludingVAT;
    }

    public void setAmountExcludingVAT(float amountExcludingVAT) {
        AmountExcludingVAT = amountExcludingVAT;
    }

    public float getVATAmount() {
        return VATAmount;
    }

    public void setVATAmount(float VATAmount) {
        this.VATAmount = VATAmount;
    }

    public float getAmountIncludingVAT() {
        return AmountIncludingVAT;
    }

    public void setAmountIncludingVAT(float amountIncludingVAT) {
        AmountIncludingVAT = amountIncludingVAT;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isTransferred() {
        return transferred;
    }

    public void setTransferred(boolean transferred) {
        this.transferred = transferred;
    }

    public String getLastTransferredBy() {
        return lastTransferredBy;
    }

    public void setLastTransferredBy(String lastTransferredBy) {
        this.lastTransferredBy = lastTransferredBy;
    }

    public String getLastTransferredDateTime() {
        return lastTransferredDateTime;
    }

    public void setLastTransferredDateTime(String lastTransferredDateTime) {
        this.lastTransferredDateTime = lastTransferredDateTime;
    }

    public boolean isDelivered() {
        return Delivered;
    }

    public void setDelivered(boolean delivered) {
        Delivered = delivered;
    }

    //helper props
    public boolean isConfirmedSo() {
        return isConfirmedSo;
    }

    public void setConfirmedSo(boolean confirmedSo) {
        isConfirmedSo = confirmedSo;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSIDate() {
        return SIDate;
    }

    public void setSIDate(String SIDate) {
        this.SIDate = SIDate;
    }

    public boolean isDownloaded() {
        return IsDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        IsDownloaded = downloaded;
    }

    public String getSINo() {
        return SINo;
    }

    public void setSINo(String SINo) {
        this.SINo = SINo;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public Integer getTotalBillQty() {
        return TotalBillQty;
    }

    public void setTotalBillQty(Integer totalBillQty) {
        TotalBillQty = totalBillQty;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SalesOrder fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SalesOrder.class);
    }
}
