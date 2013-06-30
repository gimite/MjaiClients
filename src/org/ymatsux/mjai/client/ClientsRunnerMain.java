package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;

public class ClientsRunnerMain {

    public static void main(String[] args) {
        Flags.parse(args);
        for (int i = 0; i < 4; i++) {
            new Thread() {
                @Override
                public void run() {
                    Socket socket;
                    try {
                        socket = new Socket(
                                Flags.get(Flags.SERVER), Integer.parseInt(Flags.get(Flags.PORT)));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.exit(1);
                        return;
                    }
                    ShantensuClient client;
                    try {
                        client = new ShantensuClient(socket);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.exit(1);
                        return;
                    }
                    try {
                        client.run();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.exit(1);
                        return;
                    }
                }
            }.start();
        }
    }
}
