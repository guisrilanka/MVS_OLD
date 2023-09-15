package com.gui.mdt.thongsieknavclient.rp4;

import com.gui.mdt.thongsieknavclient.rp4.Monitor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public abstract class ConnectionBase extends Thread {
    protected final Object m_LockGeneral = new Object();
    private final Object m_DataLockRecv = new Object();
    private final Object m_DataLockSend = new Object();
    private volatile boolean m_WorkerThreadActive = false;
    private ByteArrayOutputStream m_mStreamRecv = new ByteArrayOutputStream(1024);
    private boolean m_IsServerMode = false;
    protected boolean m_Reconnecting = false;
    private ArrayList<byte[]> m_DataStorageSend = new ArrayList();
    protected boolean m_IsOpen = false;
    protected boolean m_IsActive = false;
    private boolean m_IsClosing = false;
    boolean m_ClearTriggered = false;
    protected boolean m_IsSynchronous = false;

    public boolean getIsOpen() {
        return this.m_IsOpen | this.m_Reconnecting;
    }

    public boolean getIsActive() {
        return this.m_IsActive;
    }

    public long getBytesAvailable() {
        synchronized(this.m_DataLockRecv) {
            long length = (long)this.m_mStreamRecv.size();
            return length;
        }
    }

    public boolean getIsServerMode() {
        return this.m_IsServerMode;
    }

    public boolean getIsClientMode() {
        return !this.m_IsServerMode;
    }

    protected ConnectionBase(boolean isServer) {
        this.m_IsServerMode = isServer;
        this.setName("Communications");
        this.setDaemon(true);
        this.start();
    }

    protected void finalize() throws Throwable {
        System.out.println("finalize called");
        super.finalize();
    }

    public void clearReadBuffer() {
        synchronized(this.m_DataLockRecv) {
            this.m_mStreamRecv.reset();
        }
    }

    public void clearWriteBuffer() {
        synchronized(this.m_DataLockSend) {
            this.m_DataStorageSend.clear();
        }
    }

    public String read() {
        return this.read("", 0L);
    }

    public String read(String endSequence, long msecTimeout) {
        String dataRead = "";
        long timeout = msecTimeout;
        if (!this.getIsOpen()) {
            throw new UnsupportedOperationException("Can not perform a Read on a Closed connection.");
        } else if (this.m_IsClosing) {
            throw new UnsupportedOperationException("Can not perform a Read while the connection is in the process of being Closed.");
        } else {
            try {
                int size;
                byte[] data;
                char[] charArray;
                boolean foundFlag;
                boolean absoluteMode;
                long lastTime;
//                int endIndex;
                int endIndex;
                if (this.m_IsSynchronous) {
                    if (timeout == 0L) {
                        endIndex = 0;
                        data = new byte[32767];
                        if (this.getHasData()) {
                            endIndex = this.innerRead(data);
                        }

                        this.m_mStreamRecv.write(data, 0, endIndex);
                        synchronized(this.m_DataLockRecv) {
                            data = this.m_mStreamRecv.toByteArray();
                            this.m_mStreamRecv.reset();
                        }

                        dataRead = new String(data, "ISO-8859-1");
                        if (!endSequence.equals("") && dataRead.contains(endSequence)) {
                            endIndex = dataRead.indexOf(endSequence);
                            dataRead = dataRead.substring(0, endIndex + 1);
                        }
                    } else {
                        foundFlag = false;
                        absoluteMode = timeout < 0L;
                        lastTime = System.currentTimeMillis();
                        timeout = Math.abs(timeout);

                        while(true) {
                            while(!foundFlag && System.currentTimeMillis() - lastTime < timeout) {
                                if (this.getHasData()) {
                                    data = new byte[32767];
                                    endIndex = this.innerRead(data);
                                    this.m_mStreamRecv.write(data, 0, endIndex);
                                    if (this.m_mStreamRecv.size() == 0) {
                                        try {
                                            Thread.sleep(100L);
                                        } catch (InterruptedException var22) {
                                        }
                                        continue;
                                    }

                                    synchronized(this.m_DataLockRecv) {
                                        size = this.m_mStreamRecv.size();
                                        data = this.m_mStreamRecv.toByteArray();
                                        this.m_mStreamRecv.reset();
                                    }

                                    int index;
                                    if (Charset.isSupported("ISO-8859-1")) {
                                        dataRead = dataRead + new String(data, "ISO-8859-1");
                                    } else {
                                        charArray = new char[size];

                                        for(index = 0; index < size; ++index) {
                                            charArray[index] = (char)data[index];
                                        }

                                        dataRead = dataRead + new String(charArray);
                                    }

                                    foundFlag = endSequence.length() > 0 && dataRead.contains(endSequence);
                                    if (foundFlag) {
                                        index = dataRead.indexOf(endSequence);
                                        dataRead = dataRead.substring(0, index + 1);
                                    }
                                }

                                if (absoluteMode) {
                                    lastTime = System.currentTimeMillis();
                                }
                            }

                            return dataRead;
                        }
                    }
                } else if (timeout == 0L) {
                    synchronized(this.m_DataLockRecv) {
                        size = this.m_mStreamRecv.size();
                        if (size == 0) {
                            return "";
                        }

                        data = this.m_mStreamRecv.toByteArray();
                        this.m_mStreamRecv.reset();
                    }

                    charArray = new char[size];

                    for(endIndex = 0; endIndex < size; ++endIndex) {
                        charArray[endIndex] = (char)data[endIndex];
                    }

                    dataRead = new String(charArray);
                    if (!endSequence.equals("") && dataRead.contains(endSequence)) {
                        endIndex = dataRead.indexOf(endSequence);
                        dataRead = dataRead.substring(0, endIndex + 1);
                    }
                } else {
                    foundFlag = false;
                    absoluteMode = timeout < 0L;
                    lastTime = System.currentTimeMillis();
                    timeout = Math.abs(timeout);

                    while(true) {
                        while(!foundFlag && System.currentTimeMillis() - lastTime < timeout) {
                            if (this.m_mStreamRecv.size() == 0) {
                                try {
                                    Thread.sleep(100L);
                                } catch (InterruptedException var20) {
                                }
                            } else {
                                synchronized(this.m_DataLockRecv) {
                                    size = this.m_mStreamRecv.size();
                                    data = this.m_mStreamRecv.toByteArray();
                                    this.m_mStreamRecv.reset();
                                }

                                if (Charset.isSupported("ISO-8859-1")) {
                                    dataRead = dataRead + new String(data, "ISO-8859-1");
                                } else {
                                    charArray = new char[size];

                                    for(endIndex = 0; endIndex < size; ++endIndex) {
                                        charArray[endIndex] = (char)data[endIndex];
                                    }

                                    dataRead = dataRead + new String(charArray);
                                }

                                foundFlag = endSequence.length() > 0 && dataRead.contains(endSequence);
                                if (foundFlag) {
                                    endIndex = dataRead.indexOf(endSequence);
                                    dataRead = dataRead.substring(0, endIndex + 1);
                                }

                                if (absoluteMode) {
                                    lastTime = System.currentTimeMillis();
                                }
                            }
                        }

                        return dataRead;
                    }
                }

                return dataRead;
            } catch (Exception var25) {
                var25.printStackTrace();
                return dataRead;
            }
        }
    }

    public String readLine(String endSequence, long msecTimeout) {
        String dataRead = "";
        long timeout = msecTimeout;
        if (!this.getIsOpen()) {
            throw new UnsupportedOperationException("Can not perform a Read on a Closed connection.");
        } else if (this.m_IsClosing) {
            throw new UnsupportedOperationException("Can not perform a Read while the connection is in the process of being Closed.");
        } else {
            try {
                int size;
                byte[] data;
                char[] charArray;
                boolean foundFlag;
                boolean absoluteMode;
                long lastTime;
//                int endIndex;
                String dataFromEndSeq;
                int endIndex;
                String lineWithEndSequence;
                if (this.m_IsSynchronous) {
                    if (timeout == 0L) {
                        endIndex = 0;
                        data = new byte[32767];
                        if (this.getHasData()) {
                            endIndex = this.innerRead(data);
                        }

                        this.m_mStreamRecv.write(data, 0, endIndex);
                        synchronized(this.m_DataLockRecv) {
                            data = this.m_mStreamRecv.toByteArray();
                            this.m_mStreamRecv.reset();
                        }

                        dataRead = new String(data, "ISO-8859-1");
                        if (!endSequence.equals("") && dataRead.contains(endSequence)) {
                            endIndex = dataRead.indexOf(endSequence);
                            lineWithEndSequence = dataRead.substring(endIndex);
                            lineWithEndSequence = lineWithEndSequence.substring(0, lineWithEndSequence.indexOf("\r\n") + 2);
                            dataRead = dataRead.substring(0, endIndex + lineWithEndSequence.length());
                        }
                    } else {
                        foundFlag = false;
                        absoluteMode = timeout < 0L;
                        lastTime = System.currentTimeMillis();
                        timeout = Math.abs(timeout);

                        while(true) {
                            while(!foundFlag && System.currentTimeMillis() - lastTime < timeout) {
                                if (this.getHasData()) {
                                    data = new byte[32767];
                                    endIndex = this.innerRead(data);
                                    this.m_mStreamRecv.write(data, 0, endIndex);
                                    if (this.m_mStreamRecv.size() == 0) {
                                        try {
                                            Thread.sleep(100L);
                                        } catch (InterruptedException var23) {
                                        }
                                        continue;
                                    }

                                    synchronized(this.m_DataLockRecv) {
                                        size = this.m_mStreamRecv.size();
                                        data = this.m_mStreamRecv.toByteArray();
                                        this.m_mStreamRecv.reset();
                                    }

                                    int index;
                                    if (Charset.isSupported("ISO-8859-1")) {
                                        dataRead = dataRead + new String(data, "ISO-8859-1");
                                    } else {
                                        charArray = new char[size];

                                        for(index = 0; index < size; ++index) {
                                            charArray[index] = (char)data[index];
                                        }

                                        dataRead = dataRead + new String(charArray);
                                    }

                                    foundFlag = endSequence.length() > 0 && dataRead.contains(endSequence);
                                    if (foundFlag) {
                                        index = dataRead.indexOf(endSequence);
                                        dataFromEndSeq = dataRead.substring(index);
                                        lineWithEndSequence = dataFromEndSeq.substring(0, dataFromEndSeq.indexOf("\r\n") + 2);
                                        dataRead = dataRead.substring(0, index + lineWithEndSequence.length());
                                    }
                                }

                                if (absoluteMode) {
                                    lastTime = System.currentTimeMillis();
                                }
                            }

                            return dataRead;
                        }
                    }
                } else if (timeout == 0L) {
                    synchronized(this.m_DataLockRecv) {
                        size = this.m_mStreamRecv.size();
                        if (size == 0) {
                            return "";
                        }

                        data = this.m_mStreamRecv.toByteArray();
                        this.m_mStreamRecv.reset();
                    }

                    charArray = new char[size];

                    for(endIndex = 0; endIndex < size; ++endIndex) {
                        charArray[endIndex] = (char)data[endIndex];
                    }

                    dataRead = new String(charArray);
                    if (!endSequence.equals("") && dataRead.contains(endSequence)) {
                        endIndex = dataRead.indexOf(endSequence);
                        dataFromEndSeq = dataRead.substring(endIndex);
                        lineWithEndSequence = dataFromEndSeq.substring(0, dataFromEndSeq.indexOf("\r\n") + 2);
                        dataRead = dataRead.substring(0, endIndex + lineWithEndSequence.length());
                    }
                } else {
                    foundFlag = false;
                    absoluteMode = timeout < 0L;
                    lastTime = System.currentTimeMillis();
                    timeout = Math.abs(timeout);

                    while(true) {
                        while(!foundFlag && System.currentTimeMillis() - lastTime < timeout) {
                            if (this.m_mStreamRecv.size() == 0) {
                                try {
                                    Thread.sleep(100L);
                                } catch (InterruptedException var21) {
                                }
                            } else {
                                synchronized(this.m_DataLockRecv) {
                                    size = this.m_mStreamRecv.size();
                                    data = this.m_mStreamRecv.toByteArray();
                                    this.m_mStreamRecv.reset();
                                }

                                if (Charset.isSupported("ISO-8859-1")) {
                                    dataRead = dataRead + new String(data, "ISO-8859-1");
                                } else {
                                    charArray = new char[size];

                                    for(endIndex = 0; endIndex < size; ++endIndex) {
                                        charArray[endIndex] = (char)data[endIndex];
                                    }

                                    dataRead = dataRead + new String(charArray);
                                }

                                foundFlag = endSequence.length() > 0 && dataRead.contains(endSequence);
                                if (foundFlag) {
                                    endIndex = dataRead.indexOf(endSequence);
                                    dataFromEndSeq = dataRead.substring(endIndex);
                                    dataFromEndSeq = dataFromEndSeq.substring(0, dataFromEndSeq.indexOf("\r\n") + 2);
                                    dataRead = dataRead.substring(0, endIndex + dataFromEndSeq.length());
                                }

                                if (absoluteMode) {
                                    lastTime = System.currentTimeMillis();
                                }
                            }
                        }

                        return dataRead;
                    }
                }

                return dataRead;
            } catch (Exception var26) {
                var26.printStackTrace();
                return dataRead;
            }
        }
    }

    public int read(byte[] buffer, int offset, int length) {
        int lengthRead = 0;
        if (!this.getIsOpen()) {
            throw new UnsupportedOperationException("Can not perform a Read on a Closed connection.");
        } else if (this.m_IsClosing) {
            throw new UnsupportedOperationException("Can not perform a Read while the connection is in the process of being Closed.");
        } else {
            byte[] data;
            if (this.m_IsSynchronous) {
                try {
                    if (this.getHasData()) {
                        data = new byte[length];
                        lengthRead = this.innerRead(data);
                        if (lengthRead <= length) {
                            System.arraycopy(data, 0, buffer, offset, lengthRead);
                        } else {
                            lengthRead = length;
                            System.arraycopy(data, 0, buffer, offset, length);
                        }
                    }
                } catch (IOException var9) {
                    var9.printStackTrace();
                    return 0;
                }
            } else {
                synchronized(this.m_DataLockRecv) {
                    if (this.m_mStreamRecv.size() == 0) {
                        return 0;
                    }

                    data = this.m_mStreamRecv.toByteArray();
                    if (this.m_mStreamRecv.size() <= length) {
                        lengthRead = data.length;
                        System.arraycopy(data, 0, buffer, offset, lengthRead);
                        this.m_mStreamRecv.reset();
                    } else {
                        lengthRead = length;
                        System.arraycopy(data, 0, buffer, offset, length);
                        this.m_mStreamRecv.reset();
                        this.m_mStreamRecv.write(data, length, data.length - length);
                    }
                }
            }

            return lengthRead;
        }
    }

    public void write(String data) {
        byte[] dataArray = data.getBytes();
        this.write(dataArray, 0, dataArray.length);
    }

    public void write(byte[] buffer) {
        this.write(buffer, 0, buffer.length);
    }

    public void write(byte[] buffer, int offset, int length) {
        byte[] copy;
        if (buffer == null) {
            copy = new byte[0];
        } else {
            copy = new byte[Math.min(buffer.length - offset, length)];
        }

        if (!this.getIsOpen()) {
            throw new UnsupportedOperationException("Can not perform a Write on a Closed connection.");
        } else if (this.m_IsClosing) {
            throw new UnsupportedOperationException("Can not perform a Write while the connection is in the process of being Closed.");
        } else {
            if (buffer != null) {
                System.arraycopy(buffer, offset, copy, 0, copy.length);
            }

            if (this.m_IsSynchronous) {
                try {
                    this.innerWrite(copy);
                } catch (IOException var7) {
                    var7.printStackTrace();
                }

            } else {
                synchronized(this.m_DataLockSend) {
                    this.m_DataStorageSend.add(copy);
                }
            }
        }
    }

    protected ConnectionBase.PrinterResponse waitResponse(int msecTimeout) {
        String buffer = "";
        byte[] readBuffer = new byte[1];
        long time = System.currentTimeMillis();
        if (!this.getIsOpen()) {
            throw new UnsupportedOperationException("Can not perform a WaitResponse on a Closed connection.");
        } else if (this.m_IsClosing) {
            throw new UnsupportedOperationException("Can not perform a WaitResponse while the connection is in the process of being Closed.");
        } else if (this.m_IsSynchronous) {
            return ConnectionBase.PrinterResponse.Unknown;
        } else {
            while(System.currentTimeMillis() - time < (long)msecTimeout) {
                if (this.read(readBuffer, 0, 1) == 0) {
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException var7) {
                    }
                } else {
                    buffer = buffer.length() == 5 ? buffer.substring(1) + (char)readBuffer[0] : buffer + (char)readBuffer[0];
                    if (buffer.contains("{")) {
                        buffer = buffer.substring(buffer.indexOf("{"));
                    }

                    if (buffer.length() >= 5 && buffer.length() == 5 && buffer.startsWith("{") && buffer.endsWith("}")) {
                        ConnectionBase.PrinterResponse result = ConnectionBase.PrinterResponse.Unknown;
                        if (buffer.startsWith("{A")) {
                            result = ConnectionBase.PrinterResponse.ACK;
                        } else if (buffer.startsWith("{N")) {
                            result = ConnectionBase.PrinterResponse.NAK;
                        } else if (buffer.startsWith("{W")) {
                            result = ConnectionBase.PrinterResponse.WRITING;
                        } else if (buffer.startsWith("{R")) {
                            result = ConnectionBase.PrinterResponse.RESUME;
                        } else if (buffer.startsWith("{D")) {
                            result = ConnectionBase.PrinterResponse.DONE;
                        }

                        return result;
                    }
                }
            }

            return ConnectionBase.PrinterResponse.Unknown;
        }
    }

    protected abstract boolean getHasData();

    public synchronized boolean open() throws Exception {
        boolean results = true;
        if (this.m_IsSynchronous) {
            results = this.innerOpen();
            if (this.m_IsOpen) {
                this.m_Reconnecting = false;
                this.m_IsClosing = false;
            }
        } else {
            try {
                Monitor.enter(this.m_LockGeneral);
                if (!this.m_IsOpen) {
                    results = this.innerOpen();
                    if (this.m_IsOpen) {
                        if (!this.m_WorkerThreadActive) {
                            this.m_Reconnecting = false;
                            this.m_IsClosing = false;
                            this.m_WorkerThreadActive = true;
                            this.notify();
                        }

                        this.m_Reconnecting = false;
                    }
                }
            } finally {
                Monitor.exit(this.m_LockGeneral);
            }
        }

        return results;
    }

    protected abstract boolean innerOpen() throws IOException;

    public boolean waitForEmptyBuffer(int timeout_msec) {
        long endTime = System.currentTimeMillis() + (long)timeout_msec;
        if (!this.getIsActive()) {
            return true;
        } else {
            while(System.currentTimeMillis() < endTime) {
                synchronized(this.m_DataLockSend) {
                    if (this.m_DataStorageSend.size() == 0) {
                        return true;
                    }
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var6) {
                }
            }

            return false;
        }
    }

    public void close() {
        try {
            Thread.sleep(2000L);
            this.close(false);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    protected abstract void close(boolean var1);

    protected void closeBase(boolean isInternalCall) {
        this.m_IsClosing = true;
        if (!isInternalCall && !this.m_IsSynchronous) {
            while(this.m_WorkerThreadActive) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var3) {
                }
            }
        }

    }

    protected abstract int innerRead(byte[] var1) throws IOException;

    protected abstract void innerWrite(byte[] var1) throws IOException;

    protected abstract boolean innerListen();

    protected abstract String configSummary();

    protected abstract String configCompact();

    protected abstract String configDetail();

    public String toString() {
        return this.toString("L");
    }

    public String toString(String format) {
        byte var4 = -1;
        switch(format.hashCode()) {
            case 76:
                if (format.equals("L")) {
                    var4 = 0;
                }
                break;
            case 83:
                if (format.equals("S")) {
                    var4 = 1;
                }
        }

        String results;
        switch(var4) {
            case 0:
                results = this.configDetail();
                break;
            case 1:
                results = this.configSummary();
                break;
            default:
                throw new IllegalArgumentException("ToString parameter must be 'L' or 'S'");
        }

        return results;
    }

    public void run() {
        for(boolean doRunRun = true; doRunRun; this.m_WorkerThreadActive = false) {
            try {
                synchronized(this) {
                    while(!this.m_WorkerThreadActive) {
                        this.wait(1000L);
                    }
                }

                this.runInternal();
            } catch (Exception var5) {
            }
        }

    }

    private void runInternal() {
        boolean oneTry = true;
        boolean shouldSleep = false;
        boolean doReconnect = false;
        byte[] buffer = new byte[65536];
        this.m_Reconnecting = false;
        this.m_IsClosing = false;

        try {
            label380:
            while(oneTry || !this.m_IsClosing) {
                oneTry = false;
                if (this.getIsServerMode()) {
                    if (this.m_IsActive) {
                        this.m_IsActive = false;
                    }

                    if (!this.innerListen()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException var40) {
                        }
                        continue;
                    }
                }

                this.m_IsActive = true;

                while(true) {
                    while(true) {
                        if (!this.getIsActive()) {
                            continue label380;
                        }

                        if (shouldSleep && !doReconnect) {
                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException var39) {
                            }
                        }

                        shouldSleep = true;
                        if (doReconnect) {
                            if (!Monitor.tryEnter(this.m_LockGeneral, 0L)) {
                                if (this.m_IsClosing) {
                                    continue label380;
                                }
                                continue;
                            }

                            try {
                                doReconnect = false;
                                this.m_Reconnecting = true;
                                this.close(true);

                                try {
                                    Thread.sleep(500L);
                                } catch (InterruptedException var38) {
                                }

                                this.open();
                                this.m_IsClosing = false;
                                if (this.getIsServerMode()) {
                                    this.m_IsActive = false;
                                    continue;
                                }
                            } catch (Exception var42) {
                                throw new Exception("Reconnect failed: " + var42.getLocalizedMessage());
                            } finally {
                                Monitor.exit(this.m_LockGeneral);
                            }
                        }

                        byte[] sendItem;
                        synchronized(this.m_DataLockSend) {
                            sendItem = null;
                            this.m_ClearTriggered = false;
                            if (this.m_DataStorageSend.size() > 0) {
                                sendItem = (byte[])this.m_DataStorageSend.get(0);
                            }
                        }

                        if (sendItem != null) {
                            try {
                                shouldSleep = false;
                                this.innerWrite(sendItem);
                                synchronized(this.m_DataLockSend) {
                                    if (!this.m_ClearTriggered) {
                                        this.m_DataStorageSend.remove(0);
                                    }
                                }
                            } catch (Exception var41) {
                                doReconnect = true;
                                continue;
                            }
                        }

                        if (this.m_IsClosing && this.m_DataStorageSend.size() == 0) {
                            continue label380;
                        }

                        if (this.getHasData()) {
                            synchronized(this.m_DataLockRecv) {
                                int length = this.innerRead(buffer);
                                this.m_mStreamRecv.write(buffer, 0, length);
                                shouldSleep = false;
                            }
                        }
                    }
                }
            }
        } catch (Exception var45) {
        }

        try {
            this.m_Reconnecting = false;
            doReconnect = false;
            this.close(true);
            this.m_IsActive = false;
            this.m_IsOpen = false;
            this.m_IsClosing = false;
        } finally {
            this.clearWriteBuffer();
        }

    }

    public static enum PrinterResponse {
        Unknown,
        NO_RESPONSE,
        ACK,
        NAK,
        WRITING,
        RESUME,
        DONE;

        private PrinterResponse() {
        }
    }
}
