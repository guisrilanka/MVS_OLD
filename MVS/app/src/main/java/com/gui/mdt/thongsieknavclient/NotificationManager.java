package com.gui.mdt.thongsieknavclient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.gui.mdt.thongsieknavclient.ui.LoginActivity;

/**
 * Created by yeqim_000 on 11/08/16.
 */
public class NotificationManager {

    public static final String ALERT_TITLE_INTERNAL_ERROR = "INTERNAL ERROR";
    public static final String ALERT_MSG_INTERNAL_ERROR = "Please contact the software vendor";

    public static final String ALERT_TITLE_INVALID_PO = "Invalid PO Item";
    public static final String ALERT_MSG_INVALID_PO = "Scanned item does not belong to this PO";
    public static final String ALERT_TITLE_INVALID_TO = "Invalid Item";
    public static final String ALERT_MSG_INVALID_TO = "Scanned item does not belong to this order";

    public static final String ALERT_TITLE_INVALID_LOCATION = "Invalid Location";
    public static final String ALERT_MSG_INVALID_LOCATION = "Please select a valid location.";

    public static final String ALERT_TITLE_INVALID_NEW_LOCATION = "Invalid Location";
    public static final String ALERT_MSG_INVALID_NEW_LOCATION = "Please select a valid location.";

    public static final String ALERT_TITLE_INVALID_UOM = "Invalid Uom";
    public static final String ALERT_MSG_INVALID_UOM = "Please select a valid uom.";

    public static final String ALERT_TITLE_INVALID_QTY_BASED = "Invalid Quantity Based";
    public static final String ALERT_MSG_INVALID_QTY_BASED = "Quantity based cannot be null.";

    public static final String ALERT_TITLE_INVALID_BARCODE = "Invalid Barcode";
    public static final String ALERT_MSG_BARCODE_READ_ERROR = "Error reading barcode. Please try again.";
    public static final String ALERT_MSG_INVALID_BARCODE = "Invalid Barcode.";
    public static final String ALERT_TITLE_INVALID_SERIALNO = "Invalid Serial No";
    public static final String ALERT_MSG_INVALID_SERIALNO = "Duplicated barcode Serial No. This barcode has already been scanned.";
    public static final String ALERT_TITLE_INVALID_LOTNO = "Invalid Lot No";
    public static final String ALERT_MSG_INVALID_LOTNO = "Lot No. does not belong to this order";

    public static final String ALERT_TITLE_INVALID_PRODUCTION_DATE = "Invalid Production Date";
    public static final String ALERT_MSG_INVALID_PRODUCTION_DATE = "Please enter the production date.";
    public static final String ALERT_TITLE_INVALID_EXPIRY_DATE = "Invalid Expiry Date";
    public static final String ALERT_MSG_INVALID_EXPIRY_DATE = "Please enter the expiry date.";
    public static final String ALERT_TITLE_INVALID_QUANTITY = "Invalid Quantity";
    public static final String ALERT_MSG_INVALID_QUANTITY = "Please enter a valid quantity.";
    public static final String ALERT_TITLE_INVALID_LOT_NO = "Invalid Lot Number";
    public static final String ALERT_MSG_INVALID_LOT_NO = "Please enter the Lot Number.";
    public static final String ALERT_TITLE_EXCEEDS_QUANTITY = "Unable to Add Entry";
    public static final String ALERT_MSG_EXCEEDS_QUANTITY = "Quantity exceeds ordered amount.";

    public static final String ALERT_TITLE_FILTER_RESULT = "Filter Result";
    public static final String ALERT_MSG_FILTER_NO_MATCH= "No matching result found";

    public static final String MSG_INFORMATION = "Information";
    public static final String MSG_ERROR = "ERROR!";

    public static final String MSG_REMOVE_SUCCESSFUL = "Remove Successful";
    public static final String MSG_REMOVE_MESSAGE = "Entry has been removed!";

    public static final String MSG_REMOVE_FAILED = "Failed to Remove";

    public static final String MSG_SAVE_SUCCESSFUL = "Save Successful";
    public static final String MSG_SAVE_FAILED = "Unable to Save";

    public static final String TITLE_UPLOAD_SUCCESSFUL = "Upload Successful";
    public static final String MSG_UPLOAD_SUCCESSFUL = "Data has been uploaded!";
    public static final String MSG_UPLOAD_FAILED = "Unable to Upload";

    public static final String MSG_PRINT_REQUEST_SENT = "Print Request has been sent";
    public static final String MSG_PRINT_REQUEST_FAILED = "Unable to send request";

    public static final String DIALOG_TITLE_SAVE_REQUEST = "Unsaved Data";
    public static final String DIALOG_MSG_SAVE_REQUEST = "Please save or clear data before returning.";

    public static final String DIALOG_TITLE_LOAD_DATA = "LOADING DATA";
    public static final String DIALOG_MSG_LOAD = "LOADING...";
    public static final String DIALOG_TITLE_SAVE_DATA = "SAVING DATA";
    public static final String DIALOG_MSG_SAVE = "SAVING...";
    public static final String DIALOG_TITLE_UPLOAD_DATA = "UPLOADING DATA";
    public static final String DIALOG_MSG_UPLOAD = "UPLOADING...";
    public static final String DIALOG_TITLE_DELETE_DATA = "DELETING DATA";
    public static final String DIALOG_MSG_DELETE = "DELETING...";
    public static final String DIALOG_TITLE_PRINT = "PRINT REQUEST";
    public static final String DIALOG_MSG_PRINT = "SENDING PRINT REQUEST...";
    public static final String DIALOG_TITLE_PROCESSING = "PROCESSING";
    public static final String DIALOG_MSG_PROCESSING = "SENDING REQUEST";

    //New Added
    public static final String ALERT_TITLE_INVALID_ITEMRCLSNO = "Invalid Item Reclass Number";
    public static final String ALERT_MSG_INVALID_ITEMRCLSNO = "Please enter a valid item reclass number.";

    public static final String ALERT_TITLE_INVALID_POST_DATE = "Invalid Posting Date";
    public static final String ALERT_MSG_INVALID_POST_DATE = "Please enter a valid post date.";

    public static final String ALERT_TITLE_INVALID_ITEMNO = "Invalid Item Number";
    public static final String ALERT_MSG_INVALID_ITEMNO = "Please select a valid item number.";


    private static ProgressDialog progress;

    public static void DisplayAlertDialog(Context context, String title, String msg)
    {


        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void ShowProgressDialog(Context context, String title, String msg)
    {
        progress = ProgressDialog.show(context, title, msg, true);
    }

    public static void HideProgressDialog()
    {
        try {
            progress.dismiss();
        }catch (Exception e){

        }
    }

}
