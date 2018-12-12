package com.managepay.admin.byod.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;

public class ByodUtil {
	
	@Value("${upload-path}")
	private static String filePath;

	public static String createRandomBackendId(String typePrefix, int length) {
		String possibleChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyss");
		String dateSuffix = dateFormat.format(date);
			
		while(builder.length()<length) {
			int index = (int)(random.nextFloat()*possibleChar.length());
			builder.append(possibleChar.charAt(index));
		}
		String generatedString = builder.toString();
		return typePrefix + generatedString + dateSuffix;
	}
	
	
	

/*	public static String saveImageFile(String base64_img, String existing) {
		boolean checker = false;
		String uploadPath = filePath;
		String imageName = UserUtil.randomString(8);
		String[] splitString = base64_img.split(",");
		byte[] imageBytes = Base64.getDecoder().decode(splitString[1]);
		
		try {
			File checkdir = new File(uploadPath);
			checkdir.mkdirs();
			
			if (existing != null) {
				File tempfile = new File(uploadPath + existing);
				tempfile.delete();
			}
	
			do {
				File checkFile = new File(uploadPath + imageName);
				if (checkFile.exists()) {
					checker = true;
					imageName = UserUtil.randomString(8);
				} else {
					checker = false;
				}
			} while (checker);

			File file = new File(uploadPath, imageName + ".png");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(imageBytes);
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return imageName + ".png";
	}*/
	
	
	
	
}
