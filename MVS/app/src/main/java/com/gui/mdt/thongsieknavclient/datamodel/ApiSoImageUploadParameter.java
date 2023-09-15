package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by User on 10/13/2017.
 */

public class ApiSoImageUploadParameter {

    String UserName;
    String Password;
    String UserCompany;
    String SaleOrderNo;
    String EncodedImage;
    String ImageName;

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
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

    public String getSaleOrderNo() {
        return SaleOrderNo;
    }

    public void setSaleOrderNo(String saleOrderNo) {
        SaleOrderNo = saleOrderNo;
    }

    public String getEncodedImage() {
        return EncodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        EncodedImage = encodedImage;
    }
}
