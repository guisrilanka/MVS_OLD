package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-29.
 */

public class ApiPostedSalesInvoiceItemResponse {
    private String bookMarkKey;
    private List<ApiPostedSalesInvoiceItem> PostedSalesInvoiceLineListResultData;

    public String getBookMarkKey() {
        return bookMarkKey;
    }

    public void setBookMarkKey(String bookMarkKey) {
        this.bookMarkKey = bookMarkKey;
    }

    public List<ApiPostedSalesInvoiceItem> getPostedSalesInvoiceLineListResultData() {
        return PostedSalesInvoiceLineListResultData;
    }

    public void setPostedSalesInvoiceLineListResultData(List<ApiPostedSalesInvoiceItem> postedSalesInvoiceLineListResultData) {
        PostedSalesInvoiceLineListResultData = postedSalesInvoiceLineListResultData;
    }

    public static class ApiPostedSalesInvoiceItem {

        String Document_No;
        String No;
        String Description;
        float Quantity;
        String Unit_of_Measure;
        float Unit_Price;
        float Amount_Including_VAT;

        public String getDocument_No() {
            return Document_No;
        }

        public void setDocument_No(String document_No) {
            Document_No = document_No;
        }

        public String getNo() {
            return No;
        }

        public void setNo(String no) {
            No = no;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public float getQuantity() {
            return Quantity;
        }

        public void setQuantity(float quantity) {
            Quantity = quantity;
        }

        public String getUnit_of_Measure() {
            return Unit_of_Measure;
        }

        public void setUnit_of_Measure(String unit_of_Measure) {
            Unit_of_Measure = unit_of_Measure;
        }

        public float getUnit_Price() {
            return Unit_Price;
        }

        public void setUnit_Price(float unit_Price) {
            Unit_Price = unit_Price;
        }

        public float getAmount_Including_VAT() {
            return Amount_Including_VAT;
        }

        public void setAmount_Including_VAT(float amount_Including_VAT) {
            Amount_Including_VAT = amount_Including_VAT;
        }

        //Gson
        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
