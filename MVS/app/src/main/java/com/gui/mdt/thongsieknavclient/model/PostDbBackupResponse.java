package com.gui.mdt.thongsieknavclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lasith Madhushanka on 3/27/2020
 */
public class PostDbBackupResponse {
    @SerializedName("Status")
    private int status;
    @SerializedName("Message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
