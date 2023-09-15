package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/20/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.graphics.Bitmap;
import android.graphics.PointF;
import java.util.Locale;

public class MuPDFCore {
    private int numPages = -1;
    private float pageWidth;
    private float pageHeight;
    private long globals;
    private byte[] fileBuffer;
    private String file_format;
    private boolean isUnencryptedPDF;
    private final boolean wasOpenedFromBuffer;

    private native long openFile(String var1);

    private native long openBuffer(String var1);

    private native String fileFormatInternal();

    private native boolean isUnencryptedPDFInternal();

    private native int countPagesInternal();

    private native void gotoPageInternal(int var1);

    private native float getPageWidth();

    private native float getPageHeight();

    private native void drawPage(Bitmap var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

    private native void updatePageInternal(Bitmap var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9);

    private native void destroying();

    private native boolean hasChangesInternal();

    private native void saveInternal();

    private native long createCookie();

    private native void destroyCookie(long var1);

    private native void abortCookie(long var1);

    public native boolean javascriptSupported();

    public MuPDFCore(String filename) throws Exception {
        this.globals = this.openFile(filename);
        if (this.globals == 0L) {
            throw new Exception(String.format(Locale.getDefault(), "Unable to open file %s", filename));
        } else {
            this.file_format = this.fileFormatInternal();
            this.isUnencryptedPDF = this.isUnencryptedPDFInternal();
            this.wasOpenedFromBuffer = false;
        }
    }

    public MuPDFCore(byte[] buffer, String magic) throws Exception {
        this.fileBuffer = buffer;
        this.globals = this.openBuffer(magic != null ? magic : "");
        if (this.globals == 0L) {
            throw new Exception("Unable to open buffer. ");
        } else {
            this.file_format = this.fileFormatInternal();
            this.isUnencryptedPDF = this.isUnencryptedPDFInternal();
            this.wasOpenedFromBuffer = true;
        }
    }

    public int countPages() {
        if (this.numPages < 0) {
            this.numPages = this.countPagesSynchronized();
        }

        return this.numPages;
    }

    public String fileFormat() {
        return this.file_format;
    }

    public boolean isUnencryptedPDF() {
        return this.isUnencryptedPDF;
    }

    public boolean wasOpenedFromBuffer() {
        return this.wasOpenedFromBuffer;
    }

    private synchronized int countPagesSynchronized() {
        return this.countPagesInternal();
    }

    private void gotoPage(int page) {
        if (page > this.numPages - 1) {
            page = this.numPages - 1;
        } else if (page < 0) {
            page = 0;
        }

        this.gotoPageInternal(page);
        this.pageWidth = this.getPageWidth();
        this.pageHeight = this.getPageHeight();
    }

    public synchronized PointF getPageSize(int page) {
        this.gotoPage(page);
        return new PointF(this.pageWidth, this.pageHeight);
    }

    public synchronized void onDestroy() {
        this.destroying();
        this.globals = 0L;
    }

    public synchronized void drawPage(Bitmap bm, int page, int pageW, int pageH, int patchX, int patchY, int patchW, int patchH, MuPDFCore.Cookie cookie) {
        this.gotoPage(page);
        this.drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
    }

    public synchronized void updatePage(Bitmap bm, int page, int pageW, int pageH, int patchX, int patchY, int patchW, int patchH, MuPDFCore.Cookie cookie) {
        this.updatePageInternal(bm, page, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
    }

    static {
        System.loadLibrary("mupdf_java");
    }

    public class Cookie {
        private final long cookiePtr = MuPDFCore.this.createCookie();

        public Cookie() {
            if (this.cookiePtr == 0L) {
                throw new OutOfMemoryError();
            }
        }

        public void abort() {
            MuPDFCore.this.abortCookie(this.cookiePtr);
        }

        public void destroy() {
            MuPDFCore.this.destroyCookie(this.cookiePtr);
        }
    }
}
