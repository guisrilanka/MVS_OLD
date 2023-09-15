package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by User on 8/9/2017.
 */
public class ApiItemListResult {

    public List<ApiItem> ItemListResultData;
    public String ServerDate;
    public String LastModifiedDate;

    public List<ApiItem> getItemListResultData() {
        return ItemListResultData;
    }

    public void setItemListResultData(List<ApiItem> itemListResultData) {
        ItemListResultData = itemListResultData;
    }

    public String getServerDate() {
        return ServerDate;
    }

    public void setServerDate(String serverDate) {
        ServerDate = serverDate;
    }

    public String getLastModifiedDate() {
        return LastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        LastModifiedDate = lastModifiedDate;
    }

    public static class ApiItem {
        public String No;
        public String Description;
        public String Base_Unit_of_Measure;
        public String Item_Category_Code;
        public String Product_Group_Code;
        public String Qty_on_Purch_Order;
        public String Qty_on_Sales_Order;
        public Boolean Blocked;
        public String Unit_Price;
        public String Net_Invoiced_Qty;
        public String Identifier_Code;
        private String VAT_Prod_Posting_Group;

        public String getIdentifier_Code() {
            return Identifier_Code;
        }

        public void setIdentifier_Code(String identifier_Code) {
            Identifier_Code = identifier_Code;
        }

        public String getNo() {
            return No;
        }

        public void setNo(String no) {
            No = no;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getBase_Unit_of_Measure() {
            return Base_Unit_of_Measure;
        }

        public void setBase_Unit_of_Measure(String base_Unit_of_Measure) {
            Base_Unit_of_Measure = base_Unit_of_Measure;
        }

        public String getItem_Category_Code() {
            return Item_Category_Code;
        }

        public void setItem_Category_Code(String item_Category_Code) {
            Item_Category_Code = item_Category_Code;
        }

        public String getProduct_Group_Code() {
            return Product_Group_Code;
        }

        public void setProduct_Group_Code(String product_Group_Code) {
            Product_Group_Code = product_Group_Code;
        }

        public String getQty_on_Purch_Order() {
            return Qty_on_Purch_Order;
        }

        public void setQty_on_Purch_Order(String qty_on_Purch_Order) {
            Qty_on_Purch_Order = qty_on_Purch_Order;
        }

        public String getQty_on_Sales_Order() {
            return Qty_on_Sales_Order;
        }

        public void setQty_on_Sales_Order(String qty_on_Sales_Order) {
            Qty_on_Sales_Order = qty_on_Sales_Order;
        }

        public Boolean getBlocked() {
            return Blocked;
        }

        public void setBlocked(Boolean blocked) {
            Blocked = blocked;
        }

        public String getUnit_Price() {
            return Unit_Price;
        }

        public void setUnit_Price(String unit_Price) {
            Unit_Price = unit_Price;
        }

        public String getNet_Invoiced_Qty() {
            return Net_Invoiced_Qty;
        }

        public void setNet_Invoiced_Qty(String net_Invoiced_Qty) {
            Net_Invoiced_Qty = net_Invoiced_Qty;
        }

        public String getVAT_Prod_Posting_Group() {
            return VAT_Prod_Posting_Group;
        }

        public void setVAT_Prod_Posting_Group(String VAT_Prod_Posting_Group) {
            this.VAT_Prod_Posting_Group = VAT_Prod_Posting_Group;
        }
    }
}
