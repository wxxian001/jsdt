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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.debug.core.model.IThread;

/**
 *
 * every connection thread handle a http request the ProcessorFactory determine
 * different processor to handle the request
 *
 */
public class JsConnectionThread extends Thread {
	private Socket connection;

	private IThread thread;

	private IDebugServer server;

	public JsConnectionThread(Socket conn, IThread thread, IDebugServer server) {
		this.connection = conn;
		this.thread = thread;
		this.server = server;
		this.start();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src
							.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src
							.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	@Override
	public void run() {
		try {
			int contentLength = 0;// the content length of client request
			Map<String, String> requestHeader = new HashMap<String, String>(); // the
			// header
			// of
			// client
			// request
			String postData = "";
			if (this.connection != null) {
				InputStream is = connection.getInputStream();

				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				;

				// 获得HTTP的请求信息
				String requestLine = br.readLine();
				StringTokenizer tokens = new StringTokenizer(requestLine);
				String method = tokens.nextToken();

				String resource = requestLine.substring(requestLine
						.indexOf('/'), requestLine.lastIndexOf('/') - 5);
				// 显示此请求信息
				String recieved;
				while ((recieved = br.readLine()) != null
						&& recieved.length() != 0) {
					if (recieved.trim().equals("")) {
						break;
					}
					if (recieved.toLowerCase().startsWith("content-length:")
							&& recieved.length() > 16) {
						contentLength = Integer
								.parseInt(recieved.substring(16));
					}
				}
				StringBuffer buffer = new StringBuffer();
				if ("POST".equalsIgnoreCase(method) && (contentLength > 0)) {
					for (int i = 0; i < contentLength; i++) {
						buffer.append((char) br.read());
					}
					postData = unescape(buffer.toString());
				}
				JsDebugResponse response = new JsDebugResponse(this.connection
						.getOutputStream(), this.connection, this.server
						.getJsResourceManager());
				IServerProcessor processor = ProcessorFactory.createProcessor(
						resource, method, postData, response, this.thread,
						this.server, requestHeader);
				if (processor != null) {
					processor.process();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IDebugServer getServer() {
		return this.server;
	}
}
