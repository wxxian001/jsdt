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
 * Created on 2009-3-27
 *******************************************************************************/

package org.ayound.js.debug.util;

import java.util.ArrayList;
import java.util.List;

import org.ayound.js.debug.model.Variable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * VariableUtil is used to create IVaraibles from json
 *
 */
public class VariableUtil {
	/**
	 * create varible by jsonObject
	 *
	 * @param rootJson
	 * @param target
	 * @param launch
	 * @return
	 */
	public static Variable[] createVarsByObject(JSONObject rootJson) {
		if (rootJson != null) {
			List<Variable> vars = new ArrayList<Variable>();
			JSONArray names = rootJson.names();
			if (names == null) {
				return new Variable[] {};
			}
			for (int i = 0; i < names.length(); i++) {
				Object nameObj;
				try {
					nameObj = names.get(i);
					if (nameObj != null) {
						String name = nameObj.toString();
						Variable var = new Variable();
						var.setName(name);
						Object value = rootJson.get(name);
						String type = getJsonType(value);
						var.setType(type);
						var.setValue(value.toString());
						if (value instanceof JSONObject) {
							var
									.setVars(createVarsByObject((JSONObject) value));
						} else if (value instanceof JSONArray) {
							var.setVars(createVarsByArray(name,
									(JSONArray) value));
						}
						vars.add(var);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return vars.toArray(new Variable[vars.size()]);
		}
		return null;

	}

	/**
	 * create varibles by jsonarray
	 *
	 * @param arrayName
	 * @param jsonArray
	 * @param target
	 * @param launch
	 * @return
	 */
	public static Variable[] createVarsByArray(String arrayName,
			JSONArray jsonArray) {
		if (jsonArray != null) {
			List<Variable> vars = new ArrayList<Variable>();
			for (int i = 0; i < jsonArray.length(); i++) {
				Object value;
				try {
					value = jsonArray.get(i);
					if (value != null) {
						String name = arrayName + "[" + i + "]";
						Variable var = new Variable();
						var.setName(name);
						String type = getJsonType(value);
						var.setType(type);
						var.setValue(value.toString());
						var.setType(type);
						if (value instanceof JSONObject) {
							var
									.setVars(createVarsByObject((JSONObject) value));
						} else if (value instanceof JSONArray) {
							var.setVars(createVarsByArray(name,
									(JSONArray) value));
						}
						vars.add(var);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return vars.toArray(new Variable[vars.size()]);
		}
		return null;

	}

	/**
	 * get javascript varible type
	 *
	 * @param value
	 * @return
	 */
	private static String getJsonType(Object value) {
		if (value instanceof String) {
			return ("string");
		} else if (value instanceof Integer || value instanceof Double
				|| value instanceof Long) {
			return ("number");
		} else if (value instanceof Boolean) {
			return ("boolean");
		} else if (value == null) {
			return ("null");
		} else if (value instanceof JSONObject) {
			return ("[Object]");
		} else if (value instanceof JSONArray) {
			return ("[Array]");
		}
		return "unknown";
	}
}