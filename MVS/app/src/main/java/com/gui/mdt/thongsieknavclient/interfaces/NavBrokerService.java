package com.gui.mdt.thongsieknavclient.interfaces;

import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerLocations;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerSalesCodeParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerSalesCodeResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerTemplateParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerTemplateResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiDriverParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiDriverResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGSTPostingSetupParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiGSTPostingSetupResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemBalancePDAListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemBalancePdaParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemCrossReferenceParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemCrossReferenceResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemImageParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemUomListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiItemUomListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMSDAuthorizedModulesResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiMobileSalesOrderLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostCashReceiptJournalParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostCashReceiptJournalResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostEmailMessageParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileSalesInvoiceLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockRequestHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockRequestHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockRequestLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockRequestLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferLineParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostMobileStockTransferLineResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostRunningNumbersParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostRunningNumbersResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostSalesOrderStatusResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostSalesOrderStatusUpdateParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceItemParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceItemResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiPostedSalesInvoiceResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesOrderHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesOrderResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesPricesListParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSalesPricesResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadListResult;
import com.gui.mdt.thongsieknavclient.datamodel.ApiSoImageUploadParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiStockRequestHeaderParameter;
import com.gui.mdt.thongsieknavclient.datamodel.ApiStockRequestHeaderResponse;
import com.gui.mdt.thongsieknavclient.datamodel.ApiVehicleOpenQuantityResponse;
import com.gui.mdt.thongsieknavclient.datamodel.CustomerLocationCoordinates;
import com.gui.mdt.thongsieknavclient.datamodel.PdfUploadResponse;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserParameter;
import com.gui.mdt.thongsieknavclient.model.AuthenticateUserResult;
import com.gui.mdt.thongsieknavclient.model.AuthorizedModuleResult;
import com.gui.mdt.thongsieknavclient.model.AvailableLotNoResult;
import com.gui.mdt.thongsieknavclient.model.BaseResult;
import com.gui.mdt.thongsieknavclient.model.PostDbBackupResponse;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArParameter;
import com.gui.mdt.thongsieknavclient.model.SalesCustomerArResponse;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PrintPurchaseOrderReceiptLineLabelParameter;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PrintPurchaseOrderReceiptLotNoLabelParameter;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptDataCollection;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListResult;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.grnmodels.PurchaseOrderReceiptRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemCategoryListParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemCategoryListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemParameter;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemPictureResult;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomListResultData;
import com.gui.mdt.thongsieknavclient.model.itemmodels.ItemUomParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CalculateItemReclassQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CalculateItemReclassificationResultData;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CreateItemReclassBaseResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.CreateItemReclassHeaderParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.InsertReclassLineParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineLotEntriesParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassLineUpdateParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassListResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.ItemReclassificationListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.PostItemReclassParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.QueryItemReclassItemParameters;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.QueryItemReclassItemResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.RemoveItemReclassLineParameter;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.RemoveItemReclassLineResult;
import com.gui.mdt.thongsieknavclient.model.itemreclassmodels.RemoveItemReclassLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationBinListResultData;
import com.gui.mdt.thongsieknavclient.model.location.LocationListParameter;
import com.gui.mdt.thongsieknavclient.model.location.LocationListResultData;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderLinePrintLabelParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderRouteLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderRouteLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.FinishedGoodsProductionOrderRouteTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGoodsProductionOrderLinePrintParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderBomListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRouteLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRouteTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.PasteGroupProductionOrderRoutingListResult;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderBomListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderLineLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRoutingEndRouteParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRoutingListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.ProductionOrderRoutingStartRouteParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.RemoveFinishedGoodsProductionOrderRouteLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.RemovePasteGoodsProductionOrderRouteLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.RemoveProductionOrderLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.UpdateProductionOrderBomParameter;
import com.gui.mdt.thongsieknavclient.model.productionordermodels.UpdateProductionOrderLineParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PostPurchaseOrder;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderLineLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderResult;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.PurchaseOrderRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.RemovePurchaseOrderLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.UpdatePurchaseOrderLineQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.purchaseordermodels.UpdatePurchaseOrderVendorshipmentNoParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.InsertStockTakeParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.PostStockTakeParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.QueryStockTakeAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.RemoveStockTakeLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntriesListResultData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryLineLotEntriesParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeEntryParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.StockTakeLineData;
import com.gui.mdt.thongsieknavclient.model.stocktakemodels.UpdateStockTakeLineParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.PostTransferReceipt;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.QueryTransferReceiptLineLotNoResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptListResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptResult;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.TransferReceiptRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.transferinmodels.UpdateTransferReceiptLineQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.PostTransferShipment;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.RemoveTransferShipmentLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentLineLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentListResult;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentResult;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.TransferShipmentRetrieveParameter;
import com.gui.mdt.thongsieknavclient.model.transferoutmodels.UpdateTransferShipmentLineQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.PostWarehouseShipment;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.RemoveWarehouseShipmentLotEntryParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.UpdateWarehouseShipmentLineQuantityParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentAvailableLotNoParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentItemTrackingLineParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentLineLotEntriesResult;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentLineLotEntriesSearchParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentListResult;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentListSearchParameter;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentResult;
import com.gui.mdt.thongsieknavclient.model.warehouseshipmentmodels.WarehouseShipmentRetrieveParameter;
import com.gui.mdt.thongsieknavclient.ui.ApiVehicleOpenQuantityParameters;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by User on 12/7/2016.
 */
