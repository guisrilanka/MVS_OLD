package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by User on 8/28/2017.
 */

public class PaymentHeader {

    String paymentNo;
    String moduleId;
    String customerNo;
    String customerName;
    String driverCode;
    String salesPersonCode;
    String totalAmount;
    String createdBy;
    String createdDateTime;
    String lastModifiedBy;
    String lastModifiedDateTime;
    boolean transferred;
    String paymentDate;
    String lastTransferredBy;
    String lastTransferredDateTime;
    String externalDocumentNo;
    String status;
    String referenceAmount;

    boolean isChecked;
    boolean isConfirmed;

    String cashAmount;
    String chequeAmount;

    public String getExternalDocumentNo() {
        return externalDocumentNo;
    }

    public void setExternalDocumentNo(String externalDocumentNo) {
        this.externalDocumentNo = externalDocumentNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getSalesPersonCode() {
        return salesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        this.salesPersonCode = salesPersonCode;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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

    public boolean getTransferred() {
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

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getReferenceAmount() {
        return referenceAmount;
    }

    public void setReferenceAmount(String referenceAmount) {
        this.referenceAmount = referenceAmount;
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getChequeAmount() {
        return chequeAmount;
    }

    public void setChequeAmount(String chequeAmount) {
        this.chequeAmount = chequeAmount;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PaymentHeader fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, PaymentHeader.class);
    }
}
