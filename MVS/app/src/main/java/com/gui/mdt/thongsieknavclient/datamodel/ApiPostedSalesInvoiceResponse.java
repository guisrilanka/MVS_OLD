package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by nelin_000 on 08/08/2017.
 */

public class ApiPostedSalesInvoiceResponse {
    private String bookMarkKey;
    private List<ApiPostedSalesInvoice> PostedSalesInvoiceListResultData;

    public String getBookMarkKey() {
        return bookMarkKey;
    }

    public void setBookMarkKey(String bookMarkKey) {
        this.bookMarkKey = bookMarkKey;
    }

    public List<ApiPostedSalesInvoice> getPostedSalesInvoiceListResultData() {
        return PostedSalesInvoiceListResultData;
    }

    public void setPostedSalesInvoiceListResultData(List<ApiPostedSalesInvoice> postedSalesInvoiceListResultData) {
        PostedSalesInvoiceListResultData = postedSalesInvoiceListResultData;
    }

    public static class  ApiPostedSalesInvoice{
        String No;
        String Order_No;
        String Sell_to_Customer_No;
        String Amount;
        String Amount_Including_VAT;
        String Document_Dat;

        public String getNo() {
            return No;
        }

        public void setNo(String no) {
            No = no;
        }

        public String getOrder_No() {
            return Order_No;
        }

        public void setOrder_No(String order_No) {
            Order_No = order_No;
        }

        public String getSell_to_Customer_No() {
            return Sell_to_Customer_No;
        }

        public void setSell_to_Customer_No(String sell_to_Customer_No) {
            Sell_to_Customer_No = sell_to_Customer_No;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getAmount_Including_VAT() {
            return Amount_Including_VAT;
        }

        public void setAmount_Including_VAT(String amount_Including_VAT) {
            Amount_Including_VAT = amount_Including_VAT;
        }

        public String getDocument_Dat() {
            return Document_Dat;
        }

        public void setDocument_Dat(String document_Dat) {
            Document_Dat = document_Dat;
        }


        //Gson
        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }

        public static ApiPostedSalesInvoice fromJson(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, ApiPostedSalesInvoice.class);
        }
    }
}
