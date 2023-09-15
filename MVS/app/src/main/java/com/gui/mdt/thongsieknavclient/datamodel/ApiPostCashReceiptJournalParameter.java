package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 09/15/2017.
 */

public class ApiPostCashReceiptJournalParameter implements Cloneable{

        String Line_No;
        String Posting_Date;
        String Document_Date;
        String Document_Type;
        String Document_No;
        String External_Document_No;
        String Account_Type;
        String Account_No;
        String Description;
        String Salespers_Purch_Code;
        String Campaign_No;
        float Amount;
        String Gen_Posting_Type;
        String Bal_Account_Type;
        String Bal_Account_No;
        String Currency_Code;
        String UserName;
        String Password;
        String UserCompany;
    String Applies_to_Doc_No;

    public String getLine_No() {
        return Line_No;
    }

    public void setLine_No(String line_No) {
        Line_No = line_No;
    }

    public String getPosting_Date() {
        return Posting_Date;
    }

    public void setPosting_Date(String posting_Date) {
        Posting_Date = posting_Date;
    }

    public String getDocument_Date() {
        return Document_Date;
    }

    public void setDocument_Date(String document_Date) {
        Document_Date = document_Date;
    }

    public String getDocument_Type() {
        return Document_Type;
    }

    public void setDocument_Type(String document_Type) {
        Document_Type = document_Type;
    }

    public String getDocument_No() {
        return Document_No;
    }

    public void setDocument_No(String document_No) {
        Document_No = document_No;
    }

    public String getExternal_Document_No() {
        return External_Document_No;
    }

    public void setExternal_Document_No(String external_Document_No) {
        External_Document_No = external_Document_No;
    }

    public String getAccount_Type() {
        return Account_Type;
    }

    public void setAccount_Type(String account_Type) {
        Account_Type = account_Type;
    }

    public String getAccount_No() {
        return Account_No;
    }

    public void setAccount_No(String account_No) {
        Account_No = account_No;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSalespers_Purch_Code() {
        return Salespers_Purch_Code;
    }

    public void setSalespers_Purch_Code(String salespers_Purch_Code) {
        Salespers_Purch_Code = salespers_Purch_Code;
    }

    public String getCampaign_No() {
        return Campaign_No;
    }

    public void setCampaign_No(String campaign_No) {
        Campaign_No = campaign_No;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getGen_Posting_Type() {
        return Gen_Posting_Type;
    }

    public void setGen_Posting_Type(String gen_Posting_Type) {
        Gen_Posting_Type = gen_Posting_Type;
    }

    public String getBal_Account_Type() {
        return Bal_Account_Type;
    }

    public void setBal_Account_Type(String bal_Account_Type) {
        Bal_Account_Type = bal_Account_Type;
    }

    public String getBal_Account_No() {
        return Bal_Account_No;
    }

    public void setBal_Account_No(String bal_Account_No) {
        Bal_Account_No = bal_Account_No;
    }

    public String getCurrency_Code() {
        return Currency_Code;
    }

    public void setCurrency_Code(String currency_Code) {
        Currency_Code = currency_Code;
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

    public String getApplies_to_Doc_No() {
        return Applies_to_Doc_No;
    }

    public void setApplies_to_Doc_No(String applies_to_Doc_No) {
        Applies_to_Doc_No = applies_to_Doc_No;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }
}
