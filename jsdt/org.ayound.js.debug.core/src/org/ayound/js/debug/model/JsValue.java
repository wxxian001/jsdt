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

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.swt.widgets.Display;
/**
 * jsValue is a class of javascript varible
 *
 */
public class JsValue implements IValue {
	private IVariable[] variables;

	private String referenceTypeName;

	private IDebugTarget target;

	private ILaunch launch;

	private JsDebugStackFrame frame;

	private String valueString;

	private JsVariable parentVar;

	public JsValue(IDebugTarget target, ILaunch launch,JsDebugStackFrame frame) {
		super();
		this.target = target;
		this.launch = launch;
		this.frame = frame;
	}

	public void setVariables(IVariable[] variables) {
		this.variables = variables;
	}

	public void setReferenceTypeName(String referenceTypeName) {
		this.referenceTypeName = referenceTypeName;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	public String getReferenceTypeName() throws DebugException {
		return this.referenceTypeName;
	}

	public String getValueString() throws DebugException {
		return this.valueString;
	}

	synchronized  public IVariable[] getVariables() throws DebugException {
		if(this.variables!=null){
			return this.variables;
		}else{
			if(frame.isExecuted()||frame.isTerminated()){
				return new IVariable[]{};
			}
			Display.getDefault().syncExec(new Runnable(){

				public void run() {
					ValueUtil.getUpdateValue(JsValue.this,frame);
				}});
			while(this.variables==null){
				//do nothing
			}
			return this.variables;
		}
	}

	public boolean hasVariables() throws DebugException {
		if("object".equals(this.getReferenceTypeName())){
			if(this.variables==null){
				return true;
			}else{
				return this.variables.length > 0;
			}
		}else{
			return false;
		}
	}

	public boolean isAllocated() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public IDebugTarget getDebugTarget() {
		return this.target;
	}

	public ILaunch getLaunch() {
		return this.launch;
	}

	public String getModelIdentifier() {
		return JsDebugCorePlugin.MODEL_ID;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public JsVariable getParentVar() {
		return parentVar;
	}

	public void setParentVar(JsVariable parentVar) {
		this.parentVar = parentVar;
	}

}
