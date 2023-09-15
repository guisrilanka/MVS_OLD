package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by nelin_000 on 12/29/2017.
 */

public class ApiVehicleOpenQuantityResponse {
    String ServerDate;
    String LastModifiedDate;
    List<ApiVehicleOpenQuantityListResultData> VehicleOpenQuantityListResultData;

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

    public List<ApiVehicleOpenQuantityListResultData> getVehicleOpenQuantityListResultData() {
        return VehicleOpenQuantityListResultData;
    }

    public void setVehicleOpenQuantityListResultData(List<ApiVehicleOpenQuantityListResultData>
                                                             vehicleOpenQuantityListResultData) {
        VehicleOpenQuantityListResultData = vehicleOpenQuantityListResultData;
    }

    public class ApiVehicleOpenQuantityListResultData{

         String Key;
                 String Document_No;
                 String Item_No;
                 String Description;
                 int Quantity;
                 String Unit_of_Measure;
                 String Receipt_Date;
                 String Transfer_from_Code;
                 String Transfer_to_Code;

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

        public String getItem_No() {
            return Item_No;
        }

        public void setItem_No(String item_No) {
            Item_No = item_No;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int quantity) {
            Quantity = quantity;
        }

        public String getUnit_of_Measure() {
            return Unit_of_Measure;
        }

        public void setUnit_of_Measure(String unit_of_Measure) {
            Unit_of_Measure = unit_of_Measure;
        }

        public String getReceipt_Date() {
            return Receipt_Date;
        }

        public void setReceipt_Date(String receipt_Date) {
            Receipt_Date = receipt_Date;
        }

        public String getTransfer_from_Code() {
            return Transfer_from_Code;
        }

        public void setTransfer_from_Code(String transfer_from_Code) {
            Transfer_from_Code = transfer_from_Code;
        }

        public String getTransfer_to_Code() {
            return Transfer_to_Code;
        }

        public void setTransfer_to_Code(String transfer_to_Code) {
            Transfer_to_Code = transfer_to_Code;
        }
    }
}
