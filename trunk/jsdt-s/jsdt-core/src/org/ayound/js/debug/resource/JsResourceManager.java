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
package org.ayound.js.debug.resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.ayound.js.debug.server.ResponseInfo;
import org.ayound.js.debug.util.PathUtil;

/**
 *
 * JsResourceManager is used to manager javascript files create file ,remove
 * file ,or create temp project and folder
 *
 */
public class JsResourceManager {

	public JsResourceManager(String port) {
		super();
	}

	private static File getTempFolder() {
		File folder = new File(PathUtil.getJsdtHome());
		folder = new File(folder, "jsdebug");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder.getAbsoluteFile();
	}

	public static void main(String[] args) {
		System.out.println(PathUtil.getJsdtHome());
	}

	public static String getResourceRealPath(String resource) {
		if (resource.endsWith("/")) {
			resource = resource + "_homePage";
		}
		File file = new File(getTempFolder(), resource);
		return file.getAbsolutePath().replace('\\', '/');
	}

	/**
	 * create file by resource path,and write stream to the file
	 *
	 * @param resourcePath
	 * @param isResult
	 */
	public static void createFile(String resourcePath, InputStream isResult,
			String encoding) {
		String realPath = getResourceRealPath(resourcePath);
		createParentFolder(realPath);
		if("gzip".equals(encoding)){
			writeGzip2File(new File(realPath),isResult);
			return;
		}

		try {
			OutputStream out = new FileOutputStream(realPath);
			byte buf[] = new byte[1024];
			int len;
			while ((len = isResult.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			isResult.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void writeGzip2File(File file, InputStream inputStream){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Transfer bytes from the compressed stream to the output stream
        byte[] buf;
		try {
			inputStream = new GZIPInputStream(inputStream);
			buf = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			FileUtils.writeByteArrayToFile(file, out.toByteArray());
			// Close the file and stream
			inputStream.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createParentFolder(String realPath) {
		File parent = new File(realPath).getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
	}

	public static void createFile(String resourcePath, ResponseInfo info) {
		createFile(resourcePath, info.getInputStream(),info.getEncoding());
	}


}
