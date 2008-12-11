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

import org.eclipse.debug.core.model.IThread;

public class ProcessorFactory {
	/**
	 * create processor by different url "/" or baseUrl to create Homepage *.js
	 * to create script processor debugurl to create jsdebug processor other to
	 * create resource processor
	 * 
	 * @param resource
	 * @param method
	 * @param postData
	 * @param response
	 * @param thread
	 * @param server
	 * @param requestHeader
	 * @return
	 */
	public static IServerProcessor createProcessor(String resource,
			String method, String postData, JsDebugResponse response,
			IThread thread, IDebugServer server,
			Map<String, String> requestHeader) {
		String debugUrl = "/" + IDebugServer.DEBUG_PATH;
		if (resource.startsWith(debugUrl)) {
			return new DebugProcessor(resource, postData, response, thread,
					server, requestHeader);
		} else {
			URL url = computeRemoteURL(resource, server);
			ResponseInfo info = ProcesserUtil.getResponseInfo(url, method,
					postData, requestHeader);
			String contentType = info.getContentType();
			if(info.getResponseHeader()!=null){
				if(info.getResponseHeader().containsKey("Location")){
					response.writeHTMLHeader(null, info.getResponseHeader());
					response.close();
					return null;
				}
			}
			if (contentType!=null && contentType.indexOf("text/javascript")>-1) {
				return new ScriptProcessor(resource, postData, response,
						thread, server, requestHeader, info);
			} else if (contentType!=null && contentType.indexOf("text/html")>-1) {
				return new HtmlPageProcessor(resource, postData, response,
						thread, server, requestHeader, info);
			} else {
				return new ResourceProcessor(resource, method, postData,
						response, thread, server, requestHeader, info);
			}

		}
	}

	/**
	 * compute the remote url. the request url maybe
	 * http://localhost:8080/test/a.html the method convert it to
	 * http://www.site.com/test/a.html
	 * 
	 * @param resource
	 * @param server
	 * @return
	 */
	private static URL computeRemoteURL(String resource, IDebugServer server) {
		String path = resource.replace(server.getLocalBaseUrl(), "");
		try {
			return new URL(server.getRemoteBaseUrl(), path);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
