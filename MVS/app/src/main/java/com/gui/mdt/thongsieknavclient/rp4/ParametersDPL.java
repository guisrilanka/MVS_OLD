package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/20/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.Locale;

import com.gui.mdt.thongsieknavclient.rp4.Document;
import com.gui.mdt.thongsieknavclient.rp4.Parameters;

public class ParametersDPL extends Parameters {
    private ParametersDPL.SingleByteSymbolSet m_SBSymbolSet;
    private ParametersDPL.DoubleByteSymbolSet m_DBSymbolSet;
    private boolean m_isDotMode;
    private String m_typeID;
    private int m_FontHeight;
    private int m_FontWidth;
    private boolean m_isUnicode;
    private ParametersDPL.Rotation m_Rotation;
    private ParametersDPL.Alignment m_Alignment;
    private boolean m_IsMirrored;
    private ParametersDPL.MeasurementMode m_Measurement;
    private int m_symbolHeight;
    private int m_fillPattern;
    private int m_wideBarWidth;
    private int m_narrowBarWidth;
    private int m_incrementDecrementValue;
    private ParametersDPL.IncrementDecrementTypeValue m_incrementDecrementType;
    private String m_fillerCharacter;
    private Boolean m_embeddedEnable;
    private String m_embeddedIncrementDecrementValue;
    private boolean m_isBold;
    private boolean m_isItalic;
    private boolean m_isUnderline;

    public ParametersDPL() {
        this.m_SBSymbolSet = ParametersDPL.SingleByteSymbolSet.PC850_Multi;
        this.m_DBSymbolSet = ParametersDPL.DoubleByteSymbolSet.GovernmentBureau;
        this.m_isDotMode = false;
        this.m_typeID = "0";
        this.m_FontHeight = 0;
        this.m_FontWidth = 0;
        this.m_isUnicode = false;
        this.m_Rotation = ParametersDPL.Rotation.Rotate_0;
        this.m_Alignment = ParametersDPL.Alignment.Left;
        this.m_IsMirrored = false;
        this.m_Measurement = ParametersDPL.MeasurementMode.Inch;
        this.m_symbolHeight = 0;
        this.m_fillPattern = 0;
        this.m_wideBarWidth = 0;
        this.m_narrowBarWidth = 0;
        this.m_incrementDecrementValue = 0;
        this.m_incrementDecrementType = ParametersDPL.IncrementDecrementTypeValue.None;
        this.m_fillerCharacter = "";
        this.m_embeddedEnable = false;
        this.m_embeddedIncrementDecrementValue = "";
        this.m_isBold = false;
        this.m_isItalic = false;
        this.m_isUnderline = false;
    }

    public boolean getIsBold() {
        return this.m_isBold;
    }

    public void setIsBold(boolean value) {
        this.m_isBold = value;
    }

    public boolean getIsItalic() {
        return this.m_isItalic;
    }

    public void setIsItalic(boolean value) {
        this.m_isItalic = value;
    }

    public boolean getIsUnderline() {
        return this.m_isUnderline;
    }

    public void setIsUnderline(boolean value) {
        this.m_isUnderline = value;
    }

    public ParametersDPL.Rotation getRotate() {
        return this.m_Rotation;
    }

    public ParametersDPL.Alignment getAlignment() {
        return this.m_Alignment;
    }

    public int getWideBarWidth() {
        return this.m_wideBarWidth;
    }

    public void setWideBarWidth(int value) {
        if (value >= 0 && value <= 61) {
            this.m_wideBarWidth = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'wideBarWidth' must be in the range of %1$s to %2$s, a value of %3$s was given.", 0, 61, value));
        }
    }

    public int getNarrowBarWidth() {
        return this.m_narrowBarWidth;
    }

    public void setNarrowBarWidth(int value) {
        if (value >= 0 && value <= 61) {
            this.m_narrowBarWidth = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'narrowBarWidth' must be in the range of %1$s to %2$s, a value of %3$s was given.", 0, 61, value));
        }
    }

    public boolean getIsUnicode() {
        return this.m_isUnicode;
    }

    public void setIsUnicode(boolean value) {
        this.m_isUnicode = value;
    }

    public String getTypeID() {
        return this.m_typeID;
    }

    public void setTypeID(String value) {
        if (value.length() != 1) {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'typeID' must be between 1 character in length. Value given was %1$s characters long.", value.length()));
        } else {
            this.m_typeID = value;
        }
    }

    public ParametersDPL.SingleByteSymbolSet getSBSymbolSet() {
        return this.m_SBSymbolSet;
    }

    public void setSBSymbolSet(ParametersDPL.SingleByteSymbolSet value) {
        this.m_SBSymbolSet = value;
    }