public interface NavBrokerService {
    //public String endPoint = "/NavBrokerService/api/NavBrokerService/";
    public String endPoint = "/api/NavBrokerService/";


    @GET(endPoint + "GetCompaines")
    Call<List<String>> GetCompaines();

    @POST(endPoint + "AuthenticateUser")
    Call<AuthenticateUserResult> AuthenticateUser(@Body AuthenticateUserParameter userCredential);

    @POST(endPoint + "GetAuthorizedModules")
    Call<AuthorizedModuleResult> GetAuthorizedModules(@Body AuthenticateUserParameter userCredential);

    @POST(endPoint + "GetMSDAuthorizedModules")
    Call<ApiMSDAuthorizedModulesResponse> GetMSDAuthorizedModules(@Body AuthenticateUserParameter userCredential);


    //=============================================================
    //                         PURCHASE ORDER
    //=============================================================
    @POST("/api/NavBrokerService/PurchaseOrderList")
    Call<PurchaseOrderListResult> GetPurchaseOrderList(@Body PurchaseOrderListSearchParameter searchParameter);

    @POST("/api/NavBrokerService/PurchaseOrder")
    Call<PurchaseOrderResult> GetPurchaseOrderDetails(@Body PurchaseOrderRetrieveParameter retrieveParameter);


    @POST("/api/NavBrokerService/PostPurchaseOrderItemTrackingLine")
    Call<BaseResult> PostPurchaseOrderItemTrackingLine(@Body List<PurchaseOrderItemTrackingLineParameter> updateParameter);

    @POST("/api/NavBrokerService/UpdatePurchaseOrderLine")
    Call<BaseResult> UpdatePurchaseOrderLine(@Body UpdatePurchaseOrderLineQuantityParameter updateParameter);


    @POST("/api/NavBrokerService/PostPurchaseOrder")
    Call<BaseResult> PostPurchaseOrder(@Body PostPurchaseOrder postParameter);

    @POST("/api/NavBrokerService/PurchaseOrderLineLotEntries")
    Call<PurchaseOrderLineLotEntriesResult> GetPurchaseOrderLineLotEntries(@Body PurchaseOrderLineLotEntriesSearchParameter searchParameter);

    @POST("/api/NavBrokerService/RemovePurchaseOrderLotEntry")
    Call<BaseResult> RemovePurchaseOrderLotEntry(@Body RemovePurchaseOrderLotEntryParameter postParameter);

    @POST("/api/NavBrokerService/UpdatePurchaseOrderVendorshipmentNo")
    Call<BaseResult> UpdatePurchaseOrderVendorshipmentNo(@Body UpdatePurchaseOrderVendorshipmentNoParameter updatePurchaseOrderVendorshipmentNoParameter);


    //=============================================================
    //                          GRN
    //=============================================================
    @POST("/api/NavBrokerService/PurchaseOrderReceiptList")
    Call<PurchaseOrderReceiptListResult> GetPurchaseOrderReceiptList(@Body PurchaseOrderReceiptListSearchParameter searchParameter);

    @POST("/api/NavBrokerService/PurchaseOrderReceipt")
    Call<PurchaseOrderReceiptDataCollection> GetPurchaseOrderReceiptDetails(@Body PurchaseOrderReceiptRetrieveParameter retrieveParameter);

    @POST("/api/NavBrokerService/PrintPurchaseOrderReceiptLineLabel")
    Call<BaseResult> PrintPurchaseOrderReceiptLineLabel(@Body PrintPurchaseOrderReceiptLineLabelParameter printParameter);

    @POST("/api/NavBrokerService/PrintPurchaseOrderReceiptLotNoLabel")
    Call<BaseResult> PrintPurchaseOrderReceiptLotNoLabel(@Body PrintPurchaseOrderReceiptLotNoLabelParameter printParameter);


    //=============================================================
    //                         TRANSFER OUT
    //=============================================================
    @POST("/api/NavBrokerService/TransferShipmentList")
    Call<TransferShipmentListResult> GetTransferShipmentList(@Body TransferShipmentListSearchParameter searchParameter);


