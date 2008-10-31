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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.engine.EngineManager;
import org.ayound.js.debug.engine.IJsEngine;
import org.ayound.js.debug.model.JsDebugTarget;
import org.ayound.js.debug.model.JsDebugThread;
import org.ayound.js.debug.resource.JsResourceManager;
import org.eclipse.debug.core.ILaunch;

public class JsDebugServer implements IDebugServer {
	ServerSocket serverSocket;

	private boolean running = false;

	private ILaunch launch;

	private int port;

	private JsDebugTarget target;

	private JsDebugThread thread;

	private URL remoteUrl;

	private IJsEngine jsEngine;
	
	private JsResourceManager jsManager;

	private Set<String> resources = new HashSet<String>();

	public JsDebugServer(ILaunch launch, int port, URL remoteUrl,JsResourceManager jsManager) {
		super();
		this.launch = launch;
		this.port = port;
		this.remoteUrl = remoteUrl;
		this.jsManager = jsManager;

	}

	protected void startRun() {
		(new Thread() {

			@Override
			public void run() {
				while (JsDebugServer.this.isRunning()) {
					try {
						Socket conn = JsDebugServer.this.serverSocket.accept();
						new JsConnectionThread(conn, JsDebugServer.this
								.getThread(), JsDebugServer.this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	public boolean isRunning() {
		return this.running;
	}

	public ILaunch getLaunch() {
		return this.launch;
	}

	public int getPort() {
		return this.port;
	}

	public void stop() {
		jsManager.clear();
		this.running = false;
		try {
			this.serverSocket.close();
			JsDebugCorePlugin.getDefault().removePort(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JsDebugThread getThread() {
		return this.thread;
	}

	public JsDebugTarget getTarget() {
		return this.target;
	}

	public String getLocalBaseUrl() {
		return "http://localhost:" + this.port + "";
	}

	public URL getRemoteBaseUrl() {
		return this.remoteUrl;
	}

	public boolean start() {
		try {
			this.serverSocket = new ServerSocket(this.port);
			this.target = new JsDebugTarget(this, this.launch);
			this.thread = new JsDebugThread(this.target, this.launch);
			this.target.addThread(this.thread);
			this.launch.addDebugTarget(this.target);
			this.jsEngine = EngineManager.getEngine();
		} catch (Exception e) {
			return false;
		}
		this.running = true;
		startRun();
		return true;
	}

	public IJsEngine getJsEngine() {
		return jsEngine;
	}

	public String[] getResources() {
		return resources.toArray(new String[resources.size()]);
	}

	public void addResource(String resource) {
		resources.add(resource);
	}

	public JsResourceManager getJsResourceManager() {
		// TODO Auto-generated method stub
		return jsManager;
	}

}
