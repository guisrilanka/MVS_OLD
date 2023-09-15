package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by User on 8/29/2017.
 */

public class PaymentLine {
    Integer lineNo;
    String paymentNo;
    String paymentType ;//1-cash, 0-cheque
    float amount;
    String remark;
    String chequeNo;
    String chequeDate;

    String chequeName;

    //upload helper members.
    String driverCode;
    String salesPersonCode;
    String documentDate;
    String externalDocumentNo;
    String customerNo;
    String billToCustomerNo;

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
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

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getExternalDocumentNo() {
        return externalDocumentNo;
    }

    public void setExternalDocumentNo(String externalDocumentNo) {
        this.externalDocumentNo = externalDocumentNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getBillToCustomerNo() {
        return billToCustomerNo;
    }

    public void setBillToCustomerNo(String billToCustomerNo) {
        this.billToCustomerNo = billToCustomerNo;
    }

    public String getChequeName() {
        return chequeName;
    }

    public void setChequeName(String chequeName) {
        this.chequeName = chequeName;
    }
}
