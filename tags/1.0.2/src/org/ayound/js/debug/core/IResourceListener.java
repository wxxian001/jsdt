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


package org.ayound.js.debug.core;

import org.ayound.js.debug.server.IDebugServer;
/**
 * the event is fired by debug server
 * if you want to register the event,
 * you can call JsDebugCorePlugin.getDefault().addResourceListener
 * to add listener and removeResourceListener to remove listener
 *
 */
public interface IResourceListener {
	/**
	 * add resource by javascript debug server.
	 * when the server handle the http request,
	 * it create a resource and fire addResource event.
	 * @param resource
	 * @param server
	 */
	public void addResource(String resource,IDebugServer server);
	/**
	 * remove resource by javascript debug server.
	 * when the debug thread is terimated,
	 * it remove a resource and fire removeResource event.
	 * @param resource
	 * @param server
	 */
	public void removeResource(String resource,IDebugServer server);
}
