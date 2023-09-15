package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import com.orm.SugarRecord;

/**
 * Created by yeqim_000 on 30/09/16.
 */
public class BarcodeSerialNumber extends SugarRecord {
    //ORDERTYPE
    // PO = PURCHASE ORDER
    // TS = TRANSFER SHIPMENT
    // TR = TRANSFER RECEIPT
    // PG = PRODUCTION PASTE GROUP
    // PGR = PRODUCTION PASTE GOODS ROUTING
    // FG = PRODUCTION FINISHED GOODS
    // FGR = PRODUCTION FINISHED GOODS ROUTING
    // WH = WAREHOUSE SHIPMENT
    private String DocNo;
    private String OrderType;
    private String ItemNo;
    private int LineNo;
    private String LotNo;
    private String SerialNo;
    private Long TimeStamp;

    public BarcodeSerialNumber() {
    }

    public BarcodeSerialNumber(String docNo, String orderType, String itemNo, int lineNo, String lotNo, String serialNo) {
        DocNo = docNo;
        OrderType = orderType;
        ItemNo = itemNo;
        LineNo = lineNo;
        LotNo = lotNo;
        SerialNo = serialNo;
    }

    public BarcodeSerialNumber(String mDocNo, String orderType, String itemNo, int i, String barcodeSerial) {
        DocNo = mDocNo;
        OrderType = orderType;
        ItemNo = itemNo;
        LineNo = i;
        SerialNo = barcodeSerial;
    }

    public BarcodeSerialNumber(String docNo, String orderType, String itemNo, int lineNo, String lotNo, String serialNo, Long timeStamp) {
        DocNo = docNo;
        OrderType = orderType;
        ItemNo = itemNo;
        LineNo = lineNo;
        LotNo = lotNo;
        SerialNo = serialNo;
        TimeStamp = timeStamp;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
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

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    @Override
    public String toString() {
        return " DocNo: " + DocNo + " OrderType: " + OrderType + " ItemNo: " + ItemNo + " LineNo: " + LineNo + " LotNo: " + LotNo + " SerialNo: " + SerialNo;
    }
}
