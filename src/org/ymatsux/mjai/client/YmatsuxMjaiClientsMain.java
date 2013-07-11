package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class YmatsuxMjaiClientsMain {
    public static void main(String[] args) throws UnknownHostException, IOException {
        String clientName = Flags.get(Flags.CLIENT);
        Socket socket = new Socket(
                Flags.get(Flags.SERVER), Integer.parseInt(Flags.get(Flags.PORT)));
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
        default:
            throw new IllegalArgumentException("Unknown client name: " + clientName);
        }
    }
}