    public ParametersDPL.DoubleByteSymbolSet getDBSymbolSet() {
        return this.m_DBSymbolSet;
    }

    public void setDBSymbolSet(ParametersDPL.DoubleByteSymbolSet value) {
        this.m_DBSymbolSet = value;
    }

    public String SymbolSetToString(ParametersDPL.SingleByteSymbolSet symbolSet) {
        switch(symbolSet) {
            case ISO60_Danish:
                return "DN";
            case DeskTop:
                return "DT";
            case ISO8859_1_Latin1:
                return "E1";
            case ISO8859_2_Latin2:
                return "E2";
            case ISO8859_9_Latin5:
                return "E5";
            case ISO8859_10_Latin6:
                return "E6";
            case ISO8859_15_Latin9:
                return "E9";
            case ISO69_French:
                return "FR";
            case ISO21_German:
                return "GR";
            case ISO15_Italian:
                return "IT";
            case Legal:
                return "LG";
            case Math_8:
                return "M8";
            case Macintosh:
                return "MC";
            case PC858_Multi:
                return "P9";
            case PC8_Code437:
                return "PC";
            case PC8_DN_Code437N:
                return "PD";
            case PC852_Latin2:
                return "PE";
            case PC862_LatinHebrew:
                return "PH";
            case PI_Font:
                return "PI";
            case PC850_Multi:
                return "PM";
            case PC864_LatinArabic:
                return "PR";
            case PC8TK_Code437T:
                return "PT";
            case PC1004:
                return "PU";
            case PC775_Baltic:
                return "PV";
            case Roman8:
                return "R8";
            case Roman9:
                return "R9";
            case ISO17_Spanish:
                return "SP";
            case ISO11_Swedish:
                return "SW";
            case PS_Text:
                return "TS";
            case ISO4_UK:
                return "UK";
            case ISO6_ASCII:
                return "US";
            case UTF8:
                return "U8";
            case VentInt:
                return "VI";
            case VentMath:
                return "VM";
            case VentUS:
                return "VU";
            case Windows31_Latin1:
                return "W1";
            case Windows_LatinArabic:
                return "WA";
            case Windows31_Latin2:
                return "WE";
            case Windows31_Baltic:
                return "WL";
            case Windows30_Latin1:
                return "WO";
            case Windows_LatinCyrillic:
                return "WR";
            case Windows31_Latin5:
                return "WT";
            default:
                return "US";
        }
    }

    public String SymbolSetToString(ParametersDPL.DoubleByteSymbolSet symbolSet) {
        switch(symbolSet) {
            case BIG5:
                return "B5";
            case EUC:
                return "EU";
            case GovernmentBureau:
                return "GB";
            case JIS:
                return "JS";
            case ShiftJIS:
                return "SJ";
            case Unicode:
                return "UC";
            default:
                return "GB";
        }
    }

    public int getFontHeight() {
        return this.m_FontHeight;
    }

    public void setFontHeight(int value) {
        if (value == 0 || value >= 4 && value <= 4163) {
            this.m_FontHeight = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontHeight' must be equal to 0 or between 4 - 4163. Value given was %1$s", value));
        }
    }

    public int getFontWidth() {
        return this.m_FontWidth;
    }

    public void setFontWidth(int value) {
        if (value == 0 || value >= 4 && value <= 4163) {
            this.m_FontWidth = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fontWidth' must be  0 or between 4 - 4163. Value given was %1$s", value));
        }
    }

    public int getFillPattern() {
        return this.m_fillPattern;
    }

    public void setFillPattern(int value) {
        if (value >= 0 && value <= 11) {
            this.m_fillPattern = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'fillPattern' must be between 0 - 11. Value given was %1$s", value));
        }
    }

    public boolean getIsDotMode() {
        return this.m_isDotMode;
    }

    public void setIsDotMode(boolean value) {
        this.m_isDotMode = value;
    }

    public int getSymbolHeight() {
        return this.m_symbolHeight;
    }

    public void setSymbolHeight(int value) {
        if (value >= 0 && value <= 999) {
            this.m_symbolHeight = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'symbolHeight' must be between 0 - 999. Value given was %1$s", value));
        }
    }

    public void setHorizontalMultiplier(int value) {
        this.setHorizontalMultiplier(value, 0, 61);
    }

    public void setVerticalMultiplier(int value) {
        this.setVerticalMultiplier(value, 0, 61);
    }

    public void setRotate(ParametersDPL.Rotation rotationAngle) throws IllegalArgumentException {
        if (rotationAngle == null) {
            throw new IllegalArgumentException("Parameter 'rotationAngle' cannot be null.");
        } else {
            this.m_Rotation = rotationAngle;
        }
    }

