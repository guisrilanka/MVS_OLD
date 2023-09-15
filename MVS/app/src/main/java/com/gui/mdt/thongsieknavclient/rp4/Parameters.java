package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/20/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


public abstract class Parameters {
    private String m_FontName = "";
    private boolean m_IsInverse = false;
    private int m_HorizontalMultiplier = 1;
    private int m_VerticalMultiplier = 1;
    private String m_TextEncoding = "";

    public Parameters() {
    }

    public String getFont() {
        return this.m_FontName;
    }

    protected void setFont(String fontName, int size) throws IllegalArgumentException {
        if (fontName != null && (fontName.length() == size || fontName.length() == 0)) {
            this.m_FontName = fontName;
        } else {
            throw new IllegalArgumentException("Parameter 'fontName' must be either " + size + " characters in length or the empty string.");
        }
    }

    protected void setFont(String fontName) {
        if (fontName != null && (fontName.length() <= 3 || fontName.length() == 0)) {
            this.m_FontName = fontName;
        } else {
            throw new IllegalArgumentException("Parameter 'fontName' must be at least " + Integer.toString(3) + " characters in length or the empty string.");
        }
    }

    public boolean getIsInverse() {
        return this.m_IsInverse;
    }

    public void setIsInverse(boolean isPrintingInverse) {
        this.m_IsInverse = isPrintingInverse;
    }

    public int getHorizontalMultiplier() {
        return this.m_HorizontalMultiplier;
    }

    protected void setHorizontalMultiplier(int multiplier, int lowerBound, int upperBound) throws IllegalArgumentException {
        if (multiplier >= lowerBound && multiplier <= upperBound) {
            this.m_HorizontalMultiplier = multiplier;
        } else {
            throw new IllegalArgumentException("Parameter 'multiplier' must be in the range of " + lowerBound + " to " + upperBound + ", a value of " + multiplier + " was given.");
        }
    }

    public int getVerticalMultiplier() {
        return this.m_VerticalMultiplier;
    }

    protected void setVerticalMultiplier(int multiplier, int lowerBound, int upperBound) throws IllegalArgumentException {
        if (multiplier >= lowerBound && multiplier <= upperBound) {
            this.m_VerticalMultiplier = multiplier;
        } else {
            throw new IllegalArgumentException("Parameter 'multiplier' must be in the range of " + lowerBound + " to " + upperBound + ", a value of " + multiplier + " was given.");
        }
    }

    public String getTextEncoding() {
        return this.m_TextEncoding;
    }

    public void setTextEncoding(String encoding) {
        this.m_TextEncoding = encoding;
    }
}
