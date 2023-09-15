package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.datamodel.UserSetup;
import com.gui.mdt.thongsieknavclient.dbhandler.UserSetupDbHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nelin_000 on 09/04/2017.
 */

public class InitSync {

    UserSetup mUserSetup;
    NavClientApp mApp;
    Context context;

    CustomerSyncTask customerSyncTask;
    ItemSyncTask itemSyncTask;
    ItemCategorySyncTask itemCategorySyncTask;
    ItemUomSyncTask itemUomSyncTask;
    SalesOrderDownloadSyncTask salesOrderDownloadSyncTask;
    SalesPricesSyncTask salesPricesSyncTask;
    ItemBalancePdaSyncTask itemBalancePdaSyncTask;
    SalesOrderUploadSyncTask salesOrderUploadSyncTask;
    GSTPostingSetupSyncTask gSTPostingSetupSyncTask;
    UserSetupRunningNoUploadTask userSetupRunningNoUploadTask;
    PaymentUploadSyncTask paymentUploadSyncTask;

    Timer timerCustomerSyncTask;
    Timer timerItemSyncTask;
    Timer timerItemCategorySyncTask;
    Timer timerItemUomSyncTask;
    Timer timerSalesOrderDownloadSyncTask;
    Timer timerSalesPricesSyncTask;
    Timer timerItemBalancePdaSyncTask;
    Timer timerSalesOrderUploadSyncTask;
    Timer timerGSTPostingSetupSyncTask;
    Timer timerUserSetupRunningNoUploadTask;
    Timer timerPaymentUploadSyncTask;
    Timer timerItemImage;

    final int SYNC_PERIOD = 20*60*1000; //20 minutes