    @POST("/api/NavBrokerService/TransferShipment")
    Call<TransferShipmentResult> GetTransferShipmentDetails(@Body TransferShipmentRetrieveParameter retrieveParameter);

    @POST("/api/NavBrokerService/UpdateTransferShipmentLine")
    Call<BaseResult> UpdateTransferShipmentLine(@Body UpdateTransferShipmentLineQuantityParameter updateParameter);

    @POST("/api/NavBrokerService/PostTransferShipment")
    Call<BaseResult> PostTransferShipment(@Body PostTransferShipment postParameter);


    @POST("/api/NavBrokerService/PostTransferShipmentItemTrackingLine")
    Call<BaseResult> PostTransferShipmentItemTrackingLine(@Body List<TransferShipmentItemTrackingLineParameter> updateParameter);

    @POST("/api/NavBrokerService/TransferShipmentLineLotEntries")
    Call<TransferShipmentLineLotEntriesResult> GetTransferShipmentLineLotEntries(@Body TransferShipmentLineLotEntriesSearchParameter searchParameter);

    @POST("/api/NavBrokerService/RemoveTransferShipmentLotEntry")
    Call<BaseResult> RemoveTransferShipmentLotEntry(@Body RemoveTransferShipmentLotEntryParameter postParameter);

    @POST("/api/NavBrokerService/QueryTransferShipmentAvailableLotNo")
    Call<AvailableLotNoResult> QueryTransferShipmentAvailableLotNo(@Body TransferShipmentAvailableLotNoParameter retrieveParameter);


    //=============================================================
    //                ITEM BROKER - MODULE WEB SERVICE
    //=============================================================
    @POST("/api/NavBrokerService/ItemCategoryList")
    Call<List<ItemCategoryListResultData>> ItemCategoryList(@Body ItemCategoryListParameter itemCategoryListParameter);

    @POST("/api/NavBrokerService/ItemList")
    Call<List<ItemListResultData>> ItemList(@Body ItemListParameter itemListParameter);

    @POST("/api/NavBrokerService/ItemUomList")
    Call<List<ItemUomListResultData>> ItemUomList(@Body ItemUomParameter itemUomParameter);

    //What to return?
    @POST("/api/NavBrokerService/ItemPicture")
    Call<ItemPictureResult> ItemPicture(@Body ItemParameter itemParameter);

    //What to return?
    @GET("/api/NavBrokerService/ItemPictureGet")
    Call<List<String>> ItemPictureGet();

    //=============================================================
    //             LOCATION BROKER - MODULE WEB SERVICE
    //=============================================================
    @POST("/api/NavBrokerService/LocationList")
    Call<List<LocationListResultData>> LocationList(@Body LocationListParameter locationListParameter);

    @POST("/api/NavBrokerService/LocationBinList")
    Call<List<LocationBinListResultData>> LocationBinList(@Body LocationBinListParameter locationBinListParameter);

    //=============================================================
    //                         ITEM RECLASS - MODULE WEB SERVICE
    //=============================================================
    @POST("/api/NavBrokerService/ItemReclassList")
    Call<ItemReclassListResult> GetItemReclassList(@Body ItemReclassificationListSearchParameter searchParameter);

    @POST("/api/NavBrokerService/CalculateItemReclassQuantity")
    Call<CalculateItemReclassificationResultData> CalculateItemReclassQuantity(@Body CalculateItemReclassQuantityParameter calculateItemReclassQuantityParameter);

    @POST("/api/NavBrokerService/CreateItemReclass")
    Call<CreateItemReclassBaseResult> CreateItemReclass(@Body CreateItemReclassHeaderParameter createParameter);

    @POST("/api/NavBrokerService/QueryItemReclassItemParameters")
    Call<QueryItemReclassItemResult> QueryItemReclassItemParameters(@Body QueryItemReclassItemParameters queryItemReclassItemParameters);

    @POST("/api/NavBrokerService/QueryItemReclassAvailableLotNo")
    Call<AvailableLotNoResult> QueryItemReclassAvailableLotNo(@Body ItemReclassificationAvailableLotNoParameter itemReclassificationAvailableLotNoParameter);

    @POST("/api/NavBrokerService/ItemReclass")
    Call<ItemReclassResult> GetItemReclassLine(@Body ItemReclassParameter itemReclassParameter);

    @POST("/api/NavBrokerService/ItemReclassLineLotEntries")
    Call<ItemReclassLineLotEntriesResult> GetItemReclassLineLotEntries(@Body ItemReclassLineLotEntriesParameter itemReclassLineLotEntriesParameter);

    @POST("/api/NavBrokerService/RemoveItemReclassLineLotEntry")
    Call<BaseResult> RemoveItemReclassLineLotEntry(@Body RemoveItemReclassLotEntryParameter removeItemReclassLotEntryParameter);

