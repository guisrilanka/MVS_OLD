package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 08/22/2017.
 */

public class SyncStatus {

    private String scope;
    private  boolean status;
    private String message;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
