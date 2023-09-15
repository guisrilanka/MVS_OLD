package com.gui.mdt.thongsieknavclient;

/**
 * Created by yeqim_000 on 18/08/16.
 */
public class ConfigurationManager {
    public static final String BARCODE_CHECK_SUM_SYMBOL = "%%";
    public static final int BARCODE_CHECKING_TIMEOUT_VALUE = 1500;
    public static final String BARCODE_DELIMITER = "|";

    public static final int BARCODE_ARRAY_ITEM_NO = 0;
    public static final int BARCODE_ARRAY_QUANTITY = 1;
    public static final int BARCODE_ARRAY_UOM = 2;
    public static final int BARCODE_ARRAY_LOT_NO = 3;
    public static final int BARCODE_ARRAY_EXPIRYDATE = 4;
    public static final int BARCODE_ARRAY_SERIAL = 5;

    public static final boolean DATA_UPLOADED_TRUE = true;
    public static final boolean DATA_UPLOADED_FALSE = false;

    public static final boolean UPDATE_ITEM_QUANTITY_BASE_TRUE = true;
    public static final boolean UPDATE_ITEM_QUANTITY_BASE_FALSE = false;

    public static final int DATA_NO_LOCAL_ENTRY_INDEX = -1;
    public static final int DATA_NO_SERVER_ENTRY_INDEX = -1;

    public static final String ORDER_TYPE_PURCHASE_ORDER = "PO";
    public static final String ORDER_TYPE_TRANSFER_SHIPMENT = "TS";
    public static final String ORDER_TYPE_TRANSFER_RECEIPT = "TR";
    public static final String ORDER_TYPE_PRODUCTION_PASTE_GROUP = "PG";
    public static final String ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING = "PGR";
    public static final String ORDER_TYPE_PRODUCTION_FINISHED_GOODS = "FG";
    public static final String ORDER_TYPE_PRODUCTION_FINISHED_GOODS_ROUTING = "FGR";
    public static final String ORDER_TYPE_WAREHOUSE_SHIPMENT = "WH";

    //New Added
    public static final String ORDER_TYPE_ITEM_RECLASS = "IR";
    public static final String ORDER_TYPE_STOCK_TAKE = "ST";
}
