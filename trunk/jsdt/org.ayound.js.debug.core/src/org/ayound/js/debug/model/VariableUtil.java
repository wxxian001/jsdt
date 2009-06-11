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
import org.json.JSONTokener;

/**
 *
 * VariableUtil is used to create IVaraibles from json
 *
 */
public class VariableUtil {
	/**
	 * create varible by jsonObject
	 * @param rootJson
	 * @param target
	 * @param launch
	 * @return
	 */
	public static IVariable[] createVarsByObject(JSONObject rootJson,
			IDebugTarget target, ILaunch launch,JsDebugStackFrame frame,JsVariable parentVar) {
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
						JSONObject value = rootJson.getJSONObject(name);
						String type = value.getString("type");
						var.setReferenceTypeName(type);
						JsValue jsValue = new JsValue(target,launch,frame);
						if(value.has("value")){
							jsValue.setValueString(value.getString("value"));
						}else{
							jsValue.setValueString("[unkown]");
						}
						jsValue.setReferenceTypeName(type);
						jsValue.setParentVar(var);
						var.setParentVar(parentVar);
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
	public static IVariable[] createVarsByObject(String jsonString,
			IDebugTarget target, ILaunch launch,JsDebugStackFrame frame,JsVariable parentVar){
		JSONObject rootJson;
		try {
			rootJson = new JSONObject(new JSONTokener(jsonString));
			return createVarsByObject(rootJson, target, launch, frame,parentVar);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
