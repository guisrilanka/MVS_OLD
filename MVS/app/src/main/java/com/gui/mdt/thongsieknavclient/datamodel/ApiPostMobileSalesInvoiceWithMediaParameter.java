package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by chamil on 10/13/2023.
 */
public class ApiPostMobileSalesInvoiceWithMediaParameter {
    String documentNo;
    String remark;
    String deliveryDateTime;
    double deliverLongitude;
    double deliverLatitude;
    int status;
    String signature;
     List<ApiSoMediaUploadParameter> mediaList;

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(String deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public double getDeliverLongitude() {
        return deliverLongitude;
    }

    public void setDeliverLongitude(double deliverLongitude) {
        this.deliverLongitude = deliverLongitude;
    }

    public double getDeliverLatitude() {
        return deliverLatitude;
    }

    public void setDeliverLatitude(double deliverLatitude) {
        this.deliverLatitude = deliverLatitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<ApiSoMediaUploadParameter> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<ApiSoMediaUploadParameter> mediaList) {
        this.mediaList = mediaList;
    }
//    public class ApiSoMediaUploadParameter {
//        private String path;
//
//        public String getPath() {
//            return path;
//        }
//
//        public void setPath(String path) {
//            this.path = path;
//        }
//    }

}
