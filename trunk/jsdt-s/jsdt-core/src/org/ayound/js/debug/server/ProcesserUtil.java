/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * This program and the accompanying materials
 * are made available under the terms of the Apache License 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.List;
import java.util.Map;

/**
 * the tool calss of processor
 */
public class ProcesserUtil {
	/**
	 * get remote inputstram
	 *
	 * @param url
	 * @param method
	 * @param postData
	 * @param requestHeader
	 * @return
	 */
	public static ResponseInfo getResponseInfo(URL url, String method,
			String postData, Map<String, String> requestHeader) {
		InputStream stream = null;
		String encoding = null;
		String contentType = null;
		Map<String ,List<String>> responseHeader = null;
		if (method == null) {
			method = "GET";
		}
		if ("file".equalsIgnoreCase(url.getProtocol())) {
			String filePath = url.toString().substring(5);
			int paramOffset = filePath.indexOf('?');
			if(paramOffset>0){
				filePath = filePath.substring(0, paramOffset);
			}
			if(filePath.toLowerCase().endsWith(".js")){
				contentType = "text/javascript";
			}else if(filePath.toLowerCase().endsWith(".htm")||filePath.toLowerCase().endsWith(".html")){
				contentType = "text/html";
			}
			try {
				stream = new FileInputStream(filePath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("http".equalsIgnoreCase(url.getProtocol())
				|| "https".equalsIgnoreCase(url.getProtocol())) {
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setInstanceFollowRedirects(false);
				for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
				conn.setRequestMethod(method);
				if (method.equalsIgnoreCase("POST")) {
					conn.getOutputStream().write(postData.getBytes());
					conn.getOutputStream().flush();
				}
				String fileName = url.getFile();
				int offsetParam = fileName.indexOf('?');
				if(offsetParam>0){
					fileName = fileName.substring(0,offsetParam);
				}
				if(fileName.toLowerCase().endsWith(".js")){
					contentType = "text/javascript";
				}else{
					contentType = conn.getContentType();
				}
				encoding = conn.getContentEncoding();
				stream = conn.getInputStream();
				responseHeader = conn.getHeaderFields();
//				if("gzip".equals(encoding)){
//					stream = new GZIPInputStream(stream);
//					encoding = "UTF-8";
//				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ResponseInfo(encoding, stream, contentType,responseHeader);
	}

}
