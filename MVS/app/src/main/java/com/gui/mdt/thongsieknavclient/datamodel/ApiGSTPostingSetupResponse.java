package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-30.
 */

public class ApiGSTPostingSetupResponse {

    String  LastModifiedDate;
    String ServerDate;
    public List<ApiGSTPostingSetupResponse.GSTPostingSetupResultData> GSTPostingSetupResultData;

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

    public List<ApiGSTPostingSetupResponse.GSTPostingSetupResultData> getGSTPostingSetupResultData() {
        return GSTPostingSetupResultData;
    }

    public void setGSTPostingSetupResultData(List<ApiGSTPostingSetupResponse.GSTPostingSetupResultData>
                                                     GSTPostingSetupResultData) {
        this.GSTPostingSetupResultData = GSTPostingSetupResultData;
    }

    public static class GSTPostingSetupResultData {
        String Key;
        String VAT_Bus_Posting_Group;
        String VAT_Prod_Posting_Group;
        Float VAT_Percent;

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

        public String getVAT_Bus_Posting_Group() {
            return VAT_Bus_Posting_Group;
        }

        public void setVAT_Bus_Posting_Group(String VAT_Bus_Posting_Group) {
            this.VAT_Bus_Posting_Group = VAT_Bus_Posting_Group;
        }

        public String getVAT_Prod_Posting_Group() {
            return VAT_Prod_Posting_Group;
        }

        public void setVAT_Prod_Posting_Group(String VAT_Prod_Posting_Group) {
            this.VAT_Prod_Posting_Group = VAT_Prod_Posting_Group;
        }

        public Float getVAT_Percent() {
            return VAT_Percent;
        }

        public void setVAT_Percent(Float VAT_Percent) {
            this.VAT_Percent = VAT_Percent;
        }

        //Gson
        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
