/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * All rights reserved.
 * 
 * Created on 2008-10-26
 *******************************************************************************/


package org.ayound.js.debug.core;

import org.ayound.js.debug.server.IDebugServer;

public interface IResourceListener {

	public void addResource(String resources,IDebugServer server);
}
