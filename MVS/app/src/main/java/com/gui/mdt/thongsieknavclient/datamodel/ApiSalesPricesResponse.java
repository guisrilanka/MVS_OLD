package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by bhanuka on 09/08/2017.
 */

public class  ApiSalesPricesResponse {

    String ServerDate;
    List<ApiSalesPricesListResultData> SalesPriceListResultData;

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public List<ApiSalesPricesListResultData> getSalesPriceListResultData() {
        return SalesPriceListResultData;
    }

    public void setSalesPriceListResultData(List<ApiSalesPricesListResultData> salesPriceListResultData) {
        SalesPriceListResultData = salesPriceListResultData;
    }

    public class ApiSalesPricesListResultData {

        String Key;
        String SalesCodeFilterCtrl;
        String ItemNoFilterCtrl;
        String StartingDateFilter;
        String SalesCodeFilterCtrl2;
        Integer Sales_Type;
        String Sales_Code;
        String Customer_Name;
        String Item_No;
        String Item_Description;
        String Variant_Code;
        String Currency_Code;
        String Unit_of_Measure_Code;
        String Minimum_Quantity;
        String Published_Price;
        String Cost;
        String Cost_plus_Percent;
        String Discount_Amount;
        String Unit_Price;
        String Starting_Date;
        String Ending_Date;
        String Price_Includes_VAT;
        String Allow_Line_Disc;
        String Allow_Invoice_Disc;
        String VAT_Bus_Posting_Gr_Price;
        String Item_Template_Sequence;


        /*public ApiSalesPricesListResultData(String key, String salesCodeFilterCtrl, String itemNoFilterCtrl, String startingDateFilter, String salesCodeFilterCtrl2, Integer salesType, String salesCode, String customerName, String b_itemNo, String itemDescription, String variantCode, String currencyCode, String unitOfMeasureCode, String minimumQuantity, String publishedPrice, String cost, String costPlusPercent, String discountAmount, String e_unitPrice, String startingDate, String endingDate, String priceIncludesVAT, String allowLineDisc, String allowInvoiceDisc, String VATBusPostingGrPrice, String itemTemplateSequence_) {

            this.Key = key;
            this.SalesCodeFilterCtrl = salesCodeFilterCtrl;
            this.ItemNoFilterCtrl = itemNoFilterCtrl;
            this.StartingDateFilter = startingDateFilter;
            this.SalesCodeFilterCtrl2 = salesCodeFilterCtrl2;
            this.Sales_Type = salesType;
            this.Sales_Code = salesCode;
            this.Customer_Name = customerName;
            this.Item_No = b_itemNo;
            this.Item_Description = itemDescription;
            this.Variant_Code = variantCode;
            this.Currency_Code = currencyCode;
            this.Unit_of_Measure_Code = unitOfMeasureCode;
            this.Minimum_Quantity = minimumQuantity;
            this.Published_Price = publishedPrice;
            this.Cost = cost;
            this.Cost_plus_Percent = costPlusPercent;
            this.Discount_Amount = discountAmount;
            this.Unit_Price = e_unitPrice;
            this.Starting_Date = startingDate;
            this.Ending_Date = endingDate;
            this.Price_Includes_VAT = priceIncludesVAT;
            this.Allow_Line_Disc = allowLineDisc;
            this.Allow_Invoice_Disc = allowInvoiceDisc;
            this.VAT_Bus_Posting_Gr_Price = VATBusPostingGrPrice;
            this.Item_Template_Sequence = itemTemplateSequence_;
        }
*/
        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

        public String getSalesCodeFilterCtrl() {
            return SalesCodeFilterCtrl;
        }

        public void setSalesCodeFilterCtrl(String salesCodeFilterCtrl) {
            SalesCodeFilterCtrl = salesCodeFilterCtrl;
        }

        public String getItemNoFilterCtrl() {
            return ItemNoFilterCtrl;
        }

        public void setItemNoFilterCtrl(String itemNoFilterCtrl) {
            ItemNoFilterCtrl = itemNoFilterCtrl;
        }

        public String getStartingDateFilter() {
            return StartingDateFilter;
        }

        public void setStartingDateFilter(String startingDateFilter) {
            StartingDateFilter = startingDateFilter;
        }

