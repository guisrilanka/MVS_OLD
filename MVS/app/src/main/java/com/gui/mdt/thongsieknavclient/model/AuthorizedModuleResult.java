package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 14/7/2016.
 */
public class AuthorizedModuleResult extends BaseResult {

    public boolean EnableMobile;
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
    public String SalespersonCode;
    public String SO_Number_MSO;
    public String SO_Number_MVS;
    public String SI_Number;
    public String Payment_Number;
    public String Transfer_In_Number;
    public String Transfer_Out_Number;
    public boolean EnableLDS;
    public String Stock_Request_No;



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

    /*public String getSalesPersonCode() {
        return SalespersonCode;
    }

    public void setSalesPersonCode(String salespersonCode) {
        SalespersonCode = salespersonCode;
    }*/
    public String getSalespersonCode() {
        return SalespersonCode;
    }

    public void setSalespersonCode(String salespersonCode) {
        SalespersonCode = salespersonCode;
    }

    public String getSO_Number_MSO() {
        return SO_Number_MSO;
    }

    public void setSO_Number_MSO(String SO_Number_MSO) {
        this.SO_Number_MSO = SO_Number_MSO;
    }

    public String getSO_Number_MVS() {
        return SO_Number_MVS;
    }

    public void setSO_Number_MVS(String SO_Number_MVS) {
        this.SO_Number_MVS = SO_Number_MVS;
    }

    public String getSI_Number() {
        return SI_Number;
    }

    public void setSI_Number(String SI_Number) {
        this.SI_Number = SI_Number;
    }

    public String getPayment_Number() {
        return Payment_Number;
    }

    public void setPayment_Number(String payment_Number) {
        Payment_Number = payment_Number;
    }

    public String getTransfer_In_Number() {
        return Transfer_In_Number;
    }

    public void setTransfer_In_Number(String transfer_In_Number) {
        Transfer_In_Number = transfer_In_Number;
    }

    public String getTransfer_Out_Number() {
        return Transfer_Out_Number;
    }

    public void setTransfer_Out_Number(String transfer_Out_Number) {
        Transfer_Out_Number = transfer_Out_Number;
    }

    public boolean isEnableLDS() {
        return EnableLDS;
    }

    public void setEnableLDS(boolean enableLDS) {
        EnableLDS = enableLDS;
    }

    public String getStock_Request_No() {
        return Stock_Request_No;
    }

    public void setStock_Request_No(String stock_Request_No) {
        Stock_Request_No = stock_Request_No;
    }
}
