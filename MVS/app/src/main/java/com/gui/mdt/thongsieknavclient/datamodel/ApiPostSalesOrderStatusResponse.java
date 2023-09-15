package com.gui.mdt.thongsieknavclient.datamodel;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by GUISL-NB05 on 10/2/2017.
 */

public class ApiPostSalesOrderStatusResponse {
    int Status;
    String Message;
    String ServerDate;
    List<SalesOrderStatusResponse> trStatusList;

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

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public List<SalesOrderStatusResponse> getTrStatusList() {
        return trStatusList;
    }

    public void setTrStatusList(List<SalesOrderStatusResponse> trStatusList) {
        this.trStatusList = trStatusList;
    }

    public class SalesOrderStatusResponse{
        private String documentNo;
        private String lineNo;
        private boolean isTransferred;
        private BaseResult BaseResult;

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

}
