package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class YmatsuxMjaiClientsMain {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Flags.parse(args);
        String clientName = Flags.CLIENT.getValue();
        Socket socket = new Socket(
                Flags.SERVER.getValue(), Integer.parseInt(Flags.PORT.getValue()));
        socket.setTcpNoDelay(true);
        MjaiClient client = createClient(clientName, socket);
        client.run();
        socket.close();
    }

    private static MjaiClient createClient(String clientName, Socket socket) throws IOException {
        switch(clientName) {
        case "tsumogiri-java":
            return new TsumogiriClient(socket);
        case "shantensu-java":
            return new ShantensuClient(socket);
        case "shantensu-richi-java":
            return new ShantensuRichiClient(socket);
        case "ymatsux":
            return new YmatsuxClient(socket);
        default:
            throw new IllegalArgumentException("Unknown client name: " + clientName);
        }
    }
}
