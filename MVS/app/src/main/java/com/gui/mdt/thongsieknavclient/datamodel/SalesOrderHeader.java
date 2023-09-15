package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 07/26/2017.
 */

public class SalesOrderHeader {

    private int id;
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
    private String lastTransferredDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getShipToNumber() {
        return shipToNumber;
    }

    public void setShipToNumber(String shipToNumber) {
        this.shipToNumber = shipToNumber;
    }

    public String getSalepersonNumber() {
        return salepersonNumber;
    }

    public void setSalepersonNumber(String salepersonNumber) {
        this.salepersonNumber = salepersonNumber;
    }

    public Integer getDetailOrdinal() {
        return detailOrdinal;
    }

    public void setDetailOrdinal(Integer detailOrdinal) {
        this.detailOrdinal = detailOrdinal;
    }

    public String getSalesOrderDate() {
        return salesOrderDate;
    }

    public void setSalesOrderDate(String salesOrderDate) {
        this.salesOrderDate = salesOrderDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public Float getTotalAmtafterDiscount() {
        return totalAmtafterDiscount;
    }

    public void setTotalAmtafterDiscount(Float totalAmtafterDiscount) {
        this.totalAmtafterDiscount = totalAmtafterDiscount;
    }

    public Float getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(Float discountAmt) {
        this.discountAmt = discountAmt;
    }

    public Float getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Float taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public Float getTotalAmtafterTax() {
        return totalAmtafterTax;
    }

    public void setTotalAmtafterTax(Float totalAmtafterTax) {
        this.totalAmtafterTax = totalAmtafterTax;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
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
}
