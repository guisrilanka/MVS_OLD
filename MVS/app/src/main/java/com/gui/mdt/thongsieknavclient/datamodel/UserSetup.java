package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 07/26/2017.
 */

public class UserSetup {

    public Integer Id;
    public String UserName;
    public String Password;
    public boolean EnableMobile; // will use by sales app
    public boolean EnableDriverAppMobile;
    public boolean EnablePackerAppMobile;
    public boolean EnablePurchaseOrder;
    public boolean EnableGoodsReceive;
    public boolean EnableTransferIn;
    public boolean EnableTransferOut;
    public boolean EnableProduction;
    public boolean EnableDelivery;
    public boolean EnableItemReclass;
    public boolean EnableStockTake;

    //will use  by sales app
    public boolean EnableVanSale;
    public boolean EnableMobileSale;
    public boolean EnableTransferRequestIn;
    public boolean EnableTransferRequestOut;
    public boolean EnablePaymentCollection;
    public boolean EnableItems;
    public boolean EnableCustomer;
    public String DriverCode;
    public String SalesPersonCode;
    public String SoRunningNoMso;
    public String SoRunningNoMvs;
    public String SiRunningNo;
    public String PaymentRunningNo;
    public String TransferInNumber;
    public String TransferOutNumber;
    public boolean IsInitialSyncRun;
    public boolean EnableLDS;
    public String UserDisplayName;

    public String SrRunningNoMvs;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
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

    public boolean isEnableDriverAppMobile() {
        return EnableDriverAppMobile;
    }

    public void setEnableDriverAppMobile(boolean enableDriverAppMobile) {
        EnableDriverAppMobile = enableDriverAppMobile;
    }

    public boolean isEnablePackerAppMobile() {
        return EnablePackerAppMobile;
    }

    public void setEnablePackerAppMobile(boolean enablePackerAppMobile) {
        EnablePackerAppMobile = enablePackerAppMobile;
    }

    public boolean isEnableStockTake() {
        return EnableStockTake;
    }

    public void setEnableStockTake(boolean enableStockTake) {
        EnableStockTake = enableStockTake;
    }

    public boolean isEnableItemReclass() {
        return EnableItemReclass;
    }

    public void setEnableItemReclass(boolean enableItemReclass) {
        EnableItemReclass = enableItemReclass;
    }

    public boolean isEnableMobile() {
        return EnableMobile;
    }

    public void setEnableMobile(boolean enableMobile) {
        EnableMobile = enableMobile;
    }

    public boolean isEnablePurchaseOrder() {
        return EnablePurchaseOrder;
    }

    public void setEnablePurchaseOrder(boolean enablePurchaseOrder) {
        EnablePurchaseOrder = enablePurchaseOrder;
    }

    public boolean isEnableGoodsReceive() {
        return EnableGoodsReceive;
    }

    public void setEnableGoodsReceive(boolean enableGoodsReceive) {
        EnableGoodsReceive = enableGoodsReceive;
    }

    public boolean isEnableTransferIn() {
        return EnableTransferIn;
    }

    public void setEnableTransferIn(boolean enableTransferIn) {
        EnableTransferIn = enableTransferIn;
    }

    public boolean isEnableTransferOut() {
        return EnableTransferOut;
    }

    public void setEnableTransferOut(boolean enableTransferOut) {
        EnableTransferOut = enableTransferOut;
    }

    public boolean isEnableProduction() {
        return EnableProduction;
    }

    public void setEnableProduction(boolean enableProduction) {
        EnableProduction = enableProduction;
    }

    public boolean isEnableDelivery() {
        return EnableDelivery;
    }

    public void setEnableDelivery(boolean enableDelivery) {
        EnableDelivery = enableDelivery;
    }

    public boolean isEnableVanSale() {
        return EnableVanSale;
    }

    public void setEnableVanSale(boolean enableVanSale) {
        EnableVanSale = enableVanSale;
    }

    public boolean isEnableMobileSale() {
        return EnableMobileSale;
    }

    public void setEnableMobileSale(boolean enableMobileSale) {
        EnableMobileSale = enableMobileSale;
    }

    public boolean isEnableTransferRequestIn() {
        return EnableTransferRequestIn;
    }

    public void setEnableTransferRequestIn(boolean enableTransferRequestIn) {
        EnableTransferRequestIn = enableTransferRequestIn;
    }

    public boolean isEnableTransferRequestOut() {
        return EnableTransferRequestOut;
    }

    public void setEnableTransferRequestOut(boolean enableTransferRequestOut) {
        EnableTransferRequestOut = enableTransferRequestOut;
    }

    public boolean isEnablePaymentCollection() {
        return EnablePaymentCollection;
    }

    public void setEnablePaymentCollection(boolean enablePaymentCollection) {
        EnablePaymentCollection = enablePaymentCollection;
    }

    public boolean isEnableItems() {
        return EnableItems;
    }

    public void setEnableItems(boolean enableItems) {
        EnableItems = enableItems;
    }

    public boolean isEnableCustomer() {
        return EnableCustomer;
    }

    public void setEnableCustomer(boolean enableCustomer) {
        EnableCustomer = enableCustomer;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getSalesPersonCode() {
        return SalesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        SalesPersonCode = salesPersonCode;
    }

    public String getSoRunningNoMso() {
        return SoRunningNoMso;
    }

    public void setSoRunningNoMso(String soRunningNoMso) {
        SoRunningNoMso = soRunningNoMso;
    }

    public String getSoRunningNoMvs() {
        return SoRunningNoMvs;
    }

    public void setSoRunningNoMvs(String soRunningNoMvs) {
        SoRunningNoMvs = soRunningNoMvs;
    }

    public String getSiRunningNo() {
        return SiRunningNo;
    }

    public void setSiRunningNo(String siRunningNo) {
        SiRunningNo = siRunningNo;
    }

    public String getPaymentRunningNo() {
        return PaymentRunningNo;
    }

    public void setPaymentRunningNo(String paymentRunningNo) {
        PaymentRunningNo = paymentRunningNo;
    }

    public String getTransferInNumber() {
        return TransferInNumber;
    }

    public void setTransferInNumber(String transferInNumber) {
        TransferInNumber = transferInNumber;
    }

    public String getTransferOutNumber() {
        return TransferOutNumber;
    }

    public void setTransferOutNumber(String transferOutNumber) {
        TransferOutNumber = transferOutNumber;
    }

    public boolean isInitialSyncRun() {
        return IsInitialSyncRun;
    }

    public void setInitialSyncRun(boolean initialSyncRun) {
        IsInitialSyncRun = initialSyncRun;
    }

    public boolean isEnableLDS() {
        return EnableLDS;
    }

    public void setEnableLDS(boolean enableLDS) {
        EnableLDS = enableLDS;
    }

    public String getSrRunningNoMvs() {
        return SrRunningNoMvs;
    }

    public void setSrRunningNoMvs(String srRunningNoMvs) {
        SrRunningNoMvs = srRunningNoMvs;
    }

    public String getUserDisplayName() {
        return UserDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        UserDisplayName = userDisplayName;
    }
}
