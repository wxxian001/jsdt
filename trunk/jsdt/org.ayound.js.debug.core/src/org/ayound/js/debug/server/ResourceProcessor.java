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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.eclipse.debug.core.model.IThread;

public class ResourceProcessor extends AbstractProcessor {

	private String method;

	public ResourceProcessor(String requestUrl, String method, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server,Map<String, String> requestHeader) {
		super(requestUrl, postData, response, thread, server,requestHeader);
		this.method = method;
	}

	@Override
	public void process() {
		try {
			URL url = this.computeRemoteURL();
			getResponse().writeOtherHeader(url.getFile());
			DataInputStream isResult = new DataInputStream(ProcesserUtil
					.getResponseInfo(url, method, getPostData(),this.getRequestHeader()).getInputStream());
			byte[] bytes = new byte[isResult.available()];
			isResult.readFully(bytes);
			getResponse().getOutPutStream().write(bytes);
			isResult.close();
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
