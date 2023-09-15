package com.gui.mdt.thongsieknavclient.model.itemreclassmodels;

/**
 * Created by user on 12/8/2016.
 */

public class ItemReclassLocationListResult {

    private String Code;
    private String Name;
    private boolean ShowBinCode;

    public ItemReclassLocationListResult(String code, String name, boolean showBinCode) {
        Code = code;
        Name = name;
        ShowBinCode = showBinCode;
    }

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

}
