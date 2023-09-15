package com.gui.mdt.thongsieknavclient.datamodel;

import com.gui.mdt.thongsieknavclient.model.AuthorizedModuleResult;

import java.util.List;

/**
 * Created by nelin_000 on 09/04/2017.
 */

public class ApiMSDAuthorizedModulesResponse {

    String Status;
    String Message;
    String ServerDate;
    String LastModifiedDate;
    List<AuthorizedModuleResult> MSDUserSetupResultData;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
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

    public String getLastModifiedDate() {
        return LastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        LastModifiedDate = lastModifiedDate;
    }

    public List<AuthorizedModuleResult> getMSDUserSetupResultData() {
        return MSDUserSetupResultData;
    }

    public void setMSDUserSetupResultData(List<AuthorizedModuleResult> MSDUserSetupResultData) {
        this.MSDUserSetupResultData = MSDUserSetupResultData;
    }
}
