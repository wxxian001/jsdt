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
import java.io.PrintWriter;
import java.net.Socket;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.model.JsBreakPoint;
import org.ayound.js.debug.resource.JsResourceManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
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
	/**
	 * write html header of this request
	 * @param encoding
	 */
	public void writeHTMLHeader(String encoding) {
		out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
		if (encoding == null) {
			out.println("Content-Type:text/html;");
		}else{			
			out.println("Content-Type:text/html;charset=" + encoding);
		}
		out.println();// 根据 HTTP 协议, 空行将结束头信息
	}
	/**
	 * write javascript response header of this request
	 * @param encoding
	 */
	public void writeJsHeader(String encoding) {
		out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
		if (encoding == null) {
			out.println("Content-Type:text/javascript;");
		}else{			
			out.println("Content-Type:text/javascript;charset=" + encoding);
		}
		out.println();// 根据 HTTP 协议, 空行将结束头信息
	}
	/**
	 * write other header.
	 * the method will set response header accroding to different file
	 * @param fileName
	 */
	public void writeOtherHeader(String fileName) {
		fileName = fileName.toLowerCase();
		out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
		if (fileName.endsWith("gif") || fileName.endsWith("jpg")
				|| fileName.equals("bmp") || fileName.endsWith("png")) {
			out.println("image/*");
		} else if (fileName.endsWith("css")) {
			out.println("text/css");
		}
		out.println();// 根据 HTTP 协议, 空行将结束头信息
	}
	/**
	 * write string to client
	 * @param str
	 */
	public void write(String str) {
		if (!this.client.isClosed()) {
			this.out.write(str);
		}
	}
	/**
	 * write line to client
	 * @param str
	 */
	public void writeln(String str) {
		if (!this.client.isClosed()) {
			this.out.write(str + "\n");
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
	 * write resume to client
	 * the method will write breakpoints to client
	 */
	public void writeResume() {
		StringBuffer buffer = new StringBuffer(
				"{COMMAND:'BREAKPOINT',BREAKPOINTS:{");
		IBreakpointManager manager = DebugPlugin.getDefault()
				.getBreakpointManager();
		for (IBreakpoint point : manager
				.getBreakpoints(JsDebugCorePlugin.MODEL_ID)) {
			if (point instanceof JsBreakPoint) {
				String resource = jsManager.getResourceByFile((IFile) point
						.getMarker().getResource());
				int line = point.getMarker().getAttribute(IMarker.LINE_NUMBER,
						0);
				buffer.append("'").append(resource).append(line).append(
						"':true,");
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
}
