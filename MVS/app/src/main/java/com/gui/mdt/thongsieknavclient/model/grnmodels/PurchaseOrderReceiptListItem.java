package com.gui.mdt.thongsieknavclient.model.grnmodels;

/**
 * Created by yeqim_000 on 22/08/16.
 */
public class PurchaseOrderReceiptListItem {
    private String PurchaseOrderReceiptNo;
    private String VendorName;
    private String ReceiveDate;
    private String PurchaseOrderDate;
    private String PurchaseOrderNo;

    public String getPurchaseOrderReceiptNo() {
        return PurchaseOrderReceiptNo;
    }

    public void setPurchaseOrderReceiptNo(String purchaseOrderReceiptNoNo) {
        PurchaseOrderReceiptNo = purchaseOrderReceiptNoNo;
    }

    public String getPurchaseOrderNo() {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        PurchaseOrderNo = purchaseOrderNo;
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
}
