package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by User on 10/13/2017.
 */

public class SalesOrderImageUploadStatus {

    String soNo;
    String imageName;
    String imageUrl;
    boolean transferred;
    String lastTransferredBy;
    String lastTransferredDateTime;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSoNo() {
        return soNo;
    }

    public void setSoNo(String soNo) {
        this.soNo = soNo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isTransferred() {
        return transferred;
    }

    public void setTransferred(boolean transferred) {
        this.transferred = transferred;
    }

    public String getLastTransferredBy() {
        return lastTransferredBy;
    }

    public void setLastTransferredBy(String lastTransferredBy) {
        this.lastTransferredBy = lastTransferredBy;
    }

    public String getLastTransferredDateTime() {
        return lastTransferredDateTime;
    }

    public void setLastTransferredDateTime(String lastTransferredDateTime) {
        this.lastTransferredDateTime = lastTransferredDateTime;
    }
}
