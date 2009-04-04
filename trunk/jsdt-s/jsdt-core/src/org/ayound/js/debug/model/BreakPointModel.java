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

package org.ayound.js.debug.model;

import java.io.File;

public class BreakPointModel {
	private String resourcePath;
	private String resourceName;
	private int lineNumber;
	private boolean enabled;
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public BreakPointModel(String resourcePath, int lineNumber, boolean enabled) {
		super();
		this.resourcePath = resourcePath;
		this.resourceName = new File(resourcePath).getName();
		this.lineNumber = lineNumber;
		this.enabled = enabled;
	}
	public String toString(){
		return this.resourceName + "[line: "+ (this.lineNumber + 1) + "]";
	}
}
