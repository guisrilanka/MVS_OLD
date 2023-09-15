package com.gui.mdt.thongsieknavclient.model.location;

/**
 * Created by user on 1/3/2017.
 */

public class LocationListResultData {

    private String Code;
    private String Name;
    private boolean ShowBinCode;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isShowBinCode() {
        return ShowBinCode;
    }

    public void setShowBinCode(boolean showBinCode) {
        ShowBinCode = showBinCode;
    }

    @Override
    public String toString() {
        return Code;
//        return "Name: " + Name + " Code: " + Code + " ShowBinCode: " + isShowBinCode();
    }
}
