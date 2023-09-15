package com.gui.mdt.thongsieknavclient.datamodel;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by GUI-NB03 on 2017-09-28.
 */

public class ApiPostMobileStockTransferHeaderResponse {
    public String ServerDate;
    public List<StockTransferHeaderResponse> trStatusList;
    public int Status;
    public String Message;

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public List<StockTransferHeaderResponse> getTrStatusList() {
        return trStatusList;
    }

    public void setTrStatusList(List<StockTransferHeaderResponse> trStatusList) {
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

    public static class StockTransferHeaderResponse {

        public String documentNo;
        public String lineNo;
        public boolean isTransferred;
        public com.gui.mdt.thongsieknavclient.model.BaseResult BaseResult;

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
