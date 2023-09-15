package com.gui.mdt.thongsieknavclient.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.PdfUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SendEmailSyncTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailStockBalanceSummaryActivity extends AppCompatActivity implements AsyncResponse {

    private List<ItemBalancePda> mItemBalancePdaList;
    NavClientApp mApp;
    GetItemBalances mGetItemBalances;
    SendEmailSyncTask mSendEmailSyncTask;
    PdfUploadSyncTask mPdfUploadSyncTask;
    Font mPlainFont, mPlainFontTitle, mPlainFontHeader;
    Bundle mExtras;
    ProgressDialog mProgressDialog;

    String mFileName = "", mFilterDate = "", mReportDate = "", mDetails="", mPrintedDateTime="";
    File mPdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtras = getIntent().getExtras();

        if (mExtras != null) {
            if (mExtras.containsKey("details")) {
                mDetails = mExtras.getString("details"); //loadStock, unloadStock
            }
        }

        initComp();

        if (isNetworkConnected()) {
            mGetItemBalances = new GetItemBalances();
            mGetItemBalances.execute((Void) null);
        } else {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string
                            .notification_title_no_connection))
                    .setMessage(getResources().getString(R.string
                            .notification_msg_no_connection))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }

    }

    private void initComp() {
        mApp = (NavClientApp) getApplication();

        mPlainFont = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.NORMAL);  //
        mPlainFontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD); //
        mPlainFontTitle = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);


        String nameParam = "";
        if (mDetails.equals("")) {
            nameParam = " - ";
        } else {
            nameParam = " - " + mDetails + " - ";
        }

        mFileName = "Stock Summary" + nameParam
                + mApp.getmCurrentSalesPersonCode() + "-"
                + DateTime.now().toString().replace("-", "")
                .replace(":", "").replace(".", "")
                .replace("+", "")
                + ".pdf";

        mPdfFile = new File(getExternalFilesDir("MyStockBalanceReports"), mFileName);

        DateFormat df1 = new SimpleDateFormat("dd  MMM  yyyy");
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df3 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

        Date dateObj = new Date();

        mReportDate = df1.format(dateObj).toString();
        mFilterDate = df2.format(dateObj).toString();
        mPrintedDateTime = df3.format(dateObj).toString();

        mProgressDialog = new ProgressDialog(EmailStockBalanceSummaryActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {
        //when report upload success
        if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopePdf))) {

            if (syncStatus.isStatus()) {
                startSendEmail();
            } else {
                Toast.makeText(mApp, syncStatus.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                finish();
            }

        } else if (syncStatus.getScope().equals(getResources().getString(R.string.SyncScopeSendEmail))) {

            if (syncStatus.isStatus()) {
                Toast.makeText(mApp, "Email Sent.", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                finish();

            } else {
                Toast.makeText(mApp, "Email Sending Failed.", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                finish();
            }
        }
    }

    private void startSendEmail() {

        String subjectParam = "", bodyParam = "";
        if (mDetails.equals("")) {
            subjectParam = " - ";
            bodyParam = "";
        } else {
            subjectParam = " - " + mDetails + " - ";
            bodyParam = " - " + mDetails;
        }

        String mSubject = ""
                + "Stock Summary" +subjectParam
                + mApp.getmCurrentDriverCode() + " - "
                + mFilterDate;

        String mBody = ""
                + "Report: Stock Summary" + bodyParam + '\n'
                + "Driver: " + mApp.getmCurrentDriverCode() + "\n"
                + "Date :" + mFilterDate;

        mSendEmailSyncTask = new SendEmailSyncTask(getApplicationContext(), true, mFilterDate, mFileName, mBody, mSubject);
        mSendEmailSyncTask.delegate = EmailStockBalanceSummaryActivity.this;
        mSendEmailSyncTask.execute((Void) null);
        //Toast.makeText(mApp, "Sending email...", Toast.LENGTH_LONG).show();
    }

    private void startUploadPdf() {
        mPdfUploadSyncTask = new PdfUploadSyncTask(getApplicationContext(), true, mFileName);
        mPdfUploadSyncTask.delegate = EmailStockBalanceSummaryActivity.this;
        mPdfUploadSyncTask.execute((Void) null);
        //Toast.makeText(mApp, "Uploading report..", Toast.LENGTH_LONG).show();
    }

    private void getItemBalancePdaList() {
        mItemBalancePdaList = new ArrayList<ItemBalancePda>();
        ItemBalancePdaDbHandler ibpDb = new ItemBalancePdaDbHandler(getApplicationContext());
        ibpDb.open();

        mItemBalancePdaList = ibpDb.getItemBalanceList(mApp.getmCurrentDriverCode());

        ibpDb.close();
    }

    public void generatePdfReport() {
        try {
            Document document = new Document(); // will set page size to A4
            document.setMargins(15, 15, 40, 15); //top margin for header (200)
            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(mPdfFile));

            Header event = new Header();
            docWriter.setPageEvent(event);
            //print page no in header
            document.open();

            PdfPTable tblHeader = new PdfPTable(1);
            tblHeader.setWidthPercentage(100);

            //empty row
            PdfPTable innerTblEmptyRow = new PdfPTable(1);
            innerTblEmptyRow.setWidthPercentage(100);
            innerTblEmptyRow.addCell(getCell("\n", PdfPCell.ALIGN_CENTER, mPlainFont));

            //add title
            String titlePram="";
            if (mDetails.equals("")) {
                titlePram = "";
            } else {
                titlePram = " - " + mDetails;
            }

            PdfPTable innerTblHeaderTitle = new PdfPTable(1);
            innerTblHeaderTitle.setWidthPercentage(100);
            innerTblHeaderTitle.addCell(getCell("Stock Summary" + titlePram, PdfPCell.ALIGN_CENTER,
                    mPlainFontTitle));
            tblHeader.addCell(getCellTable(innerTblHeaderTitle, PdfPCell.ALIGN_CENTER));

            PdfPTable innerTblHeaderDate = new PdfPTable(1);
            innerTblHeaderDate.setWidthPercentage(100);
            innerTblHeaderDate.addCell(getCell("for " + mReportDate, PdfPCell.ALIGN_CENTER,
                    mPlainFontHeader));
            tblHeader.addCell(getCellTable(innerTblHeaderDate, PdfPCell.ALIGN_LEFT));

            tblHeader.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            //add salesman

            PdfPTable innerTblHeaderSalesMan = new PdfPTable(1);
            innerTblHeaderSalesMan.setWidthPercentage(100);
            innerTblHeaderSalesMan.addCell(getCell("Vansales : " + mApp.getCurrentUserDisplayName(), PdfPCell.ALIGN_CENTER,
                    mPlainFontHeader));
            tblHeader.addCell(getCellTable(innerTblHeaderSalesMan, PdfPCell.ALIGN_LEFT));

            tblHeader.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            PdfPTable tableStockBalance = new PdfPTable(1);
            tableStockBalance.setWidthPercentage(100);
            tableStockBalance.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            //dotedline
            tableStockBalance.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));

            if(mDetails.equals("Unload") || mDetails.equals(""))
            {
                PdfPTable innerTblStockBalenceHeader = new PdfPTable(4);
                innerTblStockBalenceHeader.setWidthPercentage(100);
                innerTblStockBalenceHeader.setWidths(new int[]{3, 14, 3, 3});
                innerTblStockBalenceHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                innerTblStockBalenceHeader.addCell(getCell("Item No", PdfPCell.ALIGN_LEFT, mPlainFont));
                innerTblStockBalenceHeader.addCell(getCell("Description", PdfPCell.ALIGN_LEFT, mPlainFont));
                innerTblStockBalenceHeader.addCell(getCell("Vehicle Qty", PdfPCell.ALIGN_RIGHT, mPlainFont));
                innerTblStockBalenceHeader.addCell(getCell("Exch. Qty", PdfPCell.ALIGN_RIGHT, mPlainFont));
                tableStockBalance.addCell(getCellTable(innerTblStockBalenceHeader, PdfPCell.ALIGN_LEFT));

                //dotedline
                tableStockBalance.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));

                PdfPTable innerTblStockBalenceContent = new PdfPTable(4);
                innerTblStockBalenceContent.setWidthPercentage(100);
                innerTblStockBalenceContent.setWidths(new int[]{3, 14, 3, 3});
                innerTblStockBalenceContent.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

                if (mItemBalancePdaList.size() > 0) {
                    for (ItemBalancePda ib : mItemBalancePdaList) {

                        float qty = ib.getOpenQty() - ib.getQuantity();

                        innerTblStockBalenceContent.addCell(
                                getCell(ib.getItemNo()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblStockBalenceContent.addCell(
                                getCell(ib.getItemDescription()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblStockBalenceContent.addCell(
                                getCell(String.valueOf(Math.round(qty))
                                        , PdfPCell.ALIGN_RIGHT
                                        , mPlainFont));
                        innerTblStockBalenceContent.addCell(
                                getCell(String.valueOf(Math.round(ib.getExchangedQty()))
                                        , PdfPCell.ALIGN_RIGHT
                                        , mPlainFont));

                    }
                }

                tableStockBalance.addCell(getCellTable(innerTblStockBalenceContent, PdfPCell.ALIGN_LEFT));
            }
            else if(mDetails.equals("Load"))
            {
                PdfPTable innerTblStockBalenceHeader = new PdfPTable(3);
                innerTblStockBalenceHeader.setWidthPercentage(100);
                innerTblStockBalenceHeader.setWidths(new int[]{3, 17, 3});
                innerTblStockBalenceHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                innerTblStockBalenceHeader.addCell(getCell("Item No", PdfPCell.ALIGN_LEFT, mPlainFont));
                innerTblStockBalenceHeader.addCell(getCell("Description", PdfPCell.ALIGN_LEFT, mPlainFont));
                innerTblStockBalenceHeader.addCell(getCell("Vehicle Qty", PdfPCell.ALIGN_RIGHT, mPlainFont));
                //innerTblStockBalenceHeader.addCell(getCell("Exch. Qty", PdfPCell.ALIGN_RIGHT, mPlainFont));
                tableStockBalance.addCell(getCellTable(innerTblStockBalenceHeader, PdfPCell.ALIGN_LEFT));

                //dotedline
                tableStockBalance.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));

                PdfPTable innerTblStockBalenceContent = new PdfPTable(3);
                innerTblStockBalenceContent.setWidthPercentage(100);
                innerTblStockBalenceContent.setWidths(new int[]{3, 17, 3});
                innerTblStockBalenceContent.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

                if (mItemBalancePdaList.size() > 0) {
                    for (ItemBalancePda ib : mItemBalancePdaList) {

                        float qty = ib.getOpenQty() - ib.getQuantity();

                        innerTblStockBalenceContent.addCell(
                                getCell(ib.getItemNo()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblStockBalenceContent.addCell(
                                getCell(ib.getItemDescription()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblStockBalenceContent.addCell(
                                getCell(String.valueOf(Math.round(qty))
                                        , PdfPCell.ALIGN_RIGHT
                                        , mPlainFont));
                        /*innerTblStockBalenceContent.addCell(
                                getCell(String.valueOf(Math.round(ib.getExchangedQty()))
                                        , PdfPCell.ALIGN_RIGHT
                                        , mPlainFont));*/

                    }
                }

                tableStockBalance.addCell(getCellTable(innerTblStockBalenceContent, PdfPCell.ALIGN_LEFT));
            }


            //dotedline
            tableStockBalance.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));

            tableStockBalance.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));
            tableStockBalance.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            //received by
            PdfPTable innerTblReceivedLine = new PdfPTable(1);
            innerTblReceivedLine.setWidthPercentage(100);
            innerTblReceivedLine.addCell(
                    getCell("-------------------------"
                            , PdfPCell.ALIGN_LEFT,
                    mPlainFont));
            tableStockBalance.addCell(getCellTable(innerTblReceivedLine, PdfPCell.ALIGN_LEFT));

            PdfPTable innerTblReceivedText = new PdfPTable(1);
            innerTblReceivedText.setWidthPercentage(100);
            innerTblReceivedText.addCell(
                    getCell("      Received by"
                            , PdfPCell.ALIGN_LEFT,
                    mPlainFont));
            tableStockBalance.addCell(getCellTable(innerTblReceivedText, PdfPCell.ALIGN_LEFT));

            tableStockBalance.setSplitLate(false);

            document.add(tblHeader);
            document.add(tableStockBalance);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public PdfPCell getCell(String element1, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(element1, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellTable(PdfPTable element1, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element1);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellParagraph(Paragraph element1, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPTable getTblDotedLines(int alignment) {

        DottedLineSeparator separator = new DottedLineSeparator();
        separator.setGap(2f);
        Paragraph p = new Paragraph();
        p.add(separator);

        PdfPTable tblDottedLines = new PdfPTable(1);
        tblDottedLines.getDefaultCell().setFixedHeight(3);
        tblDottedLines.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        tblDottedLines.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.addElement(separator);
        cell.setHorizontalAlignment(alignment);
        cell.setFixedHeight(3);
        cell.setBorder(PdfPCell.NO_BORDER);

        tblDottedLines.addCell(cell);
        return tblDottedLines;
    }

    public class Header extends PdfPageEventHelper {

        protected Phrase header;
        protected PdfPTable table;
        protected PdfPTable tableHeader;
        protected float tableHeight;
        Font plainFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);

        public float getTableHeight() {
            return tableHeight;
        }

        public void setHeader() {

        }

        public void setHeader(PdfPTable table) {
            this.tableHeader = table;
        }


        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            this.table = new PdfPTable(1);
            this.table.setTotalWidth(document.getPageSize().getWidth()- 35);
            this.table.setLockedWidth(true);

            table.addCell(getCell(
                    "Print Date: " + mPrintedDateTime
                    , Element.ALIGN_RIGHT, plainFont));

            table.addCell(getCellTable(tableHeader, PdfPCell.ALIGN_RIGHT));
            tableHeight = table.getTotalHeight();

            table.writeSelectedRows(0, -1,
                    document.left(),
                    document.top() + ((document.topMargin() + tableHeight) / 2),
                    writer.getDirectContent());

        }

        public PdfPCell getCell(String element1, int alignment, Font font) {
            PdfPCell cell = new PdfPCell(new Phrase(element1, font));
            cell.setHorizontalAlignment(alignment);
            cell.setBorder(PdfPCell.NO_BORDER);
            return cell;
        }

    }

    private class GetItemBalances extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Sending email...");
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getItemBalancePdaList();

            } catch (Exception e) {
                Log.d("Exception: ", e.toString());
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                try {
                    generatePdfReport();

                    if (mPdfFile != null) {
                        startUploadPdf();
                    } else {
                        Toast.makeText(mApp, "Pdf is not created!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }


                } catch (Exception ex) {
                    Log.d("Exception", ex.toString());
                }

            } else {

                Toast.makeText(EmailStockBalanceSummaryActivity.this
                        , "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
