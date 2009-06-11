/*******************************************************************************
 * $Header$
 * $Revision$
 * $Date$
 *
 *==============================================================================
 *
 * Copyright (c) 2001-2006 Primeton Technologies, Ltd.
 * All rights reserved.
 *
 * Created on May 20, 2009
 *******************************************************************************/


package org.ayound.js.debug.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;


public class ValueUtil {
	private static JsValue value = null;

	private static JsDebugStackFrame frame = null;

	synchronized public static void updateValue(String string){
		if(value!=null){
			IVariable[] vars = VariableUtil.createVarsByObject(string, frame.getDebugTarget(), frame.getLaunch(), frame,value.getParentVar());
			if(vars == null){
				vars = new IVariable[]{};
			}
			value.setVariables(vars);
		}
	}

	synchronized public static void getUpdateValue(JsValue value, JsDebugStackFrame frame) {
		ValueUtil.value = value;
		ValueUtil.frame = frame;
		List<String> pathList = new ArrayList<String>();
		JsVariable var = value.getParentVar();
		try {
			pathList.add(var.getName());
		} catch (DebugException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(var!=null){
			while(var.getParentVar()!=null){
				var = var.getParentVar();
				try {
					pathList.add(var.getName());
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			StringBuffer buffer = new StringBuffer();
			for(int i=pathList.size()-1;i>-1;i--){
				if(i==pathList.size()-1){
					buffer.append(pathList.get(i));
				}else{
					buffer.append("[\"" + pathList.get(i) + "\"]");
				}
			}
			String path = buffer.toString();
			if(path.length()>0){
				frame.writeValue(path);
			}else{
				value.setVariables(new IVariable[]{});
			}
		}else{
			value.setVariables(new IVariable[]{});
		}
	}

	public static JsDebugStackFrame getStackFrame() {
		// TODO Auto-generated method stub
		return frame;
	}
}
