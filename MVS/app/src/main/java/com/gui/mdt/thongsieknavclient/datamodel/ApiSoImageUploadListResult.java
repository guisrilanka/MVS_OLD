package com.gui.mdt.thongsieknavclient.datamodel;

import com.gui.mdt.thongsieknavclient.model.BaseResult;

import java.util.List;

/**
 * Created by User on 10/13/2017.
 */

public class ApiSoImageUploadListResult {

    private int Status;
    private String Message;
    private List<ApiSoImageUploadResponse> trStatusList;

    public List<ApiSoImageUploadResponse> getTrStatusList() {
        return trStatusList;
    }

    public void setTrStatusList(List<ApiSoImageUploadResponse> trStatusList) {
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


    public static class ApiSoImageUploadResponse {

        String Key;
        boolean isTransferred;
        String ImageName;
        com.gui.mdt.thongsieknavclient.model.BaseResult BaseResult;

        public String getImageName() {
            return ImageName;
        }

        public void setImageName(String imageName) {
            ImageName = imageName;
        }

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
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
