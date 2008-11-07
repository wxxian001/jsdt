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
import org.ayound.js.debug.model.JsErrorStackFrame;
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

	private String defaultEncoding;

	private int debugLine = 0;

	private String homePage = null;

	public JsDebugServer(ILaunch launch, ServerSocket socketServer,
			URL remoteUrl, JsResourceManager jsManager) {
		super();
		this.launch = launch;
		this.serverSocket = socketServer;
		this.port = socketServer.getLocalPort();
		this.remoteUrl = remoteUrl;
		this.jsManager = jsManager;

	}

	/**
	 * every JsConnectionThread handle a http request
	 * 
	 */
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

	/**
	 * when the server stop ,it remove all the resources
	 * 
	 */
	public void stop() {
		for (String resource : resources) {
			JsDebugCorePlugin.getDefault().removeResource(resource, this);
		}
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

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public void compileError(String errorMsg, String resorce, int line) {
		JsErrorStackFrame frame = new JsErrorStackFrame(this.thread,
				this.target, this.launch);
		frame.setResource(resorce);
		frame.setLineNum(line);
		frame.setErrorMsg(errorMsg);
		this.thread.addStackFrame(frame);
	}

	public void setDebugLine(int line) {
		this.debugLine = line;

	}

	public void setHomePage(String resource) {
		this.homePage = resource;
	}

	public int getDebugLine() {
		return debugLine;
	}

	public String getHomePage() {
		return homePage;
	}

}
