package com.gui.mdt.thongsieknavclient;

import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptItemLotEntry;
import com.gui.mdt.thongsieknavclient.model.grnmodels.ReceiptLotTrackingLines;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderBomListResultData;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.LineData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListResultData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryResultData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeLineData;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialNumber;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.BarcodeSerialRecords;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ItemReclassAddNewLineEntry;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ItemReclassItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ProductionOrderBOMItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.ProductionOrderRoutingItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.PurchaseOrderItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.TransferReceiptItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.TransferShipmentItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.sugarmodels.WarehouseShipmentItemLineEntries;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptLineData;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineData;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentLineData;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqim_000 on 26/07/16.
 */
public class DataManager {

    //PURCHASE ORDER TRANSITION DATA
    public String PurchaseOrderNo = "";
    public LineData transitionData;

    public void setItemTransitionLineData(LineData lineData)
    {
        transitionData = new LineData(lineData);
    }

    public void clearTransitionData()
    {
        PurchaseOrderNo = "";

        transitionData = null;
    }

    //GRN TRANSITION DATA
    public String mPurchaseOrderReceiptNo;
    public String mPurchaseOrderReceiptLineNo;
    public String mPurchaseOrderReceiptDescription;
    public String mPurchaseOrderReceiptItemIDUom;
    public String mPurchaseOrderReceiptQuantityBase;
    public List<ReceiptItemLotEntry> mReceiptItemLotEntries;

    public void CloneReceiptItemLotEntriesData(List<ReceiptLotTrackingLines> lotEntries)
    {
        mReceiptItemLotEntries = new ArrayList<ReceiptItemLotEntry>();

        for(int i = 0; i < lotEntries.size(); i++)
        {
            ReceiptItemLotEntry temp_entry = new ReceiptItemLotEntry(lotEntries.get(i).getProductionDate(), lotEntries.get(i).getExpirationDate(), lotEntries.get(i).getLotNo(), lotEntries.get(i).getQuantity(), lotEntries.get(i).getSerialNo());
            mReceiptItemLotEntries.add(temp_entry);
        }
    }

    public void clearGRNTransitionData()
    {
        mPurchaseOrderReceiptNo = "";
        mPurchaseOrderReceiptLineNo = "";
        mPurchaseOrderReceiptDescription = "";
        mPurchaseOrderReceiptItemIDUom = "";
        mPurchaseOrderReceiptQuantityBase = "";

        mReceiptItemLotEntries = null;
    }


    //TRANSFER-OUT TRANSITION DATA
    public String TransferNo = "";
    public TransferShipmentLineData transitionTransferOutData;
    public TransferReceiptLineData transitionTransferInData;

    public void setTransitionTransferOutData(TransferShipmentLineData lineData)
    {
        transitionTransferOutData = new TransferShipmentLineData(lineData);
    }

    public void setTransitionTransferInData(TransferReceiptLineData lineData)
    {
        transitionTransferInData = new TransferReceiptLineData(lineData);
    }

    public void clearTransitionTransferData()
    {
        TransferNo = "";

        transitionTransferOutData = null;
        transitionTransferInData = null;
    }

    //ITEM RECLASS TRANSITION DATA
    public String ItemReclassificationNo = "";
    public int ItemReclassHeaderLineNo;
    public String PostingDate = "";

    public ItemReclassListResultData itemReclassListResultData;
    public ItemReclassLineData transitionItemReclassLinesData;

    public void setTransitionItemReclassListResultData(ItemReclassListResultData transitionItemReclassData)
    {
        itemReclassListResultData = transitionItemReclassData;
    }

    public void clearItemReclassData()
    {
        ItemReclassificationNo = "";
        itemReclassListResultData = null;
    }

    public void setTransitionItemReclassLinesData(ItemReclassLineData mlineData)
    {
        transitionItemReclassLinesData = new ItemReclassLineData(mlineData);
    }

    public void clearTransitionItemReclassLineData()
    {
        ItemReclassificationNo = "";
        transitionItemReclassLinesData = null;
    }

    //STOCK TAKE DATA
    public StockTakeEntriesListResultData mStockTakeEntryResultData;
    public StockTakeLineData mStockTakeLineData;


    public StockTakeEntriesListResultData getStockTakeListResultData() {
        return mStockTakeEntryResultData;
    }

    public void setStockTakeListResultData(StockTakeEntriesListResultData mStockTakeListResultData) {
        mStockTakeEntryResultData = mStockTakeListResultData;
    }

    public StockTakeLineData getmStockTakeLineData() {
        return mStockTakeLineData;
    }

    public void setmStockTakeLineData(StockTakeLineData xStockTakeLineData) {
        mStockTakeLineData = xStockTakeLineData;
    }

    public void clearTransitionStockTakeListResultData()
    {
        mStockTakeEntryResultData = null;
    }

    public void clearTransitionStockTakeLineData()
    {
        mStockTakeLineData = null;
    }

    //PRODUCTION TRANSITION DATA
    public String ProductionOrderNo = "";
    public String LastLotNo = "";

