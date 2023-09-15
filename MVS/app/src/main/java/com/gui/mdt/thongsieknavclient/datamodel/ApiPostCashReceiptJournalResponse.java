package com.gui.mdt.thongsieknavclient.datamodel;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by nelin_000 on 09/15/2017.
 */

public class ApiPostCashReceiptJournalResponse {
    int Status;
    String Message;
    String ServerDate;
    List<PaymentTransactionStatusResponse> trStatusList;

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

    public List<PaymentTransactionStatusResponse> getTrStatusList() {
        return trStatusList;
    }

    public void setTrStatusList(List<PaymentTransactionStatusResponse> trStatusList) {
        this.trStatusList = trStatusList;
    }

    public class PaymentTransactionStatusResponse{
        private String documentNo;
        private String NewLineNo;
        private String OriginalLineNo;
        private boolean isTransferred;
        private BaseResult BaseResult;

        public String getDocumentNo() {
            return documentNo;
        }

        public void setDocumentNo(String documentNo) {
            this.documentNo = documentNo;
        }

        public String getNewLineNo() {
            return NewLineNo;
        }

        public String getOriginalLineNo() {
            return OriginalLineNo;
        }

        public void setOriginalLineNo(String originalLineNo) {
            OriginalLineNo = originalLineNo;
        }

        public void setNewLineNo(String newLineNo) {
            this.NewLineNo = newLineNo;
        }

        public boolean isTransferred() {
            return isTransferred;
        }

        public void setTransferred(boolean transferred) {
            this.isTransferred = transferred;
        }

        public BaseResult getBaseResult() {
            return BaseResult;
        }

        public void setBaseResult(BaseResult baseResult) {
            BaseResult = baseResult;
        }
    }
}
