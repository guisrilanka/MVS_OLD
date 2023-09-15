package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 13/7/2016.
 */
public class AuthenticateUserResult extends BaseResult {
    private boolean IsAuthenticated;
    private int GstPercentage;
    public boolean isAuthenticated() {
        return IsAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        IsAuthenticated = authenticated;
    }

    public int getGstPercentage() {
        return GstPercentage;
    }

    public void setGstPercentage(int gstPercentage) {
        GstPercentage = gstPercentage;
    }
}
