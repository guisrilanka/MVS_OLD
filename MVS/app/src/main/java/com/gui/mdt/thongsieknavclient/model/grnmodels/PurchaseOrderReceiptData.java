package com.gui.mdt.thongsieknavclient.model.grnmodels;

import java.util.List;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class PurchaseOrderReceiptData {
    private String PurchaseOrderReceiptNo;
    private String VendorName;
    private String ReceiveDate;
    private String PurchaseOrderDate;
    private List<ReceiptLineData> LinesData;

    public String getPurchaseOrderReceiptNo() {
        return PurchaseOrderReceiptNo;
    }

    public void setPurchaseOrderReceiptNo(String purchaseOrderReceiptNoNo) {
        PurchaseOrderReceiptNo = purchaseOrderReceiptNoNo;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getReceiveDate() {
        return ReceiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        ReceiveDate = receiveDate;
    }

    public String getPurchaseOrderDate() {
        return PurchaseOrderDate;
    }

    public void setPurchaseOrderDate(String purchaseOrderDate) {
        PurchaseOrderDate = purchaseOrderDate;
    }

    public List<ReceiptLineData> getLinesData() {
        if(LinesData == null)
        {
            return null;
        }
        else
        {
            return this.LinesData;
        }

    }

    public void setLinesData(List<ReceiptLineData> linesData) {
        this.LinesData = linesData;
    }
}
