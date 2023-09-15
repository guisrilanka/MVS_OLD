package com.gui.mdt.thongsieknavclient.model;

/**
 * Created by User on 7/7/2016.
 */
public class CompanyResult {
    //private String keyField;

    private String nameField;

    /*
    public String getKeyField ()
    {
        return keyField;
    }

    public void setKeyField (String keyField)
    {
        this.keyField = keyField;
    }
*/
    public String getNameField ()
    {
        return nameField;
    }

    public void setNameField (String nameField)
    {
        this.nameField = nameField;
    }

    @Override
    public String toString()
    {
        return  this.getNameField();
    }
}
