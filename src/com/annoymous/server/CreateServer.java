package com.annoymous.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CreateServer{
    private ServerSocket socket;
    private static CreateServer instance;

    public static CreateServer getInstance(int port) throws IOException {
        if (instance == null) instance = new CreateServer(port);
        return instance;
    }


    private CreateServer(int port) throws IOException {
        socket = new ServerSocket(port);
    }


    public Socket getSocket() {
        try {
            System.out.println("Listening port " + socket.getLocalPort() + "....");
            Socket server = socket.accept();
            System.out.println("Remote IP：" + server.getRemoteSocketAddress());
            return server;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
