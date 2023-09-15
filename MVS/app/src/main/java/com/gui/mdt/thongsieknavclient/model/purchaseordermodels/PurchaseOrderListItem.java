package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by yeqim_000 on 20/07/16.
 */
public class PurchaseOrderListItem {

    private String VendorShipmentNo;
    private String PurchaseOrderNo;
    private String VendorName;
    private String ReceiveDate;
    private String PurchaseOrderDate;
    private String PurchaseOrderNo2; // Added in on 24/10/2016

    public String getVendorShipmentNo() {
        return VendorShipmentNo;
    }

    public void setVendorShipmentNo(String vendorShipmentNo) {
        VendorShipmentNo = vendorShipmentNo;
    }

    public String getPurchaseOrderNo() {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        PurchaseOrderNo = purchaseOrderNo;
    }

    public String getPurchaseOrderNo2() {
        return PurchaseOrderNo2;
    }

    public void setPurchaseOrderNo2(String purchaseOrderNo2) {
        PurchaseOrderNo2 = purchaseOrderNo2;
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
