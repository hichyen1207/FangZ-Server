package com.server.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

public class Test {
	public static void main(String[] args) throws MalformedURLException, IOException {
		byte[] imageBytes = IOUtils.toByteArray(new URL("http://album.s3.hicloud.net.tw/28199B/A.JPG?t=1488368005"));
		String base64 = Base64.getEncoder().encodeToString(imageBytes);
		System.out.println(base64);

		byte[] bytes = Base64.getDecoder().decode(base64);

		File image = new File("/Users/Hao/Desktop/image.jpg");
		FileOutputStream fos = new FileOutputStream(image);
		fos.write(bytes);
		fos.flush();
		fos.close();
	}
}
