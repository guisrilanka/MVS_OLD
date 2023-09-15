package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by bhanuka on 02/08/2017.
 */

public class ItemUom {

    private Integer Id;
    private String ItemCode;
    private String UomCode;
    private String Key;
    private float Convertion;

    public  ItemUom(){}

    public Integer getId() { return Id; }

    public void setId(Integer id) { Id = id; }

    public String getItemCode() { return ItemCode; }

    public void setItemCode(String itemCode) { ItemCode = itemCode; }

    public String getUomCode() { return UomCode; }

    public void setUomCode(String uomCode) { UomCode = uomCode; }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public float getConvertion() { return Convertion; }

    public void setConvertion(float convertion) { Convertion = convertion;}

    @Override
    public String toString() {
        return super.toString();
    }


}
