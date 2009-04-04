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
 * Created on 2008-11-1
 *******************************************************************************/

package org.ayound.js.debug.server;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketServerUtil {
	/**
	 * create socket server by default port
	 * 
	 * @param port
	 * @return
	 * @throws IOException 
	 */
	public static ServerSocket createSocketServer(int port) throws IOException {
		return new ServerSocket(port);
	}
}
