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
import java.util.Map;
/**
 * 
 * this class is used to define some common field
 *
 */
public abstract class AbstractProcessor implements IServerProcessor {

	private String requestUrl;

	private String postData;

	private JsDebugResponse response;

	private Map<String, String> requestHeader;
	
	private ResponseInfo info;
	
	public AbstractProcessor(String requestUrl, String postData, JsDebugResponse response,Map<String, String> requestHeader,ResponseInfo info) {
		super();
		this.requestUrl = requestUrl;
		this.postData = postData;
		this.response = response;
		this.requestHeader = requestHeader;
		this.info = info;
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

	/**
	 * compute the remote url.
	 * the request url maybe http://localhost:8080/test/a.html
	 * the method convert it to http://www.site.com/test/a.html
	 * @return
	 */
	public URL computeRemoteURL(){
		String path = this.requestUrl.replace(JsDebugServer.getLocalBaseUrl(), "");
		try {
			return new URL(JsDebugServer.getRemoteBaseUrl(),path);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getRequestHeader() {
		return this.requestHeader;
	}

	public ResponseInfo getInfo() {
		return info;
	}
}
