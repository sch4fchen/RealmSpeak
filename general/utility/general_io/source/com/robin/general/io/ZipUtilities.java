package com.robin.general.io;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.*;

public class ZipUtilities {
	private static final int BUFFER = 2048;
	
	public static String lastError;

	/**
	 * @param zipFile		The file you want unzipped.  The files will be unzipped to the same directory.
	 * 
	 * @return				An array of File objects, describing what was unzipped
	 */
	public static File[] unzip(File zipFile) {
		lastError = null;
		File[] files = null;
		try {
			ArrayList<File> fileList = new ArrayList<>();
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
//				System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				
				// write the file to disk, in the same directory as zipFile
				String filePath = FileUtilities.getFilePathString(zipFile,false,false)+entry.getName();
				fileList.add(new File(filePath));
				FileOutputStream fos = new FileOutputStream(filePath);
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
			files = fileList.toArray(new File[fileList.size()]);
		} catch (Exception e) {
			lastError = e.toString();
			e.printStackTrace();
		}
		return files;
	}
	public static void zip(File zipFile, File[] files) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFile);
			ZipOutputStream out =
				new ZipOutputStream(new BufferedOutputStream(dest));
			out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];

			for (int i = 0; i < files.length; i++) {
//				System.out.println("Adding: " + files[i].getPath());
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(files[i].getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}