package com.gui.mdt.thongsieknavclient.rp4;

/**
 * Created by Lasith Madhushanka on 4/29/2021
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import com.gui.mdt.thongsieknavclient.rp4.ConnectionBase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection_Bluetooth extends ConnectionBase {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothServerSocket m_BtListener = null;
    private BluetoothDevice device;
    private BluetoothSocket m_BtClient = null;
    private DataInputStream m_StreamRead = null;
    private DataOutputStream m_StreamWrite = null;
    private String m_Address = "00:00:00:00:00:00";

    public String getRemoteEnd() {
        return this.m_BtClient == null ? "-none-" : this.m_Address;
    }

    public String getFriendlyName() {
        return this.device.getName();
    }

    protected Connection_Bluetooth(boolean isServer, boolean isAsync, String targetDevice) throws Exception {
        super(isServer);
        this.m_Address = targetDevice;
        this.m_IsSynchronous = !isAsync;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            throw new Exception("Bluetooth not supported on device.");
        } else if (!adapter.isEnabled()) {
            throw new Exception("Bluetooth on device not enabled.");
        } else {
            Pattern pattern = Pattern.compile("[0-9A-Fa-f]{12}");
            Matcher matcher = pattern.matcher(targetDevice);
            if (matcher.matches()) {
                targetDevice = this.formatBluetoothAddress(targetDevice);
            }

            if (!BluetoothAdapter.checkBluetoothAddress(targetDevice)) {
                throw new Exception("Invalid Bluetooth Address format");
            } else {
                this.device = adapter.getRemoteDevice(this.m_Address);
            }
        }
    }

    public static Connection_Bluetooth createClient(String targetDevice) throws Exception {
        return new Connection_Bluetooth(false, true, targetDevice);
    }

    public static Connection_Bluetooth createClient(String targetDevice, boolean isAsync) throws Exception {
        return new Connection_Bluetooth(false, isAsync, targetDevice);
    }

    public static Connection_Bluetooth createServer(int port) throws Exception {
        return new Connection_Bluetooth(true, true, "");
    }

    protected boolean getHasData() {
        boolean hasData = false;

        try {
            hasData = this.m_BtClient.getInputStream().available() > 0;
        } catch (Exception var3) {
        }

        return hasData;
    }

    protected boolean innerOpen() throws IOException {
        if (this.getIsServerMode()) {
            if (!this.m_Reconnecting) {
                this.m_BtClient = this.m_BtListener.accept();
            }

            this.m_IsOpen = true;
        } else {
            try {
                this.m_BtClient = this.device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

            if (this.m_BtClient != null) {
                this.m_BtClient.connect();
                this.m_StreamRead = new DataInputStream(this.m_BtClient.getInputStream());
                this.m_StreamWrite = new DataOutputStream(this.m_BtClient.getOutputStream());
            }

            this.m_IsOpen = this.m_StreamRead != null && this.m_StreamWrite != null;
            this.m_IsActive = this.m_IsOpen;
        }

        return this.m_IsOpen;
    }

    protected void close(boolean isInternalCall) {
        int timeout = isInternalCall ? 0 : 2147483647;

        do {
            if (Monitor.tryEnter(this.m_LockGeneral, 0L)) {
                try {
                    timeout = 0;
                    if (this.getIsOpen()) {
                        this.closeBase(isInternalCall);

                        try {
                            if (this.m_StreamRead != null) {
                                this.m_StreamRead.close();
                            }
                        } catch (Exception var15) {
                        }

                        try {
                            if (this.m_StreamWrite != null) {
                                this.m_StreamWrite.close();
                            }
                        } catch (Exception var14) {
                        }

                        try {
                            if (this.m_BtClient != null) {
                                this.m_BtClient.close();
                            }
                        } catch (Exception var13) {
                        }

                        try {
                            if (!this.m_Reconnecting && this.m_BtListener != null) {
                                this.m_BtListener.close();
                            }
                        } catch (Exception var12) {
                        }

                        this.m_StreamRead = null;
                        this.m_StreamWrite = null;
                        this.m_BtClient = null;
                        if (!this.m_Reconnecting) {
                            this.m_BtListener = null;
                        }

                        this.m_IsOpen = false;
                        this.m_IsActive = false;
                        Thread.sleep(1000L);
                    }
                } catch (Exception var16) {
                } finally {
                    Monitor.exit(this.m_LockGeneral);
                }
            } else {
                timeout -= 100;
                if (timeout > 0) {
                    try {
                        Thread.sleep(100L);
                    } catch (Exception var18) {
                    }
                }
            }
        } while(timeout > 0);

    }

    protected int innerRead(byte[] buffer) throws IOException {
        return this.m_StreamRead.read(buffer);
    }

    protected void innerWrite(byte[] buffer) throws IOException {
        this.m_StreamWrite.write(buffer);
        this.m_StreamWrite.flush();
    }

    protected boolean innerListen() {
        boolean hasConnection = false;

        try {
            this.m_BtClient = this.m_BtListener.accept();
            this.m_StreamRead = new DataInputStream(this.m_BtClient.getInputStream());
            this.m_StreamWrite = new DataOutputStream(this.m_BtClient.getOutputStream());
            hasConnection = true;
        } catch (Exception var3) {
        }

        return hasConnection;
    }

    protected String configSummary() {
        String results = "Bluetooth " + (this.getIsServerMode() ? "(Server)" : "(Client)");
        return results;
    }

    protected String configCompact() {
        return this.m_Address;
    }

    protected String configDetail() {
        String results = "";
        if (this.getIsServerMode()) {
            results = results + "Bluetooth Server Mode\r\n";
        } else if (this.getIsClientMode()) {
            results = results + "Bluetooth Client Settings\r\nTarget Address: " + this.m_Address;
        }

        return results;
    }

    public void clearWriteBuffer() {
        super.clearWriteBuffer();
        if (this.m_IsSynchronous) {
            try {
                this.m_StreamWrite.flush();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    private String formatBluetoothAddress(String bluetoothAddr) {
        StringBuilder formattedBTAddress = new StringBuilder(bluetoothAddr);

        for(int bluetoothAddrPosition = 2; bluetoothAddrPosition <= formattedBTAddress.length() - 2; bluetoothAddrPosition += 3) {
            formattedBTAddress.insert(bluetoothAddrPosition, ":");
        }

        return formattedBTAddress.toString();
    }
}