    @POST("/api/NavBrokerService/UpdateItemReclassLine")
    Call<BaseResult> UpdateItemReclassLine(@Body ItemReclassLineUpdateParameter itemReclassLineUpdateParameter);

    @POST("/api/NavBrokerService/InsertItemReclassLineLotEntries")
    Call<BaseResult> InsertItemReclassLineLotEntries(@Body List<ItemReclassTrackingLineParameter> itemReclassTrackingLineParameters);

    @POST("/api/NavBrokerService/InsertItemReclassLine")
    Call<BaseResult> InsertItemReclassLine(@Body InsertReclassLineParameter insertReclassLineParameter);

    @POST("/api/NavBrokerService/PostItemReclass")
        //not using
    Call<BaseResult> PostItemReclass(@Body PostItemReclassParameter postItemReclassParameter);

    @POST("/api/NavBrokerService/RemoveItemReclass")
    Call<BaseResult> RemoveItemReclass(@Body ItemReclassParameter itemReclassParameter);

    @POST("/api/NavBrokerService/RemoveItemReclassLine")
    Call<RemoveItemReclassLineResult> RemoveItemReclassLine(@Body RemoveItemReclassLineParameter removeItemReclassLineParameter);

    //=============================================================
    //             STOCK TAKE - MODULE WEB SERVICE
    //=============================================================
    @POST("/api/NavBrokerService/StockTakeEntriesList")
    Call<List<StockTakeEntriesListResultData>> StockTakeEntriesList(@Body StockTakeEntriesListParameter stockTakeEntriesListParameter);

    @POST("/api/NavBrokerService/StockTakeEntry")
    Call<List<StockTakeLineData>> StockTakeLineEntryList(@Body StockTakeEntryParameter stockTakeEntryParameter);

    @POST("/api/NavBrokerService/QueryStockTakeAvailableLotNo")
    Call<AvailableLotNoResult> QueryStockTakeAvailableLotNo(@Body QueryStockTakeAvailableLotNoParameter queryStockTakeAvailableLotNoParameter);

    @POST("/api/NavBrokerService/UpdateStockTakeLine")
    Call<BaseResult> UpdateStockTakeLine(@Body UpdateStockTakeLineParameter updateStockTakeLineParameter);

    @POST("/api/NavBrokerService/StockTakeLineLotEntries")
    Call<StockTakeEntryLineLotEntriesResult> StockTakeLineLotEntries(@Body StockTakeEntryLineLotEntriesParameter stockTakeEntryLineLotEntriesParameter);

    @POST("/api/NavBrokerService/InsertStockTakeLineLotEntries")
    Call<BaseResult> InsertStockTakeLineLotEntries(@Body List<StockTakeItemTrackingLineParameter> stockTakeItemTrackingLineParameters);

    @POST("/api/NavBrokerService/RemoveStockTakeLineLotEntry")
    Call<BaseResult> RemoveStockTakeLineLotEntry(@Body RemoveStockTakeLotEntryParameter removeStockTakeLotEntryParameter);

    @POST("/api/NavBrokerService/PostStockTake")
    Call<BaseResult> PostStockTake(@Body PostStockTakeParameter postStockTakeParameter);

    @POST("/api/NavBrokerService/InsertStockTakeLine")
    Call<BaseResult> InsertStockTakeLine(@Body InsertStockTakeParameter insertStockTakeParameter);


    //Temporary not using for CT Vege until 29/12/2016 - ItemReclass Module
    @POST("/api/NavBrokerService/PostTransferShipmentItemTrackingLine")
    Call<BaseResult> PostStockTakeItemTrackingLine(@Body List<StockTakeItemTrackingLineParameter> updateParameter);


    //=============================================================
    //                         TRANSFER IN
    //=============================================================
    @POST("/api/NavBrokerService/TransferReceiptList")
    Call<TransferReceiptListResult> GetTransferReceiptList(@Body TransferReceiptListSearchParameter searchParameter);

    @POST("/api/NavBrokerService/TransferReceipt")
    Call<TransferReceiptResult> GetTransferReceiptDetails(@Body TransferReceiptRetrieveParameter retrieveParameter);

    @POST("/api/NavBrokerService/UpdateTransferReceiptLine")
    Call<BaseResult> UpdateTransferReceiptLine(@Body UpdateTransferReceiptLineQuantityParameter updateParameter);

    @POST("/api/NavBrokerService/PostTransferReceipt")
    Call<BaseResult> PostTransferReceipt(@Body PostTransferReceipt postParameter);

    @POST("/api/NavBrokerService/QueryTransferReceiptLineLotNo")
    Call<QueryTransferReceiptLineLotNoResult> QueryTransferReceiptAvailableLotNo(@Body TransferReceiptAvailableLotNoParameter retrieveParameter);

