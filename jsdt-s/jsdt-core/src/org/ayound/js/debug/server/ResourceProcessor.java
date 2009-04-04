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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.ayound.js.debug.engine.CharsetDetector;

public class ResourceProcessor extends AbstractProcessor {

	private String method;

	public ResourceProcessor(String requestUrl, String method, String postData,
			JsDebugResponse response, Map<String, String> requestHeader,
			ResponseInfo info) {
		super(requestUrl, postData, response, requestHeader, info);
		this.method = method;
	}

	@Override
	public void process() {
		try {
			URL url = this.computeRemoteURL();

			DataInputStream isResult = new DataInputStream(getInfo()
					.getInputStream());
			File file = File.createTempFile("temp", "tmp");
			FileOutputStream outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = isResult.read(buffer)) != -1) {
				outputStream.write(buffer, 0, i);
			}
			outputStream.flush();
			outputStream.close();
			isResult.close();
			outputStream = null;
			String encoding = getInfo().getEncoding();
			if (encoding == null) {
				CharsetDetector detector = new CharsetDetector();
				detector.detect(file);
				encoding = detector.getCharset();
			}
			FileInputStream inputStream = new FileInputStream(file);
			getResponse().writeOtherHeader(url.getFile(), encoding,
					getInfo().getResponseHeader(), inputStream.available());
			if (getInfo().getContentType() != null
					&& getInfo().getContentType().startsWith("text/")) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, encoding));
				String line = null;
				while ((line = reader.readLine()) != null) {
					getResponse().writeln(line, encoding);
				}
				reader.close();
			} else {
				byte[] bytes = new byte[inputStream.available()];
				inputStream.read(bytes);
				getResponse().getOutPutStream().write(bytes);
			}
			inputStream.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getResponse().close();
		}
	}

}
