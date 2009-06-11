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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ayound.js.debug.resource.JsResourceManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;

public class JsDebugResponse {
	private PrintWriter out;

	private Socket client;

	private OutputStream outPutStream;

	private JsResourceManager jsManager;

	public JsDebugResponse(OutputStream outPutStream, Socket client,
			JsResourceManager manager) {
		this.client = client;
		this.outPutStream = outPutStream;
		this.jsManager = manager;
		try {
			this.out = new PrintWriter(this.client.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isClosed() {
		return this.client.isClosed();
	}

	/**
	 * write html header of this request
	 *
	 * @param encoding
	 */
	public void writeHTMLHeader(String encoding,
			Map<String, List<String>> responseHeader) {

		if (responseHeader != null) {
			if(responseHeader.containsKey("Location")){
				out.println("HTTP/1.0 301 Moved Permanently");
			}else{
				out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
			}
			for (Map.Entry<String, List<String>> entry : responseHeader
					.entrySet()) {
				if (entry.getKey() != null) {
					if (!"Content-Length".equals(entry.getKey())) {
						String value = Arrays.toString(entry.getValue()
								.toArray());
						value = value.substring(1, value.length() - 1);
						if("Location".equals(entry.getKey())){
							String localUrl = getJsManager().getServer().getLocalBaseUrl();
							try {
								URL locationUrl = new URL(value);
								value = localUrl + locationUrl.getPath();
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


						}
						out.println(entry.getKey() + ":" + value);
					}
				}
			}
		} else {
			out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
			if (encoding == null) {
				out.println("Content-Type:text/html;");
			} else {
				out.println("Content-Type:text/html;charset=" + encoding);
			}
		}
		out.println();// 根据 HTTP 协议, 空行将结束头信息
	}

	/**
	 * write javascript response header of this request
	 *
	 * @param encoding
	 */
	public void writeJsHeader(String encoding) {
		out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
		if (encoding == null) {
			out.println("Content-Type:text/javascript;");
		} else {
			out.println("Content-Type:text/javascript;charset=" + encoding);
		}
		out.println();// 根据 HTTP 协议, 空行将结束头信息
	}

	/**
	 * write other header. the method will set response header accroding to
	 * different file
	 *
	 * @param fileName
	 */
	public void writeOtherHeader(String fileName, String encoding,
			Map<String, List<String>> responseHeader, int length) {
		fileName = fileName.toLowerCase();
		out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
		if(responseHeader!=null){
			for (Map.Entry<String, List<String>> entry : responseHeader.entrySet()) {
				String key = entry.getKey();
				if (key != null ) {
					String value = Arrays.toString(entry.getValue().toArray());
					value = value.substring(1, value.length() - 1);
					out.println(key + ":" + value);
				}
			}
		}else{
			if (fileName.endsWith("gif")) {
				out.println("Content-Type:image/gif");
			} else if(fileName.endsWith("png")){
				out.println("Content-Type:image/png");
			}else if(fileName.endsWith("jpg")||fileName.endsWith("jpeg")){
				out.println("Content-Type:image/jpeg");
			}else if(fileName.endsWith("bmp")){
				out.println("Content-Type:image/bmp");
			}
			else if (fileName.endsWith("css")) {
				out.println("Content-Type:text/css");
			}
		}

		out.println();// 根据 HTTP 协议, 空行将结束头信息
	}

	/**
	 * write string to client
	 *
	 * @param str
	 */
	public void write(String str) {
		if (!this.client.isClosed()) {
			this.out.write(str);
		}
	}

	/**
	 * write line to client
	 *
	 * @param str
	 */
	public void writeln(String str) {
		if (!this.client.isClosed()) {
			this.out.write(str + "\n");
		}
	}

	public void writeln(String str,String encoding){
		OutputStreamWriter writer = null;
		try {
			this.outPutStream.flush();
			writer = new OutputStreamWriter(this.outPutStream,encoding);
			writer.write(str + "\n");
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		this.out.close();
		try {
			this.outPutStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * write resume to client the method will write breakpoints to client
	 */
	public void writeResume() {
		StringBuffer buffer = new StringBuffer(
				"{COMMAND:'BREAKPOINT',BREAKPOINTS:{");
		IBreakpointManager manager = DebugPlugin.getDefault()
				.getBreakpointManager();
		for (IBreakpoint point : manager.getBreakpoints()) {
			try {
				if (point.isEnabled()) {
					String resource = jsManager.getResourceByFile((IFile) point
							.getMarker().getResource());
					int line = point.getMarker().getAttribute(
							IMarker.LINE_NUMBER, 0);
					buffer.append("'").append(resource).append(line).append(
							"':true,");
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		buffer.append("'end':false}}");
		this.write(buffer.toString());

	}

	/**
	 * write terminate command to client
	 *
	 */
	public void writeTerminate() {
		this.write("{COMMAND:'TERMINATE'}");

	}

	/**
	 * write stepover command to client
	 *
	 */
	public void writeStepOver() {
		this.write("{COMMAND:'STEPOVER'}");
	}

	/**
	 * write stepreturn command to client
	 *
	 */
	public void writeStepReturn() {
		this.write("{COMMAND:'STEPRETURN'}");
	}

	/**
	 * write stepinto command to client
	 *
	 */
	public void writeStepInTo() {
		this.write("{COMMAND:'STEPINTO'}");
	}

	public OutputStream getOutPutStream() {
		return outPutStream;
	}

	public JsResourceManager getJsManager() {
		return jsManager;
	}

	public void writeExpression(String expression) {
		this.write("{COMMAND:'EXPRESSION',\"EXPRESSION\":\""
				+ expression.replace("\"", "\\\"") + "\"}");
	}

	public void writeValue(String expression) {
		this.write("{COMMAND:'VALUE',\"EXPRESSION\":\""
				+ expression.replace("\"", "\\\"") + "\"}");
	}
}