    @POST("/api/NavBrokerService/PostTransferReceiptItemTrackingLine")
    Call<BaseResult> PostTransferReceiptItemTrackingLine(@Body List<TransferReceiptItemTrackingLineParameter> updateParameter);


    //==========================================================================
    //                   PRODUCTION PASTE GROUPS & FINISHED GOODS WEB SERVICES
    //==========================================================================
    //PRODUCTION PASTE GROUPS & FINISHED GOODS -> BOM LAYER 2
    @POST(endPoint + "ProductionOrderBom")
    // SHARED WITH PROD. FINISHED GOODS BOM LAYER 2
    Call<PasteGroupProductionOrderBomListResult> GetPasteGroupProductionOrderBom(@Body ProductionOrderBomListSearchParameter searchParameter);

    @POST(endPoint + "UpdateProductionOrderBom")
        // SHARED WITH PROD. FINISHED GOODS BOM LAYER 2
    Call<BaseResult> UpdateProductionOrderBom(@Body UpdateProductionOrderBomParameter updateParameter);

    //=============================================================
    //                   PRODUCTION PASTE GROUPS WEB SERVICES
    //=============================================================
    //LAYER 1
    @POST(endPoint + "ProductionOrderList")
    Call<PasteGroupProductionOrderListResult> GetProductionOrderList(@Body ProductionOrderListSearchParameter searchParameter);

    @POST(endPoint + "UpdateProductionOrderLine")
    Call<BaseResult> UpdateProductionOrderLine(@Body UpdateProductionOrderLineParameter updateParameter);

    //BOM LAYER 3
    @POST(endPoint + "PostProductionOrderItemTrackingLine")
    Call<BaseResult> PostProductionOrderItemTrackingLine(@Body List<ProductionOrderItemTrackingLineParameter> updateParameter);

    @POST(endPoint + "QueryProductionOrderAvailableLotNo")
    Call<AvailableLotNoResult> QueryProductionOrderAvailableLotNo(@Body ProductionOrderAvailableLotNoParameter retrieveParameter);

    @POST(endPoint + "RemoveProductionOrderLotEntry")
    Call<BaseResult> RemoveProductionOrderLotEntry(@Body RemoveProductionOrderLotEntryParameter postParameter);

    @POST(endPoint + "ProductionOrderLotEntries")
    Call<PasteGroupProductionOrderLineLotEntriesResult> GetProductionOrderLotEntries(@Body ProductionOrderLineLotEntriesSearchParameter searchParameter);

    //ROUTING
    @POST(endPoint + "ProductionOrderRoute")
    Call<PasteGroupProductionOrderRoutingListResult> GetProductionOrderRoute(@Body ProductionOrderRoutingListSearchParameter searchParameter);

    @POST(endPoint + "ProductionOrderEndRoute")
    Call<BaseResult> ProductionOrderEndRoute(@Body ProductionOrderRoutingEndRouteParameter routeParameter);

    @POST(endPoint + "ProductionOrderStartRoute")
    Call<BaseResult> ProductionOrderStartRoute(@Body ProductionOrderRoutingStartRouteParameter routeParameter);

    @POST(endPoint + "UpdateProductionOrderRouteLine")
    Call<BaseResult> UpdateProductionOrderRouteLine(@Body UpdateProductionOrderLineParameter updateProductionOrderLineParameter);

    @POST(endPoint + "ProductionOrderLinePrintLabel")
    Call<BaseResult> ProductionOrderLinePrintLabel(@Body PasteGoodsProductionOrderLinePrintParameter printParameter);

    //ROUTING (LAYER 3)
    @POST(endPoint + "RemoveProductionOrderRouteLotEntry")
    Call<BaseResult> RemoveProductionOrderRouteLotEntry(@Body RemovePasteGoodsProductionOrderRouteLotEntryParameter postParameter);

    @POST(endPoint + "PostProductionOrderRouteTrackingLine")
    Call<BaseResult> PostProductionOrderRouteTrackingLine(@Body List<PasteGroupProductionOrderRouteTrackingLineParameter> updateParameter);

    @POST(endPoint + "ProductionOrderRouteLotEntries")
    Call<PasteGroupProductionOrderLineLotEntriesResult> ProductionOrderRouteLotEntries(@Body PasteGroupProductionOrderRouteLotEntriesSearchParameter searchParameter);


    //=================================================================
    //                   PRODUCTION FINISHED GOODS WEB SERVICES
    //=================================================================
    //LAYER 1
    @POST(endPoint + "FinishedGoodsProductionOrderList")
    Call<FinishedGoodsProductionOrderListResult> GetFinishedGoodsProductionOrderList(@Body ProductionOrderListSearchParameter searchParameter);

    @POST(endPoint + "UpdateFinishedGoodsProductionOrderLine")
    Call<BaseResult> UpdateFinishedGoodsProductionOrderLine(@Body UpdateProductionOrderLineParameter updateParameter);

