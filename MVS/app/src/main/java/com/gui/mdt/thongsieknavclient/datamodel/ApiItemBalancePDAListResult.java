package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by nelin_000 on 08/16/2017.
 */

public class ApiItemBalancePDAListResult {

    String ServerDate;
    String LastModifiedDate;
    List<ApiItemBalancePDAListResultData> ItemBalancePDAListResultData;
    int Status;
    String Message;

    public ApiItemBalancePDAListResult(){

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

    public List<ApiItemBalancePDAListResultData> getItemBalancePDAListResultData() {
        return ItemBalancePDAListResultData;
    }

    public void setItemBalancePDAListResultData(List<ApiItemBalancePDAListResultData> itemBalancePDAListResultData) {
        ItemBalancePDAListResultData = itemBalancePDAListResultData;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public class ApiItemBalancePDAListResultData{

        String Key;
        String  Item_No;
        String Location_Code;
        String  Bin_Code;
        float Quantity;
        String Unit_of_Measure_Code;

        public ApiItemBalancePDAListResultData(){

        }

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

        public String getItem_No() {
            return Item_No;
        }

        public void setItem_No(String item_No) {
            Item_No = item_No;
        }

        public String getLocation_Code() {
            return Location_Code;
        }

        public void setLocation_Code(String location_Code) {
            Location_Code = location_Code;
        }

        public String getBin_Code() {
            return Bin_Code;
        }

        public void setBin_Code(String bin_Code) {
            Bin_Code = bin_Code;
        }

        public float getQuantity() {
            return Quantity;
        }

        public void setQuantity(float quantity) {
            Quantity = quantity;
        }

        public String getUnit_of_Measure_Code() {
            return Unit_of_Measure_Code;
        }

        public void setUnit_of_Measure_Code(String unit_of_Measure_Code) {
            Unit_of_Measure_Code = unit_of_Measure_Code;
        }
    }
}
