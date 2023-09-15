package com.gui.mdt.thongsieknavclient.datamodel;

import java.util.List;

/**
 * Created by nelin_000 on 08/16/2017.
 */

public class ApiItemUomListResult {

    List<ItemUomListResultData> ItemUOMListResultData;
    String ServerDate;
    String LastModifiedDate;

    public List<ItemUomListResultData> getItemUOMListResultData() {
        return ItemUOMListResultData;
    }

    public void setItemUOMListResultData(List<ItemUomListResultData> itemUOMListResultData) {
        ItemUOMListResultData = itemUOMListResultData;
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

    public class ItemUomListResultData {

        String Key;
        String Item_No;
        String Code;
        float Qty_per_Unit_of_Measure;

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

        public String getItem_No() {
            return Item_No;
        }

        public void setItem_No(String item_No) {
            Item_No = item_No;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public float getQty_per_Unit_of_Measure() {
            return Qty_per_Unit_of_Measure;
        }

        public void setQty_per_Unit_of_Measure(float qty_per_Unit_of_Measure) {
            Qty_per_Unit_of_Measure = qty_per_Unit_of_Measure;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
