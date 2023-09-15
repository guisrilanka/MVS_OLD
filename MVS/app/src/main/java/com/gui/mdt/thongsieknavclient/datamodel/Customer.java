package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

/**
 * Created by nelin_000 on 07/14/2017.
 */

public class Customer {

    //private variables
    //private int id;
    private String key;
    private String code;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNo;
    private String contact;
    private String driverCode;
    private String salespersonCode;
    private double minimumSalesAmount;
    private double balance;
    private double creditLimit;
    private String dueDateGracePeriod;
    private String paymentTerms;
    private boolean isBlocked;
    private String email;
    private String customerPriceGroup;
    private String vatBusPostingGroup;
    private String billToCustomerNo;
    private String billToCustomerName;
    private String customerReferenceNo;
    private String ntucStoreCode;

    public Customer() {
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
*/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getSalespersonCode() {
        return salespersonCode;
    }

    public void setSalespersonCode(String salespersonCode) {
        this.salespersonCode = salespersonCode;
    }

    public double getMinimumSalesAmount() {
        return minimumSalesAmount;
    }

    public void setMinimumSalesAmount(double minimumSalesAmount) {
        this.minimumSalesAmount = minimumSalesAmount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getDueDateGracePeriod() {
        return dueDateGracePeriod;
    }

    public void setDueDateGracePeriod(String dueDateGracePeriod) {
        this.dueDateGracePeriod = dueDateGracePeriod;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerPriceGroup() {
        return customerPriceGroup;
    }

    public void setCustomerPriceGroup(String customerPriceGroup) {
        this.customerPriceGroup = customerPriceGroup;
    }

    public String getVatBusPostingGroup() {
        return vatBusPostingGroup;
    }

    public void setVatBusPostingGroup(String vatBusPostingGroup) {
        this.vatBusPostingGroup = vatBusPostingGroup;
    }

    public String getBillToCustomerNo() {
        return billToCustomerNo;
    }

    public void setBillToCustomerNo(String billToCustomerNo) {
        this.billToCustomerNo = billToCustomerNo;
    }

    public String getBillToCustomerName() {
        return billToCustomerName;
    }

    public void setBillToCustomerName(String billToCustomerName) {
        this.billToCustomerName = billToCustomerName;
    }

    public String getCustomerReferenceNo() {
        return customerReferenceNo;
    }

    public void setCustomerReferenceNo(String customerReferenceNo) {
        this.customerReferenceNo = customerReferenceNo;
    }

    public String getNtucBuyerCode() {
        return ntucStoreCode;
    }

    public void setNtucBuyerCode(String ntucBuyerCode) {
        this.ntucStoreCode = ntucBuyerCode;
    }

    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Customer fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Customer.class);
    }
}
