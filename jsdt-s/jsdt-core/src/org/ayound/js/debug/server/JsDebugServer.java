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
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.ayound.js.debug.core.ResourceListenerManager;
import org.ayound.js.debug.engine.EngineManager;
import org.ayound.js.debug.engine.IJsEngine;
import org.ayound.js.debug.model.DebugInfo;

public class JsDebugServer{
	private static ServerSocket serverSocket;

	private static boolean running = false;

	private static int port;

	private static URL remoteUrl;

	private static IJsEngine jsEngine;

	private static String defaultEncoding;

	private static int debugLine = 0;

	private static Set<String> htmlPage = new HashSet<String>();

	private static Set<String> resources = new HashSet<String>();

	/**
	 * every JsConnectionThread handle a http request
	 *
	 */
	protected static void startRun() {
		(new Thread() {

			@Override
			public void run() {
				while (JsDebugServer.isRunning()) {
					try {
						Socket conn = JsDebugServer.serverSocket.accept();
						new JsConnectionThread(conn);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	public static boolean isRunning() {
		return running;
	}

	public static int getPort() {
		return port;
	}

	/**
	 * when the server stop ,it remove all the resources
	 *
	 */
	public static void stop() {
		running = false;
		try {
			if(serverSocket!=null){
				serverSocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static String getLocalBaseUrl() {
		return "http://localhost:" + port + "";
	}

	public static URL getRemoteBaseUrl() {
		return remoteUrl;
	}

	public static void start(DebugInfo info,ServerSocket socketServer) {
		serverSocket = socketServer;
		port = socketServer.getLocalPort();
		try {
			String url = info.getUrl();
			if (url.startsWith("http") || url.startsWith("file")) {
				remoteUrl = new URL(url.replace(":///", "://"));
			} else {
				remoteUrl = new URL("file://" + url);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}

	public static boolean start() {
		try {
			jsEngine = EngineManager.getEngine();
		} catch (Exception e) {
			return false;
		}
		running = true;
		startRun();
		return true;
	}

	public static String getDefaultEncoding() {
		return defaultEncoding;
	}

	public static void setDefaultEncoding(String defaultEncoding1) {
		defaultEncoding = defaultEncoding1;
	}


	public static void addHtmlPage(String resource) {
		htmlPage.add(resource);
	}

	public static boolean isHtmlPage(String resource) {
		return htmlPage.contains(resource);
	}

	public static IJsEngine getJsEngine() {
		return jsEngine;
	}

	public static int getDebugLine() {
		return debugLine;
	}

	public static void setDebugLine(int debugLine) {
		JsDebugServer.debugLine = debugLine;
	}

	public static void compileError(String message, String scriptPath, int lineNumber) {
		// TODO Auto-generated method stub

	}

	public static void addResource(String resource) {
		resources.add(resource);
		ResourceListenerManager.addResource(resource);
	}

	public static Set<String> getResources() {
		// TODO Auto-generated method stub
		return resources;
	}



}
