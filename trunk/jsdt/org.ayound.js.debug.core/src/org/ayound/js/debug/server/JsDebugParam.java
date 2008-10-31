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

public class JsDebugParam {

	private static final String RESOURCE = "RESOURCE";

	private static final String LINE = "LINE";

	private static final String COMMAND = "COMMAND";

	private static final String STACK = "STACK";

	private String jsResource;

	private String command;

	private JSONObject jsonStack;

	private int line;

	public JsDebugParam(String jsonString) {
		try {
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
//			System.out.println("yes");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
