package com.gui.mdt.thongsieknavclient.model.itemmodels;

/**
 * Created by user on 12/29/2016.
 */

public class ItemListParameter {
    private String UserCompany;
    private String UserName;
    private String Password;
    private String FilterCategoryCode;
    private String FilterItemDescription;
    private String FilterItemCode;

    public ItemListParameter(String userCompany, String userName, String password, String filterCategoryCode, String filterItemDescription, String filterItemCode) {
        UserCompany = userCompany;
        UserName = userName;
        Password = password;
        FilterCategoryCode = filterCategoryCode;
        FilterItemDescription = filterItemDescription;
        FilterItemCode = filterItemCode;
    }

    public String getUserCompany() {
        return UserCompany;
    }

    public void setUserCompany(String userCompany) {
        UserCompany = userCompany;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFilterCategoryCode() {
        return FilterCategoryCode;
    }

    public void setFilterCategoryCode(String filterCategoryCode) {
        FilterCategoryCode = filterCategoryCode;
    }

    public String getFilterItemDescription() {
        return FilterItemDescription;
    }

    public void setFilterItemDescription(String filterItemDescription) {
        FilterItemDescription = filterItemDescription;
    }

    public String getFilterItemCode() {
        return FilterItemCode;
    }

    public void setFilterItemCode(String filterItemCode) {
        FilterItemCode = filterItemCode;
    }
}
