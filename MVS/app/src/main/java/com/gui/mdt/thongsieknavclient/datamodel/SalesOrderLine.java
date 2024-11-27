package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by nelin_000 on 07/28/2017.
 */

public class SalesOrderLine {

    /*Integer id;
    String key;
    String documentNo ;
    String lineNumber;
    String itemNumber ;
    String locationCode;
    String uomNumber;
    Float quantity ;
    Float e_unitPrice;
    Float lineAmtbeforeDiscount;
    Float discountAmt;
    Float lineAmtafterDiscount ;
    String lineComment;
    String driverCode ;
    String createdBy;
    String createdDateTime;
    String lastModifiedBy;
    String lastModifiedDateTime;
    boolean transferred;
    String lastTransferredBy;
    String lastTransferredDateTime ;
    String navDocumentNo;
    String navDocumentLineNo;*/

    Integer id;
    String Key;
    Integer Type;
    String No;
    String CrossReferenceNo;
    String DriverCode;
    String Description;
    Float Quantity;
    String UnitofMeasure;
    boolean SalesPriceExist;
    float UnitPrice;
    float LineAmount;
    boolean SalesLineDiscExists;
    String LineDiscountPercent;
    Float LineDiscountAmount;
    Float QtytoInvoice;
    Float QuantityInvoiced;
    String DocumentNo;
    String LineNo;
    Float TotalAmountExclVAT;
    Float TotalVATAmount;
    Float TotalAmountInclVAT;
    boolean transferred;
    String lastTransferredBy;
    String lastTransferredDateTime;
    private String taxPercentage;
    Float ExchangedQty;

    String itemCrossReferenceNo;
    String itemCrossReferenceDescription;

    boolean isExchangeItem;


    public SalesOrderLine(){}

    public SalesOrderLine(SalesOrderLine s)
    {
        this.id=s.getId();
        this.Key=s.getKey();
        this.Type=s.getType();
        this.No=s.getNo();
        this.CrossReferenceNo=s.getCrossReferenceNo();
        this.DriverCode=s.getDriverCode();
        this.Description=s.getDescription();
        this.Quantity=s.getQuantity();
        this.UnitofMeasure=s.getUnitofMeasure();
        this.SalesPriceExist=s.isSalesPriceExist();
        this.UnitPrice=s.getUnitPrice();
        this.LineAmount=s.getLineAmount();
        this.SalesLineDiscExists=s.isSalesLineDiscExists();
        this.LineDiscountPercent=s.getLineDiscountPercent();
        this.LineDiscountAmount=s.getLineDiscountAmount();
        this.QtytoInvoice=s.getQtytoInvoice();
        this.QuantityInvoiced=s.getQuantityInvoiced();
        this.DocumentNo=s.getDocumentNo();
        this.LineNo=s.getLineNo();
        this.TotalAmountExclVAT=s.getTotalAmountExclVAT();
        this.TotalVATAmount=s.getTotalVATAmount();
        this.TotalAmountInclVAT=s.getTotalAmountInclVAT();
        this.transferred=s.isTransferred();
        this.lastTransferredBy=s.getLastTransferredBy();
        this.lastTransferredDateTime=s.getLastTransferredDateTime();
        this.taxPercentage=s.getTaxPercentage();
        this.ExchangedQty=s.getExchangedQty();
        this.isExchangeItem= s.isExchangeItem();
        this.itemCrossReferenceNo = s.getItemCrossReferenceNo();
        this.itemCrossReferenceDescription = s.getItemCrossReferenceDescription();
    }

    public boolean isExchangeItem() {
        return isExchangeItem;
    }

    public void setExchangeItem(boolean exchangeItem) {
        isExchangeItem = exchangeItem;
    }


    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getCrossReferenceNo() {
        return CrossReferenceNo;
    }

    public void setCrossReferenceNo(String crossReferenceNo) {
        CrossReferenceNo = crossReferenceNo;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Float getQuantity() {
        return Quantity;
    }

    public void setQuantity(Float quantity) {
        Quantity = quantity;
    }

    public String getUnitofMeasure() {
        return UnitofMeasure;
    }

    public void setUnitofMeasure(String unitofMeasure) {
        UnitofMeasure = unitofMeasure;
    }

    public boolean isSalesPriceExist() {
        return SalesPriceExist;
    }

    public void setSalesPriceExist(boolean salesPriceExist) {
        SalesPriceExist = salesPriceExist;
    }

    public float getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        UnitPrice = unitPrice;
    }

    public float getLineAmount() {
        return LineAmount;
    }

    public void setLineAmount(float lineAmount) {
        LineAmount = lineAmount;
    }

    public boolean isSalesLineDiscExists() {
        return SalesLineDiscExists;
    }

    public void setSalesLineDiscExists(boolean salesLineDiscExists) {
        SalesLineDiscExists = salesLineDiscExists;
    }

    public String getLineDiscountPercent() {
        return LineDiscountPercent;
    }

    public void setLineDiscountPercent(String lineDiscountPercent) {
        LineDiscountPercent = lineDiscountPercent;
    }

    public Float getLineDiscountAmount() {
        return LineDiscountAmount;
    }

    public void setLineDiscountAmount(Float lineDiscountAmount) {
        LineDiscountAmount = lineDiscountAmount;
    }

    public Float getQtytoInvoice() {
        return QtytoInvoice;
    }

    public void setQtytoInvoice(Float qtytoInvoice) {
        QtytoInvoice = qtytoInvoice;
    }

    public Float getQuantityInvoiced() {
        return QuantityInvoiced;
    }

    public void setQuantityInvoiced(Float quantityInvoiced) {
        QuantityInvoiced = quantityInvoiced;
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

    public Float getTotalAmountExclVAT() {
        return TotalAmountExclVAT;
    }

    public void setTotalAmountExclVAT(Float totalAmountExclVAT) {
        TotalAmountExclVAT = totalAmountExclVAT;
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

    public Float getExchangedQty() {
        return ExchangedQty;
    }

    public void setExchangedQty(Float exchangedQty) {
        ExchangedQty = exchangedQty;
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

    public static SalesOrderLine fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SalesOrderLine.class);
    }
}
