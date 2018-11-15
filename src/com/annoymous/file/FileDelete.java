package com.annoymous.file;

import java.io.File;
import java.util.Objects;

public class FileDelete {
	private static File folder = new File(System.getProperty("user.home")+"/desktop/study/important-shit");
	public static void delete() {
		for (File file : Objects.requireNonNull(folder.listFiles())) {
			file.delete();
		}
		System.out.println("Delete "+ (folder.delete() ? "success" : "failed"));
	}
}
