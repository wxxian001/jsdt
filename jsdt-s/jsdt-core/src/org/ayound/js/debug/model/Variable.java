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

public class Variable {

	private String name;

	private String type;

	private String value;

	private Variable[] vars;

	public Variable[] getVars() {
		return vars;
	}

	public void setVars(Variable[] vars) {
		this.vars = vars;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Variable(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Variable() {
		super();
	}
	public String toString(){
		return this.name;
	}
}
