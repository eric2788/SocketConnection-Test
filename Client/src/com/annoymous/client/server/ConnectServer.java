package com.annoymous.client.server;

import java.io.IOException;
import java.net.Socket;

public class ConnectServer {
    private String host;
    private int port;
    private Socket socket;


    public ConnectServer(String host,int port){
        this.host = host;
        this.port = port;
    }

    public Socket getSocket(){
        try {
            socket = new Socket(host,port);
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
