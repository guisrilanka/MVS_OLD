package com.gui.mdt.thongsieknavclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QrScanHeadModel {
    @SerializedName("S")
    @Expose
    public String a_supplierCode;
    @SerializedName("I")
    @Expose
    public String b_invoiceNo;
    @SerializedName("D")
    @Expose
    public String c_deliveryDate;
    @SerializedName("IT")
    @Expose
    public List<QrScanLineModel> d_iT = null;
    @SerializedName("TN")
    @Expose
    public String e_totalNetQuantity;
    @SerializedName("SU")
    @Expose
    public String f_subTotal;
    @SerializedName("TX")
    @Expose
    public String g_tax;
    @SerializedName("GT")
    @Expose
    public String h_grandTotal;
    @SerializedName("DI")
    @Expose
    public String i_discount;
    @SerializedName("DN")
    @Expose
    public String j_driverName;
    @SerializedName("P")
    @Expose
    public String k_paragraphIndicator;
    @SerializedName("M")
    @Expose
    public String l_maxPargraph;
    @SerializedName("SC")
    @Expose
    public String m_storeCode;


}
