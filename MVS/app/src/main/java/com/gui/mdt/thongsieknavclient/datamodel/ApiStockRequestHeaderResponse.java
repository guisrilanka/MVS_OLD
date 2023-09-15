package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by nelin_000 on 10/24/2017.
 */

public class ApiStockRequestHeaderResponse {

    List<ApiStockRequestHeaderResponse.StockRequestHeaderResultData> StockRequestListResultData;
    String ServerDate;
    String LastModifiedDate;
    Integer Status;
    String Message;

    public List<StockRequestHeaderResultData> getStockRequestListResultData() {
        return StockRequestListResultData;
    }

    public void setStockRequestListResultData(List<StockRequestHeaderResultData> stockRequestListResultData) {
        StockRequestListResultData = stockRequestListResultData;
    }

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public String getLastModifiedDate() {
        return LastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        LastModifiedDate = lastModifiedDate;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public class StockRequestHeaderResultData {

        String Key;
        String Document_No;
        String No;
        String Sell_to_Customer_No;
        String Sell_to_Contact_No;
        String Sell_to_Customer_Name;
        String Sell_to_Address;
        String Sell_to_Post_Code;
        String Sell_to_City;
        String Order_Date;
        String Document_Date;
        String Requested_Delivery_Date;
        String Promised_Delivery_Date;
        String External_Document_No;
        String Salesperson_Code;
        String Driver_Code;
        String Status;
        String Created_By;
        String Created_DateTime;
        String Last_Modified_By;
        String Last_Modified_DateTime;
        float Amount_Including_VAT;
        String Due_Date;
        String Location_Code;
        String Delivery_Time;
        String Shipment_Date;
        String Created_From;
        List<ApiStockRequestLineResponse> StockRequestLine;
        boolean Delivered;
        String Posting_Date;

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

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

        public String getSell_to_Customer_No() {
            return Sell_to_Customer_No;
        }

        public void setSell_to_Customer_No(String sell_to_Customer_No) {
            Sell_to_Customer_No = sell_to_Customer_No;
        }

        public String getSell_to_Contact_No() {
            return Sell_to_Contact_No;
        }

        public void setSell_to_Contact_No(String sell_to_Contact_No) {
            Sell_to_Contact_No = sell_to_Contact_No;
        }

        public String getSell_to_Customer_Name() {
            return Sell_to_Customer_Name;
        }

        public void setSell_to_Customer_Name(String sell_to_Customer_Name) {
            Sell_to_Customer_Name = sell_to_Customer_Name;
        }

        public String getSell_to_Address() {
            return Sell_to_Address;
        }

        public void setSell_to_Address(String sell_to_Address) {
            Sell_to_Address = sell_to_Address;
        }

        public String getSell_to_Post_Code() {
            return Sell_to_Post_Code;
        }

        public void setSell_to_Post_Code(String sell_to_Post_Code) {
            Sell_to_Post_Code = sell_to_Post_Code;
        }

        public String getSell_to_City() {
            return Sell_to_City;
        }

        public void setSell_to_City(String sell_to_City) {
            Sell_to_City = sell_to_City;
        }

        public String getOrder_Date() {
            return Order_Date;
        }

        public void setOrder_Date(String order_Date) {
            Order_Date = order_Date;
        }

        public String getDocument_Date() {
            return Document_Date;
        }

        public void setDocument_Date(String document_Date) {
            Document_Date = document_Date;
        }

        public String getRequested_Delivery_Date() {
            return Requested_Delivery_Date;
        }

        public void setRequested_Delivery_Date(String requested_Delivery_Date) {
            Requested_Delivery_Date = requested_Delivery_Date;
        }

        public String getPromised_Delivery_Date() {
            return Promised_Delivery_Date;
        }

        public void setPromised_Delivery_Date(String promised_Delivery_Date) {
            Promised_Delivery_Date = promised_Delivery_Date;
        }

        public String getExternal_Document_No() {
            return External_Document_No;
        }

        public void setExternal_Document_No(String external_Document_No) {
            External_Document_No = external_Document_No;
        }

        public String getSalesperson_Code() {
            return Salesperson_Code;
        }

        public void setSalesperson_Code(String salesperson_Code) {
            Salesperson_Code = salesperson_Code;
        }

        public String getDriver_Code() {
            return Driver_Code;
        }

        public void setDriver_Code(String driver_Code) {
            Driver_Code = driver_Code;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getCreated_By() {
            return Created_By;
        }

        public void setCreated_By(String created_By) {
            Created_By = created_By;
        }

        public String getCreated_DateTime() {
            return Created_DateTime;
        }

        public void setCreated_DateTime(String created_DateTime) {
            Created_DateTime = created_DateTime;
        }

        public String getLast_Modified_By() {
            return Last_Modified_By;
        }

        public void setLast_Modified_By(String last_Modified_By) {
            Last_Modified_By = last_Modified_By;
        }

        public String getLast_Modified_DateTime() {
            return Last_Modified_DateTime;
        }

        public void setLast_Modified_DateTime(String last_Modified_DateTime) {
            Last_Modified_DateTime = last_Modified_DateTime;
        }

        public float getAmount_Including_VAT() {
            return Amount_Including_VAT;
        }

        public void setAmount_Including_VAT(float amount_Including_VAT) {
            Amount_Including_VAT = amount_Including_VAT;
        }

        public String getDue_Date() {
            return Due_Date;
        }

        public void setDue_Date(String due_Date) {
            Due_Date = due_Date;
        }

        public String getLocation_Code() {
            return Location_Code;
        }

        public void setLocation_Code(String location_Code) {
            Location_Code = location_Code;
        }

        public String getDelivery_Time() {
            return Delivery_Time;
        }

        public void setDelivery_Time(String delivery_Time) {
            Delivery_Time = delivery_Time;
        }

        public String getShipment_Date() {
            return Shipment_Date;
        }

        public void setShipment_Date(String shipment_Date) {
            Shipment_Date = shipment_Date;
        }

        public String getCreated_From() {
            return Created_From;
        }

        public void setCreated_From(String created_From) {
            Created_From = created_From;
        }

        public List<ApiStockRequestLineResponse> getStockRequestLine() {
            return StockRequestLine;
        }

        public void setStockRequestLine(List<ApiStockRequestLineResponse> stockRequestLine) {
            StockRequestLine = stockRequestLine;
        }

        public boolean isDelivered() {
            return Delivered;
        }

        public void setDelivered(boolean delivered) {
            Delivered = delivered;
        }

        public String getPosting_Date() {
            return Posting_Date;
        }

        public void setPosting_Date(String posting_Date) {
            Posting_Date = posting_Date;
        }
    }
}
