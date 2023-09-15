package com.gui.mdt.thongsieknavclient.model.stocktakemodels;

/**
 * Created by user on 12/29/2016.
 */

public class StockTakeEntriesListResultData {

    private int StockTakeHeaderLineNo;
    private String DocumentNo;
    private String LocationCode;
    private boolean ShowBinCode;

    public StockTakeEntriesListResultData(int stockTakeHeaderLineNo, String documentNo, String locationCode, boolean showBinCode) {
        StockTakeHeaderLineNo = stockTakeHeaderLineNo;
        DocumentNo = documentNo;
        LocationCode = locationCode;
        ShowBinCode = showBinCode;
    }

    public int getStockTakeHeaderLineNo() {
        return StockTakeHeaderLineNo;
    }

    public void setStockTakeHeaderLineNo(int stockTakeHeaderLineNo) {
        StockTakeHeaderLineNo = stockTakeHeaderLineNo;
    }

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public boolean isShowBinCode() {
        return ShowBinCode;
    }

    public void setShowBinCode(boolean showBinCode) {
        ShowBinCode = showBinCode;
    }
}
