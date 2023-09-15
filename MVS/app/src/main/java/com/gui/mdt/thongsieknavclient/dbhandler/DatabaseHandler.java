package com.gui.mdt.thongsieknavclient.dbhandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nelin_000 on 07/14/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 167;

    // Database Name
    public static final String DATABASE_NAME = "thongsiek";

    //table names
    public static final String TABLE_CUSTOMER = "customer";
    public static final String TABLE_SYNC_CONFIG = "SyncConfiguration";
    public static final String TABLE_ITEM = "item";
    public static final String TABLE_ITEM_UOM = "itemUom";
    public static final String TABLE_ITEM_CATEGORY = "itemCategory";
    public static final String TABLE_USER = "userSetup";
    public static final String TABLE_SO = "salesOrder";
    public static final String TABLE_SO_LINE = "salesOrderLine";
    public static final String TABLE_SALES_PRICES = "salesPrices";
    public static final String TABLE_ITEM_CROSS_REFERENCE = "itemCrossReference";
    public static final String TABLE_PAYMENT_HEADER = "paymentHeader";
    public static final String TABLE_PAYMENT_LINE = "paymentLine";
    public static final String TABLE_ITEM_BALANCE_PDA = "itemBalancePda";
    public static final String TABLE_GST_POSTING_SETUP = "gstPostingSetup";
    public static final String TABLE_STOCK_TRANSFER = "stockTransfer";
    public static final String TABLE_STOCK_TRANSFER_LINE = "stockTransferLine";
    public static final String TABLE_SO_IMAGE_UPLOAD_STATUS = "salesOrderImageUploadStatus";
    public static final String TABLE_SR = "stockRequest";
    public static final String TABLE_SR_LINE = "stockRequestLine";
    public static final String TABLE_CUSTOMER_SALES_CODE = "customerSalesCode";
    public static final String TABLE_CUSTOMER_TEMPLATE = "customerTemplate";
    public static final String TABLE_STOCK_STATUS = "stockStatus";


    //customer table columns
    public static final String KEY_CUS_KEY = "key";
    public static final String KEY_CUS_NAME = "name";
    public static final String KEY_CUS_PH_NO = "phoneNumber";
    public static final String KEY_CUS_CONTACT = "contact";
    public static final String KEY_CUS_CODE = "code";
    public static final String KEY_CUS_ADDRESS = "address";
    public static final String KEY_CUS_POSTAL_CODE = "postalCode";
    public static final String KEY_CUS_DRIVER_CODE = "driverCode";
    public static final String KEY_CUS_SALESPERSON_CODE = "salespersonCode";
    public static final String KEY_CUS_MINIMUM_SALES_AMOUNT = "minimumSalesAmount";
    public static final String KEY_CUS_BALANCE = "balance";
    public static final String KEY_CUS_CREDIT_LIMIT = "creditLimit";
    public static final String KEY_CUS_DUE_DATE_GRACE_PERIOD = "dueDateGracePeriod";
    public static final String KEY_CUS_PAYMENT_TERMS = "paymentTerms";
    public static final String KEY_CUS_IS_BLOCKED = "isBlocked";
    public static final String KEY_CUS_EMAIL = "email";
    public static final String KEY_CUS_CUSTOMER_PRICE_GROUP = "customerPriceGroup";
    public static final String KEY_CUS_VAT_BUS_GROUP = "vatBusPostingGroup";
    public static final String KEY_CUS_BILL_TO_NO = "billToCustomerNo";
    public static final String KEY_CUS_BILL_TO_NAME = "billToCustomerName";
    public static final String KEY_CUS_REF_NO = "customerReferenceNo";
    public static final String KEY_CUS_NTUC_STORE_CODE = "ntucStoreCode";

    //Sync Config table columns
    public static final String KEY_SYNC_ID = "id";
    public static final String KEY_SYNC_SCOPE = "scope";
    public static final String KEY_SYNC_LAST_SYNC_BY = "lastSyncBy";
    public static final String KEY_SYNC_LAST_SYNC__DATE = "lastSyncDateTime";
    public static final String KEY_SYNC_DATA_COUNT = "dataCount";
    public static final String KEY_SYNC_IS_SUCCESS = "isSuccess";
    //public static final String KEY_SYNC_USER = "userId";

    //Item table columns
    public static final String KEY_ITM_ID = "id";
    public static final String KEY_ITM_CODE = "itemCode";
    public static final String KEY_ITM_DESCRIPTION = "description";
    public static final String KEY_ITM_CATEGORY_CODE = "itemCategoryCode";
    public static final String KEY_ITM_BASE_UOM = "ItemBaseUom";
    public static final String KEY_ITM_PRODUCT_GROUP_CODE = "productGroupCode";
    public static final String KEY_ITM_QTY_ON_PURCH_ORDER = "qtyOnPurchOrder";
    public static final String KEY_ITM_QTY_ON_SALES_ORDER = "qtyOnSalesOrder";
    public static final String KEY_ITM_BLOCKED = "blocked";
    public static final String KEY_ITM_UNIT_PRICE = "unitPrice";
    public static final String KEY_ITM_NET_INVOICED_QTY = "netInvoicedQty";
    public static final String KEY_ITM_IDENTIFIER_CODE = "identifierCode";
    public static final String KEY_ITM_VAT_PROD_GROUP = "VatProdPostingGroup";


    //Item_Uom table columns
    public static final String KEY_UOM_ID = "id";
    public static final String KEY_UOM_ITEM_CODE = "itemCode";
    public static final String KEY_UOM_CODE = "uomCode";
    public static final String KEY_UOM_KEY = "key";
    public static final String KEY_CONVERTION = "convertion";

    //Item_Category table columns
    public static final String KEY_ITEM_CATEGORY_ID = "id";
    public static final String KEY_ITEM_CATEGORY_CODE = "itemCategoryCode";
    public static final String KEY_ITEM_CATEGORY_DESCRIPTION = "description";

    //userSetup table columns
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_ENABLE_MOBILE = "enableMobile";
    public static final String KEY_USER_ENABLE_DELIVERY = "enableDelivery";
    public static final String KEY_USER_ENABLE_VANSALE = "enableVanSale";
    public static final String KEY_USER_ENABLE_MOBILE_SALE = "enableMobileSale";
    public static final String KEY_USER_ENABLE_TRANS_REQ_IN = "enableTransferRequestIn";
    public static final String KEY_USER_ENABLE_TRANS_REQ_OUT = "enableTransferRequestOut";
    public static final String KEY_USER_ENABLE_PAYMENT = "enablePaymentCollection";
    public static final String KEY_USER_ENABLE_ITEM = "enableItems";
    public static final String KEY_USER_ENABLE_CUSTOMER = "enableCustomer";
    public static final String KEY_USER_DRIVER_CODE = "driverCode";
    public static final String KEY_USER_SALES_PERSON_CODE = "salesPersonCode";
    public static final String KEY_USER_SO_RUNNING_NO_MSO = "soRunningNoMso";
    public static final String KEY_USER_SO_RUNNING_NO_MVS = "soRunningNoMvs";
    public static final String KEY_USER_SI_RUNNING_NO = "siRunningNo";
    public static final String KEY_USER_TRANSFER_IN_RUNNING_NO = "transferInRunningNo";
    public static final String KEY_USER_TRANSFER_OUT_RUNNING_NO = "transferOutRunningNo";
    public static final String KEY_USER_PAYMENT_RUNNING_NO = "paymentRunningNo";
    public static final String KEY_USER_IS_INITIAL_SYNC_RUN = "isInitialSyncRun";
    public static final String KEY_USER_ENABLE_LDS = "enableLDS";
    public static final String KEY_USER_SR_RUNNING_NO_MVS = "srRunningNoMvs";
    public static final String KEY_USER_DISPLAY_NAME = "userDisplayName";


    //SO  table columns
    public static final String KEY_SO_ID = "id";
    public static final String KEY_SO_KEY = "key";
    public static final String KEY_SO_NO = "No";
    public static final String KEY_SO_SELL_TO_CUS_NO = "SelltoCustomerNo";
    public static final String KEY_SO_SELL_TO_CUS_NAME = "SelltoCustomerName";
    public static final String KEY_SO_SELL_TO_ADDRESS = "SelltoAddress";
    public static final String KEY_SO_SELL_TO_CONTACT_NO = "SelltoContactNo";
    public static final String KEY_SO_SELL_TO_POST_CODE = "SelltoPostCode";
    public static final String KEY_SO_SELL_TO_CITY = "SelltoCity";
    public static final String KEY_SO_ORDER_DATE = "OrderDate";
    public static final String KEY_SO_DOCUMENT_DATE = "DocumentDate";
    public static final String KEY_SO_REQUESTED_DELIVERY_DATE = "RequestedDeliveryDate";
    public static final String KEY_SO_SHIPMENT_DATE = "ShipmentDate";
    public static final String KEY_SO_EXTERNAL_DOCUMET_NO = "ExternalDocumentNo";
    public static final String KEY_SO_SALESPERSON_CODE = "SalespersonCode";
    public static final String KEY_SO_DRIVER_CODE = "DriverCode";
    public static final String KEY_SO_STATUS = "Status";
    public static final String KEY_SO_CREATED_FROM = "CreatedFrom";
    public static final String KEY_SO_CREATED_BY = "CreatedBy";
    public static final String KEY_SO_CREATED_DATE = "CreatedDateTime";
    public static final String KEY_SO_LAST_MODIFIED_BY = "LastModifiedBy";
    public static final String KEY_SO_LAST_MODIFIED_DATE = "LastModifiedDateTime";
    public static final String KEY_SO_AMOUNT_INCLUDING_VAT = "AmountIncludingVAT";
    public static final String KEY_SO_DUEDATE = "DueDate";
    public static final String KEY_SO_COMMENTS = "Comments";
    public static final String KEY_SO_TRANSFERRED = "Transferred";
    public static final String KEY_SO_LAST_TRANSFERRED_BY = "LastTransferredBy";
    public static final String KEY_SO_LAST_TRANSFERRED_DATETIME = "LastTransferredDateTime";
    public static final String KEY_SO_DELIVERED = "Delivered";
    public static final String KEY_SO_MODULE = "module";
    public static final String KEY_SO_AMOUNT_EXCLUDING_VAT = "AmountExcludingVAT";
    public static final String KEY_SO_VAT_AMOUNT = "VATAmount";
    public static final String KEY_DOCUMENT_TYPE = "DocumentType";
    public static final String KEY_SO_SI_DATE = "SIDate";
    public static final String KEY_SO_IS_DOWNLOADED = "IsDownloaded";
    public static final String KEY_SO_SI_NO = "SINo";
    public static final String KEY_SO_IS_DELETED = "IsDeleted";

    //SO LINE table columns
    public static final String KEY_SO_LINE_ID = "id";
    public static final String KEY_SO_LINE_KEY = "key";
    public static final String KEY_SO_LINE_TYPE = "Type";
    public static final String KEY_SO_LINE_NO = "No";
    public static final String KEY_SO_LINE_CROSS_REF_NO = "CrossReferenceNo";
    public static final String KEY_SO_LINE_DRIVER_CODE = "DriverCode";
    public static final String KEY_SO_LINE_DESCRIPTION = "Description";
    public static final String KEY_SO_LINE_QTY = "Quantity";
    public static final String KEY_SO_LINE_UOM = "UnitofMeasure";
    public static final String KEY_SO_LINE_SALE_PRICE_EXIST = "SalesPriceExist";
    public static final String KEY_SO_LINE_UNIT_PRICE = "UnitPrice";
    public static final String KEY_SO_LINE_AMT = "LineAmount";
    public static final String KEY_SO_LINE_DISC_EXIST = "SalesLineDiscExists";
    public static final String KEY_SO_LINE_DISC_PERCENTAGE = "LineDiscountPercent";
    public static final String KEY_SO_LINE_DISC_AMT = "LineDiscountAmount";
    public static final String KEY_SO_LINE_QTY_TO_INVOICE = "QtytoInvoice";
    public static final String KEY_SO_LINE_QTY_INVOICED = "QuantityInvoiced";
    public static final String KEY_SO_LINE_DOCUMENT_NO = "DocumentNo";
    public static final String KEY_SO_LINE_LINE_NO = "LineNo";
    public static final String KEY_SO_LINE_TOT_AMT_EXCL_VAT = "TotalAmountExclVAT";
    public static final String KEY_SO_LINE_TOT_VAT_AMOUNT = "TotalVATAmount";
    public static final String KEY_SO_LINE_TOT_AMT_INCL_VAT = "TotalAmountInclVAT";
    public static final String KEY_SO_LINE_TAX_PERCENTAGE = "TaxPercentage";
    public static final String KEY_SO_LINE_EXCHANGED_QTY = "ExchangedQty";

    //Sales Prices table columns
    public static final String KEY_SALES_PRICES_KEY = "key";
    //public static final String KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL = "salesCodeFilterCtrl";
    //public static final String KEY_SALES_PRICES_ITEM_NO_FILTER_CTRL = "itemNoFilterCtrl";
    //public static final String KEY_SALES_PRICES_STARTING_DATE_FILTER = "startingDateFilter";
    //public static final String KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL2 = "salesCodeFilterCtrl2";
    public static final String KEY_SALES_PRICES_SALES_TYPE = "salesType";
    public static final String KEY_SALES_PRICES_SALES_CODE = "salesCode";
    public static final String KEY_SALES_PRICES_CUSTOMER_NAME = "customerName";
    public static final String KEY_SALES_PRICES_ITEM_NO = "itemNo";
    public static final String KEY_SALES_PRICES_ITEM_DESCRIPTION = "itemDescription";
    public static final String KEY_SALES_PRICES_VARIANT_CODE = "variantCode";
    public static final String KEY_SALES_PRICES_CURRENCY_CODE = "currencyCode";
    public static final String KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE = "unitOfMeasureCode";
    public static final String KEY_SALES_PRICES_MINIMUM_QUANTITY = "minimumQuantity";
    public static final String KEY_SALES_PRICES_PUBLISHED_PRICE = "publishedPrice";
    public static final String KEY_SALES_PRICES_COST = "cost";
    public static final String KEY_SALES_PRICES_COST_PLUS_PERCENT = "costPlusPercent";
    public static final String KEY_SALES_PRICES_DISCOUNT_AMOUNT = "discountAmount";
    public static final String KEY_SALES_PRICES_UNIT_PRICE = "unitPrice";
    public static final String KEY_SALES_PRICES_STARTING_DATE = "startingDate";
    public static final String KEY_SALES_PRICES_ENDING_DATE = "endingDate";
    public static final String KEY_SALES_PRICES_PRICE_INCLUDES_VAT = "priceIncludesVAT";
    public static final String KEY_SALES_PRICES_ALLOW_LINE_DISC = "allowLineDisc";
    public static final String KEY_SALES_PRICES_ALLOW_INVOICE_DISC = "allowInvoiceDisc";
    public static final String KEY_SALES_PRICES_VAT_BUS_POSTING_GR_PRICE = "VATBusPostingGrPrice";
    public static final String KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE = "itemTemplateSequence";

    //Item Cross Reference table columns
    public static final String KEY_ITEM_CROSS_REFERENCE_KEY = "key";
    public static final String KEY_ITEM_CROSS_REFERENCE_TYPE = "itemCrossReferenceType";
    public static final String KEY_ITEM_CROSS_REFERENCE_TYPE_NO = "itemCrossReferenceTypeNo";
    public static final String KEY_ITEM_CROSS_REFERENCE_NO = "itemCrossReferenceNo";
    public static final String KEY_ITEM_CROSS_REFERENCE_ITEM_NO = "itemNo";
    public static final String KEY_ITEM_CROSS_REFERENCE_VARIANT_CODE = "variantCode";
    public static final String KEY_ITEM_CROSS_REFERENCE_UNIT_OF_MEASURE = "unitOfMeasure";
    public static final String KEY_ITEM_CROSS_REFERENCE_UOM = "itemCrossReferenceUOM";
    public static final String KEY_ITEM_CROSS_REFERENCE_DESCRIPTION = "description";
    public static final String KEY_ITEM_CROSS_REFERENCE_DISCONTINUE_BAR_CODE = "discontinueBarCode";

    //Payment Header tab le columns
    public static final String KEY_PAYMENT_NO = "paymentNo";
    public static final String KEY_PAYMENT_MODULE_ID = "moduleId";
    public static final String KEY_PAYMENT_CUS_CODE = "customerNo";
    public static final String KEY_PAYMENT_CUS_NAME = "customerName";
    public static final String KEY_PAYMENT_DRIVER_CODE = "driverCode";
    public static final String KEY_PAYMENT_SALES_PERSON_CODE = "salesPersonCode";
    public static final String KEY_PAYMENT_TOTAL_AMOUNT = "totalAmount";
    public static final String KEY_PAYMENT_PAYMENT_DATE = "paymentDate";
    public static final String KEY_PAYMENT_CREATED_BY = "createdBy";
    public static final String KEY_PAYMENT_CREATED_DATE = "createdDateTime";
    public static final String KEY_PAYMENT_LAST_MODIFIED_BY = "lastModifiedBy";
    public static final String KEY_PAYMENT_LAST_MODIFIED_DATE = "lastModifiedDateTime";
    public static final String KEY_PAYMENT_TRANSFERRED = "transferred";
    public static final String KEY_PAYMENT_LAST_TRANSFERRED_BY = "lastTransferredBy";
    public static final String KEY_PAYMENT_LAST_TRANSFERRED_DATE = "lastTransferredDateTime";
    public static final String KEY_PAYMENT_EXTERNAL_DOCUMENT_NO = "externalDocumentNo";
    public static final String KEY_PAYMENT_STATUS = "status";
    public static final String KEY_PAYMENT_REFERENCE_AMOUNT = "referenceAmount";


    //Payment Line table columns
    public static final String KEY_PAYMENT_LINE_NO = "lineNo";
    public static final String KEY_PAYMENT_LINE_PAYMENT_NO = "paymentNo";
    public static final String KEY_PAYMENT_LINE_PAYMENT_TYPE = "paymentType";
    public static final String KEY_PAYMENT_LINE_AMOUNT = "amount";
    public static final String KEY_PAYMENT_LINE_REMARK = "remark";
    public static final String KEY_PAYMENT_LINE_CHEQUE_NO = "chequeNo";
    public static final String KEY_PAYMENT_LINE_CHEQUE_DATE = "chequeDate";
    public static final String KEY_PAYMENT_LINE_CREATED_BY = "createdBy";
    public static final String KEY_PAYMENT_LINE_CREATED_DATE = "createdDateTime";
    public static final String KEY_PAYMENT_LINE_LAST_MODIFIED_BY = "lastModifiedBy";
    public static final String KEY_PAYMENT_LINE_LAST_MODIFIED_DATE = "lastModifiedDateTime";
    public static final String KEY_PAYMENT_LINE_TRANSFERRED = "transferred";
    public static final String KEY_PAYMENT_LINE_LAST_TRANSFERRED_BY = "lastTransferredBy";
    public static final String KEY_PAYMENT_LINE_LAST_TRANSFERRED_DATE = "lastTransferredDateTime";
    public static final String KEY_PAYMENT_LINE_CHEQUE_NAME = "chequeName";

    //Item balance pda
    public static final String KEY_ITEM_BAL_PDA_KEY = "key";
    public static final String KEY_ITEM_BAL_PDA_ITEM_NO = "item_No";
    public static final String KEY_ITEM_BAL_PDA_LOC_CODE = "locationCode";
    public static final String KEY_ITEM_BAL_PDA_BIN_CODE = "binCode";
    public static final String KEY_ITEM_BAL_PDA_QTY = "quantity";
    public static final String KEY_ITEM_BAL_PDA_OPEN_QTY = "openQuantity";
    public static final String KEY_ITEM_BAL_PDA_EXCHANGED_QTY = "exchangedQuantity";
    public static final String KEY_ITEM_BAL_PDA_UOM = "unitofMeasureCode";

    //GSTPostingSetup table columns
    public static final String KEY_GST_POSTING_SETUP_KEY = "key";
    public static final String KEY_VAT_BUS_POSTING_GROUP = "VATBusPostingGroup";
    public static final String KEY_VAT_PROD_POSTING_GROUP = "VATProdPostingGroup";
    public static final String KEY_VAT_PERCENT = "VATPercent";

    //stockTransfer table columns
    public static final String KEY_ST_KEY = "key";
    public static final String KEY_ST_NO = "No";
    public static final String KEY_ST_DRIVER_CODE = "DriverCode";
    public static final String KEY_ST_DATE = "StockTransferDate";
    public static final String KEY_ST_DOCUMENT_DATE = "DocumentDate";
    public static final String KEY_ST_TYPE = "StockTransferType";//Stock Transfer In or Stock Transfer Out
    public static final String KEY_ST_TOTAL_QTY = "TotalQuantity";
    public static final String KEY_ST_NO_OF_ITEMS = "NoOfItems";
    public static final String KEY_ST_STATUS = "Status";
    public static final String KEY_ST_TRANSFERRED = "Transferred";
    public static final String KEY_ST_CREATED_BY = "CreatedBy";
    public static final String KEY_ST_CREATED_DATE = "CreatedDateTime";
    public static final String KEY_ST_LAST_MODIFIED_BY = "LastModifiedBy";
    public static final String KEY_ST_LAST_MODIFIED_DATE = "LastModifiedDateTime";
    public static final String KEY_ST_LAST_TRANSFERRED_BY = "LastTransferredBy";
    public static final String KEY_ST_LAST_TRANSFERRED_DATETIME = "LastTransferredDateTime";

    //stockTransferLine table columns
    public static final String KEY_ST_LINE_KEY = "key";
    public static final String KEY_ST_LINE_NO = "StockTransferLineNo";
    public static final String KEY_ST_LINE_ST_NO = "StockTransferNo";
    public static final String KEY_ST_LINE_DRIVER_CODE = "DriverCode";
    public static final String KEY_ST_LINE_QTY = "Quantity";
    public static final String KEY_ST_LINE_UOM = "UnitofMeasure";
    public static final String KEY_ST_LINE_ITEM_NO = "ItemNo";
    public static final String KEY_ST_LINE_ITEM_DESCRIPTION = "ItemDescription";

    //salesOrderImageUploadStatus table columns
    public static final String KEY_SOIUS_KEY = "key";
    public static final String KEY_SOIUS_SO_No = "SoNo";
    public static final String KEY_SOIUS_IMAGE_NAME = "ImageName";
    public static final String KEY_SOIUS_IMAGE_URL = "ImageUrl";
    public static final String KEY_SOIUS_TRANSFERRED = "Transferred";
    public static final String KEY_SOIUS_LAST_TRANSFERRED_BY = "LastTransferredBy";
    public static final String KEY_SOIUS_LAST_TRANSFERRED_DATETIME = "LastTransferredDateTime";

    //stockRequestHeader table columns
    public static final String KEY_SR_ID = "id";
    public static final String KEY_SR_NO = "No";
    public static final String KEY_SR_SELL_TO_CUSTOMER_NO = "sellToCustomerNo";
    public static final String KEY_SR_SELL_TO_CUSTOMER_NAME = "sellToCustomerName";
    public static final String KEY_SR_EXTERNAL_DOCUMENT_NO = "externalDocumentNo";
    public static final String KEY_SR_SELL_TO_POST_CODE = "sellToPostCode";
    public static final String KEY_SR_SELL_TO_COUNTRY_REGION_CODE = "sellToCountryRegionCode";
    public static final String KEY_SR_SELL_TO_CONTRACT = "sellToContact";
    public static final String KEY_SR_BILL_TO_CUSTOMER_NO = "billToCustomerNo";
    public static final String KEY_SR_BILL_TO_NAME = "billToName";
    public static final String KEY_SR_BILL_TO_POST_CODE = "billToPostCode";
    public static final String KEY_SR_BILL_TO_COUNTRY_REGION_CODE = "billToCountryRegionCode";
    public static final String KEY_SR_BILL_TO_CONTACT = "billToContact";
    public static final String KEY_SR_SHIP_TO_CODE = "shipToCode";
    public static final String KEY_SR_SHIP_TO_NAME = "shipToName";
    public static final String KEY_SR_SHIP_TO_POST_CODE = "shipToPostCode";
    public static final String KEY_SR_SHIP_TO_COUNTRY_REGION_CODE = "ShiptoCountryRegionCode";
    public static final String KEY_SR_SHIP_TO_CONTRACT = "shipToContact";
    public static final String KEY_SR_POSTING_DATE = "postingDate";
    public static final String KEY_SR_ORDER_DATE = "orderDate";
    public static final String KEY_SR_SHIPMENT_DATE = "shipmentDate";
    public static final String KEY_SR_LOCATION_CODE = "locationCode";
    public static final String KEY_SR_SALES_PERSON_CODE = "salesPersonCode";
    public static final String KEY_SR_DRIVER_CODE = "driverCode";
    public static final String KEY_SR_STATUS = "status";
    public static final String KEY_SR_AMOUNT_INCL_VAT = "amountInclVAT";
    public static final String KEY_SR_VAT_AMOUNT = "VATAmount";
    public static final String KEY_SR_TRANSFERRED = "transferred";
    public static final String KEY_SR_LAST_TRANSFERRED_BY = "lastTransferredBy";
    public static final String KEY_SR_LAST_TRANSFERRED_DATETIME = "lastTransferredDateTime";
    public static final String KEY_SR_CREATED_BY = "CreatedBy";
    public static final String KEY_SR_CREATED_DATE = "CreatedDateTime";
    public static final String KEY_SR_LAST_MODIFIED_BY = "LastModifiedBy";
    public static final String KEY_SR_LAST_MODIFIED_DATE = "LastModifiedDateTime";
    public static final String KEY_SR_IS_DOWNLOADED = "IsDownloaded";

    //stockRequestLine Table columns
    public static final String KEY_SR_LINE_ID = "id";
    public static final String KEY_SR_LINE_KEY = "key";
    public static final String KEY_SR_LINE_SELL_TO_CUSTOMER_NO = "sellToCustomerNo";
    public static final String KEY_SR_LINE_SHIP_TO_CODE = "shipToCode";
    public static final String KEY_SR_LINE_SALES_PERSON_CODE = "salesPersonCode";
    public static final String KEY_SR_LINE_ORDER_DATE = "orderDate";
    public static final String KEY_SR_LINE_SHIPMENT_DATE = "shipmentDate";
    public static final String KEY_SR_LINE_DUE_DATE = "dueDate";
    public static final String KEY_SR_LINE_GRS_SALES_HEADER_EXTERNAL_DOCUMENT_NO = "grsSalesHeaderExternalDocumentNo";
    public static final String KEY_SR_LINE_CREATED_BY = "createdBy";
    public static final String KEY_SR_LINE_CREATED_DATETIME = "createdDataTime";
    public static final String KEY_SR_LINE_LAST_MODIFIED_BY = "lastModifiedBy";
    public static final String KEY_SR_LINE_LAST_MODIFIED_DATETIME = "lastModifiedDateTime";
    public static final String KEY_SR_LINE_DOCUMENT_NO = "documentNo";
    public static final String KEY_SR_LINE_LINE_NO = "lineNo";
    public static final String KEY_SR_LINE_TYPE = "type";
    public static final String KEY_SR_LINE_ITEM_NO = "itemNo";
    public static final String KEY_SR_LINE_DESCRIPTION = "description";
    public static final String KEY_SR_LINE_LOCATION_CODE = "locationCode";
    public static final String KEY_SR_LINE_QUANTITY = "quantity";
    public static final String KEY_SR_LINE_UNIT_OF_MEASURE_CODE = "unitOfMeasureCode";
    public static final String KEY_SR_LINE_UNIT_PRICE = "unitPrice";
    public static final String KEY_SR_LINE_AMOUNT = "amount";
    public static final String KEY_SR_LINE_LINE_AMOUNT = "lineAmount";
    public static final String KEY_SR_LINE_LINE_DISCOUNT_AMOUNT = "lineDiscountAmount";
    public static final String KEY_SR_LINE_LINE_DISCOUNT_PERCENT = "lineDiscountPercent";
    public static final String KEY_SR_LINE_DRIVER_CODE = "driverCode";
    public static final String KEY_SR_LINE_TOTAL_VAT_AMOUNT = "totalVATAmount";
    public static final String KEY_SR_LINE_TOTAL_AMOUNT_INCL_VAT = "totalAmountInclVAT";
    public static final String KEY_SR_LINE_TAX_PERCENTAGE = "taxPercentage";


    //customerSalesCodes Table columns
    public static final String KEY_CSD_CUSTOMER_NO = "customerNo";
    public static final String KEY_CSD_CODE = "code";
    public static final String KEY_CSD_DESCRIPTION = "description";
    public static final String KEY_CSD_VALID_FROM_DATE = "validFromDate";
    public static final String KEY_CSD_VALID_TO_DATE = "validToDate";
    public static final String KEY_CSD_BLOCKED = "blocked";

    //customerTemplate Table columns
    public static final String KEY_CUS_TEMP_SALES_CODE = "salesCode";
    public static final String KEY_CUS_TEMP_ITEM_NO = "itemNo";
    public static final String KEY_CUS_TEMP_DESCRIPTION = "description";
    public static final String KEY_CUS_TEMP_QUANTITY = "quantity";
    public static final String KEY_CUS_TEMP_ITEM_UOM = "itemUom";

    //stockStatus table columns(TABLE_STOCK_STATUS)
    public static final String KEY_SS_ID = "id";
    public static final String KEY_SS_LOAD_DATE = "loadDate";
    public static final String KEY_SS_IS_DISPATCHED = "isDispatched";
    public static final String KEY_SS_DISPATCHED_TIME = "dispatchedTime";

    /*public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }*/

    public DatabaseHandler(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null,
                DATABASE_VERSION);

    }

   /* public DatabaseHandler(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + "/DataBase/" + File.separator
                + DATABASE_NAME, null, DATABASE_VERSION);

        SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()
                + File.separator + "/DataBase/" + File.separator
                + DATABASE_NAME,null);
    }*/


    //creating table
    @Override
    public void onCreate(SQLiteDatabase db) {


        //customer table create
        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_CUS_KEY + " TEXT PRIMARY KEY,"
                + KEY_CUS_NAME + " TEXT,"
                + KEY_CUS_PH_NO + " TEXT,"
                + KEY_CUS_CONTACT + " TEXT,"
                + KEY_CUS_CODE + " TEXT,"
                + KEY_CUS_ADDRESS + " TEXT,"
                + KEY_CUS_POSTAL_CODE + " TEXT,"
                + KEY_CUS_DRIVER_CODE + " TEXT,"
                + KEY_CUS_SALESPERSON_CODE + " TEXT,"
                + KEY_CUS_MINIMUM_SALES_AMOUNT + " TEXT,"
                + KEY_CUS_BALANCE + " TEXT,"
                + KEY_CUS_CREDIT_LIMIT + " TEXT,"
                + KEY_CUS_DUE_DATE_GRACE_PERIOD + " TEXT,"
                + KEY_CUS_PAYMENT_TERMS + " TEXT,"
                + KEY_CUS_IS_BLOCKED + " TEXT,"
                + KEY_CUS_EMAIL + " TEXT,"
                + KEY_CUS_CUSTOMER_PRICE_GROUP + " TEXT, "
                + KEY_CUS_VAT_BUS_GROUP + " TEXT, "
                + KEY_CUS_BILL_TO_NO + " TEXT, "
                + KEY_CUS_BILL_TO_NAME + " TEXT, "
                + KEY_CUS_REF_NO + " TEXT, "
                + KEY_CUS_NTUC_STORE_CODE + " TEXT "
                + ")";
        db.execSQL(CREATE_CUSTOMER_TABLE);

        //Sync configuration table create
        String CREATE_SYNC_CONFIG_TABLE = "CREATE TABLE " + TABLE_SYNC_CONFIG + "("
                + KEY_SYNC_ID + " INTEGER PRIMARY KEY, "
                + KEY_SYNC_SCOPE + " TEXT, "
                + KEY_SYNC_LAST_SYNC_BY + " TEXT, "
                + KEY_SYNC_LAST_SYNC__DATE + " TEXT, "
                + KEY_SYNC_DATA_COUNT + " TEXT, "
                + KEY_SYNC_IS_SUCCESS + " TEXT "
                //+ KEY_SYNC_USER + " TEXT "
                + ")";
        db.execSQL(CREATE_SYNC_CONFIG_TABLE);

        //Item table create
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM + "("
                + KEY_ITM_ID + " INTEGER PRIMARY KEY, "
                + KEY_ITM_CODE + " TEXT, "
                + KEY_ITM_DESCRIPTION + " TEXT, "
                + KEY_ITM_CATEGORY_CODE + " TEXT, "
                + KEY_ITM_BASE_UOM + " TEXT, "
                + KEY_ITM_PRODUCT_GROUP_CODE + " TEXT, "
                + KEY_ITM_QTY_ON_PURCH_ORDER + " TEXT, "
                + KEY_ITM_QTY_ON_SALES_ORDER + " TEXT, "
                + KEY_ITM_BLOCKED + " TEXT, "
                + KEY_ITM_UNIT_PRICE + " TEXT, "
                + KEY_ITM_NET_INVOICED_QTY + " TEXT, "
                + KEY_ITM_IDENTIFIER_CODE + " TEXT, "
                + KEY_ITM_VAT_PROD_GROUP + " TEXT "
                + ")";
        db.execSQL(CREATE_ITEM_TABLE);

        //Item_Uom table create
        String CREATE_ITEM_UOM_TABLE = "CREATE TABLE " + TABLE_ITEM_UOM + "("
                + KEY_UOM_ID + " INTEGER PRIMARY KEY, "
                + KEY_UOM_ITEM_CODE + " TEXT, "
                + KEY_UOM_CODE + " TEXT, "
                + KEY_UOM_KEY + " TEXT, "
                + KEY_CONVERTION + " TEXT "
                + ")";
        db.execSQL(CREATE_ITEM_UOM_TABLE);

        //Item_category table create
        String CREATE_ITEM_CATEGORY_TABLE = "CREATE TABLE " + TABLE_ITEM_CATEGORY + "("
                + KEY_ITEM_CATEGORY_ID + " INTEGER PRIMARY KEY, "
                + KEY_ITEM_CATEGORY_CODE + " TEXT, "
                + KEY_ITEM_CATEGORY_DESCRIPTION + " TEXT "
                + ")";
        db.execSQL(CREATE_ITEM_CATEGORY_TABLE);


        //UserSetup table create
        String CREATE_USER_SETUP_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY, "
                + KEY_USER_NAME + " TEXT, "
                + KEY_USER_PASSWORD + " TEXT, "
                + KEY_USER_ENABLE_MOBILE + " TEXT, "
                + KEY_USER_ENABLE_DELIVERY + " TEXT, "
                + KEY_USER_ENABLE_VANSALE + " TEXT, "
                + KEY_USER_ENABLE_MOBILE_SALE + " TEXT, "
                + KEY_USER_ENABLE_TRANS_REQ_IN + " TEXT, "
                + KEY_USER_ENABLE_TRANS_REQ_OUT + " TEXT "
                + KEY_USER_ENABLE_PAYMENT + " TEXT, "
                + KEY_USER_ENABLE_LDS + " TEXT, "
                + KEY_USER_ENABLE_ITEM + " TEXT, "
                + KEY_USER_ENABLE_CUSTOMER + " TEXT, "
                + KEY_USER_DRIVER_CODE + " TEXT, "
                + KEY_USER_SO_RUNNING_NO_MSO + " TEXT, "
                + KEY_USER_SO_RUNNING_NO_MVS + " TEXT, "
                + KEY_USER_SI_RUNNING_NO + " TEXT, "
                + KEY_USER_TRANSFER_IN_RUNNING_NO + " TEXT, "
                + KEY_USER_TRANSFER_OUT_RUNNING_NO + " TEXT, "
                + KEY_USER_PAYMENT_RUNNING_NO + " TEXT, "
                + KEY_USER_SALES_PERSON_CODE + " TEXT, "
                + KEY_USER_IS_INITIAL_SYNC_RUN + " TEXT, "
                + KEY_USER_SR_RUNNING_NO_MVS + " TEXT, "
                + KEY_USER_DISPLAY_NAME + " TEXT "
                + ")";
        db.execSQL(CREATE_USER_SETUP_TABLE);

        //so  table create
        String CREATE_SO_HEADER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SO + "("
                + KEY_SO_ID + " INTEGER PRIMARY KEY, "
                + KEY_SO_KEY + " TEXT, "
                + KEY_SO_NO + " TEXT, "
                + KEY_SO_SELL_TO_CUS_NO + " TEXT, "
                + KEY_SO_SELL_TO_CUS_NAME + " TEXT, "
                + KEY_SO_SELL_TO_ADDRESS + " TEXT, "
                + KEY_SO_SELL_TO_CONTACT_NO + " TEXT, "
                + KEY_SO_SELL_TO_POST_CODE + " TEXT, "
                + KEY_SO_DUEDATE + " TEXT, "
                + KEY_SO_SELL_TO_CITY + " TEXT, "
                + KEY_SO_ORDER_DATE + " TEXT, "
                + KEY_SO_DOCUMENT_DATE + " TEXT, "
                + KEY_SO_REQUESTED_DELIVERY_DATE + " TEXT, "
                + KEY_SO_SHIPMENT_DATE + " TEXT, "
                + KEY_SO_EXTERNAL_DOCUMET_NO + " TEXT, "
                + KEY_SO_SALESPERSON_CODE + " TEXT, "
                + KEY_SO_DRIVER_CODE + " TEXT, "
                + KEY_SO_STATUS + " TEXT, "
                + KEY_SO_CREATED_FROM + " TEXT, "
                + KEY_SO_CREATED_BY + " TEXT, "
                + KEY_SO_CREATED_DATE + " TEXT, "
                + KEY_SO_LAST_MODIFIED_BY + " TEXT, "
                + KEY_SO_LAST_MODIFIED_DATE + " TEXT, "
                + KEY_SO_AMOUNT_INCLUDING_VAT + " TEXT, "
                + KEY_SO_COMMENTS + " TEXT, "
                + KEY_SO_TRANSFERRED + " TEXT, "
                + KEY_SO_LAST_TRANSFERRED_BY + " TEXT, "
                + KEY_SO_LAST_TRANSFERRED_DATETIME + " TEXT, "
                + KEY_SO_DELIVERED + " TEXT, "
                + KEY_SO_MODULE + " TEXT, "
                + KEY_SO_AMOUNT_EXCLUDING_VAT + " TEXT, "
                + KEY_SO_VAT_AMOUNT + " TEXT, "
                + KEY_DOCUMENT_TYPE + " INTEGER, "
                + KEY_SO_SI_DATE + " TEXT ,"
                + KEY_SO_IS_DOWNLOADED + " TEXT, "
                + KEY_SO_SI_NO + " TEXT, "
                + KEY_SO_IS_DELETED + " TEXT "
                + ")";
        db.execSQL(CREATE_SO_HEADER_TABLE);

        //so line table create
        String CREATE_SO_LINE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SO_LINE + "("
                + KEY_SO_LINE_ID + " INTEGER PRIMARY KEY, "
                + KEY_SO_LINE_KEY + " TEXT, "
                + KEY_SO_LINE_TYPE + " INTEGER, "
                + KEY_SO_LINE_NO + " TEXT, "
                + KEY_SO_LINE_CROSS_REF_NO + " TEXT, "
                + KEY_SO_LINE_DRIVER_CODE + " TEXT, "
                + KEY_SO_LINE_DESCRIPTION + " TEXT, "
                + KEY_SO_LINE_QTY + " TEXT, "
                + KEY_SO_LINE_UOM + " TEXT, "
                + KEY_SO_LINE_SALE_PRICE_EXIST + " TEXT, "
                + KEY_SO_LINE_UNIT_PRICE + " TEXT, "
                + KEY_SO_LINE_AMT + " TEXT, "
                + KEY_SO_LINE_DISC_EXIST + " TEXT, "
                + KEY_SO_LINE_DISC_PERCENTAGE + " TEXT, "
                + KEY_SO_LINE_DISC_AMT + " TEXT, "
                + KEY_SO_LINE_QTY_TO_INVOICE + " TEXT, "
                + KEY_SO_LINE_QTY_INVOICED + " TEXT, "
                + KEY_SO_LINE_DOCUMENT_NO + " TEXT, "
                + KEY_SO_LINE_LINE_NO + " TEXT, "
                + KEY_SO_LINE_TOT_AMT_EXCL_VAT + " TEXT, "
                + KEY_SO_LINE_TOT_VAT_AMOUNT + " TEXT, "
                + KEY_SO_LINE_TOT_AMT_INCL_VAT + " TEXT, "
                + KEY_SO_LINE_TAX_PERCENTAGE + " TEXT, "
                + KEY_SO_LINE_EXCHANGED_QTY + " TEXT "
                + ")";
        db.execSQL(CREATE_SO_LINE_TABLE);

        //Payment header table create
        String CREATE_PAYMENT_HEADER_TABLE = "CREATE TABLE " + TABLE_PAYMENT_HEADER + "("
                + KEY_PAYMENT_NO + " TEXT PRIMARY KEY, "
                + KEY_PAYMENT_MODULE_ID + " TEXT, "
                + KEY_PAYMENT_CUS_CODE + " TEXT, "
                + KEY_PAYMENT_CUS_NAME + " TEXT, "
                + KEY_PAYMENT_DRIVER_CODE + " TEXT, "
                + KEY_PAYMENT_SALES_PERSON_CODE + " TEXT, "
                + KEY_PAYMENT_TOTAL_AMOUNT + " TEXT, "
                + KEY_PAYMENT_PAYMENT_DATE + " TEXT, "
                + KEY_PAYMENT_CREATED_BY + " TEXT, "
                + KEY_PAYMENT_CREATED_DATE + " TEXT, "
                + KEY_PAYMENT_LAST_MODIFIED_BY + " TEXT, "
                + KEY_PAYMENT_LAST_MODIFIED_DATE + " TEXT, "
                + KEY_PAYMENT_TRANSFERRED + " TEXT, "
                + KEY_PAYMENT_LAST_TRANSFERRED_BY + " TEXT, "
                + KEY_PAYMENT_LAST_TRANSFERRED_DATE + " TEXT, "
                + KEY_PAYMENT_EXTERNAL_DOCUMENT_NO + " TEXT, "
                + KEY_PAYMENT_STATUS + " TEXT, "
                + KEY_PAYMENT_REFERENCE_AMOUNT + " TEXT "
                + ")";
        db.execSQL(CREATE_PAYMENT_HEADER_TABLE);

        //Payment Line table create
        String CREATE_PAYMENT_LINE_TABLE = "CREATE TABLE " + TABLE_PAYMENT_LINE + "("
                + KEY_PAYMENT_LINE_NO + " INTEGER PRIMARY KEY, "
                + KEY_PAYMENT_LINE_PAYMENT_NO + " TEXT, "
                + KEY_PAYMENT_LINE_PAYMENT_TYPE + " INTEGER, "
                + KEY_PAYMENT_LINE_AMOUNT + " TEXT, "
                + KEY_PAYMENT_LINE_REMARK + " TEXT, "
                + KEY_PAYMENT_LINE_CHEQUE_NO + " TEXT, "
                + KEY_PAYMENT_LINE_TRANSFERRED + " TEXT, "
                + KEY_PAYMENT_LINE_LAST_TRANSFERRED_BY + " TEXT, "
                + KEY_PAYMENT_LINE_LAST_TRANSFERRED_DATE + " TEXT, "
                + KEY_PAYMENT_LINE_CHEQUE_DATE + " TEXT, "
                + KEY_PAYMENT_LINE_CHEQUE_NAME + " TEXT "
                + ")";
        db.execSQL(CREATE_PAYMENT_LINE_TABLE);

        //Sales Prices table create
        String CREATE_SALES_PRICES_TABLE = "CREATE TABLE " + TABLE_SALES_PRICES + "("
                + KEY_SALES_PRICES_KEY + " TEXT PRIMARY KEY, "
                //+ KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL + " TEXT, "
                //+ KEY_SALES_PRICES_ITEM_NO_FILTER_CTRL + " TEXT, "
                //+ KEY_SALES_PRICES_STARTING_DATE_FILTER + " TEXT, "
                //+ KEY_SALES_PRICES_SALES_CODE_FILTER_CTRL2 + " TEXT, "
                + KEY_SALES_PRICES_SALES_TYPE + " INTEGER, "
                + KEY_SALES_PRICES_SALES_CODE + " TEXT, "
                + KEY_SALES_PRICES_CUSTOMER_NAME + " TEXT, "
                + KEY_SALES_PRICES_ITEM_NO + " TEXT, "
                + KEY_SALES_PRICES_ITEM_DESCRIPTION + " TEXT, "
                + KEY_SALES_PRICES_VARIANT_CODE + " TEXT, "
                + KEY_SALES_PRICES_CURRENCY_CODE + " TEXT, "
                + KEY_SALES_PRICES_UNIT_OF_MEASURE_CODE + " TEXT, "
                + KEY_SALES_PRICES_MINIMUM_QUANTITY + " TEXT, "
                + KEY_SALES_PRICES_PUBLISHED_PRICE + " TEXT, "
                + KEY_SALES_PRICES_COST + " TEXT, "
                + KEY_SALES_PRICES_COST_PLUS_PERCENT + " TEXT, "
                + KEY_SALES_PRICES_DISCOUNT_AMOUNT + " TEXT, "
                + KEY_SALES_PRICES_UNIT_PRICE + " TEXT, "
                + KEY_SALES_PRICES_STARTING_DATE + " TEXT, "
                + KEY_SALES_PRICES_ENDING_DATE + " TEXT, "
                + KEY_SALES_PRICES_PRICE_INCLUDES_VAT + " TEXT, "
                + KEY_SALES_PRICES_ALLOW_LINE_DISC + " TEXT, "
                + KEY_SALES_PRICES_ALLOW_INVOICE_DISC + " TEXT, "
                + KEY_SALES_PRICES_VAT_BUS_POSTING_GR_PRICE + " TEXT, "
                + KEY_SALES_PRICES_ITEM_TEMPLATE_SEQUENCE + " INTEGER "
                + ")";
        db.execSQL(CREATE_SALES_PRICES_TABLE);

        //Item Cross Reference table create
        String CREATE_ITEM_CROSS_REFERENCE_TABLE = "CREATE TABLE " + TABLE_ITEM_CROSS_REFERENCE + "("
                + KEY_ITEM_CROSS_REFERENCE_KEY + " TEXT PRIMARY KEY, "
                + KEY_ITEM_CROSS_REFERENCE_TYPE + " INTEGER, "
                + KEY_ITEM_CROSS_REFERENCE_TYPE_NO + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_NO + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_ITEM_NO + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_VARIANT_CODE + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_UNIT_OF_MEASURE + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_UOM + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_DESCRIPTION + " TEXT, "
                + KEY_ITEM_CROSS_REFERENCE_DISCONTINUE_BAR_CODE + " TEXT "
                + ")";
        db.execSQL(CREATE_ITEM_CROSS_REFERENCE_TABLE);

        //customer table ItemBalancePda
        String CREATE_ITEM_BALANCE_TABLE = "CREATE TABLE " + TABLE_ITEM_BALANCE_PDA + "("
                + KEY_ITEM_BAL_PDA_KEY + " TEXT,"
                + KEY_ITEM_BAL_PDA_ITEM_NO + " TEXT,"
                + KEY_ITEM_BAL_PDA_LOC_CODE + " TEXT,"
                + KEY_ITEM_BAL_PDA_BIN_CODE + " TEXT,"
                + KEY_ITEM_BAL_PDA_QTY + " TEXT,"
                + KEY_ITEM_BAL_PDA_OPEN_QTY + " TEXT,"
                + KEY_ITEM_BAL_PDA_EXCHANGED_QTY + " TEXT,"
                + KEY_ITEM_BAL_PDA_UOM + " TEXT "
                + ")";
        db.execSQL(CREATE_ITEM_BALANCE_TABLE);

        //customer table TABLE_GST_POSTING_SETUP
        String CREATE_TABLE_GST_POSTING_SETUP = "CREATE TABLE " + TABLE_GST_POSTING_SETUP + "("
                + KEY_GST_POSTING_SETUP_KEY + " TEXT,"
                + KEY_VAT_BUS_POSTING_GROUP + " TEXT,"
                + KEY_VAT_PROD_POSTING_GROUP + " TEXT,"
                + KEY_VAT_PERCENT + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE_GST_POSTING_SETUP);

        //stockTransfer table create
        String CREATE_STOCK_TRANSFER_TABLE = "CREATE TABLE " + TABLE_STOCK_TRANSFER + "("
                + KEY_ST_KEY + " TEXT,"
                + KEY_ST_NO + " TEXT,"
                + KEY_ST_DRIVER_CODE + " TEXT,"
                + KEY_ST_DATE + " TEXT,"
                + KEY_ST_DOCUMENT_DATE + " TEXT,"
                + KEY_ST_TYPE + " TEXT,"
                + KEY_ST_TOTAL_QTY + " TEXT,"
                + KEY_ST_NO_OF_ITEMS + " TEXT,"
                + KEY_ST_STATUS + " TEXT,"
                + KEY_ST_TRANSFERRED + " TEXT, "
                + KEY_ST_CREATED_BY + " TEXT,"
                + KEY_ST_CREATED_DATE + " TEXT,"
                + KEY_ST_LAST_MODIFIED_BY + " TEXT,"
                + KEY_ST_LAST_MODIFIED_DATE + " TEXT,"
                + KEY_ST_LAST_TRANSFERRED_BY + " TEXT, "
                + KEY_ST_LAST_TRANSFERRED_DATETIME + " TEXT "
                + ")";
        db.execSQL(CREATE_STOCK_TRANSFER_TABLE);

        //stockTransferLine table create
        String CREATE_STOCK_TRANSFER_LINE_TABLE = "CREATE TABLE " + TABLE_STOCK_TRANSFER_LINE + "("
                + KEY_ST_LINE_KEY + " TEXT,"
                + KEY_ST_LINE_NO + " TEXT,"
                + KEY_ST_LINE_ST_NO + " TEXT,"
                + KEY_ST_LINE_DRIVER_CODE + " TEXT,"
                + KEY_ST_LINE_QTY + " TEXT,"
                + KEY_ST_LINE_UOM + " TEXT,"
                + KEY_ST_LINE_ITEM_NO + " TEXT,"
                + KEY_ST_LINE_ITEM_DESCRIPTION + " TEXT "
                + ")";
        db.execSQL(CREATE_STOCK_TRANSFER_LINE_TABLE);

        //salesOrderImageUploadStatus table create
        String CREATE_SALES_ORDER_IMAGE_UPLOAD_STATUS_TABLE = "CREATE TABLE " + TABLE_SO_IMAGE_UPLOAD_STATUS + "("
                + KEY_SOIUS_KEY + " INTEGER PRIMARY KEY,"
                + KEY_SOIUS_SO_No + " TEXT,"
                + KEY_SOIUS_IMAGE_NAME + " TEXT,"
                + KEY_SOIUS_IMAGE_URL + " TEXT,"
                + KEY_SOIUS_TRANSFERRED + " TEXT,"
                + KEY_SOIUS_LAST_TRANSFERRED_BY + " TEXT,"
                + KEY_SOIUS_LAST_TRANSFERRED_DATETIME + " TEXT "
                + ")";
        db.execSQL(CREATE_SALES_ORDER_IMAGE_UPLOAD_STATUS_TABLE);

        //stockRequestHeader table create
        String CREATE_STOCK_REQUEST_HEADER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SR + "("
                + KEY_SR_ID + " INTEGER PRIMARY KEY,"
                + KEY_SR_NO + " TEXT,"
                + KEY_SR_SELL_TO_CUSTOMER_NO + " TEXT,"
                + KEY_SR_SELL_TO_CUSTOMER_NAME + " TEXT,"
                + KEY_SR_EXTERNAL_DOCUMENT_NO + " TEXT,"
                + KEY_SR_SELL_TO_POST_CODE + " TEXT,"
                + KEY_SR_SELL_TO_COUNTRY_REGION_CODE + " TEXT,"
                + KEY_SR_SELL_TO_CONTRACT + " TEXT,"
                + KEY_SR_BILL_TO_CUSTOMER_NO + " TEXT,"
                + KEY_SR_BILL_TO_NAME + " TEXT,"
                + KEY_SR_BILL_TO_POST_CODE + " TEXT,"
                + KEY_SR_BILL_TO_COUNTRY_REGION_CODE + " TEXT,"
                + KEY_SR_BILL_TO_CONTACT + " TEXT,"
                + KEY_SR_SHIP_TO_CODE + " TEXT,"
                + KEY_SR_SHIP_TO_NAME + " TEXT,"
                + KEY_SR_SHIP_TO_POST_CODE + " TEXT,"
                + KEY_SR_SHIP_TO_COUNTRY_REGION_CODE + " TEXT,"
                + KEY_SR_SHIP_TO_CONTRACT + " TEXT,"
                + KEY_SR_POSTING_DATE + " TEXT,"
                + KEY_SR_ORDER_DATE + " TEXT,"
                + KEY_SR_SHIPMENT_DATE + " TEXT,"
                + KEY_SR_LOCATION_CODE + " TEXT,"
                + KEY_SR_SALES_PERSON_CODE + " TEXT,"
                + KEY_SR_DRIVER_CODE + " TEXT,"
                + KEY_SR_STATUS + " TEXT ,"
                + KEY_SR_AMOUNT_INCL_VAT + " TEXT ,"
                + KEY_SR_VAT_AMOUNT + " TEXT ,"
                + KEY_SR_TRANSFERRED + " TEXT ,"
                + KEY_SR_LAST_TRANSFERRED_BY + " TEXT ,"
                + KEY_SR_LAST_TRANSFERRED_DATETIME + " TEXT ,"
                + KEY_SR_CREATED_BY + " TEXT ,"
                + KEY_SR_CREATED_DATE + " TEXT ,"
                + KEY_SR_LAST_MODIFIED_BY + " TEXT ,"
                + KEY_SR_LAST_MODIFIED_DATE + " TEXT, "
                + KEY_SR_IS_DOWNLOADED + " TEXT "
                + ")";
        db.execSQL(CREATE_STOCK_REQUEST_HEADER_TABLE);

        //stockRequestLine table create
        String CREATE_STOCK_REQUEST_LINE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SR_LINE + "("
                + KEY_SR_LINE_ID + " INTEGER PRIMARY KEY,"
                + KEY_SR_LINE_KEY + " TEXT,"
                + KEY_SR_LINE_SELL_TO_CUSTOMER_NO + " TEXT,"
                + KEY_SR_LINE_SHIP_TO_CODE + " TEXT,"
                + KEY_SR_LINE_SALES_PERSON_CODE + " TEXT,"
                + KEY_SR_LINE_ORDER_DATE + " TEXT,"
                + KEY_SR_LINE_SHIPMENT_DATE + " TEXT,"
                + KEY_SR_LINE_DUE_DATE + " TEXT,"
                + KEY_SR_LINE_GRS_SALES_HEADER_EXTERNAL_DOCUMENT_NO + " TEXT,"
                + KEY_SR_LINE_CREATED_BY + " TEXT,"
                + KEY_SR_LINE_CREATED_DATETIME + " TEXT,"
                + KEY_SR_LINE_LAST_MODIFIED_BY + " TEXT,"
                + KEY_SR_LINE_LAST_MODIFIED_DATETIME + " TEXT,"
                + KEY_SR_LINE_DOCUMENT_NO + " TEXT,"
                + KEY_SR_LINE_LINE_NO + " TEXT,"
                + KEY_SR_LINE_TYPE + " TEXT,"
                + KEY_SR_LINE_ITEM_NO + " TEXT,"
                + KEY_SR_LINE_DESCRIPTION + " TEXT,"
                + KEY_SR_LINE_LOCATION_CODE + " TEXT,"
                + KEY_SR_LINE_QUANTITY + " TEXT,"
                + KEY_SR_LINE_UNIT_OF_MEASURE_CODE + " TEXT,"
                + KEY_SR_LINE_UNIT_PRICE + " TEXT,"
                + KEY_SR_LINE_AMOUNT + " TEXT,"
                + KEY_SR_LINE_LINE_AMOUNT + " TEXT,"
                + KEY_SR_LINE_LINE_DISCOUNT_AMOUNT + " TEXT,"
                + KEY_SR_LINE_LINE_DISCOUNT_PERCENT + " TEXT,"
                + KEY_SR_LINE_DRIVER_CODE + " TEXT ,"
                + KEY_SR_LINE_TOTAL_VAT_AMOUNT + " TEXT ,"
                + KEY_SR_LINE_TOTAL_AMOUNT_INCL_VAT + " TEXT ,"
                + KEY_SR_LINE_TAX_PERCENTAGE + " TEXT "
                + ")";
        db.execSQL(CREATE_STOCK_REQUEST_LINE_TABLE);

        //customerSalesCode table create
        String CREATE_CUSTOMER_SALES_CODE_TABLE = "CREATE TABLE " + TABLE_CUSTOMER_SALES_CODE + "("
                + KEY_CSD_CUSTOMER_NO + " TEXT,"
                + KEY_CSD_CODE + " TEXT,"
                + KEY_CSD_DESCRIPTION + " TEXT,"
                + KEY_CSD_VALID_FROM_DATE + " TEXT,"
                + KEY_CSD_VALID_TO_DATE + " TEXT,"
                + KEY_CSD_BLOCKED + " TEXT "
                + ")";
        db.execSQL(CREATE_CUSTOMER_SALES_CODE_TABLE);

        //customertEMPLATE table create
        String CREATE_CUSTOMER_TEMPLATE_TABLE = "CREATE TABLE " + TABLE_CUSTOMER_TEMPLATE + "("
                + KEY_CUS_TEMP_SALES_CODE + " TEXT,"
                + KEY_CUS_TEMP_ITEM_NO + " TEXT,"
                + KEY_CUS_TEMP_DESCRIPTION + " TEXT,"
                + KEY_CUS_TEMP_QUANTITY + " TEXT,"
                + KEY_CUS_TEMP_ITEM_UOM + " TEXT "
                + ")";
        db.execSQL(CREATE_CUSTOMER_TEMPLATE_TABLE);

        //stockStatus table create
        String CREATE_STOCK_STATUS_TABLE = "CREATE TABLE " + TABLE_STOCK_STATUS + "("
                + KEY_SS_ID + " INTEGER PRIMARY KEY, "
                + KEY_SS_LOAD_DATE + " TEXT,"
                + KEY_SS_IS_DISPATCHED + " TEXT,"
                + KEY_SS_DISPATCHED_TIME + " TEXT "
                + ")";
        db.execSQL(CREATE_STOCK_STATUS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exist
        if (oldVersion == 166) {
            //this code only for this 166 version . if you new to this remove for
            // next versions
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
            String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                    + KEY_CUS_KEY + " TEXT PRIMARY KEY,"
                    + KEY_CUS_NAME + " TEXT,"
                    + KEY_CUS_PH_NO + " TEXT,"
                    + KEY_CUS_CONTACT + " TEXT,"
                    + KEY_CUS_CODE + " TEXT,"
                    + KEY_CUS_ADDRESS + " TEXT,"
                    + KEY_CUS_POSTAL_CODE + " TEXT,"
                    + KEY_CUS_DRIVER_CODE + " TEXT,"
                    + KEY_CUS_SALESPERSON_CODE + " TEXT,"
                    + KEY_CUS_MINIMUM_SALES_AMOUNT + " TEXT,"
                    + KEY_CUS_BALANCE + " TEXT,"
                    + KEY_CUS_CREDIT_LIMIT + " TEXT,"
                    + KEY_CUS_DUE_DATE_GRACE_PERIOD + " TEXT,"
                    + KEY_CUS_PAYMENT_TERMS + " TEXT,"
                    + KEY_CUS_IS_BLOCKED + " TEXT,"
                    + KEY_CUS_EMAIL + " TEXT,"
                    + KEY_CUS_CUSTOMER_PRICE_GROUP + " TEXT, "
                    + KEY_CUS_VAT_BUS_GROUP + " TEXT, "
                    + KEY_CUS_BILL_TO_NO + " TEXT, "
                    + KEY_CUS_BILL_TO_NAME + " TEXT, "
                    + KEY_CUS_REF_NO + " TEXT, "
                    + KEY_CUS_NTUC_STORE_CODE + " TEXT "
                    + ")";
            db.execSQL(CREATE_CUSTOMER_TABLE);
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_CONFIG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_SO);
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_SO_LINE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_UOM);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT_HEADER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT_LINE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES_PRICES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_CROSS_REFERENCE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_BALANCE_PDA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GST_POSTING_SETUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_TRANSFER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_TRANSFER_LINE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SO_IMAGE_UPLOAD_STATUS);
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_SR);
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_SR_LINE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_SALES_CODE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_TEMPLATE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_STATUS);
            // Create tables again

            onCreate(db);
        }
    }
}