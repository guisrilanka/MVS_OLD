package com.gui.mdt.thongsieknavclient.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.PdfUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SendEmailSyncTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmailInvoiceSummaryActivity extends AppCompatActivity implements AsyncResponse {

    List<SalesOrder> mSalesOrderList;
    NavClientApp mApp;
    GetSalesOrderListTask mGetSalesOrderListTask;
    SendEmailSyncTask mSendEmailSyncTask;
    PdfUploadSyncTask mPdfUploadSyncTask;
    Font mPlainFont, mPlainFontTitle, mPlainFontHeader;
    Bundle mExtras;
    ProgressDialog mProgressDialog;

    String mFileName = "", mFilterDate = "", mHeaderDate = "", mPrintedDateTime = "";
    File mPdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtras = getIntent().getExtras();

        if (mExtras.containsKey("filterCreatedDate")) {
            mFilterDate = mExtras.getString("filterCreatedDate"); //2018-04-18
        }

        initComp();

        mGetSalesOrderListTask = new GetSalesOrderListTask();
        mGetSalesOrderListTask.execute((Void) null);

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

    private void initComp() {
        mApp = (NavClientApp) getApplication();

        mPlainFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        mPlainFontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        mPlainFontTitle = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

        mFileName = "Invoice Summary" + " - "
                + mApp.getmCurrentDriverCode() + "-"
                + DateTime.now().toString().replace("-", "")
                .replace(":", "").replace(".", "")
                .replace("+", "")
                + ".pdf";

        mPdfFile = new File(getExternalFilesDir("MyInvoiceSummaryReports"), mFileName);

        DateFormat df1 = new SimpleDateFormat("dd  MMM  yyyy");
        DateFormat df3 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

        Date dateObj = new Date();

        try {
            if (mFilterDate != null) {
                if (!mFilterDate.equals("")) {
                    Date initDate
                            = new SimpleDateFormat(
                            getApplicationContext().getResources().getString(R.string.date_format_yyyy_MM_dd)
                            , Locale.ENGLISH).parse(mFilterDate);
                    mHeaderDate = "for " + df1.format(initDate);
                } else {
                    mHeaderDate = "for " + df1.format(dateObj).toString();
                }
            } else {
                mHeaderDate = "for " + df1.format(dateObj).toString();
            }
        } catch (Exception ee) {
            Log.e("Exception:", "Date conversion error!");
        }

        mPrintedDateTime = df3.format(dateObj).toString();
        mProgressDialog = new ProgressDialog(EmailInvoiceSummaryActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void getSalesInvoiceSummaryList(String mFilterDate) {
        SalesOrderDbHandler dbAdapter
                = new SalesOrderDbHandler(EmailInvoiceSummaryActivity.this);
        dbAdapter.open();
        mSalesOrderList = dbAdapter.getSalesInvoiceSummaryList(mFilterDate);
        dbAdapter.close();
    }

    private class GetSalesOrderListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                getSalesInvoiceSummaryList(mFilterDate);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Sending email...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                try {

                    generateInvoiceSummaryPDF();

                    if (mPdfFile != null) {
                        startUploadPdf();
                    } else {
                        Toast.makeText(mApp, "Pdf is not created!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }

                } catch (Exception ex) {
                    mProgressDialog.dismiss();
                }

            } else {

                Toast.makeText(EmailInvoiceSummaryActivity.this
                        , "No data", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
        }
    }

    private void generateInvoiceSummaryPDF() {


        try {
            Document document = new Document(); // will set page size to A4
            document.setMargins(15, 15, 40, 15); //top margin for header (40)
            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(mPdfFile));

            Header event = new Header();
            docWriter.setPageEvent(event);
            //print Print date in header
            document.open();

            PdfPTable tblHeader = new PdfPTable(1);
            tblHeader.setWidthPercentage(100);

            //empty row
            PdfPTable innerTblEmptyRow = new PdfPTable(1);
            innerTblEmptyRow.setWidthPercentage(100);
            innerTblEmptyRow.addCell(getCell("\n", PdfPCell.ALIGN_CENTER, mPlainFont));

            // Title
            PdfPTable innerTblHeaderTitle = new PdfPTable(1);
            innerTblHeaderTitle.setWidthPercentage(100);
            innerTblHeaderTitle.addCell(getCell("Invoice Summary", PdfPCell.ALIGN_CENTER,
                    mPlainFontTitle));
            tblHeader.addCell(getCellTable(innerTblHeaderTitle, PdfPCell.ALIGN_CENTER));

            //header date
            PdfPTable innerTblHeaderDate = new PdfPTable(1);
            innerTblHeaderDate.setWidthPercentage(100);
            innerTblHeaderDate.addCell(getCell(mHeaderDate, PdfPCell.ALIGN_CENTER,
                    mPlainFontHeader));
            tblHeader.addCell(getCellTable(innerTblHeaderDate, PdfPCell.ALIGN_LEFT));

            tblHeader.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            //add vansales
            PdfPTable innerTblHeaderSalesMan = new PdfPTable(1);
            innerTblHeaderSalesMan.setWidthPercentage(100);
            innerTblHeaderSalesMan.addCell(getCell("Vansales : "
                            + mApp.getCurrentUserDisplayName(), PdfPCell.ALIGN_CENTER,
                    mPlainFontHeader));
            tblHeader.addCell(getCellTable(innerTblHeaderSalesMan, PdfPCell.ALIGN_LEFT));

            tblHeader.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            PdfPTable tableInvoiceSummary = new PdfPTable(1);
            tableInvoiceSummary.setWidthPercentage(100);
            tableInvoiceSummary.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            //dotedline
            tableInvoiceSummary.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));
            PdfPTable innerTblStockBalenceHeader = new PdfPTable(4);
            innerTblStockBalenceHeader.setWidthPercentage(100);
            innerTblStockBalenceHeader.setWidths(new int[]{3, 3, 15, 2});
            innerTblStockBalenceHeader.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            innerTblStockBalenceHeader.addCell(getCell("Invoice", PdfPCell.ALIGN_LEFT, mPlainFont));
            innerTblStockBalenceHeader.addCell(getCell("Request No", PdfPCell.ALIGN_LEFT, mPlainFont)); //updated on 2018-04-11
            innerTblStockBalenceHeader.addCell(getCell("Customer", PdfPCell.ALIGN_LEFT, mPlainFont));
            innerTblStockBalenceHeader.addCell(getCell("Sales($)", PdfPCell.ALIGN_RIGHT, mPlainFont));

            tableInvoiceSummary.addCell(getCellTable(innerTblStockBalenceHeader, PdfPCell.ALIGN_LEFT));

            //dotedline
            tableInvoiceSummary.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));

            PdfPTable innerTblInvoiceContent = new PdfPTable(4);
            innerTblInvoiceContent.setWidthPercentage(100);
            innerTblInvoiceContent.setWidths(new int[]{3, 3, 15, 2});
            innerTblInvoiceContent.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            float totalSalesAmt = 0f;

            if (mSalesOrderList.size() > 0) {
                for (SalesOrder so : mSalesOrderList) {

                    if(so.getAmountIncludingVAT() > 0f)
                    {
                        innerTblInvoiceContent.addCell(
                                getCell(so.getSINo()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblInvoiceContent.addCell(
                                getCell(so.getNo()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblInvoiceContent.addCell(
                                getCell(so.getSelltoCustomerName()
                                        , PdfPCell.ALIGN_LEFT
                                        , mPlainFont));
                        innerTblInvoiceContent.addCell(
                                getCell(String.format("%.2f", so.getAmountIncludingVAT())
                                        , PdfPCell.ALIGN_RIGHT
                                        , mPlainFont));

                        totalSalesAmt = totalSalesAmt + so.getAmountIncludingVAT();
                    }

                }
            }

            tableInvoiceSummary.addCell(getCellTable(innerTblInvoiceContent, PdfPCell.ALIGN_LEFT));

            PdfPTable outerTblSummaryContent = new PdfPTable(3);
            outerTblSummaryContent.setWidthPercentage(100);
            outerTblSummaryContent.setWidths(new int[]{3, 7, 3});
            outerTblSummaryContent.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            outerTblSummaryContent.addCell(
                    getCell("Total Sales :"
                            , PdfPCell.ALIGN_LEFT
                            , mPlainFontHeader));
            outerTblSummaryContent.addCell(
                    getCell("$"
                            , PdfPCell.ALIGN_RIGHT
                            , mPlainFontHeader));
            outerTblSummaryContent.addCell(
                    getCell(String.format("%.2f", totalSalesAmt)
                            , PdfPCell.ALIGN_RIGHT
                            , mPlainFontHeader));

            //dash lines
            tableInvoiceSummary.addCell(getTblDashLines(Element.ALIGN_MIDDLE));

            tableInvoiceSummary.addCell(getCellTable(outerTblSummaryContent, PdfPCell.ALIGN_LEFT));

            //dash lines
            tableInvoiceSummary.addCell(getTblDashLines(Element.ALIGN_MIDDLE));
            tableInvoiceSummary.setSplitLate(false);// this will avoid starting invoice list in a new page
                                                        // when more than one page. nalaka 2018-05-18
            document.add(tblHeader);
            document.add(tableInvoiceSummary);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
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

    public PdfPTable getTblDashLines(int alignment) {
        DottedLineSeparator seperator = new CustomDashedLineSeparator();
        seperator.setGap(2f);
        seperator.setLineWidth(0.8f);
        Paragraph p = new Paragraph();
        p.add(seperator);

        PdfPTable tblDottedLines = new PdfPTable(1);
        tblDottedLines.getDefaultCell().setFixedHeight(3);
        tblDottedLines.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        tblDottedLines.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.addElement(seperator);
        cell.setHorizontalAlignment(alignment);
        cell.setFixedHeight(3);
        cell.setBorder(PdfPCell.NO_BORDER);

        tblDottedLines.addCell(cell);
        return tblDottedLines;
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
            this.table.setTotalWidth(document.getPageSize().getWidth() - 35);
            //this.table.setLockedWidth(true);


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

    class CustomDashedLineSeparator extends DottedLineSeparator {
        protected float dash = 5;
        protected float phase = 2.5f;

        public float getDash() {
            return dash;
        }

        public float getPhase() {
            return phase;
        }

        public void setDash(float dash) {
            this.dash = dash;
        }

        public void setPhase(float phase) {
            this.phase = phase;
        }

        public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
            canvas.saveState();
            canvas.setLineWidth(lineWidth);
            canvas.setLineDash(dash, gap, phase);
            drawLine(canvas, llx, urx, y);
            canvas.restoreState();
        }
    }

    private void startSendEmail() {

        String mSubject = ""
                + "Invoice Summary" + " - "
                + mApp.getmCurrentDriverCode() + " - "
                + mFilterDate;

        String mBody = ""
                + "Report: Invoice Summary" + '\n'
                + "Driver: " + mApp.getmCurrentDriverCode() + "\n"
                + "Date :" + mFilterDate;

        mSendEmailSyncTask = new SendEmailSyncTask(getApplicationContext()
                , true
                , mFilterDate
                , mFileName
                , mBody
                , mSubject);
        mSendEmailSyncTask.delegate = EmailInvoiceSummaryActivity.this;
        mSendEmailSyncTask.execute((Void) null);
        //Toast.makeText(mApp, "Sending email...", Toast.LENGTH_LONG).show();
    }

    private void startUploadPdf() {
        mPdfUploadSyncTask = new PdfUploadSyncTask(getApplicationContext()
                , true
                , mFileName
                ,"InvoiceSummaryReport");
        mPdfUploadSyncTask.delegate = EmailInvoiceSummaryActivity.this;
        mPdfUploadSyncTask.execute((Void) null);
        //Toast.makeText(mApp, "Uploading report..", Toast.LENGTH_LONG).show();
    }
}
