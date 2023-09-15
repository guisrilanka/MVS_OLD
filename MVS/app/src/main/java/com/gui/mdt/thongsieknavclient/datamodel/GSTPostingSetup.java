package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by GUI-NB03 on 2017-08-30.
 */

public class GSTPostingSetup {

    //private variables
    private String key;
    private String VATBusPostingGroup;
    private String VATProdPostingGroup;
    private double VATPercent;

    public GSTPostingSetup(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVATBusPostingGroup() {
        return VATBusPostingGroup;
    }

    public void setVATBusPostingGroup(String VATBusPostingGroup) {
        this.VATBusPostingGroup = VATBusPostingGroup;
    }

    public String getVATProdPostingGroup() {
        return VATProdPostingGroup;
    }

    public void setVATProdPostingGroup(String VATProdPostingGroup) {
        this.VATProdPostingGroup = VATProdPostingGroup;
    }

    public double getVATPercent() {
        return VATPercent;
    }

    public void setVATPercent(double VATPercent) {
        this.VATPercent = VATPercent;
    }
}
