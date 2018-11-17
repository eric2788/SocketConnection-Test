package com.annoymous.client.cmd;

import com.annoymous.client.server.ConnectServer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Command extends Thread{
    @Override
    public void run() {
        Socket client;
        try {
            String host;
            int port;
            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println(">> 請輸入 IP: (輸入q退出)");
                host = scanner.nextLine();
                if (host.equalsIgnoreCase("q")) break;
                System.out.println(">> 請輸入 Port: ");
                port = scanner.nextInt();
                client = new ConnectServer(host, port).getSocket();
                BufferedReader reader;
                PrintWriter writer;
                if (client != null) {
                    System.out.println(">> Connected successfully");
                } else {
                    System.out.println(">> Cannot connect to that server");
                    return;
                }
                while (true) {
                    writer = new PrintWriter(client.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                    Scanner scanner1 = new Scanner(System.in);
                    String input = scanner1.nextLine();
                    if (input.equalsIgnoreCase("end")) {
                        System.out.println(">> Exited.");
                        break;
                    }
                    if (input.isEmpty()) continue;
                    writer.println(input);
                    writer.flush();
                    if (input.equals("shutdown")) {
                        System.out.println(">> Server has been shut down.");
                        break;
                    }
                    String s = reader.readLine();
                    if (s == null) {
                        System.out.println(">> server disconnected.");
                        break;
                    }
                    System.out.println(">> Output: ");
                    System.out.println("   "+s);
                    while (reader.ready()) {
                        System.out.println("   "+reader.readLine());
                    }
                }
                reader.close();
                writer.close();
                client.close();
            }
        } catch (IOException e) {
            System.out.println("Error: "+e.getMessage());
            System.out.println(">> Cannot connect to that server");
        }
    }
}
