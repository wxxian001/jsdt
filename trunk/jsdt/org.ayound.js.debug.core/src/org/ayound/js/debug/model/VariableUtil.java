/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * All rights reserved.
 * 
 * Created on 2008-10-26
 *******************************************************************************/
package org.ayound.js.debug.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class VariableUtil {
	public static IVariable[] createVarsByObject(JSONObject rootJson,
			IDebugTarget target, ILaunch launch) {
		if (rootJson != null) {
			List<JsVariable> vars = new ArrayList<JsVariable>();
			JSONArray names = rootJson.names();
			if(names==null){
				return new IVariable[]{};
			}
			for (int i = 0; i < names.length(); i++) {
				Object nameObj;
				try {
					nameObj = names.get(i);
					if (nameObj != null) {
						String name = nameObj.toString();
						JsVariable var = new JsVariable(name, target, launch);
						Object value = rootJson.get(name);
						String type = getJsonType(value);
						var.setReferenceTypeName(type);
						JsValue jsValue = new JsValue(target, launch);
						jsValue.setValueString(value.toString());
						jsValue.setReferenceTypeName(type);
						if(value instanceof JSONObject){
							jsValue.setVariables(createVarsByObject((JSONObject)value,target,launch));
						}else if(value instanceof JSONArray){
							jsValue.setVariables(createVarsByArray(name, (JSONArray)value, target, launch));
						}
						var.setValue(jsValue);
						vars.add(var);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return vars.toArray(new JsVariable[vars.size()]);
		}
		return null;

	}

	public static IVariable[] createVarsByArray(String arrayName,
			JSONArray jsonArray, IDebugTarget target, ILaunch launch) {
		if (jsonArray != null) {
			List<JsVariable> vars = new ArrayList<JsVariable>();
			for (int i = 0; i < jsonArray.length(); i++) {
				Object value;
				try {
					value = jsonArray.get(i);
					if (value != null) {
						String name = arrayName + "[" + i + "]";
						JsVariable var = new JsVariable(name, target, launch);
						String type = getJsonType(value);
						var.setReferenceTypeName(type);
						JsValue jsValue = new JsValue(target, launch);
						jsValue.setValueString(value.toString());
						jsValue.setReferenceTypeName(type);
						if(value instanceof JSONObject){
							jsValue.setVariables(createVarsByObject((JSONObject)value,target,launch));
						}else if(value instanceof JSONArray){
							jsValue.setVariables(createVarsByArray(name, (JSONArray)value, target, launch));
						}
						var.setValue(jsValue);
						vars.add(var);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return vars.toArray(new JsVariable[vars.size()]);
		}
		return null;

	}

	private static String getJsonType(Object value) {
		if (value instanceof String) {
			return ("string");
		} else if (value instanceof Integer || value instanceof Double
				|| value instanceof Long) {
			return ("number");
		} else if (value instanceof Boolean) {
			return ("boolean");
		} else if (value == null) {
			return ("object");
		} else if (value instanceof JSONObject) {
			return ("object");
		} else if (value instanceof JSONArray) {
			return ("array");
		}
		return "unknown";
	}
}
