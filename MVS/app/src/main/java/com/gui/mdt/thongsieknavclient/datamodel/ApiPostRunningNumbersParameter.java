package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 09/12/2017.
 */

public class ApiPostRunningNumbersParameter {


    String SONumberMSO;
    String SONumberMVS;
    String PaymentNumber;
    String SINumber;
    String SRNumber;
    String UserName;
    String Password;
    String UserCompany;

    public String getSONumberMSO() {
        return SONumberMSO;
    }

    public void setSONumberMSO(String SONumberMSO) {
        this.SONumberMSO = SONumberMSO;
    }

    public String getSONumberMVS() {
        return SONumberMVS;
    }

    public void setSONumberMVS(String SONumberMVS) {
        this.SONumberMVS = SONumberMVS;
    }

    public String getPaymentNumber() {
        return PaymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        PaymentNumber = paymentNumber;
    }

    public String getSINumber() {
        return SINumber;
    }

    public void setSINumber(String SINumber) {
        this.SINumber = SINumber;
    }

    public String getSRNumber() {
        return SRNumber;
    }

    public void setSRNumber(String SRNumber) {
        this.SRNumber = SRNumber;
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

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }
}