    //BOM LAYER 3
    @POST("/api/NavBrokerService/RemoveFinishedGoodsProductionOrderLotEntry")
    Call<BaseResult> RemoveFinishedGoodsProductionOrderLotEntry(@Body RemoveProductionOrderLotEntryParameter postParameter);

    @POST("/api/NavBrokerService/PostFinishedGoodsProductionOrderItemTrackingLine")
    Call<BaseResult> PostFinishedGoodsProductionOrderItemTrackingLine(@Body List<ProductionOrderItemTrackingLineParameter> updateParameter);

    @POST("/api/NavBrokerService/FinishedGoodsProductionOrderLotEntries")
    Call<PasteGroupProductionOrderLineLotEntriesResult> GetFinishedGoodsProductionOrderLotEntries(@Body ProductionOrderLineLotEntriesSearchParameter searchParameter);

    //ROUTING LAYER
    @POST(endPoint + "FinishedGoodsProductionOrderRoute")
    Call<PasteGroupProductionOrderRoutingListResult> FinishedGoodsProductionOrderRoute(@Body ProductionOrderRoutingListSearchParameter searchParameter);

    @POST(endPoint + "FinishedGoodsProductionOrderStartRoute")
    Call<BaseResult> FinishedGoodsProductionOrderStartRoute(@Body ProductionOrderRoutingStartRouteParameter routeParameter);

    @POST(endPoint + "FinishedGoodsProductionOrderEndRoute")
    Call<BaseResult> FinishedGoodsProductionOrderEndRoute(@Body ProductionOrderRoutingEndRouteParameter routeParameter);

    @POST(endPoint + "FinishedGoodsProductionOrderLinePrintLabel")
    Call<BaseResult> FinishedGoodsProductionOrderLinePrintLabel(@Body FinishedGoodsProductionOrderLinePrintLabelParameter printParameter);

    @POST(endPoint + "FinishedGoodsProductionOrderLinePrintPalletLabel")
    Call<BaseResult> FinishedGoodsProductionOrderLinePrintPalletLabel(@Body FinishedGoodsProductionOrderLinePrintLabelParameter printParameter);

    @POST(endPoint + "UpdateFinishedGoodsProductionOrderRouteLine")
    Call<BaseResult> UpdateFinishedGoodsProductionOrderRouteLine(@Body UpdateProductionOrderLineParameter updateProductionOrderLineParameter);

    //ROUTING (LAYER 3)
    @POST(endPoint + "PostFinishedGoodsProductionOrderRouteTrackingLine")
    Call<BaseResult> PostFinishedGoodsProductionOrderRouteTrackingLine(@Body List<FinishedGoodsProductionOrderRouteTrackingLineParameter> updateParameter);

    @POST("/api/NavBrokerService/FinishedGoodsProductionOrderRouteLotEntries")
    Call<FinishedGoodsProductionOrderRouteLotEntriesResult> GetFinishedGoodsProductionOrderRouteLotEntries(@Body FinishedGoodsProductionOrderRouteLotEntriesSearchParameter searchParameter);

    @POST("/api/NavBrokerService/RemoveFinishedGoodsProductionOrderRouteLotEntry")
    Call<BaseResult> RemoveFinishedGoodsProductionOrderRouteLotEntry(@Body RemoveFinishedGoodsProductionOrderRouteLotEntryParameter postParameter);

    @POST("/api/NavBrokerService/QueryFinishedGoodsProductionOrderAvailableLotNo")
    Call<AvailableLotNoResult> QueryFinishedGoodsProductionOrderAvailableLotNo(@Body ProductionOrderAvailableLotNoParameter retrieveParameter);

    //=============================================================
    //           PRODUCTION FINISHED GOODS ROUTING (LAYER 3)
    //=============================================================
    /*@POST(endPoint + "PostPasteGoodsProductionOrderRouteTrackingLine")
    Call<BaseResult> PostPasteGoodsProductionOrderRouteTrackingLine(@Body List<PasteGoodsProductionOrderRouteTrackingLineParameter> updateParameter);

    @POST("/api/NavBrokerService/PasteGoodsProductionOrderRouteLotEntries")
    Call<PasteGroupProductionOrderLineLotEntriesResult> GetPasteGoodsProductionOrderRouteLotEntries(@Body FinishedGoodsProductionOrderRouteLotEntriesSearchParameter searchParameter);

    @POST("/api/NavBrokerService/RemoveFinishedGoodsProductionOrderRouteLotEntry")
    Call<BaseResult> RemoveFinishedGoodsProductionOrderRouteLotEntry(@Body RemoveFinishedGoodsProductionOrderRouteLotEntryParameter postParameter);*/


