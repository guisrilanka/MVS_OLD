package com.gui.mdt.thongsieknavclient.model.transferinmodels;

/**
 * Created by yeqim_000 on 30/09/16.
 */
public class TransferReceiptAvailableLotNoParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String TransferReceiptNo;
    private int LineNo;

    public TransferReceiptAvailableLotNoParameter(String userCompany, String userName, String password, String transferReceiptNo, int lineNo) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        TransferReceiptNo = transferReceiptNo;
        LineNo = lineNo;
    }
}
