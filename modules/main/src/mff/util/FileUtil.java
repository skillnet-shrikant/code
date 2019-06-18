package mff.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
	
	private FileUtil () { }
	
	public static boolean fileExists(String filePath) {
		if (StringUtil.isEmpty(filePath))
				return false;
		
		File file = new File(filePath);
		return file.exists();	
	}
	
	public static boolean createEmptyFile(String pFilePathWithFileName) throws IOException {
    File lFile = new File(pFilePathWithFileName);
    return lFile.createNewFile();
  }
	
	public static boolean renameFile(String fromFilePath, String toFilePath) throws IOException {
		File fromFile = new File(fromFilePath);
		File toFile = new File(toFilePath);
		return fromFile.renameTo(toFile);
	}
	
	public static boolean deleteFile(String filePath) throws IOException {
		File file = new File(filePath);
		return file.delete();
	}
	
	
	public static void copyFile(String fromFilePath, String toFilePath) throws IOException  {
		copyFile(new File(fromFilePath), new File(toFilePath));
	}
	
	public static void copyFile(File fromFile, File toFile) throws IOException  {
		FileChannel inChannel = new FileInputStream(fromFile).getChannel();
		FileChannel outChannel = new FileOutputStream(toFile).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}

	
	public static BufferedReader getBufferedFileReader(String filePath) throws IOException {
		return getBufferedFileReader(new File(filePath));	
	}
	
	public static BufferedReader getBufferedFileReader(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		return bufferedReader;	
	}
	
	public static BufferedWriter getBufferedFileWriter(String filePath) throws IOException {
		return getBufferedFileWriter(filePath, false);
	}
	
	public static BufferedWriter getBufferedFileWriter(String filePath, boolean append) throws IOException {
		return getBufferedFileWriter(new File(filePath), append);
	}
	
	public static BufferedWriter getBufferedFileWriter(File file) throws IOException {
		return getBufferedFileWriter(file, false);
	}
	
	public static BufferedWriter getBufferedFileWriter(File file, boolean append) throws IOException {
		FileWriter fileWriter = new FileWriter(file, append);
		BufferedWriter  bufferedWriter = new BufferedWriter(fileWriter);
		return bufferedWriter;
	}
	
}