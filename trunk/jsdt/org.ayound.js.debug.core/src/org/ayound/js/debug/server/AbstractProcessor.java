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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.debug.core.model.IThread;

public abstract class AbstractProcessor implements IServerProcessor {

	private String requestUrl;

	private String postData;

	private JsDebugResponse response;

	private IThread thread;

	private IDebugServer server;

	public AbstractProcessor(String requestUrl, String postData, JsDebugResponse response, IThread thread, IDebugServer server) {
		super();
		this.requestUrl = requestUrl;
		this.postData = postData;
		this.response = response;
		this.thread = thread;
		this.server = server;
	}

	abstract public void process();

	public String getPostData() {
		return this.postData;
	}

	public String getRequestUrl() {
		return this.requestUrl;
	}

	public JsDebugResponse getResponse() {
		return this.response;
	}

	public IDebugServer getServer() {
		return this.server;
	}

	public IThread getThread() {
		return this.thread;
	}
	public URL computeRemoteURL(){
		String path = this.requestUrl.replace(this.server.getLocalBaseUrl(), "");
		try {
			return new URL(this.server.getRemoteBaseUrl(),path);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
