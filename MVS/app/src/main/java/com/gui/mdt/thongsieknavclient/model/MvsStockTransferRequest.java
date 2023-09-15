package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by BhanukaBandara on 7/17/17.
 */


/**
 * Created by BhanukaBandara on 7/17/17.
 */

public class MvsStockTransferRequest {

    String itemNo;
    String price;
    String uom;
    String QTY;
    String total;
    String itemDescription;


    public MvsStockTransferRequest(String itemNo_, String uom_, String QTY_, String itemDescription_) {
        this.itemNo = itemNo_;
        this.uom = uom_;
        this.QTY = QTY_;
        this.itemDescription = itemDescription_;

    }



    public String getItemNo() {
        return itemNo;
    }

    public String getUom() {
        return uom;
    }

    public String getQTY() {
        return QTY;
    }

    public String getItemDescription() {
        return itemDescription;
    }


}


