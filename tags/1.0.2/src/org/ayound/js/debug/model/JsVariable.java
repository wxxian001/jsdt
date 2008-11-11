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

public class JsVariable extends JsDebugElement implements IVariable {

	private String name;

	private String referenceTypeName;

	private IValue value;

	private IDebugTarget target;

	private ILaunch launch;

	public JsVariable(String name, IDebugTarget target, ILaunch launch) {
		super(target, launch);
		this.name = name;
		this.target = target;
		this.launch = launch;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReferenceTypeName(String referenceTypeName) {
		this.referenceTypeName = referenceTypeName;
	}

	public String getName() throws DebugException {
		return this.name;
	}

	public String getReferenceTypeName() throws DebugException {
		return this.referenceTypeName;
	}

	public IValue getValue() throws DebugException {
		return this.value;
	}

	public boolean hasValueChanged() throws DebugException {
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

	public void setValue(String expression) throws DebugException {
		// TODO Auto-generated method stub

	}

	public void setValue(IValue value) throws DebugException {
		this.value = value;

	}

	public boolean supportsValueModification() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

}
