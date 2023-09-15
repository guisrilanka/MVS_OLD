package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/20/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.pdf.PdfRenderer;
import android.graphics.pdf.PdfRenderer.Page;
import android.os.ParcelFileDescriptor;
import android.os.Build.VERSION;

import com.gui.mdt.thongsieknavclient.rp4.Document;
import com.gui.mdt.thongsieknavclient.rp4.Document.PrintingType;
import com.gui.mdt.thongsieknavclient.rp4.MuPDFCore;
import com.gui.mdt.thongsieknavclient.rp4.MuPDFCore.Cookie;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL.Alignment;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL.IncrementDecrementTypeValue;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL.MeasurementMode;
import com.gui.mdt.thongsieknavclient.rp4.ParametersDPL.Rotation;
import com.gui.mdt.thongsieknavclient.rp4.UPSMessage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DocumentDPL extends Document {
    private int m_RowOffset = 0;
    private int m_ColumnOffset = 0;
    private int m_DotWidthMultiplier = 1;
    private int m_DotHeightMultiplier = 1;
    private int m_HeatSettings = 10;
    private int m_PrintQuantity;
    private int m_LineSpacing = 0;

    private DocumentDPL.Label_Format m_LabelFormat;
    private ByteArrayOutputStream m_DownloadImageStream;
    private List<String> m_ImageNameList;
    private boolean m_enableAdvanceFormatAtrribute;

    public DocumentDPL() {
        this.m_LabelFormat = DocumentDPL.Label_Format.XOR_Mode;
        this.m_DownloadImageStream = new ByteArrayOutputStream();
        this.m_ImageNameList = new ArrayList();
        this.m_enableAdvanceFormatAtrribute = false;
    }

    public boolean getEnableAdvanceFormatAttribute() {
        return this.m_enableAdvanceFormatAtrribute;
    }

    public void setEnableAdvanceFormatAttribute(boolean value) {
        this.m_enableAdvanceFormatAtrribute = value;
    }

    public DocumentDPL.Label_Format getLabelFormat() {
        return this.m_LabelFormat;
    }

    public void setLabelFormat(DocumentDPL.Label_Format value) {
        this.m_LabelFormat = value;
    }

    public int getColumnOffset() {
        return this.m_ColumnOffset;
    }

    public void setColumnOffset(int offset) {
        if (offset >= 0 && offset <= 9999) {
            this.m_ColumnOffset = offset;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'columnOffset' must be from 0 - 9999, a value of %1$s was given.", offset));
        }
    }

    public int getRowOffset() {
        return this.m_RowOffset;
    }

    public void setRowOffset(int value) {
        if (value >= 0 && value <= 9999) {
            this.m_RowOffset = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'rowOffset' must be from 0 - 9999, a value of %1$s was given.", value));
        }
    }

    public int getDotWidthMultiplier() {
        return this.m_DotWidthMultiplier;
    }

    public void setDotWidthMultiplier(int value) {
        if (value >= 1 && value <= 2) {
            this.m_DotWidthMultiplier = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'dot-width-multiplier' must be from 1 to 2, a value of %1$s was given.", value));
        }
    }

    public int getDotHeightMultiplier() {
        return this.m_DotHeightMultiplier;
    }

    public void setDotHeightMultiplier(int value) {
        if (value >= 1 && value <= 3) {
            this.m_DotHeightMultiplier = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'dot-height-multiplier' must be from 1 to 3, a value of %1$s was given.", value));
        }
    }

    public int getHeatSettings() {
        return this.m_HeatSettings;
    }

    public void setHeatSettings(int value) {
        if (value >= 0 && value <= 30) {
            this.m_HeatSettings = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'heat settings' must be from 0 to 30, a value of %1$s was given.", value));
        }
    }

    public int getPrintQuantity() {
        return this.m_PrintQuantity;
    }

    public void setPrintQuantity(int value) {
        if (value >= 1 && value <= 255) {
            this.m_PrintQuantity = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'quantity' must be from 1 to 255, a value of %1$s was given.", value));
        }
    }

    public void clear() {
        super.clear();
        this.m_DownloadImageStream.reset();
    }

    public void writeTextInternalBitmapped(String textString, int fontID, int row, int col) {
        ParametersDPL parameters = new ParametersDPL();
        this.writeTextInternalBitmapped(textString, fontID, row, col, parameters);
    }

    public void writeTextInternalBitmapped(String textString, int fontID, int row, int col, ParametersDPL parameters) {
        parameters.setFont("000");
        if (fontID >= 0 && fontID <= 8) {
            parameters.setTypeID(Integer.toString(fontID));
            this.writeText(textString, row, col, parameters.getTypeID(), parameters);
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'font-id' must be between 0 - 8. Value given was %1$s", fontID));
        }
    }

    public void writeTextInternalSmooth(String textString, int fontSize, int row, int col) {
        ParametersDPL parameters = new ParametersDPL();
        this.writeTextInternalSmooth(textString, fontSize, row, col, parameters);
    }

    public void writeTextInternalSmooth(String textString, int fontSize, int row, int col, ParametersDPL parameters) {
        if (fontSize != 4 && fontSize != 5 && fontSize != 6 && fontSize != 8 && fontSize != 10 && fontSize != 12 && fontSize != 14 && fontSize != 18 && fontSize != 24 && fontSize != 30 && fontSize != 36 && fontSize != 48 && fontSize != 72) {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontSize' must be 4 - 6, 8, 10, 12, 14, 18, 24, 30, 36, 48, or 72. Value given was %d", fontSize));
        } else {
            parameters.setFont(String.format(Locale.US, "A%02d", fontSize));
            this.writeText(textString, row, col, "9", parameters);
        }
    }

    public void writeTextDownloadedBitmapped(String textString, int fontID, int row, int col) {
        ParametersDPL parameters = new ParametersDPL();
        this.writeTextDownloadedBitmapped(textString, fontID, row, col, parameters);
    }

    public void writeTextDownloadedBitmapped(String textString, int fontID, int row, int col, ParametersDPL parameters) {
        if (fontID >= 100 && fontID <= 999) {
            parameters.setFont(String.format(Locale.US, "%03d", fontID));
            this.writeText(textString, row, col, "9", parameters);
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'font-id' must be between 100 - 999. Value given was %1$s", fontID));
        }
    }

    public void writeTextScalable(String textString, String fontID, int row, int col) throws Exception {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setIsDotMode(false);
        parameters.setIsUnicode(false);
        parameters.setFontHeight(8);
        parameters.setFontWidth(8);
        this.writeTextScalable(textString, fontID, row, col, parameters);
    }

    public void writeTextScalable(String textString, String fontID, int row, int col, ParametersDPL parameters) throws Exception {
        if (fontID.length() != 2) {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'font-id' must be 2 characters long. Value given was %1$s", fontID));
        } else {
            char firstFontChar = fontID.charAt(0);
            char secFontChar = fontID.charAt(1);
            if ((firstFontChar < '0' || firstFontChar > '9') && (firstFontChar < 'a' || firstFontChar > 'z')) {
                throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'font-id' must between 00 - zz. Value given was %1$s", fontID));
            } else if ((secFontChar < '0' || secFontChar > '9') && (secFontChar < 'a' || secFontChar > 'z')) {
                throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'font-id' must between 00 - zz. Value given was %1$s", fontID));
            } else {
                parameters.setFont(fontID);
                String tempString;
                if (parameters.getIsUnicode()) {
                    byte[] textBytes = textString.getBytes("UTF-16BE");
                    tempString = this.bytesToHex(textBytes);
                } else {
                    tempString = textString;
                }

                if (parameters.getFontHeight() == 0) {
                    parameters.setFontHeight(8);
                }

                if (parameters.getFontWidth() == 0) {
                    parameters.setFontWidth(8);
                }

                if (parameters.getFontHeight() != 0 && parameters.getFontWidth() != 0) {
                    if (!parameters.getIsDotMode()) {
                        label61: {
                            if (parameters.getFontHeight() >= 4 && parameters.getFontHeight() <= 999) {
                                if (parameters.getFontWidth() >= 4 && parameters.getFontWidth() <= 999) {
                                    textString = "P" + String.format(Locale.US, "%03d", parameters.getFontHeight()) + "P" + String.format(Locale.US, "%03d", parameters.getFontWidth()) + tempString;
                                    break label61;
                                }

                                throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontWidth' must be between 4 - 999 for points unit. Value given was %1$s", parameters.getFontWidth()));
                            }

                            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontHeight' must be between 4 - 999 for points unit. Value given was %1$s", parameters.getFontHeight()));
                        }
                    } else {
                        if (parameters.getFontHeight() < 14 || parameters.getFontHeight() > 4163) {
                            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontHeight' must be between 14 - 4163 for points unit. Value given was %1$s", parameters.getFontHeight()));
                        }

                        if (parameters.getFontWidth() < 16 || parameters.getFontWidth() > 4163) {
                            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontWidth' must be between 16 - 4163 for points unit. Value given was %1$s", parameters.getFontWidth()));
                        }

                        textString = String.format(Locale.US, "%04d", parameters.getFontHeight()) + String.format(Locale.US, "%04d", parameters.getFontWidth()) + tempString;
                    }
                }

                this.writeText(textString, row, col, "9", parameters);
            }
        }
    }

    public void writeText(String textString, int row, int col, String fontIDType, ParametersDPL parameters) {
        this.ValidatePosition(row, col);
        if (textString == null) {
            throw new IllegalArgumentException("Parameter 'textString' was null.");
        } else if (parameters == null) {
            throw new IllegalArgumentException("Parameter 'parameters' was null.");
        } else {
            byte[] tempArray = this.parameterString(fontIDType, parameters, PrintingType.General);
            this.addToDoc(this.m_Document, tempArray);
            this.addToDoc(this.m_Document, String.format(Locale.US, "%04d", row) + String.format(Locale.US, "%04d", col));
            byte[] textData = this.decodeAsExtendedASCII(textString);
            this.addToDoc(this.m_Document, textData);
            this.addToDoc(this.m_Document, EOL);
            if (parameters.getIncrementDecrementType() != IncrementDecrementTypeValue.None && (parameters.getIncrementDecrementValue() != 0 || !parameters.getEmbeddedIncrementDecrementValue().equals(""))) {
                String incrementString = Character.toString((char)parameters.getIncrementDecrementType().getValue());
                if (!parameters.getFillerCharacter().equals("")) {
                    incrementString = incrementString + parameters.getFillerCharacter();
                }

                if (parameters.getEmbeddedEnable()) {
                    if (parameters.getIncrementDecrementType() != IncrementDecrementTypeValue.NumericIncrement && parameters.getIncrementDecrementType() != IncrementDecrementTypeValue.NumericIncrement) {
                        throw new IllegalArgumentException("Increment/decrement type must be numeric for embedded incrementing.");
                    }

                    incrementString = incrementString + parameters.getEmbeddedIncrementDecrementValue();
                } else {
                    incrementString = incrementString + String.format(Locale.US, "%03d", parameters.getIncrementDecrementValue());
                }

                this.addToDoc(this.m_Document, incrementString);
                this.addToDoc(this.m_Document, EOL);
            }

        }
    }

    public void writeBarCode(String barcodeID, String textString, int row, int col) {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setWideBarWidth(0);
        parameters.setNarrowBarWidth(0);
        parameters.setSymbolHeight(0);
        this.writeBarCode(barcodeID, textString, row, col, parameters);
    }

    public void writeBarCodeUPSMaxiCode(int mode, UPSMessage message, int row, int col, ParametersDPL parameters) {
        if (mode != 2 && mode != 3) {
            throw new IllegalArgumentException("Parameter 'mode' must be either 2 or 3. Value given was " + mode);
        } else {
            String textString = "#" + Integer.valueOf(mode).toString() + "[)>" + RS + "01" + GS + "96";
            if (mode == 2) {
                if (message.getZipCode().length() != 5 && message.getZipCode().length() != 9) {
                    throw new RuntimeException("Zip code provided for Mode 2 (US) must be 5 or 9 digits.");
                }

                textString = textString + rightPad(message.getZipCode(), 9, '0') + GS;
                if (message.getCountryCode() != 840) {
                    throw new RuntimeException("Country Code provided for Mode 2 (US) must be 840");
                }

                textString = textString + String.format(Locale.US, "%03d", message.getCountryCode()) + GS;
            } else {
                if (message.getZipCode().length() != 6) {
                    throw new RuntimeException("Zip code provided for Mode 3 (International) must be 6 characters in length.");
                }

                textString = textString + message.getZipCode() + GS;
                if (message.getCountryCode() == 840) {
                    throw new RuntimeException("Country Code provided for Mode 3 (International) cannot be 840");
                }

                textString = textString + String.format(Locale.US, "%03d", message.getCountryCode()) + GS;
            }

            textString = textString + String.format(Locale.US, "%03d", message.getClassOfService()) + GS + message.getTrackingNumber() + GS + message.getSCAC() + GS + message.getShipperNumber() + GS + String.format(Locale.US, "%03d", message.getJulianPickupDay()) + GS + message.getShipmentID() + GS + Integer.valueOf(message.getCurrentPackage()).toString() + "/" + Integer.valueOf(message.getTotalPackage()).toString() + GS + Integer.valueOf(message.getPackageWeight()).toString() + GS + (message.getValidateAddress() ? "Y" : "N") + GS + message.getShipToAddr() + GS + message.getShipToCity() + GS + message.getShipToState() + RS + EOT;
            this.writeBarCode("u", textString, row, col, parameters);
        }
    }

    public void writeBarCodePDF417(String textString, boolean isTruncated, int securityLevel, int aspectRatio, int dataRows, int dataCols, int row, int col, ParametersDPL parameters) {
        if (securityLevel >= 0 && securityLevel <= 8) {
            if (aspectRatio >= 0 && aspectRatio <= 99) {
                if (dataRows >= 0 && dataRows <= 99) {
                    if (dataCols >= 0 && dataCols <= 99) {
                        String newTextString = (isTruncated ? "T" : "F") + Integer.valueOf(securityLevel).toString() + String.format(Locale.US, "%02d", aspectRatio);
                        newTextString = newTextString + (dataRows == 0 ? String.format(Locale.US, "%02d", 0) : (dataRows < 3 ? String.format(Locale.US, "%02d", 3) : (dataRows > 90 ? String.format(Locale.US, "%02d", 90) : String.format(Locale.US, "%02d", dataRows))));
                        newTextString = newTextString + (dataCols == 0 ? String.format(Locale.US, "%02d", 0) : (dataCols > 30 ? String.format(Locale.US, "%02d", 30) : String.format(Locale.US, "%02d", dataCols))) + textString;
                        this.writeBarCode("z", newTextString, row, col, parameters);
                    } else {
                        throw new IllegalArgumentException("Parameter 'dataCols' must be between 0-99. Value given was " + Integer.valueOf(dataCols).toString());
                    }
                } else {
                    throw new IllegalArgumentException("Parameter 'dataRows' must be between 0-99. Value given was " + Integer.valueOf(dataRows).toString());
                }
            } else {
                throw new IllegalArgumentException("Parameter 'aspectRatio' must be between 0-99. Value given was " + Integer.valueOf(aspectRatio).toString());
            }
        } else {
            throw new IllegalArgumentException("Parameter 'securityLevel' must be between 0-8. Value given was " + Integer.valueOf(securityLevel).toString());
        }
    }

    public void writeBarCodeDataMatrix(String textString, int correctionLevel, int digitFormatID, int rowsRequested, int colsRequested, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(0);
        if (correctionLevel >= 0 && correctionLevel <= 200) {
            if (digitFormatID >= 0 && digitFormatID <= 6) {
                if (rowsRequested == 0 || rowsRequested >= 9 && rowsRequested <= 144) {
                    if (colsRequested != 0 && (colsRequested < 9 || colsRequested > 144)) {
                        throw new IllegalArgumentException("Parameter 'colsRequested' must be 0 or between 9-144. Value given was " + Integer.valueOf(colsRequested).toString());
                    } else {
                        this.writeBarCode("W1c", String.format(Locale.US, "%03d", correctionLevel) + (correctionLevel == 200 ? "0" : Integer.valueOf(digitFormatID).toString()) + String.format(Locale.US, "%03d", rowsRequested) + String.format(Locale.US, "%03d", colsRequested) + textString, row, col, parameters);
                    }
                } else {
                    throw new IllegalArgumentException("Parameter 'rowsRequested' must be 0 or between 9-144. Value given was " + Integer.valueOf(rowsRequested).toString());
                }
            } else {
                throw new IllegalArgumentException("Parameter 'digitFormatID' must be between 0-6. Value given was " + Integer.valueOf(digitFormatID).toString());
            }
        } else {
            throw new IllegalArgumentException("Parameter 'correctionLevel' must be between 0-200. Value given was " + Integer.valueOf(correctionLevel).toString());
        }
    }

    public void writeBarCodeQRCode(String textString, boolean autoFormatting, int QRCodeModel, String correctionLevel, String maskNumber, String inputMode, String charMode, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(0);
        if (autoFormatting) {
            this.writeBarCode("W1d", textString + "\r", row, col, parameters);
        } else {
            if (QRCodeModel != 1 && QRCodeModel != 2) {
                throw new IllegalArgumentException("Parameter 'QRCodeModel' must be 1 or 2.Value given was " + Integer.valueOf(QRCodeModel).toString());
            }

            if (!correctionLevel.equals("H") && !correctionLevel.equals("Q") && !correctionLevel.equals("M") && !correctionLevel.equals("L")) {
                throw new IllegalArgumentException("Parameter 'correctionLevel' must be H,Q,M, or L. Value given was " + correctionLevel);
            }

            if (!Document.matches(maskNumber, "^[0-8]{1}$") && !maskNumber.equals("")) {
                throw new IllegalArgumentException("Parameter 'Mask Number' must be between 0-8 or blank. Value given was " + maskNumber);
            }

            if (!inputMode.equals("A") && !inputMode.equals("a") && !inputMode.equals("M") && !inputMode.equals("m")) {
                throw new IllegalArgumentException("Parameter 'inputMode' must be A,a,M, or m. Value given was " + inputMode);
            }

            if (!charMode.equals("N") && !charMode.equals("A") && !charMode.equals("B") && !charMode.equals("K")) {
                throw new IllegalArgumentException("Parameter 'charMode' must be N,A,B, or K. Value given was " + charMode);
            }

            this.writeBarCode("W1D", Integer.valueOf(QRCodeModel).toString() + ',' + correctionLevel + maskNumber + inputMode + ',' + charMode + textString, row, col, parameters);
        }

    }

    public void writeBarCodeAztec(String textString, int textLength, boolean ECIMode, int errorCorrection, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(0);
        if (textLength >= 0 && textLength <= 9999) {
            if (errorCorrection >= 0 && errorCorrection <= 300) {
                String text = String.format(Locale.getDefault(), "%04d", textLength);
                if (ECIMode) {
                    text = text + Integer.valueOf(1).toString();
                } else {
                    text = text + Integer.valueOf(0).toString();
                }

                text = text + String.format(Locale.US, "%03d", errorCorrection);
                text = text + textString;
                this.writeBarCode("W1f", text, row, col, parameters);
            } else {
                throw new IllegalArgumentException("Parameter 'errorCorrection' must be between 0-300. Value given was " + Integer.valueOf(errorCorrection).toString());
            }
        } else {
            throw new IllegalArgumentException("Parameter 'textLength' must be between 0-9999. Value given was " + Integer.valueOf(textLength).toString());
        }
    }

    public void writeBarCodeGS1DataBar(String primaryData, String compositeData, String GS1Type, int pixelMultiplier, int xPixelUndercut, int yPixelUndercut, int segmentsPerRow, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(0);
        if (!GS1Type.equals("E") && !GS1Type.equals("R") && !GS1Type.equals("T") && !GS1Type.equals("S") && !GS1Type.equals("D") && !GS1Type.equals("L")) {
            throw new IllegalArgumentException("Parameter 'GS1Type' must be E,R,T,S,D, or L. Value given was " + GS1Type);
        } else if (pixelMultiplier >= 1 && pixelMultiplier <= 9) {
            if (xPixelUndercut >= 0 && xPixelUndercut <= pixelMultiplier - 1) {
                if (yPixelUndercut >= 0 && yPixelUndercut <= pixelMultiplier - 1) {
                    if (GS1Type.equals("E")) {
                        if (!Document.matches(compositeData, "^[a-zA-Z0-9!%&\\+\\!\\?'\"\\-\\./,:;<=>_ ]*$")) {
                            throw new IllegalArgumentException("Parameter 'primaryData' must contain only alphanumeric characters or certain special characters.  Value given was " + compositeData);
                        }
                    } else {
                        if (GS1Type.equals("L") && primaryData.charAt(0) != '0' && primaryData.charAt(0) != '1') {
                            throw new IllegalArgumentException("Parameter 'primaryData' must be begin with 0 or 1. Value given was " + primaryData);
                        }

                        if (!Document.matches(primaryData, "^[0-9]{13}$")) {
                            throw new IllegalArgumentException("Parameter 'primaryData' must be numeric and 13 digits. Value given was " + primaryData);
                        }
                    }

                    if (compositeData != null && compositeData.length() != 0 && !Document.matches(compositeData, "^[a-zA-Z0-9!%&\\+\\!\\?'\"\\-\\./,:;<=>_ ]*$")) {
                        throw new IllegalArgumentException("Parameter 'compositeData' must be alphanumeric. Value given was " + compositeData);
                    } else if (segmentsPerRow % 2 == 0 && segmentsPerRow >= 0 && segmentsPerRow <= 22) {
                        if (GS1Type.equals("E") && segmentsPerRow < 4 && compositeData != null && compositeData.length() != 0) {
                            throw new IllegalArgumentException("Parameter 'segmentsPerRow' must be at least 4 for GS1 Expanded Barcode containing 2-D composite data. Value given was " + segmentsPerRow);
                        } else {
                            String text = GS1Type + String.format(Locale.US, "%d%d%d", pixelMultiplier, xPixelUndercut, yPixelUndercut);
                            if (segmentsPerRow != 0) {
                                text = text + String.format(Locale.US, "%02d", segmentsPerRow);
                            }

                            text = text + primaryData;
                            if (compositeData != null && compositeData.length() != 0) {
                                text = text + "|" + compositeData;
                            }

                            this.writeBarCode("W1k", text, row, col, parameters);
                        }
                    } else {
                        throw new IllegalArgumentException("Parameter 'segmentsPerRow' must be even and no greater than 22. Value given was " + Integer.valueOf(segmentsPerRow).toString());
                    }
                } else {
                    throw new IllegalArgumentException("Parameter 'yPixelUndercut' must be between 0 and pixelMultiplier value. Value given was " + Integer.valueOf(yPixelUndercut).toString());
                }
            } else {
                throw new IllegalArgumentException("Parameter 'xPixelUndercut' must be between 0 and pixelMultiplier value. Value given was " + Integer.valueOf(xPixelUndercut).toString());
            }
        } else {
            throw new IllegalArgumentException("Parameter 'pixelMultiplier' must be between 1-9. Value given was " + Integer.valueOf(pixelMultiplier).toString());
        }
    }

    public void writeBarCodeAusPost4State(String textString, boolean readableText, int formatControlCode, int DPID, int row, int col, ParametersDPL parameters) {
        parameters.setNarrowBarWidth(0);
        parameters.setWideBarWidth(0);
        parameters.setSymbolHeight(0);
        if (formatControlCode != 11 && formatControlCode != 44 && formatControlCode != 45 && formatControlCode != 59 && formatControlCode != 62 && formatControlCode != 87 && formatControlCode != 92) {
            throw new IllegalArgumentException("Parameter 'formatControlCode' must be 11,44,45,59,62,87,92. Value given was " + Integer.valueOf(formatControlCode).toString());
        } else if (DPID >= 0 && DPID <= 99999999) {
            if (formatControlCode == 59) {
                if (!Document.matches(textString, "^[0-9a-zA-Z]*$")) {
                    throw new IllegalArgumentException("Parameter 'textString' must contains a maximum of 5 alphanumeric characters or 8 numeric digits for BarCode 2.");
                }

                if (Document.matches(textString, "^[0-9]*$")) {
                    if (textString.length() > 8) {
                        throw new IllegalArgumentException("Parameter 'textString' contains numeric digit.Must be a maximum 8 digits for BarCode 2.");
                    }
                } else if (textString.length() > 5) {
                    throw new IllegalArgumentException("Parameter 'textString' contains alphanumeric characters.Must be a maximum 5 alphanumeric characters for BarCode 2.");
                }
            } else if (formatControlCode == 62) {
                if (!Document.matches(textString, "^[0-9a-zA-Z]*$")) {
                    throw new IllegalArgumentException("Parameter 'textString' must contains a maximum of 10 alphanumeric characters or 15 numeric digits for BarCode 3.");
                }

                if (Document.matches(textString, "^[0-9]*$")) {
                    if (textString.length() > 15) {
                        throw new IllegalArgumentException("Parameter 'textString' contains numeric digit.Must be a maximum 15 digits for BarCode 3.");
                    }
                } else if (textString.length() > 10) {
                    throw new IllegalArgumentException("Parameter 'textString' contains alphanumeric characters.Must be a maximum 10 alphanumeric characters for BarCode 3.");
                }
            }

            this.writeBarCode(readableText ? "W1M" : "W1m", Integer.valueOf(formatControlCode).toString() + String.format(Locale.US, "%08d", DPID) + textString, row, col, parameters);
        } else {
            throw new IllegalArgumentException("Parameter 'DPID' must be between 0-99999999. Value given was " + Integer.valueOf(DPID).toString());
        }
    }

    public void writeBarCodeCODABLOCK(String textString, int rowHeight, String codaBlockMode, boolean generateCheckSum, int rowsToEncode, int charPerRow, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(rowHeight);
        if (!codaBlockMode.equals("A") && !codaBlockMode.equals("E") && !codaBlockMode.equals("F")) {
            throw new IllegalArgumentException("Parameter 'codaBlockMode' must be A,E, or F. Value given was " + codaBlockMode);
        } else if (rowsToEncode >= 1 && rowsToEncode <= 44) {
            if (charPerRow >= 2 && charPerRow <= 62) {
                this.writeBarCode("W1q", codaBlockMode + (generateCheckSum ? Integer.valueOf(1).toString() : Integer.valueOf(0).toString()) + String.format(Locale.US, "%02d", rowsToEncode) + String.format(Locale.US, "%02d", charPerRow) + textString, row, col, parameters);
            } else {
                throw new IllegalArgumentException("Parameter 'charPerRow' must be between 2-62. Value given was " + Integer.valueOf(charPerRow).toString());
            }
        } else {
            throw new IllegalArgumentException("Parameter 'rowsToEncode' must be between 1-44. Value given was " + Integer.valueOf(rowsToEncode).toString());
        }
    }

    public void writeBarCodeTLC39(String serialNumber, int barCodeHeight, int ECINumber, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(barCodeHeight);
        if (ECINumber >= 0 && ECINumber <= 999999) {
            if (Document.matches(serialNumber, "^[a-zA-Z0-9]*$") && serialNumber.length() <= 25) {
                this.writeBarCode("W1t", Integer.valueOf(ECINumber).toString() + ';' + serialNumber, row, col, parameters);
            } else {
                throw new IllegalArgumentException("Parameter 'serialNumber' must be 0-25 alphanumeric characters. Value given was " + serialNumber);
            }
        } else {
            throw new IllegalArgumentException("Parameter 'ECINumber' must be between 0-999999. Value given was " + ECINumber);
        }
    }

    public void writeBarCodeMicroPDF417(String textString, int dataCols, int rowIndex, boolean byteCompactMode, boolean macroCharSub, int row, int col, ParametersDPL parameters) {
        parameters.setSymbolHeight(0);
        if (rowIndex >= 0 && rowIndex <= 10) {
            if (dataCols >= 1 && dataCols <= 4) {
                String newTextString = Integer.valueOf(dataCols).toString() + (rowIndex == 10 ? "A" : Integer.valueOf(rowIndex).toString()) + (byteCompactMode ? Integer.valueOf(1).toString() : Integer.valueOf(0).toString()) + (macroCharSub ? Integer.valueOf(1).toString() : Integer.valueOf(0).toString()) + '0' + textString;
                this.writeBarCode("W1z", newTextString, row, col, parameters);
            } else {
                throw new IllegalArgumentException("Parameter 'dataCols' must be between 1-4. Value given was " + Integer.valueOf(dataCols).toString());
            }
        } else {
            throw new IllegalArgumentException("Parameter 'rowIndex' must be between 0-10. Value given was " + Integer.valueOf(rowIndex).toString());
        }
    }

    public void writeBarCode(String barcodeID, String textString, int row, int col, ParametersDPL parameters) {
        this.ValidatePosition(row, col);
        if (barcodeID != null && (barcodeID.length() == 0 || barcodeID.length() <= 3)) {
            if (textString == null) {
                throw new IllegalArgumentException("Parameter 'textString' was null.");
            } else if (parameters == null) {
                throw new IllegalArgumentException("Parameter 'parameters' was null.");
            } else {
                byte[] tempArray = this.parameterString(barcodeID, parameters, PrintingType.Barcode);
                this.addToDoc(this.m_Document, tempArray);
                this.addToDoc(this.m_Document, String.format(Locale.US, "%04d", row) + String.format(Locale.US, "%04d", col));
                this.addToDoc(this.m_Document, textString);
                this.addToDoc(this.m_Document, EOL);
                if (parameters.getIncrementDecrementType() != IncrementDecrementTypeValue.None && (parameters.getIncrementDecrementValue() != 0 || !parameters.getEmbeddedIncrementDecrementValue().equals(""))) {
                    String incrementString = Character.toString((char)parameters.getIncrementDecrementType().getValue());
                    if (!parameters.getFillerCharacter().equals("")) {
                        incrementString = incrementString + parameters.getFillerCharacter();
                    }

                    if (parameters.getEmbeddedEnable()) {
                        if (parameters.getIncrementDecrementType() != IncrementDecrementTypeValue.NumericIncrement && parameters.getIncrementDecrementType() != IncrementDecrementTypeValue.NumericIncrement) {
                            throw new IllegalArgumentException("Increment/decrement type must be numeric for embedded incrementing.");
                        }

                        incrementString = incrementString + parameters.getEmbeddedIncrementDecrementValue();
                    } else {
                        incrementString = incrementString + String.format(Locale.US, "%03d", parameters.getIncrementDecrementValue());
                    }

                    this.addToDoc(this.m_Document, incrementString);
                    this.addToDoc(this.m_Document, EOL);
                }

            }
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'barcodeID' must be at least %1$s characters in length.", 3));
        }
    }

    public void writeImageStored(String imageName, int row, int col, ParametersDPL parameters) {
        parameters.setRotate(Rotation.Rotate_0);
        parameters.setFont("000");
        byte[] tempArray = this.parameterString("Y", parameters, PrintingType.Image);
        this.addToDoc(this.m_Document, tempArray);
        this.addToDoc(this.m_Document, String.format(Locale.US, "%04d", row) + String.format(Locale.US, "%04d", col));
        this.addToDoc(this.m_Document, imageName);
        this.addToDoc(this.m_Document, EOL);
    }

    public void writeImage(String imagePath, DocumentDPL.ImageType imageType, int row, int col, ParametersDPL parameters) throws Exception {
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        if (imageBitmap == null) {
            File imageFile = new File(imagePath);
            byte[] bitmapData = new byte[(int)imageFile.length()];
            InputStream inputStream = new BufferedInputStream(new FileInputStream(imageFile));
            if (inputStream.read(bitmapData) == 0) {
                inputStream.close();
                throw new Exception("Unable to read image file or file was empty.");
            }

            inputStream.close();
            this.writeImage(bitmapData, imageType, row, col, parameters);
        } else {
            this.writeImage(imageBitmap, row, col, parameters);
        }

    }

    public void writeImage(Bitmap imageObject, int row, int col, ParametersDPL parameters) throws Exception {
        this.writeImage(imageObject, 128, row, col, parameters);
    }

    public void writeImage(Bitmap imageObject, int darknessThreshold, int row, int col, ParametersDPL parameters) throws Exception {
        if (imageObject == null) {
            throw new IllegalArgumentException("Parameter 'imageObject' was null.");
        } else if (parameters == null) {
            throw new IllegalArgumentException("Parameter 'parameters' was null.");
        } else if (darknessThreshold >= 0 && darknessThreshold <= 255) {
            DocumentDPL.ImageType imageType = DocumentDPL.ImageType.BMPFlipped_8Bit;
            byte[] bitmapData = this.convertTo1BPP(imageObject, darknessThreshold);
            this.writeImage(bitmapData, imageType, row, col, parameters);
        } else {
            throw new IllegalArgumentException("Parameter 'darknessThreshold' was invalid.  Value must be between 0 - 255.");
        }
    }

    public void writeImage(byte[] imageData, DocumentDPL.ImageType imageType, int row, int col, ParametersDPL parameters) throws Exception {
        this.ValidatePosition(row, col);
        if (imageData == null) {
            throw new IllegalArgumentException("Parameter 'imageData' was null.");
        } else if (parameters == null) {
            throw new IllegalArgumentException("Parameter 'parameters' was null.");
        } else {
            parameters.setRotate(Rotation.Rotate_0);
            parameters.setFont("000");
            this.addToDoc(this.m_DownloadImageStream, "\u0002XD\r\n");
            String imageName = (new SimpleDateFormat("yyyyMMddhhmmssSS", Locale.US)).format(new Date()).substring(0, 16);
            this.m_ImageNameList.add(imageName + ".DIM");
            this.addToDoc(this.m_DownloadImageStream, this.getInputImageDataCommand(imageName, imageType));
            this.addToDoc(this.m_DownloadImageStream, imageData);
            this.addToDoc(this.m_DownloadImageStream, EOL);
            byte[] tempArray = this.parameterString("Y", parameters, PrintingType.Image);
            this.addToDoc(this.m_Document, tempArray);
            this.addToDoc(this.m_Document, String.format(Locale.US, "%04d", row) + String.format(Locale.US, "%04d", col));
            this.addToDoc(this.m_Document, imageName);
            this.addToDoc(this.m_Document, EOL);
        }
    }



    public void writeLine(int row, int col, int height, int width) {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setFillPattern(0);
        parameters.setRotate(Rotation.Rotate_0);
        parameters.setHorizontalMultiplier(1);
        parameters.setVerticalMultiplier(1);
        if (width >= 0 && width <= 9999) {
            if (height >= 0 && height <= 9999) {
                String lineData = "l" + String.format(Locale.US, "%04d", width) + String.format(Locale.US, "%04d", height);
                this.writeGraphics(lineData, row, col, parameters);
            } else {
                throw new IllegalArgumentException("Parameter 'height' must be between 0 and 9999.");
            }
        } else {
            throw new IllegalArgumentException("Parameter 'width' must be between 0 and 9999.");
        }
    }

    public void writeBox(int row, int col, int height, int width, int topBottomThickness, int sideThickness) {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setFillPattern(0);
        parameters.setRotate(Rotation.Rotate_0);
        parameters.setHorizontalMultiplier(1);
        parameters.setVerticalMultiplier(1);
        if (width >= 0 && width <= 9999) {
            if (height >= 0 && height <= 9999) {
                if (topBottomThickness >= 0 && topBottomThickness <= 9999) {
                    if (sideThickness >= 0 && sideThickness <= 9999) {
                        String boxData = "b" + String.format(Locale.US, "%04d", width) + String.format(Locale.US, "%04d", height) + String.format(Locale.US, "%04d", topBottomThickness) + String.format(Locale.US, "%04d", sideThickness);
                        this.writeGraphics(boxData, row, col, parameters);
                    } else {
                        throw new IllegalArgumentException("Parameter 'sideThickness' must be between 0 and 9999.");
                    }
                } else {
                    throw new IllegalArgumentException("Parameter 'topBottomThickness' must be between 0 and 9999.");
                }
            } else {
                throw new IllegalArgumentException("Parameter 'height' must be between 0 and 9999.");
            }
        } else {
            throw new IllegalArgumentException("Parameter 'width' must be between 0 and 9999.");
        }
    }

    public void writeTriangle(int fillPattern, int rowPt1, int colPt1, int rowPt2, int colPt2, int rowPt3, int colPt3) {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setFillPattern(fillPattern);
        parameters.setRotate(Rotation.Rotate_0);
        parameters.setHorizontalMultiplier(1);
        parameters.setVerticalMultiplier(1);
        if (rowPt2 >= 0 && rowPt2 <= 9999) {
            if (colPt2 >= 0 && colPt2 <= 9999) {
                if (rowPt3 >= 0 && rowPt3 <= 9999) {
                    if (colPt3 >= 0 && colPt3 <= 9999) {
                        String TriangleData = "P0010001" + String.format(Locale.US, "%04d", rowPt2) + String.format(Locale.US, "%04d", colPt2) + String.format(Locale.US, "%04d", rowPt3) + String.format(Locale.US, "%04d", colPt3);
                        this.writeGraphics(TriangleData, rowPt1, colPt1, parameters);
                    } else {
                        throw new IllegalArgumentException("Parameter 'colPt3' must be between 0 and 9999.");
                    }
                } else {
                    throw new IllegalArgumentException("Parameter 'rowPt3' must be between 0 and 9999.");
                }
            } else {
                throw new IllegalArgumentException("Parameter 'colPt2' must be between 0 and 9999.");
            }
        } else {
            throw new IllegalArgumentException("Parameter 'rowPt2' must be between 0 and 9999.");
        }
    }

    public void writeRectangle(int fillPattern, int rowPt1, int colPt1, int rowPt2, int colPt2, int rowPt3, int colPt3, int rowPt4, int colPt4) {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setFillPattern(fillPattern);
        parameters.setRotate(Rotation.Rotate_0);
        parameters.setHorizontalMultiplier(1);
        parameters.setVerticalMultiplier(1);
        if (rowPt2 >= 0 && rowPt2 <= 9999) {
            if (colPt2 >= 0 && colPt2 <= 9999) {
                if (rowPt3 >= 0 && rowPt3 <= 9999) {
                    if (colPt3 >= 0 && colPt3 <= 9999) {
                        if (rowPt4 >= 0 && rowPt4 <= 9999) {
                            if (colPt4 >= 0 && colPt4 <= 9999) {
                                String rectData = "P0010001" + String.format(Locale.US, "%04d", rowPt2) + String.format(Locale.US, "%04d", colPt2) + String.format(Locale.US, "%04d", rowPt3) + String.format(Locale.US, "%04d", colPt3) + String.format(Locale.US, "%04d", rowPt4) + String.format(Locale.US, "%04d", colPt4);
                                this.writeGraphics(rectData, rowPt1, colPt1, parameters);
                            } else {
                                throw new IllegalArgumentException("Parameter 'colPt4' must be between 0 and 9999.");
                            }
                        } else {
                            throw new IllegalArgumentException("Parameter 'rowPt4' must be between 0 and 9999.");
                        }
                    } else {
                        throw new IllegalArgumentException("Parameter 'colPt3' must be between 0 and 9999.");
                    }
                } else {
                    throw new IllegalArgumentException("Parameter 'rowPt3' must be between 0 and 9999.");
                }
            } else {
                throw new IllegalArgumentException("Parameter 'colPt2' must be between 0 and 9999.");
            }
        } else {
            throw new IllegalArgumentException("Parameter 'rowPt2' must be between 0 and 9999.");
        }
    }

    public void writeCircle(int fillPattern, int centerRow, int centerCol, int radius) {
        ParametersDPL parameters = new ParametersDPL();
        parameters.setFillPattern(fillPattern);
        parameters.setRotate(Rotation.Rotate_0);
        parameters.setHorizontalMultiplier(1);
        parameters.setVerticalMultiplier(1);
        if (radius >= 0 && radius <= 9999) {
            String circleData = "C0010001" + String.format(Locale.US, "%04d", radius);
            this.writeGraphics(circleData, centerRow, centerCol, parameters);
        } else {
            throw new IllegalArgumentException("Parameter 'radius' must be between 0 and 9999.");
        }
    }

    public void writeGraphics(String graphicData, int row, int col, ParametersDPL parameters) {
        this.ValidatePosition(row, col);
        if (parameters == null) {
            throw new IllegalArgumentException("Parameter 'parameters' was null.");
        } else if (graphicData == null) {
            throw new IllegalArgumentException("Parameter 'graphicData' was null");
        } else {
            byte[] tempArray = this.parameterString("X", parameters, PrintingType.Graphics);
            this.addToDoc(this.m_Document, tempArray);
            this.addToDoc(this.m_Document, String.format(Locale.US, "%04d", row) + String.format(Locale.US, "%04d", col));
            this.addToDoc(this.m_Document, graphicData);
            this.addToDoc(this.m_Document, EOL);
        }
    }

    private void ValidatePosition(int row, int col) {
        if (row >= 0 && row <= 9999) {
            if (col < 0 || col > 9999) {
                throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'col' must be an integer from 0 to 9999, a value of %1$s was given.", col));
            }
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'row' must be an integer from 0 to 9999, a value of %1$s was given.", row));
        }
    }

    private byte[] parameterString(String typeID, ParametersDPL parameters, PrintingType printingType) {
        ByteArrayOutputStream mStream = new ByteArrayOutputStream(128);
        if (parameters.getAlignment() == Alignment.Center) {
            this.addToDoc(mStream, "JC\r\n");
        } else if (parameters.getAlignment() == Alignment.Right) {
            this.addToDoc(mStream, "JR\r\n");
        }

        if (parameters.getIsMirrored()) {
            this.addToDoc(mStream, "M\r\n");
        }

        if (parameters.getMeasurement() == MeasurementMode.Metric) {
            this.addToDoc(mStream, "m\r\n");
        }

        String encodingValue;
        if (parameters.getIsUnicode()) {
            if (printingType == PrintingType.General || printingType == PrintingType.Barcode) {
                encodingValue = parameters.SymbolSetToString(parameters.getDBSymbolSet());
                this.addToDoc(mStream, "yU" + encodingValue + "\r\n");
            }
        } else if (printingType == PrintingType.General || printingType == PrintingType.Barcode) {
            encodingValue = parameters.SymbolSetToString(parameters.getSBSymbolSet());
            this.addToDoc(mStream, "yS" + encodingValue + "\r\n");
        }

        if (this.getEnableAdvanceFormatAttribute()) {
            String textAttribute = String.format(Locale.US, "F%s%s%s\r\n", parameters.getIsBold() ? "B+" : "B-", parameters.getIsItalic() ? "I+" : "I-", parameters.getIsUnderline() ? "U+" : "U-");
            this.addToDoc(mStream, textAttribute);
        }

        this.addToDoc(mStream, Integer.toString(parameters.getRotate().getValue()));
        this.addToDoc(mStream, typeID);
        String tempArray;
        int remappedValue;
        int value;
        if (printingType == PrintingType.Barcode) {
            value = parameters.getWideBarWidth();
            remappedValue = this.valueRemap(value);
            tempArray = remappedValue < 10 ? Integer.toString(remappedValue) : String.format(Locale.US, "%c", remappedValue);
            this.addToDoc(mStream, tempArray);
            value = parameters.getNarrowBarWidth();
            remappedValue = this.valueRemap(value);
            tempArray = remappedValue < 10 ? Integer.toString(remappedValue) : String.format(Locale.US, "%c", remappedValue);
            this.addToDoc(mStream, tempArray);
            this.addToDoc(mStream, String.format(Locale.US, "%03d", parameters.getSymbolHeight()));
        } else if (printingType == PrintingType.Graphics) {
            value = parameters.getHorizontalMultiplier();
            remappedValue = this.valueRemap(value);
            tempArray = remappedValue < 10 ? Integer.toString(remappedValue) : String.format(Locale.US, "%c", remappedValue);
            this.addToDoc(mStream, tempArray);
            value = parameters.getVerticalMultiplier();
            remappedValue = this.valueRemap(value);
            tempArray = remappedValue < 10 ? Integer.toString(remappedValue) : String.format(Locale.US, "%c", remappedValue);
            this.addToDoc(mStream, tempArray);
            this.addToDoc(mStream, String.format(Locale.US, "%03d", parameters.getFillPattern()));
        } else {
            value = parameters.getHorizontalMultiplier();
            remappedValue = this.valueRemap(value);
            tempArray = remappedValue < 10 ? Integer.toString(remappedValue) : String.format(Locale.US, "%c", remappedValue);
            this.addToDoc(mStream, tempArray);
            value = parameters.getVerticalMultiplier();
            remappedValue = this.valueRemap(value);
            tempArray = remappedValue < 10 ? Integer.toString(remappedValue) : String.format(Locale.US, "%c", remappedValue);
            this.addToDoc(mStream, tempArray);
            String font = parameters.getFont();
            if (font.length() == 3) {
                tempArray = parameters.getFont();
                this.addToDoc(mStream, tempArray);
            } else if (font.length() == 2) {
                if (parameters.getIsUnicode()) {
                    tempArray = "u" + font;
                    this.addToDoc(mStream, tempArray);
                } else {
                    tempArray = "S" + font;
                    this.addToDoc(mStream, tempArray);
                }
            }
        }

        return mStream.toByteArray();
    }

    private int valueRemap(int value) {
        if (value > 0 && value < 10) {
            return value;
        } else if (value >= 10 && value < 36) {
            return value + 55;
        } else {
            return value >= 36 && value < 62 ? value + 61 : value;
        }
    }

    public byte[] getDocumentData() {
        ByteArrayOutputStream mStream = new ByteArrayOutputStream();
        if (this.m_DownloadImageStream != null && this.m_DownloadImageStream.size() > 0) {
            this.addToDoc(mStream, this.m_DownloadImageStream.toByteArray());
        }

        this.addToDoc(mStream, "\u0002L\r\n");
        this.addToDoc(mStream, "A" + this.getLabelFormat().getValue() + "\r\n");
        if (this.getColumnOffset() > 0) {
            this.addToDoc(mStream, "C" + String.format(Locale.US, "%04d", this.getColumnOffset()) + "\r\n");
        }

        if (this.getRowOffset() > 0) {
            this.addToDoc(mStream, "R" + String.format(Locale.US, "%04d", this.getRowOffset()) + "\r\n");
        }

        this.addToDoc(mStream, "D" + Integer.toString(this.getDotWidthMultiplier()) + Integer.toString(this.getDotHeightMultiplier()) + "\r\n");
        if (this.getHeatSettings() != 10) {
            this.addToDoc(mStream, "H" + String.format(Locale.US, "%02d", this.getHeatSettings()) + "\r\n");
        }

        if (this.getEnableAdvanceFormatAttribute()) {
            this.addToDoc(mStream, "FA+\r\n");
        }

        this.addToDoc(mStream, this.m_Document.toByteArray());
        if (this.getEnableAdvanceFormatAttribute()) {
            this.addToDoc(mStream, "FB-U-I-\r\n");
        }

        if (this.getPrintQuantity() > 1) {
            this.addToDoc(mStream, "Q" + String.format(Locale.US, "%04d", this.getPrintQuantity()) + "\r\n");
        }

        this.addToDoc(mStream, "E\r\n");
        if (this.m_DownloadImageStream != null && this.m_DownloadImageStream.size() > 0) {
            Iterator var2 = this.m_ImageNameList.iterator();

            while(var2.hasNext()) {
                String imageName = (String)var2.next();
                this.addToDoc(mStream, "\u0002~EF:/rdsk/D/" + imageName + ";\r");
            }

            this.m_ImageNameList.clear();
        }

        return mStream.toByteArray();
    }

    /** @deprecated */
    @Deprecated
    public byte[] getDocumentImageData() {
        ByteArrayOutputStream mStream = new ByteArrayOutputStream();
        if (this.m_DownloadImageStream != null && this.m_DownloadImageStream.size() > 0) {
            this.addToDoc(mStream, this.m_DownloadImageStream.toByteArray());
        }

        this.addToDoc(mStream, "\u0002L\r\n");
        this.addToDoc(mStream, "A" + this.getLabelFormat().getValue() + "\r\n");
        if (this.getColumnOffset() > 0) {
            this.addToDoc(mStream, "C" + String.format(Locale.US, "%04d", this.getColumnOffset()) + "\r\n");
        }

        if (this.getRowOffset() > 0) {
            this.addToDoc(mStream, "R" + String.format(Locale.US, "%04d", this.getRowOffset()) + "\r\n");
        }

        this.addToDoc(mStream, "D" + Integer.toString(this.getDotWidthMultiplier()) + Integer.toString(this.getDotHeightMultiplier()) + "\r\n");
        if (this.getHeatSettings() != 10) {
            this.addToDoc(mStream, "H" + String.format(Locale.US, "%02d", this.getHeatSettings()) + "\r\n");
        }

        this.addToDoc(mStream, this.m_Document.toByteArray());
        if (this.getPrintQuantity() > 1) {
            this.addToDoc(mStream, "Q" + String.format(Locale.US, "%04d", this.getPrintQuantity()) + "\r\n");
        }

        this.addToDoc(mStream, "E\r\n");
        if (this.m_DownloadImageStream != null && this.m_DownloadImageStream.size() > 0) {
            Iterator var2 = this.m_ImageNameList.iterator();

            while(var2.hasNext()) {
                String imageName = (String)var2.next();
                this.addToDoc(mStream, "\u0002~EF:/rdsk/D/" + imageName + ";\r");
            }

            this.m_ImageNameList.clear();
        }

        return mStream.toByteArray();
    }

    public String getInputImageDataCommand(String imageName, DocumentDPL.ImageType imageType) throws Exception {
        String inputImageDataCommand = "\u0002ID";
        if (imageType == DocumentDPL.ImageType.DOImage_7Bit) {
            inputImageDataCommand = inputImageDataCommand + "A";
            inputImageDataCommand = inputImageDataCommand + "F";
        } else {
            switch(imageType) {
                case BMPFlipped_8Bit:
                    inputImageDataCommand = inputImageDataCommand + "B";
                    break;
                case BMP_8Bit:
                    inputImageDataCommand = inputImageDataCommand + "b";
                    break;
                case IMGFlipped_8Bit:
                    inputImageDataCommand = inputImageDataCommand + "I";
                    break;
                case IMG_8Bit:
                    inputImageDataCommand = inputImageDataCommand + "i";
                    break;
                case PCXFlipped_8Bit:
                    inputImageDataCommand = inputImageDataCommand + "P";
                    break;
                case PCX_8Bit:
                    inputImageDataCommand = inputImageDataCommand + "p";
                    break;
                default:
                    inputImageDataCommand = inputImageDataCommand + "B";
            }

            if (imageName.length() == 0 || imageName.length() > 16) {
                throw new Exception("Image name specified must be at most 16 characters. Image name is " + imageName);
            }

            inputImageDataCommand = inputImageDataCommand + imageName + "\r";
        }

        return inputImageDataCommand;
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int pads = size - str.length();
            if (pads <= 0) {
                return str;
            } else {
                return pads > 8192 ? rightPad(str, size, String.valueOf(padChar)) : str.concat(padding(pads, padChar));
            }
        }
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        } else {
            if (padStr.equals("")) {
                padStr = " ";
            }

            int padLen = padStr.length();
            int strLen = str.length();
            int pads = size - strLen;
            if (pads <= 0) {
                return str;
            } else if (padLen == 1 && pads <= 8192) {
                return rightPad(str, size, padStr.charAt(0));
            } else if (pads == padLen) {
                return str.concat(padStr);
            } else if (pads < padLen) {
                return str.concat(padStr.substring(0, pads));
            } else {
                char[] padding = new char[pads];
                char[] padChars = padStr.toCharArray();

                for(int i = 0; i < pads; ++i) {
                    padding[i] = padChars[i % padLen];
                }

                return str.concat(new String(padding));
            }
        }
    }

    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        } else {
            char[] buf = new char[repeat];

            for(int i = 0; i < buf.length; ++i) {
                buf[i] = padChar;
            }

            return new String(buf);
        }
    }

    private byte[] convertTo1BPP(Bitmap inputBitmap, int darknessThreshold) {
        int width = inputBitmap.getWidth();
        int height = inputBitmap.getHeight();
        ByteArrayOutputStream mImageStream = new ByteArrayOutputStream();
        int BITMAPFILEHEADER_SIZE = 14;
        int BITMAPINFOHEADER_SIZE = 40;
        short biPlanes = 1;
        short biBitCount = 1;
        int biCompression = 0;
        int biSizeImage = (width * biBitCount + 31 & -32) / 8 * height;
        int biXPelsPerMeter = 0;
        int biYPelsPerMeter = 0;
        int biClrUsed = 2;
        int biClrImportant = 2;
        byte[] bfType = new byte[]{66, 77};
        short bfReserved1 = 0;
        short bfReserved2 = 0;
        int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE + 8;
        int bfSize = bfOffBits + biSizeImage;
        byte[] colorPalette = new byte[]{0, 0, 0, -1, -1, -1, -1, -1};
        int monoBitmapStride = (width + 31 & -32) / 8;
        byte[] newBitmapData = new byte[biSizeImage];

        try {
            mImageStream.write(bfType);
            mImageStream.write(this.intToDWord(bfSize));
            mImageStream.write(this.intToWord(bfReserved1));
            mImageStream.write(this.intToWord(bfReserved2));
            mImageStream.write(this.intToDWord(bfOffBits));
            mImageStream.write(this.intToDWord(BITMAPINFOHEADER_SIZE));
            mImageStream.write(this.intToDWord(width));
            mImageStream.write(this.intToDWord(height));
            mImageStream.write(this.intToWord(biPlanes));
            mImageStream.write(this.intToWord(biBitCount));
            mImageStream.write(this.intToDWord(biCompression));
            mImageStream.write(this.intToDWord(biSizeImage));
            mImageStream.write(this.intToDWord(biXPelsPerMeter));
            mImageStream.write(this.intToDWord(biYPelsPerMeter));
            mImageStream.write(this.intToDWord(biClrUsed));
            mImageStream.write(this.intToDWord(biClrImportant));
            mImageStream.write(colorPalette);
            int[] imageData = new int[height * width];
            inputBitmap.getPixels(imageData, 0, width, 0, 0, width, height);

            for(int y = 0; y < height; ++y) {
                for(int x = 0; x < width; ++x) {
                    int pixelIndex = y * width + x;
                    int mask = 128 >> (x & 7);
                    int pixel = imageData[pixelIndex];
                    int R = Color.red(pixel);
                    int G = Color.green(pixel);
                    int B = Color.blue(pixel);
                    int A = Color.alpha(pixel);
                    boolean set = A < darknessThreshold || R + G + B > darknessThreshold * 3;
                    if (set) {
                        int index = (height - y - 1) * monoBitmapStride + (x >>> 3);
                        newBitmapData[index] = (byte)(newBitmapData[index] | mask);
                    }
                }
            }

            mImageStream.write(newBitmapData);
        } catch (Exception var36) {
            var36.printStackTrace();
        }

        return mImageStream.toByteArray();
    }

    private byte[] intToWord(int parValue) {
        byte[] retValue = new byte[]{(byte)(parValue & 255), (byte)(parValue >> 8 & 255)};
        return retValue;
    }

    private byte[] intToDWord(int parValue) {
        byte[] retValue = new byte[]{(byte)(parValue & 255), (byte)(parValue >> 8 & 255), (byte)(parValue >> 16 & 255), (byte)(parValue >> 24 & 255)};
        return retValue;
    }

    private String bytesToHex(byte[] in) {
        StringBuilder builder = new StringBuilder();
        byte[] var3 = in;
        int var4 = in.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            builder.append(String.format(Locale.US, "%02x", b));
        }

        return builder.toString().toUpperCase(Locale.US);
    }

    public static enum ImageType {
        DOImage_7Bit,
        BMPFlipped_8Bit,
        BMP_8Bit,
        IMG_8Bit,
        IMGFlipped_8Bit,
        PCXFlipped_8Bit,
        PCX_8Bit,
        Other;

        private ImageType() {
        }
    }

    public static enum Label_Format {
        XOR_Mode(1),
        Transparent_Mode(2),
        Opaque_Mode(3),
        Inverse_Mode(5);

        private int value;

        private Label_Format(int intValue) {
            this.value = intValue;
        }

        public int getValue() {
            return this.value;
        }
    }



}
