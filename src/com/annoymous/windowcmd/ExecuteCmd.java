package com.annoymous.windowcmd;

import com.annoymous.file.FileCreate;
import com.annoymous.file.FileDelete;
import com.annoymous.server.CreateServer;

import java.io.*;
import java.net.Socket;
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
		try {
			boolean wincmd = false;
			//boolean lincmd = false;
			String dir = "";
			Socket server = new CreateServer(4399).getSocket();
			if (server == null) {
				System.out.println("Create not create socket for server, terminated.");
				return;
			}
			BufferedReader reader;
			PrintWriter writer;
			System.out.println(">> server connection started.");
			while(true) {
				reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
				writer = new PrintWriter(server.getOutputStream());
				String inputtxt = reader.readLine();
				if (inputtxt == null) {
					System.out.println("Client "+server.getInetAddress()+":"+server.getLocalPort()+" disconnected.");
					break;
				}
				if (inputtxt.isEmpty()) continue;
				System.out.println("received command: "+inputtxt);
				if(inputtxt.equalsIgnoreCase("done")) {
					writer.println("exited");
					writer.flush();
					server.close();
					break;
				}
				switch(inputtxt) {
					case "wincmd":
						wincmd = !wincmd;
						//lincmd = false;
						writer.println("window cmd mode switched "+(wincmd ? "on" : "off"));
						writer.flush();
						continue;
				/*case "lincmd":
					lincmd = !lincmd;
					wincmd = false;
					System.out.println("linux cmd mode switched "+(lincmd ? "on" : "off"));
					continue;*/
					case "switchdir":
						writer.println("Type a dir name (or leave blank to use default)");
						dir = reader.readLine();
						writer.println(!dir.isEmpty() ? "you used custom dir" : "you used default dir");
						writer.println("done");
						writer.flush();
						continue;
					case "jvmmemory":
						Runtime run = Runtime.getRuntime();
						writer.println("Max Memory: "+run.maxMemory()/1000000 +"MB");
						writer.println("Used Memory: "+ (run.totalMemory() - run.freeMemory())/1000000 + "/" + run.totalMemory()/1000000 +"MB");
						writer.println("Free Memory: "+ run.freeMemory()/1000000 +"MB");
						writer.flush();
						continue;
					case "properties":
						for (Object key : System.getProperties().keySet()) {
							writer.println(key + ": " + System.getProperties().get(key));
						}
						writer.flush();
						continue;
					case "filecreate":
						FileCreate.create();
						continue;
					case "filedelete":
						FileDelete.delete();
						continue;
				}
				String[] cmd = inputtxt.split(" ");
				List<String> regularcmd = new ArrayList<>();
				if (wincmd) {
					regularcmd.add("cmd");
					regularcmd.add("/c");
				}/*else if (lincmd){
				regularcmd.add("command");
			}*/
				regularcmd.addAll(Arrays.asList(cmd));
				ProcessBuilder pb = wincmd ? new ProcessBuilder(regularcmd) : new ProcessBuilder(cmd);
				pb.directory(!dir.isEmpty() ? new File(dir) : new File(System.getProperty("user.dir")));
				pb.redirectErrorStream(true);
				Process process = pb.start();
				BufferedReader Input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader Error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String s;
				while ((s = Input.readLine()) != null) {
					writer.println(s);
				}
				String e;
				while((e = Error.readLine()) != null) {
					writer.println(e);
				}
				writer.flush();
			}
            writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println("command prompt restart in 5secs.....");
			//new ScheduledThreadPoolExecutor(0).schedule(this,5, TimeUnit.SECONDS);
		}
	}
}