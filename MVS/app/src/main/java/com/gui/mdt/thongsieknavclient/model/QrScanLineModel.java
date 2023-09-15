package com.gui.mdt.thongsieknavclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QrScanLineModel {
    @SerializedName("N")
    @Expose
    public String a_runningNo;
    @SerializedName("E")
    @Expose
    public String b_itemNo;
    @SerializedName("G")
    @Expose
    public String c_issuedQuantity;
    @SerializedName("R")
    @Expose
    public String d_returnQuantity;
    @SerializedName("U")
    @Expose
    public String e_unitPrice;
    @SerializedName("F")
    @Expose
    public String f_freeGoods;

}
