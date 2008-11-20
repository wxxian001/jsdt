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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * this class is used to parse debug post data. the data mabye {
 * "RESOURCE":"/test/a.js", "LINE":9, "COMMAND":"STEPOVER", "STACK":{
 * "arg0":"test" } }
 * 
 */
public class JsDebugParam {

	private static final String RESOURCE = "RESOURCE";

	private static final String LINE = "LINE";

	private static final String COMMAND = "COMMAND";

	private static final String STACK = "STACK";

	private static final String ERROR = "ERROR";
	
	private static final String EXPRESSION = "EXPRESSION";
	
	private static final String RESULT = "RESULT";
	
	private static final String ERRORFUNC = "ERRORFUNC";
	
	private static final String ISIE = "ISIE";
	
	
	
	private String jsResource;

	private String command;

	private JSONObject jsonStack;

	private int line;

	private String error;
	
	private String expression;
	
	private String result;
	
	private String errorFunc;
	
	private boolean isIE = false;
	
	
	public JsDebugParam(String jsonString) throws JSONException {
		JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));
		if (jsonObject.has(RESOURCE)) {
			this.jsResource = jsonObject.getString(RESOURCE);
		}
		if (jsonObject.has(LINE)) {
			this.line = jsonObject.getInt(LINE);
		}
		if (jsonObject.has(STACK)) {
			this.jsonStack = jsonObject.getJSONObject(STACK);
		}
		if (jsonObject.has(COMMAND)) {
			this.command = jsonObject.getString(COMMAND);
		}
		if (jsonObject.has(ERROR)) {
			this.error = jsonObject.getString(ERROR);
		}
		if (jsonObject.has(EXPRESSION)) {
			this.expression = jsonObject.getString(EXPRESSION);
		}
		if (jsonObject.has(RESULT)) {
			this.result = jsonObject.getString(RESULT);
		}
		if (jsonObject.has(ERRORFUNC)) {
			this.errorFunc = jsonObject.getString(ERRORFUNC);
		}
		if (jsonObject.has(ISIE)) {
			this.isIE = jsonObject.getBoolean(ISIE);
		}
	}

	public String getJsResource() {
		return this.jsResource;
	}

	public void setJsResource(String flowResource) {
		this.jsResource = flowResource;
	}

	public int getLine() {
		return this.line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public JSONObject getJsonStack() {
		return this.jsonStack;
	}

	public String getError() {
		return error;
	}

	public String getExpression() {
		return expression;
	}

	public String getResult() {
		return result;
	}

	public String getErrorFunc() {
		return errorFunc;
	}

	public boolean isIE() {
		return isIE;
	}

}
