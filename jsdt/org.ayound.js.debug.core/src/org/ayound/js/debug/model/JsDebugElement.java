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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;

public class JsDebugElement extends DebugElement {

	private boolean terminated = false;

	private ILaunch launch;

	public JsDebugElement(IDebugTarget target, ILaunch launch) {
		super(target);
		this.launch = launch;
	}

	public IBreakpoint[] getBreakpoints() {
		return null;
	}

	public boolean isTerminated() {
		return this.terminated;
	}

	public void setTerminated(boolean terminate) {
		this.terminated = terminate;
	}

	public String getModelIdentifier() {
		return JsDebugCorePlugin.MODEL_ID;
	}

	@Override
	public ILaunch getLaunch() {
		return this.launch;
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

}
