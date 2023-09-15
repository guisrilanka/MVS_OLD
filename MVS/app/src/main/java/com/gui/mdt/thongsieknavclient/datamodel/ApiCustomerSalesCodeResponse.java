package com.gui.mdt.thongsieknavclient.datamodel;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class ApiCustomerSalesCodeResponse extends BaseResult{

    String  LasLastModifiedDate;
    String ServerDate;
    public List<ApiCustomerSalesCodeResponse.ApiSalesCodeResultData> SalesCodeResultData;

    public String getLasLastModifiedDate() {
        return LasLastModifiedDate;
    }

    public void setLasLastModifiedDate(String lasLastModifiedDate) {
        LasLastModifiedDate = lasLastModifiedDate;
    }

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public List<ApiSalesCodeResultData> getSalesCodeResultData() {
        return SalesCodeResultData;
    }

    public void setSalesCodeResultData(List<ApiSalesCodeResultData> salesCodeResultData) {
        SalesCodeResultData = salesCodeResultData;
    }

    public class ApiSalesCodeResultData{

        String Customer_No;
        String Code;
        String Description;
        String Valid_From_Date;
        String Valid_To_date;
        boolean Blocked;

        public String getCustomer_No() {
            return Customer_No;
        }

        public void setCustomer_No(String customer_No) {
            Customer_No = customer_No;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getValid_From_Date() {
            return Valid_From_Date;
        }

        public void setValid_From_Date(String valid_From_Date) {
            Valid_From_Date = valid_From_Date;
        }

        public String getValid_To_date() {
            return Valid_To_date;
        }

        public void setValid_To_date(String valid_To_date) {
            Valid_To_date = valid_To_date;
        }

        public boolean isBlocked() {
            return Blocked;
        }

        public void setBlocked(boolean blocked) {
            Blocked = blocked;
        }
    }

}