    //=============================================================
    //                      WAREHOUSE SHIPMENT
    //=============================================================
    @POST("/api/NavBrokerService/WarehouseShipmentList")
    Call<WarehouseShipmentListResult> GetWarehouseShipmentList(@Body WarehouseShipmentListSearchParameter searchParameter);


    @POST("/api/NavBrokerService/WarehouseShipment")
    Call<WarehouseShipmentResult> GetWarehouseShipmentDetails(@Body WarehouseShipmentRetrieveParameter retrieveParameter);

    @POST("/api/NavBrokerService/UpdateWarehouseShipmentLine")
    Call<BaseResult> UpdateWarehouseShipmentLine(@Body UpdateWarehouseShipmentLineQuantityParameter updateParameter);

    @POST("/api/NavBrokerService/PostWarehouseShipment")
    Call<BaseResult> PostWarehouseShipment(@Body PostWarehouseShipment postParameter);


    @POST("/api/NavBrokerService/PostWarehouseShipmentItemTrackingLine")
    Call<BaseResult> PostWarehouseShipmentItemTrackingLine(@Body List<WarehouseShipmentItemTrackingLineParameter> updateParameter);

    @POST("/api/NavBrokerService/WarehouseShipmentLineLotEntries")
    Call<WarehouseShipmentLineLotEntriesResult> GetWarehouseShipmentLineLotEntries(@Body WarehouseShipmentLineLotEntriesSearchParameter searchParameter);

    @POST("/api/NavBrokerService/RemoveWarehouseShipmentLotEntry")
    Call<BaseResult> RemoveWarehouseShipmentLotEntry(@Body RemoveWarehouseShipmentLotEntryParameter postParameter);

    @POST("/api/NavBrokerService/QueryWarehouseShipmentAvailableLotNo")
    Call<AvailableLotNoResult> QueryWarehouseShipmentAvailableLotNo(@Body WarehouseShipmentAvailableLotNoParameter retrieveParameter);


    //=============================================================
    //                      SALES MODULE - COMMON
    //=============================================================
    @POST(endPoint + "customer")
    Call<ApiCustomerListResult> GetCustomers(@Body ApiCustomerListParameter params);

    @POST(endPoint + "SalesOrder")
    Call<ApiSalesOrderResult> GetSalesOrders(@Body ApiSalesOrderHeaderParameter params);

    /*@POST(endPoint +"MobileSalesOrderHeader")
    Call<ApiMobileSalesOrderHeaderResponse> GetMvsSalesOrders(@Body ApiMobileSalesOrderHeaderParameter params);

    @POST(endPoint +"MobileSalesOrderLine")
    Call<ApiMobileSalesOrderLineResponse> GetMvsSalesOrderLines(@Body ApiMobileSalesOrderLineParameter params);*/

    //MVS sales order download
    @POST(endPoint + "MobileBlanketOrderHeader")
    Call<ApiMobileSalesOrderHeaderResponse> GetMvsSalesOrders(@Body ApiMobileSalesOrderHeaderParameter params);

    @POST(endPoint + "MobileBlanketOrderLine")
    Call<ApiMobileSalesOrderLineResponse> GetMvsSalesOrderLines(@Body ApiMobileSalesOrderLineParameter params);

    @POST(endPoint + "ItemPicture")
    Call<ResponseBody> GetItemPicture(@Body ApiItemImageParameter params);

    @POST(endPoint + "PostedSalesInvoice")
    Call<ApiPostedSalesInvoiceResponse> GetPostedSalesInvoice(@Body ApiPostedSalesInvoiceParameter params);

    @POST(endPoint + "ARInvoice")
    Call<SalesCustomerArResponse> GetSalesCustomerAr(@Body SalesCustomerArParameter params);

    @POST("/api/NavBrokerService/SalesPrice")
    Call<ApiSalesPricesResponse> GetSalesPrices(@Body ApiSalesPricesListParameter params);

    @POST("/api/NavBrokerService/ItemCrossReference")
    Call<List<ApiItemCrossReferenceResponse>> GetItemCrossReference(@Body ApiItemCrossReferenceParameter params);

    @POST(endPoint + "ItemFullList")
    Call<ApiItemListResult> GetItems(@Body ApiItemListParameter params);

    @POST("/api/NavBrokerService/ItemUom")
    Call<ApiItemUomListResult> GetItemUomList(@Body ApiItemUomListParameter params);

    @POST("/api/NavBrokerService/ItemBalancePDA")
    Call<ApiItemBalancePDAListResult> GetItemBalancePDA(@Body ApiItemBalancePdaParameter params);

    @POST("/api/NavBrokerService/VehicleOpenQuantity")
    Call<ApiVehicleOpenQuantityResponse> GetVehicleOpenQuantity(@Body ApiVehicleOpenQuantityParameters params);

