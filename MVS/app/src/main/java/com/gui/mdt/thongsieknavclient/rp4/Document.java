package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/20/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Document {
    public static char ESC = 27;
    protected static byte[] EOL = new byte[]{13, 10};
    public static char GS = 29;
    public static char RS = 30;
    public static char EOT = 4;
    public static final Character[] EXTENDED_ASCII = new Character[]{'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', '¢', '£', '¥', '₧', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', '¬', '½', '¼', '¡', '«', '»', '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐', '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧', '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀', 'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩', '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', ' '};
    protected ByteArrayOutputStream m_Document = new ByteArrayOutputStream(1024);
    private String m_DefaultFont = "";
    int m_FontNameLength = 0;
    private boolean m_LandscapeMode = false;

    public Document() {
    }

    public String getDefaultFont() {
        return this.m_DefaultFont;
    }

    public void setDefaultFont(String fontName) {
        if (fontName != null && fontName.length() == this.m_FontNameLength) {
            this.m_DefaultFont = fontName;
        } else {
            throw new IllegalArgumentException("Parameter 'fontName' must be " + this.m_FontNameLength + " characters in length.");
        }
    }

    public boolean getIsLandscapeMode() {
        return this.m_LandscapeMode;
    }

    public void setIsLandscapeMode(boolean isLandscapeMode) {
        this.m_LandscapeMode = isLandscapeMode;
    }

    public void clear() {
        this.m_Document.reset();
    }

    protected void addToDoc(ByteArrayOutputStream dataStream, String text) {
        char[] charArray = text.toCharArray();

        for(int index = 0; index < charArray.length; ++index) {
            if (charArray[index] <= 255) {
                this.addToDoc(dataStream, (byte)charArray[index]);
            } else {
                this.addToDoc(dataStream, (byte)(charArray[index] >> 8));
                this.addToDoc(dataStream, (byte)(charArray[index] & 255));
            }
        }

    }

    protected void addToDoc(ByteArrayOutputStream dataStream, byte[] data) {
        try {
            dataStream.write(data);
        } catch (IOException var4) {
        }

    }

    public static boolean matches(CharSequence input, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    protected void addToDoc(ByteArrayOutputStream dataStream, byte data) {
        dataStream.write(data);
    }

    protected byte[] decodeAsExtendedASCII(String text) {
        ByteArrayOutputStream decodedByteStream = new ByteArrayOutputStream();

        for(int index = 0; index < text.length(); ++index) {
            char code = text.charAt(index);
            List<Character> extendedASCIIList = Arrays.asList(EXTENDED_ASCII);
            int asciiIndex = extendedASCIIList.indexOf(code);
            if (asciiIndex != -1) {
                int asciiCode = asciiIndex + 128;
                decodedByteStream.write(asciiCode);
            } else {
                decodedByteStream.write(code);
            }
        }

        return decodedByteStream.toByteArray();
    }

    public abstract byte[] getDocumentData();

    protected static enum PrintingType {
        General,
        Barcode,
        Barcode417,
        BarcodeQR,
        BarcodeAztec,
        Line,
        Image,
        Graphics;

        private PrintingType() {
        }
    }
}
