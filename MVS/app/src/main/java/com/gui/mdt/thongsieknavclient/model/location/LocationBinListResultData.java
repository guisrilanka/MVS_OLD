package com.gui.mdt.thongsieknavclient.model.location;

/**
 * Created by user on 12/30/2016.
 */

public class LocationBinListResultData {

    private String BinCode;
    private String LocationCode;

    public LocationBinListResultData(String binCode, String locationCode) {
        BinCode = binCode;
        LocationCode = locationCode;
    }

    public String getBinCode() {
        return BinCode;
    }

    public void setBinCode(String binCode) {
        BinCode = binCode;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    @Override
    public String toString() {
        return BinCode;
    }
}
