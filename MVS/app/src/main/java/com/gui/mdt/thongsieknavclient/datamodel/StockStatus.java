package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GuiUser on 2/2/2018.
 */

public class StockStatus {
    private Integer Id;
    private String LoadDate;
    private Boolean IsDispatched;
    private String DispatchedTime;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getLoadDate() {
        return LoadDate;
    }

    public void setLoadDate(String loadDate) {
        LoadDate = loadDate;
    }

    public Boolean isDispatched() {
        return IsDispatched;
    }

    public void setDispatched(Boolean dispatched) {
        IsDispatched = dispatched;
    }

    public String getDispatchedTime() {
        return DispatchedTime;
    }

    public void setDispatchedTime(String dispatchedTime) {
        DispatchedTime = dispatchedTime;
    }
}
