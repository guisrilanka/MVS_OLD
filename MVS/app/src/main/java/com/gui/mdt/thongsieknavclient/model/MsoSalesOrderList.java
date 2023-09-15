package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 7/7/2017.
 */

public class MsoSalesOrderList {
    String name;
    String soNo;
    String soDate;
    String deliverDate;

    public MsoSalesOrderList(String name_, String soNo_, String soDate_, String deliverDate_ ) {
        this.name=name_;

        this.soNo=soNo_;
        this.soDate=soDate_;
        this.deliverDate=deliverDate_;

    }

    public String getName() {
        return name;
    }

    public String getSoNo() {
        return soNo;
    }

    public String getSoDate() {
        return soDate;
    }

    public String getDeliverDate() {
        return deliverDate;
    }

}
