package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 18/7/2016.
 */


public class BaseResult {
    public  static  int BaseResultStatusOk = 200;
    public  static  int BaseResultStatusFailed = 500;

    private int Status;
    private String Message;

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
}
