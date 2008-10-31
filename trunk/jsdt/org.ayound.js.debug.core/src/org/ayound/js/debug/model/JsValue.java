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

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class JsValue implements IValue {
	private IVariable[] variables;

	private String referenceTypeName;

	private IDebugTarget target;

	private ILaunch launch;

	private String valueString;

	public JsValue(IDebugTarget target, ILaunch launch) {
		super();
		this.target = target;
		this.launch = launch;
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

	public IVariable[] getVariables() throws DebugException {
		return this.variables;
	}

	public boolean hasVariables() throws DebugException {
		return this.variables != null && this.variables.length > 0;
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
		// TODO Auto-generated method stub
		return null;
	}

}
