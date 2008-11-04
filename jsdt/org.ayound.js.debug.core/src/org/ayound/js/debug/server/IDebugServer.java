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

import java.net.URL;

import org.ayound.js.debug.engine.IJsEngine;
import org.ayound.js.debug.resource.JsResourceManager;

public interface IDebugServer {
	public static final String DEBUG_PATH = "jsdebug.debug";

	/**
	 * get the remote homepage url
	 * 
	 * @return
	 */
	public URL getRemoteBaseUrl();

	/**
	 * get base url
	 * 
	 * @return
	 */
	public String getLocalBaseUrl();

	/**
	 *  get the javascript engine
	 * @return
	 */
	public IJsEngine getJsEngine();
	/**
	 * get resources of this server
	 * @return
	 */
	public String[] getResources();
	/**
	 * add resource to this server
	 * @param resource
	 */
	public void addResource(String resource);
	/**
	 * get javascript resource manager
	 * @return
	 */
	public JsResourceManager getJsResourceManager();

	/**
	 *  get the port of this socket server
	 * @return
	 */
	public int getPort();

	public boolean isRunning();
	
	public String getDefaultEncoding();
	
	public void setDefaultEncoding(String defaultEncoding);
}
