package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by nelin_000 on 07/19/2017.
 */

public class ApiCustomerListResult {

    public List<ApiCustomerResultResponse> CustomerListResultData;
    String LastModifiedDate;
    String ServerDate;

    public List<ApiCustomerResultResponse> getCustomerListResultResultData() {
        return CustomerListResultData;
    }

    public void setCustomerListResultResultData(List<ApiCustomerResultResponse> customerListResultResultData) {
        CustomerListResultData = customerListResultResultData;
    }

    public String getLastModifiedDate() {
        return LastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        LastModifiedDate = lastModifiedDate;
    }

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public static class ApiCustomerResultResponse {
        String Key;
        String No;
        String Name;
        String Address;
        String Address_2;
        String Post_Code;
        String County;
        String Phone_No;
        String Primary_Contact_No;
        String Contact;
        String City;
        String Driver_Code;
        String Search_Name;
        double Balance;
        double Balance_LCY;
        double Credit_Limit_LCY;
        double Credit_Limit_Reference_LCY;
        String Due_Date_Grace_Period;
        String Salesperson_Code;
        String Responsibility_Center;
        Integer Blocked;
        String Last_Date_Modified;
        String E_Mail;
        String Bill_to_Customer_No;
        String VAT_Registration_No;
        String Invoice_Disc_Code;
        Integer TaxDocumentType;
        String Customer_Price_Group;
        String Customer_Disc_Group;
        Boolean Allow_Line_Disc;
        String Customer_Reference_No;
        String Company_Registration_No;
        double Minimum_Sales_Amount_LCY;
        String Payment_Terms_Code;
        String Reminder_Terms_Code;
        String Location_Code;
        String VAT_Bus_Posting_Group;
        String Bill_to_Customer_Name;
        String NTUC_Store_Code;

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

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getAddress_2() {
            return Address_2;
        }

        public void setAddress_2(String address_2) {
            Address_2 = address_2;
        }

        public String getPost_Code() {
            return Post_Code;
        }

        public void setPost_Code(String post_Code) {
            Post_Code = post_Code;
        }

        public String getCounty() {
            return County;
        }

        public void setCounty(String county) {
            County = county;
        }

        public String getPhone_No() {
            return Phone_No;
        }

        public void setPhone_No(String phone_No) {
            Phone_No = phone_No;
        }

        public String getPrimary_Contact_No() {
            return Primary_Contact_No;
        }

        public void setPrimary_Contact_No(String primary_Contact_No) {
            Primary_Contact_No = primary_Contact_No;
        }

        public String getContact() {
            return Contact;
        }

        public void setContact(String contact) {
            Contact = contact;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String city) {
            City = city;
        }

        public String getDriver_Code() {
            return Driver_Code;
        }

        public void setDriver_Code(String driver_Code) {
            Driver_Code = driver_Code;
        }

        public String getSearch_Name() {
            return Search_Name;
        }

        public void setSearch_Name(String search_Name) {
            Search_Name = search_Name;
        }

        public double getBalance() {
            return Balance;
        }

        public void setBalance(double balance) {
            Balance = balance;
        }

        public double getBalance_LCY() {
            return Balance_LCY;
        }

        public void setBalance_LCY(double balance_LCY) {
            Balance_LCY = balance_LCY;
        }

        public double getCredit_Limit_LCY() {
            return Credit_Limit_LCY;
        }

        public void setCredit_Limit_LCY(double credit_Limit_LCY) {
            Credit_Limit_LCY = credit_Limit_LCY;
        }

        public double getCredit_Limit_Reference_LCY() {
            return Credit_Limit_Reference_LCY;
        }

        public void setCredit_Limit_Reference_LCY(double credit_Limit_Reference_LCY) {
            Credit_Limit_Reference_LCY = credit_Limit_Reference_LCY;
        }

        public String getDue_Date_Grace_Period() {
            return Due_Date_Grace_Period;
        }

        public void setDue_Date_Grace_Period(String due_Date_Grace_Period) {
            Due_Date_Grace_Period = due_Date_Grace_Period;
        }

        public String getSalesperson_Code() {
            return Salesperson_Code;
        }

        public void setSalesperson_Code(String salesperson_Code) {
            Salesperson_Code = salesperson_Code;
        }

        public String getResponsibility_Center() {
            return Responsibility_Center;
        }

        public void setResponsibility_Center(String responsibility_Center) {
            Responsibility_Center = responsibility_Center;
        }

        public Integer getBlocked() {
            return Blocked;
        }

        public void setBlocked(Integer blocked) {
            Blocked = blocked;
        }

        public String getLast_Date_Modified() {
            return Last_Date_Modified;
        }

        public void setLast_Date_Modified(String last_Date_Modified) {
            Last_Date_Modified = last_Date_Modified;
        }

        public String getE_Mail() {
            return E_Mail;
        }

        public void setE_Mail(String e_Mail) {
            E_Mail = e_Mail;
        }

        public String getBill_to_Customer_No() {
            return Bill_to_Customer_No;
        }

        public void setBill_to_Customer_No(String bill_to_Customer_No) {
            Bill_to_Customer_No = bill_to_Customer_No;
        }

        public String getVAT_Registration_No() {
            return VAT_Registration_No;
        }

        public void setVAT_Registration_No(String VAT_Registration_No) {
            this.VAT_Registration_No = VAT_Registration_No;
        }

        public String getInvoice_Disc_Code() {
            return Invoice_Disc_Code;
        }

        public void setInvoice_Disc_Code(String invoice_Disc_Code) {
            Invoice_Disc_Code = invoice_Disc_Code;
        }

        public Integer getTaxDocumentType() {
            return TaxDocumentType;
        }

        public void setTaxDocumentType(Integer taxDocumentType) {
            TaxDocumentType = taxDocumentType;
        }

        public String getCustomer_Price_Group() {
            return Customer_Price_Group;
        }

        public void setCustomer_Price_Group(String customer_Price_Group) {
            Customer_Price_Group = customer_Price_Group;
        }

        public String getCustomer_Disc_Group() {
            return Customer_Disc_Group;
        }

        public void setCustomer_Disc_Group(String customer_Disc_Group) {
            Customer_Disc_Group = customer_Disc_Group;
        }

        public Boolean getAllow_Line_Disc() {
            return Allow_Line_Disc;
        }

        public void setAllow_Line_Disc(Boolean allow_Line_Disc) {
            Allow_Line_Disc = allow_Line_Disc;
        }

        public String getCustomer_Reference_No() {
            return Customer_Reference_No;
        }

        public void setCustomer_Reference_No(String customer_Reference_No) {
            Customer_Reference_No = customer_Reference_No;
        }

        public String getCompany_Registration_No() {
            return Company_Registration_No;
        }

        public void setCompany_Registration_No(String company_Registration_No) {
            Company_Registration_No = company_Registration_No;
        }

        public double getMinimum_Sales_Amount_LCY() {
            return Minimum_Sales_Amount_LCY;
        }

        public void setMinimum_Sales_Amount_LCY(double minimum_Sales_Amount_LCY) {
            Minimum_Sales_Amount_LCY = minimum_Sales_Amount_LCY;
        }

        public String getPayment_Terms_Code() {
            return Payment_Terms_Code;
        }

        public void setPayment_Terms_Code(String payment_Terms_Code) {
            Payment_Terms_Code = payment_Terms_Code;
        }

        public String getReminder_Terms_Code() {
            return Reminder_Terms_Code;
        }

        public void setReminder_Terms_Code(String reminder_Terms_Code) {
            Reminder_Terms_Code = reminder_Terms_Code;
        }

        public String getLocation_Code() {
            return Location_Code;
        }

        public void setLocation_Code(String location_Code) {
            Location_Code = location_Code;
        }

        public String getVAT_Bus_Posting_Group() {
            return VAT_Bus_Posting_Group;
        }

        public void setVAT_Bus_Posting_Group(String VAT_Bus_Posting_Group) {
            this.VAT_Bus_Posting_Group = VAT_Bus_Posting_Group;
        }

        public String getBill_to_Customer_Name() {
            return Bill_to_Customer_Name;
        }

        public void setBill_to_Customer_Name(String bill_to_Customer_Name) {
            Bill_to_Customer_Name = bill_to_Customer_Name;
        }

        public String getNTUC_Store_Code() {
            return NTUC_Store_Code;
        }

        public void setNTUC_Store_Code(String NTUC_Store_Code) {
            this.NTUC_Store_Code = NTUC_Store_Code;
        }

        @Override
        public String toString() {
            return super.toString();
        }

        //Gson
        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}

