package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by nelin_000 on 12/21/2017.
 */

public class ApiCustomerTemplateResponse {

    String ServerDate;
    String LastModifiedDate;
    List<ApiStandardSalesCodeResultData> StandardSalesCodeResultData;

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

    public List<ApiStandardSalesCodeResultData> getStandardSalesCodeResultData() {
        return StandardSalesCodeResultData;
    }

    public void setStandardSalesCodeResultData(List<ApiStandardSalesCodeResultData> standardSalesCodeResultData) {
        StandardSalesCodeResultData = standardSalesCodeResultData;
    }

    public class ApiStandardSalesCodeResultData{
        String Standard_Sales_Code;
        String No;
        String Description;
        float Quantity;
        String Unit_of_Measure_Code;

        public String getStandard_Sales_Code() {
            return Standard_Sales_Code;
        }

        public void setStandard_Sales_Code(String standard_Sales_Code) {
            Standard_Sales_Code = standard_Sales_Code;
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

        public String getUnit_of_Measure_Code() {
            return Unit_of_Measure_Code;
        }

        public void setUnit_of_Measure_Code(String unit_of_Measure_Code) {
            Unit_of_Measure_Code = unit_of_Measure_Code;
        }
    }
}