    //PRODUCTION PASTE GROUP TRANSITION DATA
    public PasteGroupProductionOrderListResultData pasteGroupProductionOrderListResultData;

    public void setPasteGroupProductionOrderListResultData(PasteGroupProductionOrderListResultData data)
    {
        pasteGroupProductionOrderListResultData = new PasteGroupProductionOrderListResultData(data);
    }

    //PRODUCTION FINISHED GOODS TRANSITION DATA
    public FinishedGoodsProductionOrderListResultData finishedGoodsProductionOrderListResultData;

    public void setFinishedGoodsProductionOrderListResultData(FinishedGoodsProductionOrderListResultData data)
    {
        finishedGoodsProductionOrderListResultData = new FinishedGoodsProductionOrderListResultData(data);
    }

    public void clearProductionTransitionData()
    {
        ProductionOrderNo = "";

        pasteGroupProductionOrderListResultData = null;

        finishedGoodsProductionOrderListResultData = null;
    }

    //PRODUCTION BOM TRANSITION DATA
    public ProductionOrderBomListResultData productionOrderBomListResultData;

    public void setProductionOrderBomListResultData(ProductionOrderBomListResultData data)
    {
        productionOrderBomListResultData = new ProductionOrderBomListResultData(data);
    }

    public void clearProductionOrderBomData()
    {
        productionOrderBomListResultData = null;
    }


    //WAREHOUSE TRANSITION DATA
    public String WarehouseShipNo = "";
    public WarehouseShipmentLineData transitionWarehouseShipmentData;

    public void setTransitionWarehouseShipmentData(WarehouseShipmentLineData lineData)
    {
        transitionWarehouseShipmentData = new WarehouseShipmentLineData(lineData);
    }

    public void clearTransitionWarehouseShipmentData()
    {
        WarehouseShipNo = "";

        transitionWarehouseShipmentData = null;
    }


    private static DataManager dataManager;

    public static DataManager getInstance() {
        if(dataManager == null)
        {
            dataManager = new DataManager();
        }

        return dataManager;

    }

    //New added
//    public void AddSerialNumber(String orderNo, String orderType, String b_itemNo, String lineNo, String LotNo, String barcodeSerial)
//    {
//        BarcodeSerialRecords temp_records = new BarcodeSerialRecords(orderNo, orderType, b_itemNo, Integer.parseInt(lineNo), LotNo, barcodeSerial, System.currentTimeMillis());  //, System.currentTimeMillis()
//        temp_records.save();
//    }
//
//    public List<BarcodeSerialRecords> GetSerialNumber(String mDocNo, String orderType, String b_itemNo, String lineNo, String LotNo)
//    {
//
//        List<BarcodeSerialRecords> entryList =
//                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ? and item_no = ? and line_no = ? and lot_no = ?",
//                        mDocNo, orderType, b_itemNo, LotNo);
//
//        return entryList;
//    }
//
//    public void DeleteSerialNumber(String orderNo, String orderType, String b_itemNo, String lotNo)
//    {
//        List<BarcodeSerialRecords> entryList =
//                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ? and item_no = ? and lot_no = ?",
//                        orderNo, orderType, b_itemNo, lotNo);
//
//        if(entryList != null && entryList.size() > 0)
//        {
//            for(int i = 0; i < entryList.size(); i++)
//            {
//                entryList.get(i).delete();
//            }
//        }
//    }

    //Original
    public List<BarcodeSerialRecords> GetSerialRecords(String orderNo, String orderType, String itemNo, String lineNo, String lotNo)
    {

        List<BarcodeSerialRecords> entryList =
                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ? and item_no = ? and line_no = ? and lot_no = ?",
                        orderNo, orderType, itemNo, lineNo, lotNo);

