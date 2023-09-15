package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

/**
 * Created by yeqim_000 on 01/09/16.
 */
public class UpdateTransferShipmentLineQuantityParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferNo;
    private int LineNo;
    private Float QuantityToShip;

    public UpdateTransferShipmentLineQuantityParameter(String userCompany, String userName, String password, String transferNo, int lineNo, Float quantityToShip) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferNo = transferNo;
        LineNo = lineNo;
        QuantityToShip = quantityToShip;
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

    public String getTransferNo() {
        return TransferNo;
    }

    public void setTransferNo(String transferNo) {
        TransferNo = transferNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public Float getQuantityToShip() {
        return QuantityToShip;
    }

    public void setQuantityToShip(Float quantityToShip) {
        QuantityToShip = quantityToShip;
    }
}
