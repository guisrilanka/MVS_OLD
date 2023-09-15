package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/20/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.util.Locale;

import com.gui.mdt.thongsieknavclient.rp4.Document;

public class UPSMessage {
    private String m_zipCode;
    private int m_countryCode = 840;
    private int m_classOfService = 0;
    private String m_trackingNumber = "1Z01234567";
    private String m_SCAC = "UPSN";
    private String m_shipperNumber = "123456";
    private int m_julianPickupDay = 1;
    private String m_shipmentID = "";
    private int m_currentPackage = 1;
    private int m_totalPackage = 1;
    private int m_packageWeight = 1;
    private boolean m_validateAddress = true;
    private String m_shipToAddr = "1234 APPLE ST";
    private String m_shipToCity = "ORANGE";
    private String m_shipToState = "CA";

    public UPSMessage(String zipCode, int countryCode, int classOfService, String trackingNum, String scac, String shipperNum, int pickupDay, String shipID, int numberOfPackages, int packageNumber, int packageWeight, boolean validateAddr, String shipToAddr, String shipToCity, String shipToState) {
        this.setZipCode(zipCode);
        this.setCountryCode(countryCode);
        this.setClassOfService(classOfService);
        this.setTrackingNumber(trackingNum);
        this.setSCAC(scac);
        this.setShipperNumber(shipperNum);
        this.setJulianPickupDay(pickupDay);
        this.setShipmentID(shipID);
        this.setTotalPackage(numberOfPackages);
        this.setCurrentPackage(packageNumber);
        this.setPackageWeight(packageWeight);
        this.setValidateAddress(validateAddr);
        this.setShipToAddr(shipToAddr);
        this.setShipToCity(shipToCity);
        this.setShipToState(shipToState);
    }

    public final String getZipCode() {
        return this.m_zipCode;
    }

    public final void setZipCode(String value) {
        if ((value.length() == 5 || value.length() == 9) && Document.matches(value, "^[0-9]*$") || value.length() == 6 && Document.matches(value, "^[A-Z0-9]*$")) {
            this.m_zipCode = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Zip code must be 5-9 numeric digits or 6 alphanumeric characters. Value given was %1$s", value));
        }
    }

    public final int getCountryCode() {
        return this.m_countryCode;
    }

    public final void setCountryCode(int value) {
        if (value >= 0 && value <= 999) {
            this.m_countryCode = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "CountryCode must be between 0 and 999. Value given was %1$s", value));
        }
    }

    public final int getClassOfService() {
        return this.m_classOfService;
    }

    public final void setClassOfService(int value) {
        if (value >= 0 && value <= 999) {
            this.m_classOfService = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "ClassOfService must be between 0 and 999. Value given was %1$s", value));
        }
    }

    public final String getTrackingNumber() {
        return this.m_trackingNumber;
    }

    public final void setTrackingNumber(String value) {
        if ((value.length() == 10 || value.length() == 11) && Document.matches(value, "^[A-Z0-9]*$")) {
            this.m_trackingNumber = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "TrackingNumber must be 10 or 11 alphanumeric characters(Alpha must be uppercase). Value given was %1$s.", value));
        }
    }

    public final String getSCAC() {
        return this.m_SCAC;
    }

    public final void setSCAC(String value) {
        if (value.length() == 4 && Document.matches(value, "^[A-Z]*$")) {
            this.m_SCAC = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "SCAC must be 4 uppercase alpha characters. Value given was %1$s.", value));
        }
    }

    public final String getShipperNumber() {
        return this.m_shipperNumber;
    }

    public final void setShipperNumber(String value) {
        if (!Document.matches(value, "[A-Z0-9]{6}")) {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "ShipperNumber must be 6 alphanumeric characters(Alpha must be uppercase). Value given was %1$s", value));
        } else {
            this.m_shipperNumber = value;
        }
    }

    public final int getJulianPickupDay() {
        return this.m_julianPickupDay;
    }

    public final void setJulianPickupDay(int value) {
        if (value >= 1 && value <= 365) {
            this.m_julianPickupDay = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "JulianPickupDay must be between 1 and 365. Value given was %1$s", value));
        }
    }

    public final String getShipmentID() {
        return this.m_shipmentID;
    }

    public final void setShipmentID(String value) {
        if (value.length() <= 30 && Document.matches(value, "^[A-Z0-9]*$")) {
            this.m_shipmentID = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "ShipmentID  must be 0-30 alphanumeric characters(Alpha must be uppercase). Value given was %1$s", value));
        }
    }

    public final int getCurrentPackage() {
        return this.m_currentPackage;
    }

    public final void setCurrentPackage(int value) {
        if (value >= 0 && value <= 999 && value <= this.getTotalPackage()) {
            this.m_currentPackage = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "currentPackage must be between 0-999. Value given was %1$s", value));
        }
    }

    public final int getTotalPackage() {
        return this.m_totalPackage;
    }

    public final void setTotalPackage(int value) {
        if (value >= 0 && value <= 999 && value >= this.getCurrentPackage()) {
            this.m_totalPackage = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "totalPackage must be between 0 - 999 OR greater than/equal to the currentPackage number. Value given was %1$s", value));
        }
    }

    public final int getPackageWeight() {
        return this.m_packageWeight;
    }

    public final void setPackageWeight(int value) {
        if (value >= 0 && value <= 999) {
            this.m_packageWeight = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "PackageWeight must be between 0 and 999. Value given was %1$s", value));
        }
    }

    public final boolean getValidateAddress() {
        return this.m_validateAddress;
    }

    public final void setValidateAddress(boolean value) {
        this.m_validateAddress = value;
    }

    public final String getShipToAddr() {
        return this.m_shipToAddr;
    }

    public final void setShipToAddr(String value) {
        if (value.length() <= 35 && Document.matches(value, "^[A-Z0-9 ]*$")) {
            this.m_shipToAddr = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "ShipToAddr must be 0-35 alphanumeric characters(Alpha must be uppercase). Value given was %1$s", value));
        }
    }

    public final String getShipToCity() {
        return this.m_shipToCity;
    }

    public final void setShipToCity(String value) {
        if (value.length() >= 1 && value.length() <= 20 && Document.matches(value, "^[A-Z0-9 ]*$")) {
            this.m_shipToCity = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "ShipToCity must be 1-20 alphanumeric characters(Alpha must be uppercase). Value given was %1$s", value));
        }
    }

    public final String getShipToState() {
        return this.m_shipToState;
    }

    public final void setShipToState(String value) {
        if (value.length() == 2 && Document.matches(value, "^[A-Z]*$")) {
            this.m_shipToState = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "ShipToState must be 2 uppercase alpha characters. Value given was %1$s", value));
        }
    }
}
