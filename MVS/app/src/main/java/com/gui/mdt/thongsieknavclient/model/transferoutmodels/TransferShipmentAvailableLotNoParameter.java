package com.gui.mdt.thongsieknavclient.model.transferoutmodels;

/**
 * Created by yeqim_000 on 06/09/16.
 */
public class TransferShipmentAvailableLotNoParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferShipmentNo;
    private int LineNo;

    public TransferShipmentAvailableLotNoParameter(String userCompany, String userName, String password, String transferShipmentNo, int lineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferShipmentNo = transferShipmentNo;
        LineNo = lineNo;
    }


}
