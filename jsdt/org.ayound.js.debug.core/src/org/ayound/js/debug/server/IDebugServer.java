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

	public IJsEngine getJsEngine();

	public String[] getResources();

	public void addResource(String resource);

	public JsResourceManager getJsResourceManager();

	public int getPort();
}
