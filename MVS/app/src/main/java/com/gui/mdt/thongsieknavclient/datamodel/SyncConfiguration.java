package com.gui.mdt.thongsieknavclient.datamodel;

/**
 * Created by nelin_000 on 07/20/2017.
 */

public class SyncConfiguration {
    int Id;
    String Scope;
    String LastSyncBy;
    String LastSyncDateTime;
    Boolean IsSuccess;
    int DataCount;

    public SyncConfiguration() {

    }

    public SyncConfiguration(String scope, String lastSyncBy, String lastSyncDateTime) {
        this.Scope = scope;
        this.LastSyncBy = lastSyncBy;
        this.LastSyncDateTime = lastSyncDateTime;
        this.IsSuccess = false;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String scope) {
        Scope = scope;
    }

    public String getLastSyncBy() {
        return LastSyncBy;
    }

    public void setLastSyncBy(String lastSyncBy) {
        LastSyncBy = lastSyncBy;
    }

    public String getLastSyncDateTime() {
        return LastSyncDateTime;
    }

    public void setLastSyncDateTime(String lastSyncDateTime) {
        LastSyncDateTime = lastSyncDateTime;
    }

    public Boolean getSuccess() {
        return IsSuccess;
    }

    public void setSuccess(Boolean success) {
        IsSuccess = success;
    }

    public int getDataCount() {
        return DataCount;
    }

    public void setDataCount(int dataCount) {
        DataCount = dataCount;
    }
}
