package com.gui.mdt.thongsieknavclient.interfaces;

import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;

/**
 * Created by nelin_000 on 08/21/2017.
 */

public interface AsyncResponse {
    void onAsyncTaskFinished(SyncStatus syncStatus);
}
