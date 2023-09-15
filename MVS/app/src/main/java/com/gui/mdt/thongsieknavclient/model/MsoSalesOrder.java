package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 7/8/2017.
 */
//TextView tvNo, tvPrice, tvUom, tvQTY, tvTotal, tvItemDescroption;

public class MsoSalesOrder {

    String itemNo;
    String price;
    String uom;
    String QTY;
    String total;
    String itemDescroption;


    public MsoSalesOrder(String itemNo_, String price_, String uom_, String QTY_, String total_, String itemDescroption_) {
        this.price = price_;
        this.itemNo = itemNo_;
        this.uom = uom_;
        this.QTY = QTY_;
        this.total = total_;
        this.itemDescroption = itemDescroption_;

    }

    public String getItemNo() {
        return itemNo;
    }

    public String getPrice() {
        return price;
    }

    public String getUom() {
        return uom;
    }

    public String getQTY() {
        return QTY;
    }

    public String getTotal() {
        return total;
    }

    public String getItemDescroption() {
        return itemDescroption;
    }


}
