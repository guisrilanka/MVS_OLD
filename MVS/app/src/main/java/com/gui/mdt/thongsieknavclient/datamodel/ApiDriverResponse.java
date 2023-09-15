package com.gui.mdt.thongsieknavclient.datamodel;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by nelin_000 on 01/23/2018.
 */

public class ApiDriverResponse extends BaseResult {

    String ServerDate;
    String LastModifiedDate;
    List<ApiDriverResultData> DriverResultData;

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

    public List<ApiDriverResultData> getDriverResultData() {
        return DriverResultData;
    }

    public void setDriverResultData(List<ApiDriverResultData> driverResultData) {
        DriverResultData = driverResultData;
    }

    public class ApiDriverResultData{

        int Type;
        String Code;
        String Description;

        public int getType() {
            return Type;
        }

        public void setType(int type) {
            Type = type;
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
    }

}
