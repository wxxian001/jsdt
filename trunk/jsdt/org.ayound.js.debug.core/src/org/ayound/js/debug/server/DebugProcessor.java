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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.ayound.js.debug.model.JsDebugStackFrame;
import org.ayound.js.debug.model.JsDebugThread;
import org.ayound.js.debug.model.JsErrorStackFrame;
import org.ayound.js.debug.model.VariableUtil;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.json.JSONException;

public class DebugProcessor extends AbstractProcessor {

	public DebugProcessor(String requestUrl, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server,
			Map<String, String> requestHeader) {
		super(requestUrl, postData, response, thread, server, requestHeader,null);
	}

	public void process() {
		getResponse().writeHTMLHeader("UTF-8");
		JsDebugParam param = null;
		try {
			param = new JsDebugParam(getPostData());
		} catch (JSONException e) {
			getResponse().close();
			return;
		}
		IThread thread = getThread();
		if (thread instanceof JsDebugThread) {
			JsDebugThread jsThread = (JsDebugThread) thread;
			if ("START".equalsIgnoreCase(param.getCommand())
					|| "RESUME".equalsIgnoreCase(param.getCommand())) {
				getResponse().writeResume();
				getResponse().close();
			} else if ("STEPOVER".equalsIgnoreCase(param.getCommand())
					|| "STEPINTO".equalsIgnoreCase(param.getCommand())
					|| "STEPRETURN".equalsIgnoreCase(param.getCommand())) {

				JsDebugStackFrame frame = new JsDebugStackFrame(getThread(),
						getThread().getDebugTarget(), getThread().getLaunch());
				frame.setResponse(getResponse());
				frame.setResource(param.getJsResource());
				frame.setLineNum(param.getLine());
				frame.setVariables(VariableUtil.createVarsByObject(param
						.getJsonStack(), thread.getDebugTarget(), thread
						.getLaunch()));
				jsThread.addStackFrame(frame);
			} else if ("ERROR".equalsIgnoreCase(param.getCommand())) {
				JsErrorStackFrame frame = new JsErrorStackFrame(getThread(),
						getThread().getDebugTarget(), getThread().getLaunch());
				frame.setResponse(getResponse());
				String resource = param.getJsResource();
				if(resource!=null){
					resource =resource.replace(getServer().getLocalBaseUrl(), "");
					frame.setResource(resource);
				}
				if (getServer().isHtmlPage(param.getJsResource())) {
					frame.setLineNum(param.getLine()
							- getServer().getDebugLine());
				} else {
					frame.setLineNum(param.getLine());
				}
				
				try {
					frame.setErrorMsg(URLDecoder.decode(param.getError(),
							"utf-8"));
				} catch (UnsupportedEncodingException e) {
					frame.setErrorMsg(param.getError());
				}
				jsThread.addStackFrame(frame);
				getResponse().writeResume();
				getResponse().close();
			} else if ("BREAKPOINT".equalsIgnoreCase(param.getCommand())) {
				JsDebugStackFrame frame = new JsDebugStackFrame(getThread(),
						getThread().getDebugTarget(), getThread().getLaunch());
				frame.setResponse(getResponse());
				frame.setResource(param.getJsResource());
				frame.setLineNum(param.getLine());
				frame.setVariables(VariableUtil.createVarsByObject(param
						.getJsonStack(), thread.getDebugTarget(), thread
						.getLaunch()));
				jsThread.addStackFrame(frame);
			} else if("EXPRESSION".equalsIgnoreCase(param.getCommand())){
				try {
					IStackFrame frame = jsThread.getTopStackFrame();
					if(frame instanceof JsDebugStackFrame){
						JsDebugStackFrame jsFrame = (JsDebugStackFrame)frame;
						jsFrame.setResponse(getResponse());
						jsFrame.finishExpression(param.getExpression(), param.getResult(), param.getError());
					}
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				getResponse().writeResume();
				getResponse().close();
			}
		} else {
			getResponse().close();
		}
	}

}
