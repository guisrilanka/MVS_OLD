package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by User on 10/24/2017.
 */

public class StockRequestLine {
    String Key;
    String SelltoCustomerNo;
    String ShiptoCode;
    String SalespersonCode;
    String OrderDate;
    String ShipmentDate;
    String DueDate;
    String grsSalesHeaderExternalDocumentNo;
    String CreatedBy;
    String CreatedDataTime;
    String LastModifiedBy;
    String LastModifiedDateTime;
    String DocumentNo;
    String LineNo;
    String Type;
    String ItemNo;
    String Description;
    String LocationCode;
    Float Quantity;
    String UnitofMeasureCode;
    Float UnitPrice;
    Float Amount;
    Float LineAmount;
    Float LineDiscountAmount;
    String LineDiscountPercent;
    String DriverCode;

    Float TotalVATAmount;
    Float TotalAmountInclVAT;
    String TaxPercentage;

    String itemCrossReferenceNo;
    String itemCrossReferenceDescription;


    public StockRequestLine(){};

    public StockRequestLine(StockRequestLine srl) {
        this.Key = srl.getKey();
        this.SelltoCustomerNo = srl.getSelltoCustomerNo();
        this.ShiptoCode = srl.getShiptoCode();
        this.SalespersonCode = srl.getSalespersonCode();
        this.OrderDate = srl.getOrderDate();
        this.ShipmentDate = srl.getShipmentDate();
        this.DueDate = srl.getDueDate();
        this.grsSalesHeaderExternalDocumentNo = srl.getGrsSalesHeaderExternalDocumentNo();
        this.CreatedBy = srl.getCreatedBy();
        this.CreatedDataTime = srl.getCreatedDataTime();
        this.LastModifiedBy = srl.getLastModifiedBy();
        this.LastModifiedDateTime = srl.getLastModifiedDateTime();
        this.DocumentNo = srl.getDocumentNo();
        this.LineNo = srl.getLineNo();
        this.Type = srl.getType();
        this.ItemNo = srl.getItemNo();
        this.Description = srl.getDescription();
        this.LocationCode = srl.getLocationCode();
        this.Quantity = srl.getQuantity();
        this.UnitofMeasureCode = srl.getUnitofMeasureCode();
        this.UnitPrice = srl.getUnitPrice();
        this.Amount = srl.getAmount();
        this.LineAmount = srl.getLineAmount();
        this.LineDiscountAmount = srl.getLineDiscountAmount();
        this.LineDiscountPercent = srl.getLineDiscountPercent();
        this.DriverCode = srl.getDriverCode();

        this.TotalVATAmount = srl.getTotalVATAmount();
        this.TotalAmountInclVAT = srl.getTotalAmountInclVAT();
        this.TaxPercentage = srl.getTaxPercentage();
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getSelltoCustomerNo() {
        return SelltoCustomerNo;
    }

    public void setSelltoCustomerNo(String selltoCustomerNo) {
        SelltoCustomerNo = selltoCustomerNo;
    }

    public String getShiptoCode() {
        return ShiptoCode;
    }

    public void setShiptoCode(String shiptoCode) {
        ShiptoCode = shiptoCode;
    }

    public String getSalespersonCode() {
        return SalespersonCode;
    }

    public void setSalespersonCode(String salespersonCode) {
        SalespersonCode = salespersonCode;
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

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getGrsSalesHeaderExternalDocumentNo() {
        return grsSalesHeaderExternalDocumentNo;
    }

    public void setGrsSalesHeaderExternalDocumentNo(String grsSalesHeaderExternalDocumentNo) {
        this.grsSalesHeaderExternalDocumentNo = grsSalesHeaderExternalDocumentNo;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDataTime() {
        return CreatedDataTime;
    }

    public void setCreatedDataTime(String createdDataTime) {
        CreatedDataTime = createdDataTime;
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

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getLineNo() {
        return LineNo;
    }

    public void setLineNo(String lineNo) {
        LineNo = lineNo;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public Float getQuantity() {
        return Quantity;
    }

    public void setQuantity(Float quantity) {
        Quantity = quantity;
    }

    public String getUnitofMeasureCode() {
        return UnitofMeasureCode;
    }

    public void setUnitofMeasureCode(String unitofMeasureCode) {
        UnitofMeasureCode = unitofMeasureCode;
    }

    public Float getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(Float unitPrice) {
        UnitPrice = unitPrice;
    }

    public Float getAmount() {
        return Amount;
    }

    public void setAmount(Float amount) {
        Amount = amount;
    }

    public Float getLineAmount() {
        return LineAmount;
    }

    public void setLineAmount(Float lineAmount) {
        LineAmount = lineAmount;
    }

    public Float getLineDiscountAmount() {
        return LineDiscountAmount;
    }

    public void setLineDiscountAmount(Float lineDiscountAmount) {
        LineDiscountAmount = lineDiscountAmount;
    }

    public String getLineDiscountPercent() {
        return LineDiscountPercent;
    }

    public void setLineDiscountPercent(String lineDiscountPercent) {
        LineDiscountPercent = lineDiscountPercent;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public Float getTotalVATAmount() {
        return TotalVATAmount;
    }

    public void setTotalVATAmount(Float totalVATAmount) {
        TotalVATAmount = totalVATAmount;
    }

    public Float getTotalAmountInclVAT() {
        return TotalAmountInclVAT;
    }

    public void setTotalAmountInclVAT(Float totalAmountInclVAT) {
        TotalAmountInclVAT = totalAmountInclVAT;
    }

    public String getTaxPercentage() {
        return TaxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        TaxPercentage = taxPercentage;
    }

    public String getItemCrossReferenceNo() {
        return itemCrossReferenceNo;
    }

    public void setItemCrossReferenceNo(String itemCrossReferenceNo) {
        this.itemCrossReferenceNo = itemCrossReferenceNo;
    }

    public String getItemCrossReferenceDescription() {
        return itemCrossReferenceDescription;
    }

    public void setItemCrossReferenceDescription(String itemCrossReferenceDescription) {
        this.itemCrossReferenceDescription = itemCrossReferenceDescription;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static StockRequestLine fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, StockRequestLine.class);
    }
}