    public InitSync(Context context) {

        mApp = (NavClientApp) context;
        this.context = context;
        mUserSetup = getUserSetup(context);

        if (mUserSetup.isInitialSyncRun()) {
            StartSyncTasks();
        }

    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private UserSetup getUserSetup(Context context) {
        mUserSetup = new UserSetup();
        UserSetupDbHandler db = new UserSetupDbHandler(context);
        db.open();
        mUserSetup = db.getUserSetUp(mApp.getCurrentUserName());
        db.close();

        return mUserSetup;
    }

    public void StartSyncTasks() {
        //if (isNetworkConnected()) {
            SyncCustomers(0);
            SyncItems(5000);
            SyncSalesOrders(10000);
            //SyncItemImage(20000);
            //SyncItemCategory(40000);
            SyncSalesPrices(45000);
            //SyncItemUom(55000);
            //SyncItemBalancePda(60000);
            SyncUploadSalesOrders(65000);
            //SyncGstTable(65000);
            SyncRunningNos(70000);
            SyncPayments(75000);
        //}
    }

    public void stopAsyncTasks() {

        if (customerSyncTask != null) {
            customerSyncTask.cancel(true);
        }
        if (itemSyncTask != null) {
            itemSyncTask.cancel(true);
        }
        if (itemCategorySyncTask != null) {
            itemCategorySyncTask.cancel(true);
        }
        if (itemUomSyncTask != null) {
            itemUomSyncTask.cancel(true);
        }
        if (salesOrderDownloadSyncTask != null) {
            salesOrderDownloadSyncTask.cancel(true);
        }
        if (salesPricesSyncTask != null) {
            salesPricesSyncTask.cancel(true);
        }
        if (itemBalancePdaSyncTask != null) {
            itemBalancePdaSyncTask.cancel(true);
        }
        if (salesOrderUploadSyncTask != null) {
            salesOrderUploadSyncTask.cancel(true);
        }
        if (gSTPostingSetupSyncTask != null) {
            gSTPostingSetupSyncTask.cancel(true);
        }
        if (userSetupRunningNoUploadTask != null) {
            userSetupRunningNoUploadTask.cancel(true);
        }
        if (paymentUploadSyncTask != null) {
            paymentUploadSyncTask.cancel(true);
        }
    }

    public void stopTimers() {

        if (timerCustomerSyncTask != null) {
            timerCustomerSyncTask.cancel();
        }
        if (timerItemSyncTask != null) {
            timerItemSyncTask.cancel();
        }
        if (timerItemCategorySyncTask != null) {
            timerItemCategorySyncTask.cancel();
        }
        if (timerItemUomSyncTask != null) {
            timerItemUomSyncTask.cancel();
        }
        if (timerSalesOrderDownloadSyncTask != null) {
            timerSalesOrderDownloadSyncTask.cancel();
        }
        if (timerSalesPricesSyncTask != null) {
            timerSalesPricesSyncTask.cancel();
        }
        if (timerItemBalancePdaSyncTask != null) {
            timerItemBalancePdaSyncTask.cancel();
        }
        if (timerSalesOrderUploadSyncTask != null) {
            timerSalesOrderUploadSyncTask.cancel();
        }
        if (timerGSTPostingSetupSyncTask != null) {
            timerGSTPostingSetupSyncTask.cancel();
        }
        if (timerUserSetupRunningNoUploadTask != null) {
            timerUserSetupRunningNoUploadTask.cancel();
        }
        if (timerPaymentUploadSyncTask != null) {
            timerPaymentUploadSyncTask.cancel();
        }
        if (timerItemImage != null) {
            timerItemImage.cancel();
        }
    }

    void SyncCustomers(long delay) {
        final Handler handler = new Handler();
        timerCustomerSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        customerSyncTask = new CustomerSyncTask(context, false, true);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        customerSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerCustomerSyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    //Sync Items
    void SyncItems(long delay) {
        final Handler handler = new Handler();
        timerItemSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        itemSyncTask = new ItemSyncTask(context, false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        itemSyncTask.execute((Void) null);

                    }
                });
            }
        };
        timerItemSyncTask.schedule(task, delay, SYNC_PERIOD);


    }

    //Sync Item Category
    void SyncItemCategory(long delay) {
        final Handler handler = new Handler();
        timerItemCategorySyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        itemCategorySyncTask = new ItemCategorySyncTask(context, false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        itemCategorySyncTask.execute((Void) null);


                    }
                });
            }
        };
        timerItemCategorySyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    //Sync Item Uom Code
    void SyncItemUom(long delay) {
        final Handler handler = new Handler();
        timerItemUomSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        itemUomSyncTask = new ItemUomSyncTask(context, false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        itemUomSyncTask.execute((Void) null);

                    }
                });
            }
        };
        timerItemUomSyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    void SyncSalesOrders(long delay) {
        final Handler handler = new Handler();
        timerSalesOrderDownloadSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        salesOrderDownloadSyncTask = new SalesOrderDownloadSyncTask(context
                                , false, true);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        salesOrderDownloadSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerSalesOrderDownloadSyncTask.schedule(task, delay, SYNC_PERIOD);

    }

    void SyncItemImage(long delay) {
        final Handler handler = new Handler();
        timerItemImage = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new ItemImageSync(context);
                    }
                });
            }
        };
        timerItemImage.schedule(task, delay, SYNC_PERIOD);
    }

    //Sync Sales Prices
    void SyncSalesPrices(long delay) {
        final Handler handler = new Handler();
        timerSalesPricesSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        salesPricesSyncTask = new SalesPricesSyncTask(context, false, true);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        salesPricesSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerSalesPricesSyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    //Sync Item Balances
    void SyncItemBalancePda(long delay) {
        final Handler handler = new Handler();
        timerItemBalancePdaSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        itemBalancePdaSyncTask = new ItemBalancePdaSyncTask(context, false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        itemBalancePdaSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerItemBalancePdaSyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    //upload sales orders
    void SyncUploadSalesOrders(long delay) {
        final Handler handler = new Handler();
        timerSalesOrderUploadSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        salesOrderUploadSyncTask = new SalesOrderUploadSyncTask(context,
                                false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        salesOrderUploadSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerSalesOrderUploadSyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    //download gst table
    void SyncGstTable(long delay) {
        final Handler handler = new Handler();
        timerGSTPostingSetupSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        gSTPostingSetupSyncTask = new GSTPostingSetupSyncTask(context,
                                false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        gSTPostingSetupSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerGSTPostingSetupSyncTask.schedule(task, delay, SYNC_PERIOD);
    }

    //upload running nos'
    void SyncRunningNos(long delay) {
        final Handler handler = new Handler();
        timerUserSetupRunningNoUploadTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        userSetupRunningNoUploadTask = new UserSetupRunningNoUploadTask(context,
                                false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        userSetupRunningNoUploadTask.execute((Void) null);
                    }
                });
            }
        };
        timerUserSetupRunningNoUploadTask.schedule(task, delay, SYNC_PERIOD);
    }

    //upload payments'
    void SyncPayments(long delay) {
        final Handler handler = new Handler();
        timerPaymentUploadSyncTask = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        paymentUploadSyncTask = new PaymentUploadSyncTask(context,
                                false);
                        //asyncTask.delegate = DashBoardActivity.this; // will not use when run in time intervals.
                        paymentUploadSyncTask.execute((Void) null);
                    }
                });
            }
        };
        timerPaymentUploadSyncTask.schedule(task, delay, SYNC_PERIOD);
    }


}
