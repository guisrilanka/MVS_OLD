package com.gui.mdt.thongsieknavclient.model.grnmodels;

import java.util.List;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class ReceiptLineData {
    private String ItemDescription;
    private String ItemNo;
    private String LineNo;
    private String Quantity;
    private String Uom;
    private String QuantityBase;


    private List<ReceiptLotTrackingLines> LotTrackingLines;

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getLineNo() {
        return LineNo;
    }

    public void setLineNo(String lineNo) {
        LineNo = lineNo;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getQuantityBase() {
        return QuantityBase;
    }

    public void setQuantityBase(String quantityBase) {
        QuantityBase = quantityBase;
    }

    public List<ReceiptLotTrackingLines> getLotTrackingLines() {
        if(LotTrackingLines == null)
        {
            return null;
        }
        else
        {
            return this.LotTrackingLines;
        }

    }

    public void setLotTrackingLines(List<ReceiptLotTrackingLines> lotTrackingLines) {
        this.LotTrackingLines = lotTrackingLines;
    }
}
