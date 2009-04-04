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
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * every connection thread handle a http request the ProcessorFactory determine
 * different processor to handle the request
 * 
 */
public class JsConnectionThread extends Thread {
	private Socket connection;

	public JsConnectionThread(Socket conn) {
		this.connection = conn;
		this.start();
	}

	@Override
	public void run() {
		try {
			int contentLength = 0;// the content length of client request
			Map<String, String> requestHeader = new HashMap<String, String>(); // the
			// //
			// client
			// request
			if (this.connection != null) {
				try {
					// first open inputstream
					BufferedReader in = new BufferedReader(
							new InputStreamReader(this.connection
									.getInputStream(), "utf-8"));

					// read the first line of url
					String line = in.readLine();
					String resource = line.substring(line.indexOf('/'), line
							.lastIndexOf('/') - 5);
					// decode the url and get real path
					resource = URLDecoder.decode(resource,
							"utf-8");
					// get request method
					String method = new StringTokenizer(line).nextElement()
							.toString();

					// read all the head info
					while ((line = in.readLine()) != null) {
						if (line.equals("")) {
							// the header end
							break;
						} else {
							// read the length of post data
							if (line.startsWith("Content-Length")) {
								try {
									contentLength = Integer.parseInt(line
											.substring(line.indexOf(':') + 1)
											.trim());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							String[] headLines = line.split(":");
							if (headLines.length == 2) {
								requestHeader.put(headLines[0], headLines[1]);
							}
						}
					}
					StringBuffer buffer = new StringBuffer();
					// read the post data to buffer
					if ("POST".equalsIgnoreCase(method) && (contentLength > 0)) {
						for (int i = 0; i < contentLength; i++) {
							buffer.append((char) in.read());
						}
					}

					JsDebugResponse response = new JsDebugResponse(
							this.connection.getOutputStream(), this.connection);
					IServerProcessor processor = ProcessorFactory
							.createProcessor(resource, method, buffer
									.toString(), response, requestHeader);
					if (processor != null) {
						processor.process();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
