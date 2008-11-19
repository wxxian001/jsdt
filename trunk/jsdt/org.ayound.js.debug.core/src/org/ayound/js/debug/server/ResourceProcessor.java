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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.eclipse.debug.core.model.IThread;

public class ResourceProcessor extends AbstractProcessor {

	private String method;

	public ResourceProcessor(String requestUrl, String method, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server,
			Map<String, String> requestHeader, ResponseInfo info) {
		super(requestUrl, postData, response, thread, server, requestHeader,
				info);
		this.method = method;
	}

	@Override
	public void process() {
		try {
			URL url = this.computeRemoteURL();

			DataInputStream isResult = new DataInputStream(getInfo()
					.getInputStream());
			if (url.getFile().toLowerCase().endsWith("css")) {
				File file = new File("charset");
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
				getResponse().writeOtherHeader(url.getFile(), encoding);
				FileInputStream inputStream = new FileInputStream(file);
				byte[] bytes = new byte[inputStream.available()];
				inputStream.read(bytes);
				getResponse().getOutPutStream().write(bytes);
				inputStream.close();
			} else {
				getResponse().writeOtherHeader(url.getFile(), null);
				byte[] bytes = new byte[isResult.available()];
				isResult.read(bytes);
				getResponse().getOutPutStream().write(bytes);
				isResult.close();
			}
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
