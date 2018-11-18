package com.annoymous.windowcmd;

import com.annoymous.file.FileCreate;
import com.annoymous.file.FileDelete;
import com.annoymous.main.Main;
import com.annoymous.server.CreateServer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecuteCmd extends Thread {
	@Override
	public void run() {
        while(true) {
            try {
                boolean wincmd = false;
                //boolean lincmd = false;
                StringBuilder dir = new StringBuilder();
                Socket server = CreateServer.getInstance(4399).getSocket();
                if (server == null) {
                    System.out.println("Cannot not create socket for server, terminated.");
                    return;
                }
                BufferedReader reader;
                PrintWriter writer;
                System.out.println(">> server connection started.");
                while (true) {
                    boolean restart = false;
                    int writes = 0;
                    reader = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.UTF_8));
                    writer = new PrintWriter(server.getOutputStream());
                    String inputtxt = reader.readLine();
                    if (inputtxt == null) {
                        System.out.println("Client " + server.getRemoteSocketAddress() +" disconnected.");
                        break;
                    }
                    if (inputtxt.isEmpty()) continue;
                    System.out.println("received command: " + inputtxt);
                    String[] cmdarray = inputtxt.split(" ");
                    if (cmdarray[0].equalsIgnoreCase("bd")) {
                        if (cmdarray.length == 1){
                            writer.println("too few argument.");
                            writer.flush();
                            continue;
                        }
                        switch (cmdarray[1]) {
                            case "wincmd":
                                wincmd = !wincmd;
                                //lincmd = false;
                                writer.println("window cmd mode switched " + (wincmd ? "on" : "off"));
                                writer.flush();
                                continue;
                            case "direct":
                                if (cmdarray.length == 2) dir = new StringBuilder();
                                else dir.append(cmdarray[2]);
                                writer.println(!dir.toString().isEmpty() ? "directory is now " + dir.toString() : "you used back the default dir");
                                writer.flush();
                                continue;
                            case "jvm":
                                Runtime run = Runtime.getRuntime();
                                writer.println("Max Memory: " + run.maxMemory() / 1000000 + "MB");
                                writer.println("Used Memory: " + (run.totalMemory() - run.freeMemory()) / 1000000 + "/" + run.totalMemory() / 1000000 + "MB");
                                writer.println("Free Memory: " + run.freeMemory() / 1000000 + "MB");
                                writer.flush();
                                continue;
                            case "properties":
                                for (Object key : System.getProperties().keySet()) {
                                    writer.println(key + ": " + System.getProperties().get(key));
                                }
                                writer.flush();
                                continue;
                            case "filecreate":
                                writer.println(FileCreate.create() ? "Success" : "Failed");
                                writer.flush();
                                continue;
                            case "filedelete":
                                writer.println(FileDelete.delete() ? "Success" : "Failed");
                                writer.flush();
                                continue;
                            case "end":
                            case "stop":
                                server.close();
                                System.out.println("Server successfully shutdown. bye!");
                                return;
                            case "wait":
                            case "sleep":
                                int time = 5;
                                if (cmdarray.length > 2) {
                                    try {
                                        Integer.parseInt(cmdarray[2]);
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }
                                    time = Integer.parseInt(cmdarray[2]);
                                }
                                Thread.sleep(time * 1000);
                                restart = true;
                                break;
                        }
                    }
                    List<String> cmd = new ArrayList<>();
                    if (wincmd) {
                        cmd.add("cmd");
                        cmd.add("/c");
                    }
                    cmd.addAll(Arrays.asList(cmdarray));
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.directory(!dir.toString().isEmpty() ? new File(dir.toString()) : new File(System.getProperty("user.dir")));
                    pb.redirectErrorStream(true);
                    try {
                        Process process = pb.start();
                        BufferedReader Input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        BufferedReader Error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String s;
                        //writer.println(pb.directory().getPath()+">>");
                        while ((s = Input.readLine()) != null) {
                            writer.println(s);
                            writes++;
                        }
                        String e;
                        while ((e = Error.readLine()) != null) {
                            writer.println(e);
                            writes++;
                        }
                        if (writes == 0) writer.println("(NO OUTPUT)");
                        writer.flush();
                    }catch (IOException e){
                        if (restart) {
                            restart = false;
                            break;
                        }
                        System.out.println("Error: "+e.getMessage());
                        writer.println("Error: Unknown command or directory.");
                        writer.println("Please check your command or directory whether it is valid");
                        writer.flush();
                    }
                }
                writer.close();
                reader.close();
            } catch (IOException | InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
}
