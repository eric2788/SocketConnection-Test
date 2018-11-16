package com.annoymous.file;

import java.io.File;
import java.io.IOException;

public class FileCreate {
	public static boolean create(){
		File folder = new File(System.getProperty("user.home")+"/desktop/study/important-shit");
		String[] files = {"A","B","C","D","E","F","G","H",
				"I","J","K","L","M","N","O","P","Q","R","S",
				"T","U","V","W","X","Y","Z"};
		if (!folder.mkdir()) return false;
		for (String filename : files) {
			File file = new File(folder+"/"+filename);
			try {
				if (!file.createNewFile()) return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
