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

import java.util.Map;

import org.ayound.js.debug.model.DebugStackFrame;
import org.ayound.js.debug.util.DebugManager;
import org.ayound.js.debug.util.VariableUtil;
import org.json.JSONException;

public class DebugProcessor extends AbstractProcessor {

	public DebugProcessor(String requestUrl, String postData,
			JsDebugResponse response,
			Map<String, String> requestHeader) {
		super(requestUrl, postData, response,requestHeader, null);
	}

	public void process() {
		getResponse().writeHTMLHeader("UTF-8", null);
		JsDebugParam param = null;
		try {
			param = new JsDebugParam(getPostData());
		} catch (JSONException e) {
			getResponse().close();
			return;
		}
		if ("START".equalsIgnoreCase(param.getCommand())
				|| "RESUME".equalsIgnoreCase(param.getCommand())) {
			getResponse().writeResume();
			getResponse().close();
		} else if ("STEPOVER".equalsIgnoreCase(param.getCommand())
				|| "STEPINTO".equalsIgnoreCase(param.getCommand())
				|| "STEPRETURN".equalsIgnoreCase(param.getCommand())) {

			DebugStackFrame frame = new DebugStackFrame();
			frame.setResponse(getResponse());
			frame.setResource(param.getJsResource());
			frame.setLineNumber(param.getLine());
			frame
					.setVars(VariableUtil.createVarsByObject(param
							.getJsonStack()));
			DebugManager.addStackFrame(frame);
		} else if ("ERROR".equalsIgnoreCase(param.getCommand())) {
			DebugStackFrame frame = new DebugStackFrame();
			frame.setResponse(getResponse());
			String resource = param.getJsResource();
			int errorLine = param.getLine();
			if (resource != null) {
				resource = resource.replace(JsDebugServer.getLocalBaseUrl(), "");
			}
			if (param.isIE()) {
				if (param.getErrorFunc() != null) {
					String[] jsLines = JsDebugServer.getJsEngine()
							.getScriptLines(resource);
					boolean isRightResource = false;
					int index = param.getLine() - 1;
					if (jsLines != null) {
						if (JsDebugServer.isHtmlPage(resource)) {
							index = index - JsDebugServer.getDebugLine();
						} else {
							index = index - 1;
						}
						if (index > -1 && index < jsLines.length) {
							String errLineStr = jsLines[index];
							if (param.getErrorFunc().indexOf(errLineStr.trim()) >= 0) {
								isRightResource = true;
								errorLine = index + 1;
							}
						}
					}
					if (!isRightResource) {
						for (String res : JsDebugServer.getResources()) {
							index = param.getLine() - 1;
							if (JsDebugServer.isHtmlPage(res)) {
								index = index - JsDebugServer.getDebugLine();
							} else {
								index = index - 1;
							}
							jsLines = JsDebugServer.getJsEngine().getScriptLines(
									res);
							if (jsLines != null && index > -1
									&& index < jsLines.length) {
								if (param.getErrorFunc().indexOf(
										jsLines[index].trim()) >= 0) {
									resource = res;
									errorLine = index + 1;
									break;
								}
							}
						}
					}
				} else {
					if (JsDebugServer.isHtmlPage(resource)) {
						errorLine = errorLine - JsDebugServer.getDebugLine();
					} else {
						errorLine = errorLine + 1;
					}
				}
			} else {
				if (JsDebugServer.isHtmlPage(resource)) {
					errorLine = errorLine - JsDebugServer.getDebugLine();
				}
			}
			frame.setLineNumber(errorLine);
			frame.setResource(resource);

			// try {
			// frame.setErrorMsg(URLDecoder.decode(param.getError(),
			// "utf-8"));
			// } catch (UnsupportedEncodingException e) {
			// frame.setErrorMsg(param.getError());
			// }
			DebugManager.addStackFrame(frame);
			getResponse().writeResume();
			getResponse().close();
		} else if ("BREAKPOINT".equalsIgnoreCase(param.getCommand())) {
			DebugStackFrame frame = new DebugStackFrame();
			frame.setResponse(getResponse());
			frame.setResource(param.getJsResource());
			frame.setLineNumber(param.getLine());
			frame
					.setVars(VariableUtil.createVarsByObject(param
							.getJsonStack()));
			DebugManager.addStackFrame(frame);
		} else if ("EXPRESSION".equalsIgnoreCase(param.getCommand())) {
			 DebugStackFrame frame = DebugManager.getCurrentFrame();
			 frame.setResponse(getResponse());
			 frame.finishExpression(param.getExpression(), param
			 .getResult(), param.getError());
		} else {
			getResponse().writeResume();
			getResponse().close();
		}

	}
}
