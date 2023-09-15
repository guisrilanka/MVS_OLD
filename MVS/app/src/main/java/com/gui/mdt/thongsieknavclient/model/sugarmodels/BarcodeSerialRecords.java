package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import com.orm.SugarRecord;

/**
 * Created by yeqim_000 on 30/09/16.
 */
public class BarcodeSerialRecords extends SugarRecord {
    //ORDERTYPE
    // PO = PURCHASE ORDER
    // TS = TRANSFER SHIPMENT
    // TR = TRANSFER RECEIPT
    // PG = PRODUCTION PASTE GROUP
    // PGR = PRODUCTION PASTE GOODS ROUTING
    // FG = PRODUCTION FINISHED GOODS
    // FGR = PRODUCTION FINISHED GOODS ROUTING
    // WH = WAREHOUSE SHIPMENT
    // SO = SALES ORDER
    private String OrderNo;
    private String OrderType;
    private String ItemNo;
    private int LineNo;
    private String LotNo;
    private String BarcodeSerialNo;
    private Long TimeStamp;

    public BarcodeSerialRecords(){

    }

    public BarcodeSerialRecords(String orderNo, String orderType, String itemNo, int lineNo, String lotNo, String barcodeSerialNo, Long timeStamp) {
        OrderNo = orderNo;
        OrderType = orderType;
        ItemNo = itemNo;
        LineNo = lineNo;
        LotNo = lotNo;
        BarcodeSerialNo = barcodeSerialNo;
        TimeStamp = timeStamp;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getBarcodeSerialNo() {
        return BarcodeSerialNo;
    }

    public void setBarcodeSerialNo(String barcodeSerialNo) {
        BarcodeSerialNo = barcodeSerialNo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public int getLineNo() {
        return LineNo;
    }

    public void setLineNo(int lineNo) {
        LineNo = lineNo;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return " OrderNo: " + OrderNo + " OrderType: " + OrderType + " ItemNo: " + ItemNo + " LineNo: " + LineNo
                + " LotNo: " + LotNo + " Barcode: " + BarcodeSerialNo;
    }
}
