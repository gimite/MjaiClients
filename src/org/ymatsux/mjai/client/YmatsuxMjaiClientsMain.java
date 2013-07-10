package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class YmatsuxMjaiClientsMain {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Flags.parse(args);
        System.out.println(args);
        System.out.println(Flags.get(Flags.GAMES));
        String clientName = Flags.get(Flags.CLIENT);
        switch (clientName) {
        case "shantensu-richi-java":
            for (int i = 0; i < Integer.parseInt(Flags.get(Flags.GAMES)); i++) {
                Socket socket = new Socket(
                        Flags.get(Flags.SERVER), Integer.parseInt(Flags.get(Flags.PORT)));
                ShantensuRichiClient client = new ShantensuRichiClient(socket);
                client.run();
            }
            break;
        // TODO: Handle other clients.
        default:
            throw new IllegalArgumentException();
        }
    }
}
