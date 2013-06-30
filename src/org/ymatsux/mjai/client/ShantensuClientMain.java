package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ShantensuClientMain {

    public static void main(String[] args) throws UnknownHostException, IOException {
        Flags.parse(args);
        Socket socket =
                new Socket(Flags.get(Flags.SERVER), Integer.parseInt(Flags.get(Flags.PORT)));
        ShantensuClient client = new ShantensuClient(socket);
        client.run();
    }
}
