/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * All rights reserved.
 * 
 * Created on 2008-10-26
 *******************************************************************************/
package org.ayound.js.debug.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProcesserUtil {
	public static InputStream getInputStream(URL url,String method,String postData){
		if(method==null){
			method = "GET";
		}
		if("file".equalsIgnoreCase(url.getProtocol())){
			String filePath = url.toString().substring(5);
			try {
				return new FileInputStream(filePath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if("http".equalsIgnoreCase(url.getProtocol())||"https".equalsIgnoreCase(url.getProtocol())){			
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("enctype", "multipart/form-data");
				conn.setRequestProperty("contentType", "charset=utf-8");
				conn.setRequestMethod(method);
				if(method.equalsIgnoreCase("POST")){
					conn.getOutputStream().write(postData.getBytes());
				}
				return conn.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
