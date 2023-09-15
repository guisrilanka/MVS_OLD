package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

import java.util.List;

/**
 * Created by yeqim_000 on 26/07/16.
 */
public class PurchaseOrderData {
    private String PurchaseOrderNo;
    private String VendorName;
    private String PurchaseOrderDate;
    private String ReceiveDate;
    private List<LineData> LinesData;

    public List<LineData> getLinesDataList() {
        if(LinesData == null)
        {
            return null;
        }
        else {
            return this.LinesData;
        }

    }

    public void setLinesDataList(List<LineData> lineData) {
        this.LinesData = lineData;
    }

    public String getPurchaseOrderNo() {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        PurchaseOrderNo = purchaseOrderNo;
    }

    public String getPurchaseOrderDate() {
        return PurchaseOrderDate;
    }

    public void setPurchaseOrderDate(String purchaseOrderDate) {
        PurchaseOrderDate = purchaseOrderDate;
    }

    public String getReceiveDate() {
        return ReceiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        ReceiveDate = receiveDate;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }
}
