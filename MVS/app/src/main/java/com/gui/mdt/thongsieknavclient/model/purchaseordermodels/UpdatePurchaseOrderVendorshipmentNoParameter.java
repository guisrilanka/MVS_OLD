package com.gui.mdt.thongsieknavclient.model.purchaseordermodels;

/**
 * Created by user on 2/21/2017.
 */

public class UpdatePurchaseOrderVendorshipmentNoParameter {

    private String UserCompany;
    private String UserName;
    private String Password;
    private String PurchaseOrderNo;
    private String VendorShipmentNo;

    public UpdatePurchaseOrderVendorshipmentNoParameter(String userCompany, String userName, String password, String purchaseOrderNo, String vendorShipmentNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        PurchaseOrderNo = purchaseOrderNo;
        VendorShipmentNo = vendorShipmentNo;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPurchaseOrderNo() {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        PurchaseOrderNo = purchaseOrderNo;
    }

    public String getVendorShipmentNo() {
        return VendorShipmentNo;
    }

    public void setVendorShipmentNo(String vendorShipmentNo) {
        VendorShipmentNo = vendorShipmentNo;
    }
}
