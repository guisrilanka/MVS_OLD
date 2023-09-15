package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 2/7/2017.
 */

public class PostItemReclassLineBaseResult {

    public  static  int BaseResultStatusOk = 200;
    public  static  int BaseResultStatusFailed = 500;

    private int Status;
    private String Message;
    private int ReclassHeaderLineNo;

    public int getReclassHeaderLineNo() {
        return ReclassHeaderLineNo;
    }

    public void setReclassHeaderLineNo(int reclassHeaderLineNo) {
        ReclassHeaderLineNo = reclassHeaderLineNo;
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

}