    @POST(endPoint + "PostMobileSalesInvoiceHeader")
    Call<ApiPostMobileSalesInvoiceHeaderResponse> PostMobileSalesInvoiceHeader(@Body List<ApiPostMobileSalesInvoiceHeaderParameter> params);

    @POST(endPoint + "PostMobileSalesInvoiceLine")
    Call<ApiPostMobileSalesInvoiceLineResponse> PostMobileSalesInvoiceLine(@Body List<ApiPostMobileSalesInvoiceLineParameter> params);

    /*  @POST(endPoint+"PostMobileSalesOrderHeader")
      Call<ApiPostMobileSalesInvoiceHeaderResponse> PostMobileSalesOrderHeaderMSO(@Body List<ApiPostMobileSalesInvoiceHeaderParameter> params);
  */
    /*@POST(endPoint+"PostMobileSalesOrderLine")
    Call<ApiPostMobileSalesInvoiceLineResponse> PostMobileSalesOrderLineMSO(@Body List<ApiPostMobileSalesInvoiceLineParameter> params);
*/
    @POST(endPoint + "PostedSalesInvoiceLine")
    Call<ApiPostedSalesInvoiceItemResponse> GetPostedSalesInvoiceLine(@Body ApiPostedSalesInvoiceItemParameter params);

    @POST(endPoint + "GSTPostingSetup")
    Call<ApiGSTPostingSetupResponse> GetGSTPostingSetup(@Body ApiGSTPostingSetupParameter params);

    @POST(endPoint + "PostRunningNumbers")
    Call<ApiPostRunningNumbersResponse> PostRunningNumbers(@Body ApiPostRunningNumbersParameter params);

    @POST(endPoint + "PostCashReceiptJournal")
    Call<ApiPostCashReceiptJournalResponse> PostCashReceiptJournal(@Body List<ApiPostCashReceiptJournalParameter> params);

    @POST(endPoint + "PostMobileStockTransferHeader")
    Call<ApiPostMobileStockTransferHeaderResponse> UpdateStockTransferHeaderDetails(@Body List<ApiPostMobileStockTransferHeaderParameter> params);

    @POST(endPoint + "PostedStockTransferLine")
    Call<ApiPostMobileStockTransferLineResponse> UpdateStockTransferLineDetails(@Body List<ApiPostMobileStockTransferLineParameter> params);

    @POST(endPoint + "PostSalesOrder")
    Call<ApiPostSalesOrderStatusResponse> PostSalesOrderStatus(@Body List<ApiPostSalesOrderStatusUpdateParameter> params);

    @POST(endPoint + "PostSOPicture")
    Call<ApiSoImageUploadListResult> PostSOImage(@Body List<ApiSoImageUploadParameter> params);

    @Multipart
    @POST(endPoint + "PostPdfFile")
    Call<PdfUploadResponse> UploadPDF(@Part MultipartBody.Part file);

    @POST(endPoint + "stockRequest")
    Call<ApiStockRequestHeaderResponse> GetStockRequest(@Body ApiStockRequestHeaderParameter params);

    @POST(endPoint + "PostEmailMessage")
    Call<BaseResult> PostEmailMessage(@Body ApiPostEmailMessageParameter params);

    @POST(endPoint + "PostStockRequestHeader")
    Call<ApiPostMobileStockRequestHeaderResponse> PostMobileStockRequestHeader(@Body List<ApiPostMobileStockRequestHeaderParameter> params);

    @POST(endPoint + "PostStockRequestLine")
    Call<ApiPostMobileStockRequestLineResponse> PostMobileStockRequestLine(@Body List<ApiPostMobileStockRequestLineParameter> params);

    @POST(endPoint + "SalesCode")
    Call<ApiCustomerSalesCodeResponse> GetCustomerSalesCodes(@Body ApiCustomerSalesCodeParameter params);

    @POST(endPoint + "CustomerTemplate")
    Call<ApiCustomerTemplateResponse> GetCustomerTemplates(@Body ApiCustomerTemplateParameter params);

    //MVS stock request upload
    @POST(endPoint + "PostBlanketOrderHeader")
    Call<ApiPostMobileSalesInvoiceHeaderResponse> PostBlanketOrderHeaderMVS(@Body List<ApiPostMobileSalesInvoiceHeaderParameter> params);

    @POST(endPoint + "PostBlanketOrderLine")
    Call<ApiPostMobileSalesInvoiceLineResponse> PostBlanketOrderLineMVS(@Body List<ApiPostMobileSalesInvoiceLineParameter> params);

    @POST(endPoint + "driver")
    Call<ApiDriverResponse> GetDriver(@Body ApiDriverParameter params);

    @Multipart
    @POST(endPoint + "PostDbZipFile")
    Call<PostDbBackupResponse> uploadDbBackup(@Part MultipartBody.Part file);

    @GET
    Call<ApiCustomerLocations> GetCustomerCoordinate(@Url String url);
}
