package com.gui.mdt.thongsieknavclient.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-08.
 */

public class SalesCustomerArResponse {
    private String bookMarkKey;
    private String ServerDate;
    public List<SalesCustomerAr> CustomerLedgerEntryListResultData;

    public List<SalesCustomerAr> getCustomerLedgerEntryListResultData() {
        return CustomerLedgerEntryListResultData;
    }

    public void setSalesCustomerArList(List<SalesCustomerAr> salesCustomerArList) {
        this.CustomerLedgerEntryListResultData = salesCustomerArList;
    }

    public String getBookMarkKey() {
        return bookMarkKey;
    }

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public static class SalesCustomerAr
    {
        public String Document_No;
        public String Document_Date;
        public String Amount;
        public String Remaining_Amount;
        public String Due_Date;

        public boolean isArSelected;

        public SalesCustomerAr(String invoiceNo, String date, String amount, String balance,String dueDate) {
            this.Document_No=invoiceNo;
            this.Document_Date=date;
            this.Amount=amount;
            this.Remaining_Amount=balance;
            this.Due_Date=dueDate;

        }

        public String getInvoiceNo() {
            return Document_No;
        }

        public void setInvoiceNo(String document_No) {
            Document_No = document_No;
        }

        public String getDocumentDate() {
            return Document_Date;
        }

        public void setDocumentDate(String document_Date) {
            Document_Date = document_Date;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getBalance() {
            return Remaining_Amount;
        }

        public void setBalance(String remaining_Amount) {
            Remaining_Amount = remaining_Amount;
        }

        public String getDue_Date() {
            return Due_Date;
        }

        public void setDue_Date(String due_Date) {
            Due_Date = due_Date;
        }

        public boolean getArSelected() {
            return isArSelected;
        }

        public void setArSelected(boolean arSelected) {
            isArSelected = arSelected;
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
