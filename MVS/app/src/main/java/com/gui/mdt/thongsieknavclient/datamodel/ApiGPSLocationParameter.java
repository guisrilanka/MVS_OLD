package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

public class ApiGPSLocationParameter {
    String driverCode;
    List<ApiGPSLocationLineParameter> driverLocations;

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public List<ApiGPSLocationLineParameter> getDriverLocations() {
        return driverLocations;
    }

    public void setDriverLocations(List<ApiGPSLocationLineParameter> driverLocations) {
        this.driverLocations = driverLocations;
    }
}