        return entryList;
    }

    public void AddSerialRecord(String orderNo, String orderType, String itemNo, String lineNo, String lotNo, String barcodeSerial)
    {
        BarcodeSerialRecords temp_record = new BarcodeSerialRecords(orderNo, orderType, itemNo, Integer.parseInt(lineNo), lotNo, barcodeSerial, System.currentTimeMillis());
        temp_record.save();
    }

    public void DeleteSerialRecord(String orderNo, String orderType, String itemNo, String lineNo, String lotNo)
    {
        List<BarcodeSerialRecords> entryList =
                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ? and item_no = ? and line_no = ? and lot_no = ?",
                        orderNo, orderType, itemNo, lineNo, lotNo);

        if(entryList != null && entryList.size() > 0)
        {
            for(int i = 0; i < entryList.size(); i++)
            {
                entryList.get(i).delete();
            }
        }
    }

    public void DeleteSerialRecord(String orderNo, String orderType, String itemNo, String lineNo)
    {
        List<BarcodeSerialRecords> entryList =
                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ? and item_no = ? and line_no = ?",
                        orderNo, orderType, itemNo, lineNo);

        if(entryList != null && entryList.size() > 0)
        {
            for(int i = 0; i < entryList.size(); i++)
            {
                entryList.get(i).delete();
            }
        }
    }

    //rememeber that cannot simply query only parameter that you want,must follow exact sequence of original parameter
    public List<BarcodeSerialRecords> mDeleteSerialRecord(String orderNo, String orderType, String itemNo, String lineNo,String lotNo)
    {
        List<BarcodeSerialRecords> entryList =
                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ? and item_no = ? and line_no = ? and lot_no = ?",
                        orderNo, orderType, itemNo, lineNo, lotNo);

        if(entryList != null && entryList.size() > 0)
        {
            for(int i = 0; i < entryList.size(); i++)
            {
                entryList.get(i).delete();
            }
        }
        return entryList;
    }

    public void DeleteSerialRecord(String orderNo, String orderType)
    {
        List<BarcodeSerialRecords> entryList =
                SugarRecord.find(BarcodeSerialRecords.class, "order_no = ? and order_type = ?",
                        orderNo, orderType);

        if(entryList != null && entryList.size() > 0)
        {
            for(int i = 0; i < entryList.size(); i++)
            {
                entryList.get(i).delete();
            }
        }
    }

    // REMOVE OVERDUE DATA IN LOCAL DATABASE, PREVENT "OUT OF MEMORY"
    public void ClearOverdueRecordsData(String OrderType)
    {
        switch(OrderType)
        {
            case ConfigurationManager.ORDER_TYPE_PURCHASE_ORDER:
                List<PurchaseOrderItemLineEntries> purchaseList = SugarRecord.listAll(PurchaseOrderItemLineEntries.class);
                for(int i = 0; i < purchaseList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(purchaseList.get(i).getTimeStamp()) > 5)
                    {
                        purchaseList.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_TRANSFER_SHIPMENT:
                List<TransferShipmentItemLineEntries> transferShipmentList = SugarRecord.listAll(TransferShipmentItemLineEntries.class);
                for(int i = 0; i < transferShipmentList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(transferShipmentList.get(i).getTimeStamp()) > 5)
                    {
                        transferShipmentList.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_TRANSFER_RECEIPT:
                List<TransferReceiptItemLineEntries> transferReceiptList = SugarRecord.listAll(TransferReceiptItemLineEntries.class);
                for(int i = 0; i < transferReceiptList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(transferReceiptList.get(i).getTimeStamp()) > 5)
                    {
                        transferReceiptList.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_WAREHOUSE_SHIPMENT:
                List<WarehouseShipmentItemLineEntries> warehouseShipmentList = SugarRecord.listAll(WarehouseShipmentItemLineEntries.class);
                for(int i = 0; i < warehouseShipmentList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(warehouseShipmentList.get(i).getTimeStamp()) > 5)
                    {
                        warehouseShipmentList.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GROUP:
                List<ProductionOrderBOMItemLineEntries> productionListPaste = SugarRecord.listAll(ProductionOrderBOMItemLineEntries.class);
                for(int i = 0; i < productionListPaste.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(productionListPaste.get(i).getTimeStamp()) > 5)
                    {
                        productionListPaste.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_PRODUCTION_PASTE_GOODS_ROUTING:
                List<ProductionOrderRoutingItemLineEntries> routingListPaste = SugarRecord.listAll(ProductionOrderRoutingItemLineEntries.class);
                for(int i = 0; i < routingListPaste.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(routingListPaste.get(i).getTimeStamp()) > 5)
                    {
                        routingListPaste.get(i).delete();
                    }
                }
                break;

            case ConfigurationManager.ORDER_TYPE_PRODUCTION_FINISHED_GOODS:
                List<ProductionOrderBOMItemLineEntries> productionList = SugarRecord.listAll(ProductionOrderBOMItemLineEntries.class);
                for(int i = 0; i < productionList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(productionList.get(i).getTimeStamp()) > 5)
                    {
                        productionList.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_PRODUCTION_FINISHED_GOODS_ROUTING:
                List<ProductionOrderRoutingItemLineEntries> routingList = SugarRecord.listAll(ProductionOrderRoutingItemLineEntries.class);
                for(int i = 0; i < routingList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(routingList.get(i).getTimeStamp()) > 5)
                    {
                        routingList.get(i).delete();
                    }
                }
                break;
            case ConfigurationManager.ORDER_TYPE_ITEM_RECLASS:
                List<ItemReclassItemLineEntries> itemReclassList = SugarRecord.listAll(ItemReclassItemLineEntries.class);
                for(int i = 0; i < itemReclassList.size(); i++)
                {
                    if(DataConverter.getDaysBetweenTimeRange(itemReclassList.get(i).getTimeStamp()) > 5)
                    {
                        itemReclassList.get(i).delete();
                    }
                }
                break;
            default:
        }

        List<BarcodeSerialRecords> barcodeList = SugarRecord.listAll(BarcodeSerialRecords.class);
        for(int i = 0; i < barcodeList.size(); i++)
        {
            if(DataConverter.getDaysBetweenTimeRange(barcodeList.get(i).getTimeStamp()) > 5)
            {
                barcodeList.get(i).delete();
            }
        }

    }

}
