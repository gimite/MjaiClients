package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TsumogiriClientMain {

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket("gimite.net", 11600);
        TsumogiriClient client = new TsumogiriClient(socket);
        client.run();
    }
}