    public void setAlignment(ParametersDPL.Alignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Parameter 'rotationAngle' cannot be null.");
        } else {
            this.m_Alignment = alignment;
        }
    }

    public boolean getIsMirrored() {
        return this.m_IsMirrored;
    }

    public void setIsMirrored(boolean value) {
        this.m_IsMirrored = value;
    }

    public ParametersDPL.MeasurementMode getMeasurement() {
        return this.m_Measurement;
    }

    public void setMeasurement(ParametersDPL.MeasurementMode value) {
        this.m_Measurement = value;
    }

    public String getEmbeddedIncrementDecrementValue() {
        return this.m_embeddedIncrementDecrementValue;
    }

    public void setEmbeddedIncrementDecrementValue(String value) {
        if (value.length() != 0 && !Document.matches(value, "^([0-9]*)$")) {
            throw new IllegalArgumentException(String.format("Parameter 'embeddedIncrementDecrementValue' must be from numeric. Value was %s", value));
        } else {
            this.m_embeddedIncrementDecrementValue = value;
        }
    }

    public Boolean getEmbeddedEnable() {
        return this.m_embeddedEnable;
    }

    public void setEmbeddedEnable(Boolean value) {
        this.m_embeddedEnable = value;
    }

    public int getIncrementDecrementValue() {
        return this.m_incrementDecrementValue;
    }

    public void setIncrementDecrementValue(int value) {
        if (value >= 0 && value <= 999) {
            this.m_incrementDecrementValue = value;
        } else {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), "Parameter 'incrementDecrementValue' must be from 0 to 999, a value of %d was given.", value));
        }
    }

    public ParametersDPL.IncrementDecrementTypeValue getIncrementDecrementType() {
        return this.m_incrementDecrementType;
    }

    public void setIncrementDecrementType(ParametersDPL.IncrementDecrementTypeValue value) {
        this.m_incrementDecrementType = value;
    }

    public String getFillerCharacter() {
        return this.m_fillerCharacter;
    }

    public void setFillerCharacter(String value) {
        if (!value.equals("") && value.length() != 1) {
            throw new IllegalArgumentException(String.format("Parameter 'fillerCharacter' must be from 1 character long, a value of %s was given.", value));
        } else {
            this.m_fillerCharacter = value;
        }
    }

    public static enum SingleByteSymbolSet {
        Unknown,
        ISO60_Danish,
        DeskTop,
        ISO8859_1_Latin1,
        ISO8859_2_Latin2,
        ISO8859_9_Latin5,
        ISO8859_10_Latin6,
        ISO8859_15_Latin9,
        ISO69_French,
        ISO21_German,
        ISO15_Italian,
        Legal,
        Math_8,
        Macintosh,
        PS_Math,
        PC858_Multi,
        PC8_Code437,
        PC8_DN_Code437N,
        PC852_Latin2,
        PC862_LatinHebrew,
        PI_Font,
        PC850_Multi,
        PC864_LatinArabic,
        PC8TK_Code437T,
        PC1004,
        PC775_Baltic,
        Roman8,
        Roman9,
        ISO17_Spanish,
        ISO11_Swedish,
        PS_Text,
        ISO4_UK,
        ISO6_ASCII,
        UTF8,
        VentInt,
        VentMath,
        VentUS,
        Windows31_Latin1,
        Windows_LatinArabic,
        Windows31_Latin2,
        Windows31_Baltic,
        Windows30_Latin1,
        Windows_LatinCyrillic,
        Windows31_Latin5;

        private SingleByteSymbolSet() {
        }
    }

    public static enum DoubleByteSymbolSet {
        Unknown,
        BIG5,
        EUC,
        GovernmentBureau,
        JIS,
        ShiftJIS,
        Unicode;

        private DoubleByteSymbolSet() {
        }
    }

    public static enum MeasurementMode {
        Metric,
        Inch;

        private MeasurementMode() {
        }
    }

    public static enum Alignment {
        Left,
        Center,
        Right;

        private Alignment() {
        }
    }

    public static enum Rotation {
        Rotate_0(1),
        Rotate_90(2),
        Rotate_180(3),
        Rotate_270(4);

        private int angle;

        private Rotation(int intValue) {
            this.angle = intValue;
        }

        public int getValue() {
            return this.angle;
        }
    }

    public static enum IncrementDecrementTypeValue {
        None(0),
        NumericIncrement(43),
        AlphanumericIncrement(62),
        HexdecimalIncrement(40),
        NumericDecrement(45),
        AlphanumericDecrement(60),
        HexdecimalDecrement(41);

        private int intValue;

        private IncrementDecrementTypeValue(int value) {
            this.intValue = value;
        }

        public int getValue() {
            return this.intValue;
        }
    }
}
