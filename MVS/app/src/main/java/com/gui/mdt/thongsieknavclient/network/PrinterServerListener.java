package com.gui.mdt.thongsieknavclient.network;

import java.net.Socket;

public interface PrinterServerListener {
    public void onConnect(Socket socket);
}
