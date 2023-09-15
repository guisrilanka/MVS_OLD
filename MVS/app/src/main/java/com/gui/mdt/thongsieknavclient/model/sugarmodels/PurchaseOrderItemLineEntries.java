package com.gui.mdt.thongsieknavclient.model.sugarmodels;

import android.content.ClipData;

import com.orm.SugarRecord;

/**
 * Created by yeqim_000 on 18/08/16.
 */
public class PurchaseOrderItemLineEntries extends SugarRecord {
    private String PurchaseOrderNo;
    private String ItemNo;
    private int LineNo;

    private String ProductionDate;
    private String ExpiryDate;
    private String QtyToReceive;
    private String LotNumber;
    private String BarcodeSerialNo;
    private String SerialNo;
    private boolean IsScanned;

    private int EntryIndexNo;
    private boolean IsUploaded;
    private Long TimeStamp;

    public PurchaseOrderItemLineEntries()
    {

    }

    public PurchaseOrderItemLineEntries(String purchaseOrderNo, String itemNo, int lineNo, String productionDate, String expiryDate,
                                        String qtyToReceive, String lotNumber, String barcodeSerialNo, String serialNo, boolean isScanned, int indexNo, boolean isUploaded, Long timeStamp)
    {
        PurchaseOrderNo = purchaseOrderNo;
        ItemNo = itemNo;
        LineNo = lineNo;

        ProductionDate = productionDate;
        ExpiryDate = expiryDate;
        QtyToReceive = qtyToReceive;
        LotNumber = lotNumber;
        BarcodeSerialNo = barcodeSerialNo;
        SerialNo = serialNo;
        IsScanned = isScanned;

        EntryIndexNo = indexNo;
        IsUploaded = isUploaded;
        TimeStamp = timeStamp;
    }

    public String getPurchaseOrderNo()
    {
        return PurchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo)
    {
        this.PurchaseOrderNo = purchaseOrderNo;
    }

    public String getItemNo()
    {
        return ItemNo;
    }

    public void setItemNo(String itemNo)
    {
        this.ItemNo = itemNo;
    }

    public int getLineNo()
    {
        return LineNo;
    }

    public void setLineNo(int lineNo)
    {
        this.LineNo = lineNo;
    }

    public String getProductionDate()
    {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate)
    {
        this.ProductionDate = productionDate;
    }

    public String getExpiryDate()
    {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.ExpiryDate = expiryDate;
    }

    public String getQtyToReceive()
    {
        return QtyToReceive;
    }

    public void setQtyToReceive(String qtyToReceive)
    {
        this.QtyToReceive = qtyToReceive;
    }

    public String getLotNumber()
    {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber)
    {
        this.LotNumber = lotNumber;
    }

    public String getBarcodeSerialNo()
    {
        return BarcodeSerialNo;
    }

    public void setBarcodeSerialNo(String barcodeSerialNo)
    {
        this.BarcodeSerialNo = barcodeSerialNo;
    }

    public String getSerialNo()
    {
        return SerialNo;
    }

    public void setSerialNo(String serialNo)
    {
        this.SerialNo = serialNo;
    }

    public boolean getIsScanned()
    {
        return IsScanned;
    }

    public void setIsScanned(boolean isScanned)
    {
        this.IsScanned = isScanned;
    }

    public int getEntryIndexNo() {return EntryIndexNo; }

    public void setEntryIndexNo(int entryIndexNo)
    {
        EntryIndexNo = entryIndexNo;
    }

    public boolean getIsUploaded()
    {
        return IsUploaded;
    }

    public void setIsUploaded(boolean isUploaded)
    {
        this.IsUploaded = isUploaded;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return " PONo: " + PurchaseOrderNo + " ItemNo: " + ItemNo + " ProdDate: " + ProductionDate + " ExpDate: " + ExpiryDate
                + " QTY: " + QtyToReceive +" LotNo: " + LotNumber;
    }
}
