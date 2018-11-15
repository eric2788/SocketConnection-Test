package com.annoymous.file;

import java.io.File;
import java.io.IOException;

public class FileCreate {
	public static void create(){
		File folder = new File(System.getProperty("user.home")+"/desktop/study/important-shit");
		String[] files = {"A","B","C","D","E","F","G","H",
				"I","J","K","L","M","N","O","P","Q","R","S",
				"T","U","V","W","X","Y","Z"};
		folder.mkdir();
		for (String filename : files) {
			File file = new File(folder+"/"+filename);
			try {
				System.out.println("Create "+ (file.createNewFile() ? "success" : "failed"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
