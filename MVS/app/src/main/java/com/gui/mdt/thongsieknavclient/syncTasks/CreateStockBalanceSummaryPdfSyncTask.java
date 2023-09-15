package com.gui.mdt.thongsieknavclient.syncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ItemBalancePda;
import com.gui.mdt.thongsieknavclient.datamodel.SyncConfiguration;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.ItemBalancePdaDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateStockBalanceSummaryPdfSyncTask extends AsyncTask<Void, Void, Boolean> {

    private List<ItemBalancePda> mItemBalancePdaList;
    Context mContext;
    String mFileName = "", mFilterDate = "", mReportDate = "", mDetails = "", mPrintedDateTime = "";
    File mPdfFile;
    Font mPlainFont, mPlainFontTitle, mPlainFontHeader;
    NavClientApp mApp;
    SyncConfiguration mSyncConfig;
    public AsyncResponse mDelegate = null;
    Logger mLog;

    public CreateStockBalanceSummaryPdfSyncTask(Context context, String details) {
        this.mDetails = details;
        this.mContext = context;
    }

    private void initComp() {
        mApp = (NavClientApp) mContext;
        this.mLog = Logger.getLogger(CreateStockBalanceSummaryPdfSyncTask.class);
        mPlainFont = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.NORMAL);
        mPlainFontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD);
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

        mPdfFile = new File(mContext.getExternalFilesDir("MyStockBalanceReports"), mFileName);

        DateFormat df1 = new SimpleDateFormat("dd  MMM  yyyy");
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df3 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

        Date dateObj = new Date();

        mReportDate = df1.format(dateObj).toString();
        mFilterDate = df2.format(dateObj).toString();
        mPrintedDateTime = df3.format(dateObj).toString();
    }

    private void getItemBalancePdaList() {
        mItemBalancePdaList = new ArrayList<ItemBalancePda>();
        ItemBalancePdaDbHandler ibpDb = new ItemBalancePdaDbHandler(mContext);
        ibpDb.open();

        mItemBalancePdaList = ibpDb.getItemBalanceList(mApp.getmCurrentDriverCode());

        ibpDb.close();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        initComp();

        mSyncConfig = new SyncConfiguration();
        mSyncConfig.setLastSyncBy(mApp.getCurrentUserName());
        mSyncConfig.setScope(mContext.getResources().getString(R.string.SyncScopeCreateStockBalSumPdf));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            getItemBalancePdaList();

            generatePdfReport();

            logResponse(mPdfFile);

        } catch (Exception e) {
            return false;
        }

        return true;
    }


    @Override
    protected void onPostExecute(final Boolean success) {
        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setScope(mSyncConfig.getScope());

        if (success) {

            if (mPdfFile != null) {
                syncStatus.setStatus(true);
                syncStatus.setMessage(mFileName + "#" + mFilterDate);
                mDelegate.onAsyncTaskFinished(syncStatus);
            } else {
                syncStatus.setStatus(false);
                mDelegate.onAsyncTaskFinished(syncStatus);
            }
        } else {
            syncStatus.setStatus(false);
            mDelegate.onAsyncTaskFinished(syncStatus);
        }
    }

    @Override
    protected void onCancelled() {
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
            String titlePram = "";
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
            innerTblHeaderSalesMan.addCell(getCell("Vansales : "
                            + mApp.getCurrentUserDisplayName(), PdfPCell.ALIGN_CENTER,
                    mPlainFontHeader));
            tblHeader.addCell(getCellTable(innerTblHeaderSalesMan, PdfPCell.ALIGN_LEFT));

            tblHeader.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            PdfPTable tableStockBalance = new PdfPTable(1);
            tableStockBalance.setWidthPercentage(100);
            tableStockBalance.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            //dotedline
            tableStockBalance.addCell(getTblDotedLines(Element.ALIGN_MIDDLE));

            if (mDetails.equals("Unload") || mDetails.equals("")) {
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
            } else if (mDetails.equals("Load")) {
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
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
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
            this.table.setTotalWidth(document.getPageSize().getWidth() - 35);
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

    private void logResponse(File params) {
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(params.getName());
        //Log.d("SYNC_SO_DOWN_PARAMS", json);
        mLog.info("SYNC_CREATE_STOCK_BALANCE_SUMMARY_PDF_RESPONSE :" + json);
    }
}
