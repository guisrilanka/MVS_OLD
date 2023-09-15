package com.gui.mdt.thongsieknavclient.model.grnmodels;

/**
 * Created by yeqim_000 on 23/08/16.
 */
public class ReceiptItemEntry {
    public String ItemDescription;
    public String ItemNo;
    public String LineNo;
    public String Quantity;
    public String Uom;
    public boolean IsLotRequired;

    public ReceiptItemEntry(String itemDescription, String itemNo, String lineNo, String quantity, String uom, boolean isLotRequired)
    {
        ItemDescription = itemDescription;
        ItemNo = itemNo;
        LineNo = lineNo;
        Quantity = quantity;
        Uom = uom;
        IsLotRequired = isLotRequired;
    }
}
