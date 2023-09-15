package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-08-17.
 */

public class ApiPostMobileSalesInvoiceHeaderResponse {

//"ServerDate": "2017-08-22 12:12:30",
//        "trStatusList": [
//    {
//        "documentNo": "SIN1609-00003",
//            "lineNo": 0,
//            "isTransferred": false,
//            "BaseResult": {
//        "Status": 500,
//                "Message": "HEADER_ALREADY_EXIST"
//    }
//    }
//    ],
//            "Status": 200,
//            "Message": null

    public String ServerDate;
    public List<SalesInvoiceHeaderResponse> trStatusList;
    public int Status;
    public String Message;

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public List<SalesInvoiceHeaderResponse> getTrStatusList() {
        return trStatusList;
    }

    public void setTrStatusList(List<SalesInvoiceHeaderResponse> trStatusList) {
        this.trStatusList = trStatusList;
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

    public static class SalesInvoiceHeaderResponse {

        public String documentNo;
        public String lineNo;
        public boolean isTransferred;
        public BaseResult BaseResult;

        public String getDocumentNo() {
            return documentNo;
        }

        public void setDocumentNo(String documentNo) {
            this.documentNo = documentNo;
        }

        public String getLineNo() {
            return lineNo;
        }

        public void setLineNo(String lineNo) {
            this.lineNo = lineNo;
        }

        public boolean isTransferred() {
            return isTransferred;
        }

        public void setTransferred(boolean transferred) {
            isTransferred = transferred;
        }

        public com.gui.mdt.thongsieknavclient.model.BaseResult getBaseResult() {
            return BaseResult;
        }

        public void setBaseResult(com.gui.mdt.thongsieknavclient.model.BaseResult baseResult) {
            BaseResult = baseResult;
        }

    }
    //Gson
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
