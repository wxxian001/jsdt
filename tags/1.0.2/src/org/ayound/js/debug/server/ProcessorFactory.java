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
	 * create processor by different url
	 * "/" or baseUrl to create Homepage
	 * *.js to create script processor
	 * debugurl to create jsdebug processor
	 * other to create resource processor
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
			IThread thread, IDebugServer server,Map<String, String> requestHeader) {
		if(resource.equals("/")){
			return  new HomePageProcessor(resource, postData, response,
					thread, server,requestHeader);
		}
		if (resource.startsWith("/")) {
			resource = server.getLocalBaseUrl() + resource;
		}
		String debugUrl = server.getLocalBaseUrl() + "/"
				+ IDebugServer.DEBUG_PATH;
		if (resource.startsWith(debugUrl)) {
			return new DebugProcessor(resource, postData, response, thread,
					server,requestHeader);
		} else {
			String remotePath = resource.replace(server.getLocalBaseUrl(), "");
			try {
				URL remoteFile = new URL(server.getRemoteBaseUrl(), remotePath);
				if (remoteFile.getFile().toLowerCase().endsWith("js")) {
					return new ScriptProcessor(resource, postData, response,
							thread, server,requestHeader);
				} else if (remoteFile.equals(server.getRemoteBaseUrl())) {
					return new HomePageProcessor(resource, postData, response,
							thread, server,requestHeader);
				} else {
					return new ResourceProcessor(resource, method, postData,
							response, thread, server,requestHeader);
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