        public String getSalesCodeFilterCtrl2() {
            return SalesCodeFilterCtrl2;
        }

        public void setSalesCodeFilterCtrl2(String salesCodeFilterCtrl2) {
            SalesCodeFilterCtrl2 = salesCodeFilterCtrl2;
        }

        public Integer getSalesType() {
            return Sales_Type;
        }

        public void setSalesType(Integer salesType) {
            Sales_Type = salesType;
        }

        public String getSalesCode() {
            return Sales_Code;
        }

        public void setSalesCode(String salesCode) {
            Sales_Code = salesCode;
        }

        public String getCustomerName() {
            return Customer_Name;
        }

        public void setCustomerName(String customerName) {
            Customer_Name = customerName;
        }

        public String getItemNo() {
            return Item_No;
        }

        public void setItemNo(String itemNo) {
            Item_No = itemNo;
        }

        public String getItemDescription() {
            return Item_Description;
        }

        public void setItemDescription(String itemDescription) {
            Item_Description = itemDescription;
        }

        public String getVariantCode() {
            return Variant_Code;
        }

        public void setVariantCode(String variantCode) {
            Variant_Code = variantCode;
        }

        public String getCurrencyCode() {
            return Currency_Code;
        }

        public void setCurrencyCode(String currencyCode) {
            Currency_Code = currencyCode;
        }

        public String getUnitOfMeasureCode() {
            return Unit_of_Measure_Code;
        }

        public void setUnitOfMeasureCode(String unitOfMeasureCode) {
            Unit_of_Measure_Code = unitOfMeasureCode;
        }

        public String getMinimumQuantity() {
            return Minimum_Quantity;
        }

        public void setMinimumQuantity(String minimumQuantity) {
            Minimum_Quantity = minimumQuantity;
        }

        public String getPublishedPrice() {
            return Published_Price;
        }

        public void setPublishedPrice(String publishedPrice) {
            Published_Price = publishedPrice;
        }

        public String getCost() {
            return Cost;
        }

        public void setCost(String cost) {
            Cost = cost;
        }

        public String getCostPlusPercent() {
            return Cost_plus_Percent;
        }

        public void setCostPlusPercent(String costPlusPercent) {
            Cost_plus_Percent = costPlusPercent;
        }

        public String getDiscountAmount() {
            return Discount_Amount;
        }

        public void setDiscountAmount(String discountAmount) {
            Discount_Amount = discountAmount;
        }

        public String getUnit_Price() {
            return Unit_Price;
        }

        public void setUnit_Price(String unitPrice) {
            Unit_Price = unitPrice;
        }

        public String getStartingDate() {
            return Starting_Date;
        }

        public void setStartingDate(String startingDate) {
            Starting_Date = startingDate;
        }

        public String getEndingDate() {
            return Ending_Date;
        }

        public void setEndingDate(String endingDate) {
            Ending_Date = endingDate;
        }

        public String getPriceIncludesVAT() {
            return Price_Includes_VAT;
        }

        public void setPriceIncludesVAT(String priceIncludesVAT) {
            Price_Includes_VAT = priceIncludesVAT;
        }

        public String getAllowLineDisc() {
            return Allow_Line_Disc;
        }

        public void setAllowLineDisc(String allowLineDisc) {
            Allow_Line_Disc = allowLineDisc;
        }

        public String getAllowInvoiceDisc() {
            return Allow_Invoice_Disc;
        }

        public void setAllowInvoiceDisc(String allowInvoiceDisc) {
            Allow_Invoice_Disc = allowInvoiceDisc;
        }

        public String getVATBusPostingGrPrice() {
            return VAT_Bus_Posting_Gr_Price;
        }

        public void setVATBusPostingGrPrice(String VATBusPostingGrPrice) {
            this.VAT_Bus_Posting_Gr_Price = VATBusPostingGrPrice;
        }

        public String getItem_Template_Sequence() {
            return Item_Template_Sequence;
        }

        public void setItem_Template_Sequence(String item_Template_Sequence) {
            Item_Template_Sequence = item_Template_Sequence;
        }

        @Override
        public String toString() {
            return super.toString();
        }

    }
}
