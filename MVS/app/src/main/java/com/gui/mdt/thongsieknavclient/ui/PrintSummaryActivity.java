package com.gui.mdt.thongsieknavclient.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.adapters.MsoSalesOrderListAdapter;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentHeader;
import com.gui.mdt.thongsieknavclient.datamodel.PaymentLine;
import com.gui.mdt.thongsieknavclient.datamodel.SalesOrder;
import com.gui.mdt.thongsieknavclient.datamodel.SyncStatus;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentHeaderDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.PaymentLineDbHandler;
import com.gui.mdt.thongsieknavclient.dbhandler.SalesOrderDbHandler;
import com.gui.mdt.thongsieknavclient.interfaces.AsyncResponse;
import com.gui.mdt.thongsieknavclient.syncTasks.PdfUploadSyncTask;
import com.gui.mdt.thongsieknavclient.syncTasks.SendEmailSyncTask;
import com.gui.mdt.thongsieknavclient.utils.BluetoothPrinter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by GUISL-NB05 on 8/29/2017.
 */

public class PrintSummaryActivity extends AppCompatActivity implements AsyncResponse {

    private static Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private static MsoSalesOrderListAdapter salesOrderListAdapter;
    NavClientApp mApp;
    List<SalesOrder> salesOrderList;
    List<PaymentHeader> paymentList;
    List<PaymentLine> paymentLineList;
    DateFormat outputDateFormat;
    DateFormat inputDateFormat;
    PdfUploadSyncTask pdfUploadSyncTask;
    SendEmailSyncTask sendEmailSyncTask;
    private File pdfFile;
    private String filename = "Payment";
    private String filepath = "MyInvoices";
    private float totalValue = 0;
    private float totalValuePaymentCash = 0;
    private float totalValuePaymentCheque = 0;
    private Bundle extras;
    private String mFilterDate, mFilterDateforPrint, mSelectedItem, mFilterSalesPersonCode,
            mFilterCustomerCode, mFilterCustomerName, mFilterSalesOrderNo, mFilterStatus;
    //private ProgressDialog mProgressDialog;
    private String strDate = "";
    private getSalesOrderListTask getSalesOrderListTask = null;
    private boolean isEmail, isPrint;
    Font plainFont,
            plainFontCompanyName,
            plainFontCompanyDetail,
            plainFontReportTitle,
            plainFontFilterDetail,
            plainFontColumnHeader,
            PlainFontCashRegistry;

    //printer configuration
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String printerName = "BlueTooth Printer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NavClientApp) getApplicationContext();

        outputDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        plainFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
        plainFontCompanyName = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        plainFontCompanyDetail = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        plainFontReportTitle = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD | Font.UNDERLINE);
        plainFontFilterDetail = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        plainFontColumnHeader = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
        PlainFontCashRegistry= new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);


        filename = filename + "-"
                + mApp.getmCurrentSalesPersonCode() + "-"
                + DateTime.now().toString().replace("-", "").replace(":", "").replace(".", "").replace("+", "")
                + ".pdf";
        pdfFile = new File(getExternalFilesDir(filepath), filename);
        /*if(pdfFile.exists()){
            String revNo= filename.substring(filename.length()-1);
            int no =
            filename=filename+
        }*/
        extras = getIntent().getExtras();
        mFilterDate = extras.getString("filterCreatedDate");
        try {
            mFilterDateforPrint = outputDateFormat.format(inputDateFormat.parse(mFilterDate));
        } catch (Exception ex) {

        }
        mSelectedItem = extras.getString("selectedItem");
        isEmail = extras.getBoolean("isEmail", false);
        isPrint = extras.getBoolean("isPrint", false);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        strDate = sdf.format(c.getTime());

        mFilterSalesPersonCode = "";
        mFilterCustomerCode = "";
        mFilterCustomerName = "";
        mFilterSalesOrderNo = "";
        mFilterStatus = "";


        mFilterSalesPersonCode = mApp.getmCurrentSalesPersonCode();

        getSalesOrderListTask = new getSalesOrderListTask();
        getSalesOrderListTask.delegate = PrintSummaryActivity.this;
        getSalesOrderListTask.execute((Void) null);

        //mProgressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    @Override
    public void onAsyncTaskFinished(SyncStatus syncStatus) {
        //when report upload success
        if (syncStatus.getScope() == getResources().getString(R.string.SyncScopePdf)) {

            if (syncStatus.isStatus()) {
                startSendEmail();
            } else {
                Toast.makeText(mApp, syncStatus.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (syncStatus.getScope() == getResources().getString(R.string.SyncScopeSendEmail)) {

            if (syncStatus.isStatus()) {
                Toast.makeText(mApp, "Email Sent.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mApp, "Email Sending Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getPaymentList(String customerName, String paymentDate, String status) {
        PaymentHeaderDbHandler paymentAdapter = new PaymentHeaderDbHandler(PrintSummaryActivity.this);
        paymentAdapter.open();
        paymentList = paymentAdapter.getAllPaymentHeaders(customerName, paymentDate, status, "");
        paymentAdapter.close();
    }

    private void getSalesOrderList(
            String mFilterSalesPersonCode,
            String mFilterCustomerCode,
            String mFilterCustomerName,
            String mFilterSalesOrderNo,
            String mFilterDate,
            String mFilterStatus) {
        SalesOrderDbHandler dbAdapter = new SalesOrderDbHandler(PrintSummaryActivity.this);
        dbAdapter.open();
        salesOrderList = dbAdapter.getSalesOrderList(
                mFilterSalesPersonCode,
                mFilterCustomerCode,
                mFilterCustomerName,
                mFilterSalesOrderNo,
                mFilterDate,
                mFilterStatus, false);
        dbAdapter.close();
    }

    private void generatePaymentSummaryPDF(String personName) {
        try {
            Document document = new Document(); // will set page size to A4
            document.setMargins(20, 20, 200, 20); //top margin for header (200)
            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            //print page no in header
            Header event = new Header();
            docWriter.setPageEvent(event);
            //event.setHeader();

            document.open();

            /*Font plainFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
            Font plainFontCompanyName = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
            Font plainFontCompanyDetail = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font plainFontReportTitle = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD | Font.UNDERLINE);
            Font plainFontFilterDetail = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
            Font plainFontColumnHeader = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);*/

            PdfPTable tableHeader = new PdfPTable(1);
            tableHeader.setWidthPercentage(100);

            //add logo and name
            PdfPTable innerTblCompanyInfo = new PdfPTable(2);
            innerTblCompanyInfo.setWidthPercentage(100);
            innerTblCompanyInfo.setWidths(new int[]{1, 4});
            innerTblCompanyInfo.setTotalWidth(document.getPageSize().getWidth() - 40);

            //Add company Logo cell
            InputStream inputStream = getAssets().open("DODOLogo.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAlignment(PdfPCell.ALIGN_RIGHT);
            //companyLogo.setAbsolutePosition(55, 750);
            companyLogo.scalePercent(25);
            innerTblCompanyInfo.addCell(getCellImage(companyLogo, PdfPCell.ALIGN_RIGHT));

            //add company name cell
            String lineCompanyName = "DODO MARKETING PTE. LTD.\n";
            String lineCompanyAddress = "22 SENOKO WAY SINGAPORE 758044\n";
            String lineCompanyContact = "TEL: 6852 8303 FAX: 6555 2391";
            Paragraph phCompanyDetail = new Paragraph();
            phCompanyDetail.setPaddingTop(5);
            Phrase phCompanyName = new Phrase(Element.ALIGN_CENTER, lineCompanyName, plainFontCompanyName);
            Phrase phCompanyAddress = new Phrase(Element.ALIGN_CENTER, lineCompanyAddress, plainFontCompanyDetail);
            Phrase phCompanyContact = new Phrase(Element.ALIGN_CENTER, lineCompanyContact, plainFontCompanyDetail);
            phCompanyDetail.add(phCompanyName);
            phCompanyDetail.add(phCompanyAddress);
            phCompanyDetail.add(phCompanyContact);
            innerTblCompanyInfo.addCell(getCellParagraph(phCompanyDetail, PdfPCell.ALIGN_CENTER));
            tableHeader.addCell(getCellTable(innerTblCompanyInfo, PdfPCell.ALIGN_CENTER));


            //Add report name
            PdfPTable innerTblEmptyRow = new PdfPTable(1);
            innerTblEmptyRow.setWidthPercentage(100);
            //innerTblEmptyRow.setTotalWidth(document.getPageSize().getWidth());
            innerTblEmptyRow.addCell(getCellLineSeperator(" "));
            tableHeader.addCell(getCellTable(innerTblEmptyRow, PdfPCell.ALIGN_LEFT));

            PdfPTable innerTblEmptyRowBelowSeparator = new PdfPTable(1);
            innerTblEmptyRowBelowSeparator.setWidthPercentage(100);
            innerTblEmptyRowBelowSeparator.addCell(getCell("\n", PdfPCell.ALIGN_CENTER, plainFont));
            tableHeader.addCell(getCellTable(innerTblEmptyRowBelowSeparator, PdfPCell.ALIGN_LEFT));
            //tableHeader.addCell(getCellTable(innerTblEmptyRowBelowSeperator, PdfPCell.ALIGN_LEFT));

            PdfPTable innerTblReportName = new PdfPTable(1);
            innerTblReportName.setWidthPercentage(100);
            innerTblReportName.addCell(getCell("Payment Collection Summary Report", PdfPCell.ALIGN_CENTER,
                    plainFontReportTitle));
            tableHeader.addCell(getCellTable(innerTblReportName, PdfPCell.ALIGN_CENTER));

            PdfPTable innerTblEmptyRow1 = new PdfPTable(1);
            innerTblEmptyRow1.setWidthPercentage(100);
            innerTblEmptyRow1.addCell(getCell("\n", PdfPCell.ALIGN_CENTER, plainFont));
            tableHeader.addCell(getCellTable(innerTblEmptyRow1, PdfPCell.ALIGN_LEFT));

            //add filter info
            PdfPTable innerTblReportFilterInfo = new PdfPTable(2);
            innerTblReportFilterInfo.setWidthPercentage(100);
            innerTblReportFilterInfo.addCell(getCell("Payment Date : " + mFilterDateforPrint, PdfPCell.ALIGN_LEFT,
                    plainFontColumnHeader));
            innerTblReportFilterInfo.addCell(getCell("Print Date : " + strDate, PdfPCell.ALIGN_RIGHT,
                    plainFontColumnHeader));
            tableHeader.addCell(getCellTable(innerTblReportFilterInfo, PdfPCell.ALIGN_LEFT));

            ////add filter Salesperson Info
            PdfPTable innerTblSalespersonInfo = new PdfPTable(2);
            innerTblSalespersonInfo.setWidthPercentage(100);
            innerTblSalespersonInfo.addCell(getCell("Salesperson: " + mFilterSalesPersonCode, PdfPCell.ALIGN_LEFT,
                    plainFontColumnHeader));
            innerTblSalespersonInfo.addCell(getCell("REC : ______________", PdfPCell.ALIGN_RIGHT,
                    plainFontColumnHeader));
            tableHeader.addCell(getCellTable(innerTblSalespersonInfo, PdfPCell.ALIGN_LEFT));

            PdfPTable innerTblEmptyRow4 = new PdfPTable(1);
            innerTblEmptyRow4.setWidthPercentage(100);
            innerTblEmptyRow4.addCell(getCell("\n", PdfPCell.ALIGN_CENTER, plainFont));
            tableHeader.addCell(getCellTable(innerTblEmptyRow4, PdfPCell.ALIGN_LEFT));

            //document.add(tableHeader);
            event.setHeader(tableHeader);

            //payment list
            PdfPTable tableCash = new PdfPTable(1);
            tableCash.setWidthPercentage(100);
            PdfPTable tableCheque = new PdfPTable(1);
            tableCheque.setWidthPercentage(100);

            //add payment details inner tables
            PdfPTable innerTblCashPaymentList = new PdfPTable(6);
            innerTblCashPaymentList.setComplete(false);
            innerTblCashPaymentList.setWidthPercentage(100);
            innerTblCashPaymentList.setWidths(new int[]{1, 3, 2, 12, 6, 3});
            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "No", plainFontColumnHeader));
            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Payment No", plainFontColumnHeader));
            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Customer Code", plainFontColumnHeader));
            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Customer Name", plainFontColumnHeader));
            //innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Payment Mode", plainFontColumnHeader));
            //innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Cheque No", plainFontColumnHeader));
            //innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Cheque Date", plainFontColumnHeader));
            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Remark", plainFontColumnHeader));
            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_RIGHT, "Amount($)", plainFontColumnHeader));
            innerTblCashPaymentList.setHeaderRows(1);

            PdfPTable innerTblChequePaymentList = new PdfPTable(9);
            innerTblChequePaymentList.setComplete(false);
            innerTblChequePaymentList.setWidthPercentage(100);
            innerTblChequePaymentList.setWidths(new int[]{1, 3, 2, 5,2, 2, 2, 6, 3});
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "No", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Payment No", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Customer Code", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Customer Name", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Payer", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Cheque No", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Cheque Date", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Remark", plainFontColumnHeader));
            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_RIGHT, "Amount($)", plainFontColumnHeader));
            innerTblChequePaymentList.setHeaderRows(1);

            int lineNoCash = 0;
            int lineNoCheque = 0;
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

            for (int i = 0; i < paymentList.size(); ) {


                //show payment mode
                PaymentLineDbHandler paymentLineAdapter = new PaymentLineDbHandler(getApplicationContext());
                paymentLineAdapter.open();
                paymentLineList = paymentLineAdapter.getAllPaymentLinesByPaymentNo(paymentList.get(i).getPaymentNo());
                paymentLineAdapter.close();

                if (paymentLineList.size() > 0) {

                    for (PaymentLine pl : paymentLineList) {

                        if (pl.getPaymentType().equals("0")) {
                            lineNoCash = lineNoCash + 1;
                            innerTblCashPaymentList.getDefaultCell().setMinimumHeight(20);

                            //show line no
                            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, String.valueOf(lineNoCash)
                                    , plainFont));
                            //show payment no
                            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, paymentList.get(i)
                                    .getPaymentNo(), plainFont));
                            //show customer code
                            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, paymentList.get(i)
                                    .getCustomerNo(), plainFont));
                            //show customer name
                            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, paymentList.get(i)
                                    .getCustomerName(), plainFont));
                            //show payment Mode
                            //innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Cash", plainFont));

                            //show cheque no
                            /*innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, pl.getChequeNo(),
                                    plainFont));*/
                            //show cheque date
                            /*innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, pl.getChequeDate(),
                                    plainFont));*/
                            //show remark
                            innerTblCashPaymentList.addCell(new Phrase(Element.ALIGN_LEFT, pl.getRemark(), plainFont));
                            //show amount
                            innerTblCashPaymentList.addCell(
                                    getCellTableCurrency(
                                            formatter.format(Double.valueOf(pl.getAmount())),
                                            Element.ALIGN_RIGHT,
                                            plainFont));
                            totalValuePaymentCash = totalValuePaymentCash +
                                    Float.parseFloat(String.valueOf(pl.getAmount()));

                        } else {

                            lineNoCheque = lineNoCheque + 1;
                            innerTblChequePaymentList.getDefaultCell().setMinimumHeight(20);

                            //show line no
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, String.valueOf
                                    (lineNoCheque), plainFont));
                            //show payment no
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, paymentList.get(i)
                                    .getPaymentNo(), plainFont));
                            //show customer code
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, paymentList.get(i)
                                    .getCustomerNo(), plainFont));
                            //show customer name
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, paymentList.get(i)
                                    .getCustomerName(), plainFont));
                            //show Payer (cheque name)
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, pl.getChequeName()
                                    , plainFont));
                            //show payment Mode
                            //innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, "Cheque", plainFont));

                            //show cheque no
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, pl.getChequeNo(),
                                    plainFont));
                            //show cheque date
                            String chDate = outputDateFormat.format(inputDateFormat.parse(pl.getChequeDate()));
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, chDate,
                                    plainFont));
                            //show remark
                            innerTblChequePaymentList.addCell(new Phrase(Element.ALIGN_LEFT, pl.getRemark(),
                                    plainFont));
                            //show amount
                            innerTblChequePaymentList.addCell(
                                    getCellTableCurrency(
                                            formatter.format(Double.valueOf(pl.getAmount())),
                                            Element.ALIGN_RIGHT,
                                            plainFont));

                            totalValuePaymentCheque = totalValuePaymentCheque +
                                    Float.parseFloat(String.valueOf(pl.getAmount()));

                        }

                    }
                }
                i++;
            }
            innerTblCashPaymentList.completeRow();
            innerTblChequePaymentList.completeRow();

            tableCash.addCell(getCellTable(innerTblCashPaymentList, PdfPCell.ALIGN_LEFT));
            tableCheque.addCell(getCellTable(innerTblChequePaymentList, PdfPCell.ALIGN_LEFT));

            //show cash total amount at bottom
            PdfPTable innerTblTotalCash = new PdfPTable(1);
            innerTblTotalCash.setWidthPercentage(100);
            innerTblTotalCash.addCell(getCell(
                    "Total Cash Collection($) : " + String.format("%.2f", totalValuePaymentCash)
                    , PdfPCell.ALIGN_RIGHT, plainFontFilterDetail));
            tableCash.addCell(getCellTable(innerTblTotalCash, PdfPCell.ALIGN_RIGHT));

            //show cheque total amount at bottom
            PdfPTable innerTblTotalCheque = new PdfPTable(1);
            innerTblTotalCheque.setWidthPercentage(100);
            innerTblTotalCheque.addCell(getCell(
                    "Total Cheque Collection($) : " + String.format("%.2f", totalValuePaymentCheque)
                    , PdfPCell.ALIGN_RIGHT, plainFontFilterDetail));
            tableCheque.addCell(getCellTable(innerTblTotalCheque, PdfPCell.ALIGN_RIGHT));

            //show total()cheque+cash) amount at bottom
            PdfPTable innerTblTotal = new PdfPTable(1);
            innerTblTotal.setWidthPercentage(100);
            innerTblTotal.addCell(getCell(
                    "Total Collection($) : " + String.format("%.2f", totalValuePaymentCheque + totalValuePaymentCash)
                    , PdfPCell.ALIGN_RIGHT, plainFontFilterDetail));
            //tableCheque.addCell(getCellTable(innerTblTotal, PdfPCell.ALIGN_RIGHT)); //UAT-2017/11/22

            PdfPTable tableCashHeader = new PdfPTable(1);
            tableCashHeader.setWidthPercentage(100);
            tableCashHeader.addCell(getCell("Cash", PdfPCell.ALIGN_LEFT, plainFontFilterDetail));
            document.add(tableCashHeader);
            tableCash.setSplitLate(false);
            document.add(tableCash);
            document.add(innerTblEmptyRow4);
            document.add(innerTblEmptyRow4);
            document.add(getCashRegistryTable(document));

            PdfPTable tableChequeHeader = new PdfPTable(1);
            tableChequeHeader.setWidthPercentage(100);
            tableChequeHeader.addCell(getCell("Cheque", PdfPCell.ALIGN_LEFT, plainFontFilterDetail));
            document.newPage();
            document.add(tableChequeHeader);
            tableCheque.setSplitLate(false);
            document.add(tableCheque);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        //start print
        print();

        //open preview
        if (isPrint) {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent openPDFIntent_ = Intent.createChooser(target, "Open File");
            try {
                //mProgressDialog.dismiss();
                startActivity(openPDFIntent_);

            } catch (Exception e) {
                Toast.makeText(mApp, "Please install PDF reader to open the file!", Toast.LENGTH_SHORT).show();
            }
        }

        if (isEmail) {
            //startUploadPdf();

            try {
                findBT();
                openBT();
                sendData();
            }catch (Exception ex){

            }


        }

    }

    private PdfPTable getCashRegistryTable(Document doc){
        PdfPTable cashRegistryTable = new PdfPTable(1);
        cashRegistryTable.setTotalWidth(doc.getPageSize().getWidth());


        try {

            PdfPTable innerTableNotes = new PdfPTable(9);
            innerTableNotes.setComplete(false);
            innerTableNotes.setWidthPercentage(100);
            innerTableNotes.setWidths(new int[]{4,1,1,4,  2  ,4,1,1,4});

            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$1,000",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("$1.00",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));


            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$100",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("$0.50",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));

            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$50",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("$0.20",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));


            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$10",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("$0.10",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));

            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$5",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("$0.05",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));

            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$2",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("$0.01",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));


            innerTableNotes.getDefaultCell().setMinimumHeight(30);
            innerTableNotes.addCell(getCell("$1",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("X",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell(" ",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableNotes.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));

            cashRegistryTable.addCell(getCellTable(innerTableNotes, PdfPCell.ALIGN_LEFT));

            PdfPTable innerTableTotal = new PdfPTable(5);
            innerTableTotal.setComplete(false);
            innerTableTotal.setWidthPercentage(100);
            innerTableTotal.setWidths(new int[]{6,4,  2  ,6,4});
            innerTableTotal.getDefaultCell().setMinimumHeight(30);
            innerTableTotal.addCell(getCell("TOTAL NOTES",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableTotal.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableTotal.addCell(getCell("",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableTotal.addCell(getCell("TOTAL COINS",Element.ALIGN_CENTER,PlainFontCashRegistry));
            innerTableTotal.addCell(getCell("=",Element.ALIGN_CENTER,PlainFontCashRegistry));

            cashRegistryTable.addCell(getCellTable(innerTableTotal, PdfPCell.ALIGN_LEFT));

        }catch(Exception ex){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
            Log.d("Report Error", "getCashRegistryTable: " + ex.toString());
        }

        return cashRegistryTable;
    }

    public PdfPCell getCell(String element1, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(element1, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellTableCurrency(String element1, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(element1, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public PdfPCell getCellTable(String element1, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(element1, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public PdfPCell getCellLineSeperator(String element1) {
        PdfPCell cell = new PdfPCell(new Phrase(element1));
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthRight(0);
        return cell;
    }

    public PdfPCell getCellImage(Image element1, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellTable(PdfPTable element1, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellTableWithBorder(PdfPTable element1, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element1);
//        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
//        cell.setBorder(PdfPCell.NO_BORDER);
//        cell.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
        return cell;
    }

    public PdfPCell getCellParagraph(Paragraph element1, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(element1));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("Nexus 5X")) {
                        mmDevice = device;
                        break;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    public void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //after opening a connection to bluetooth printer device,
    //we have to listen and check if a data were sent to be printed.
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    public void sendData() throws IOException {
        try {

            // the text typed by the user
            //String msg = myTextbox.getText().toString();
            // msg += "\n";
            InputStream is = new FileInputStream(pdfFile); // Where this is Activity
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int FILE_CHUNK_SIZE = 1024 * 4;
            byte[] b = new byte[FILE_CHUNK_SIZE];
            int read = -1;
            while ((read = is.read(b)) != -1) {
                bos.write(b, 0, read);
            }
            byte[] bytes = bos.toByteArray();

            byte[] printformat = {27, 33, 0}; //try adding this print format

            mmOutputStream.write(bytes);

            // tell the user data were sent
            openBT();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startUploadPdf() {
        pdfUploadSyncTask = new PdfUploadSyncTask(getApplicationContext(), true, filename);
        pdfUploadSyncTask.delegate = PrintSummaryActivity.this;
        pdfUploadSyncTask.execute((Void) null);
        Toast.makeText(mApp, "Uploading report..", Toast.LENGTH_LONG).show();
        //mProgressDialog.setMessage("Uploading report...");
        //mProgressDialog.show();
    }

    private void startSendEmail() {
        String mSubject=""
                +"Payment Summery - "
                +  mApp.getmCurrentSalesPersonCode() +" - "
                + mFilterDate;

        String mBody=""
                +"Report: Payment Summary \n"
                + "Salesperson: " + mApp.getmCurrentSalesPersonCode() + "\n"
                + "Date :" + mFilterDate;

        sendEmailSyncTask = new SendEmailSyncTask(getApplicationContext(), true, mFilterDate, filename, mBody, mSubject);
        sendEmailSyncTask.delegate = PrintSummaryActivity.this;
        sendEmailSyncTask.execute((Void) null);
        Toast.makeText(mApp, "Sending email...", Toast.LENGTH_LONG).show();
        //mProgressDialog.setMessage("Sending Mail...");
        //mProgressDialog.show();
    }

    private class getSalesOrderListTask extends AsyncTask<Void, Void, Boolean> {

        public AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                if (mSelectedItem.equals("Sales Order Summary")) {
                    getSalesOrderList(
                            mFilterSalesPersonCode
                            , mFilterCustomerCode,
                            mFilterCustomerName,
                            mFilterSalesOrderNo,
                            mFilterDate,
                            mFilterStatus);
                } else {
                    getPaymentList("", mFilterDate, mFilterStatus);
                }


            } catch (Exception e) {
                Log.d("Exception", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            /*mProgressDialog = new ProgressDialog(PrintSummaryActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();*/
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            SyncStatus syncStatus = new SyncStatus();
            syncStatus.setScope("");

            if (success) {
                try {

                    if (mSelectedItem.equals("Sales Order Summary")) {
                        //generateSoSummaryPDF();
                    } else {
                        generatePaymentSummaryPDF("");
                        /*if(isEmail){
                            syncStatus.setStatus(true);
                            delegate.onAsyncTaskFinished(syncStatus);
                        }*/
                    }

                    //mProgressDialog.dismiss();
                } catch (Exception ex) {
                }
                //mProgressDialog.dismiss();
            } else {
                //mProgressDialog.dismiss();
                Toast.makeText(PrintSummaryActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            //mProgressDialog.dismiss();
        }
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
            this.table.setTotalWidth(document.getPageSize().getWidth() - 40);
            this.table.setLockedWidth(true);

            table.addCell(getCell(
                    "Page No." +String.valueOf(document.getPageNumber())
                    ,Element.ALIGN_RIGHT,plainFont));

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

    private void print() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mBtDevice = btAdapter.getBondedDevices().iterator().next();   // Get first paired device
        //Toast.makeText(this, mBtDevice.getName(), Toast.LENGTH_SHORT).show();

        final BluetoothPrinter mPrinter = new BluetoothPrinter(mBtDevice);
        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {

            @Override
            public void onConnected() {

                try {

                    byte[] pdfarray = getBytes(pdfFile);

                    mPrinter.setAlign(100);
                    mPrinter.addNewLine();

                    mPrinter.printUnicode(pdfarray);
                    mPrinter.addNewLine();

                    /*Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.download);
                    mPrinter.printImage(bm);
                    mPrinter.addNewLine();
                    mPrinter.addNewLine();

                    mPrinter.setBold(true);
                    mPrinter.setAlign(100);
                    mPrinter.printText("TAX INVOICE");
                    mPrinter.addNewLine();
                    mPrinter.addNewLine();

                    mPrinter.setAlign(102);
                    mPrinter.printText("Supplier Code: 1499\n"+
                            "INV No: VS1818-00001\n"+
                            "Delivery date: thu 28/12/2017\n"+
                            "Deliver To:\n"+
                            "Ship To:");

                    mPrinter.setBold(false);
                    mPrinter.addNewLine();
                    mPrinter.printText("Salesman: V18 TS CHECK..\n");*/



                    mPrinter.finish();

                } catch (Exception ex) {
                    Toast.makeText(PrintSummaryActivity.this, mPrinter.getDevice().getName().toString(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailed() {
                Log.d("BluetoothPrinter", "Conection failed");
            }

        });
    }

    public static byte[] getBytes(Object obj) throws java.io.IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte [] data = bos.toByteArray();
        return data;
    }

}
