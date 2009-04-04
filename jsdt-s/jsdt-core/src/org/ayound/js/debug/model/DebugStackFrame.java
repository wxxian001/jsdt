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

import java.util.HashMap;
import java.util.Map;

import org.ayound.js.debug.server.JsDebugResponse;

public class DebugStackFrame {
	private String resource;

	private int lineNumber;

	private Variable[] vars;

	private JsDebugResponse response;

	private Map<String, ExpressionModel> expressions = new HashMap<String, ExpressionModel>();

	private boolean terminated = false;

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Variable[] getVars() {
		return vars;
	}

	public void setVars(Variable[] vars) {
		this.vars = vars;
	}


	public void setResponse(JsDebugResponse response) {
		this.response = response;
	}

	public JsDebugResponse getResponse() {
		return response;
	}
	public String toString(){
		if(terminated){
			return "<executed>[line: " + lineNumber + "]" + resource;
		}else{
			return "[line: " + lineNumber + "]" + resource;
		}
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	public void addExpression(ExpressionModel model){
		expressions.put(model.getExpression(), model);
	}

	public ExpressionModel getExpressionModel(String expression){
		return expressions.get(expression);
	}

	public void finishExpression(String expression, String result, String error) {
		ExpressionModel model = new ExpressionModel(expression,result,error);
		this.addExpression(model);
		ExpressionManager.addExpression(expression);
	}

	public Map<String, ExpressionModel> getExpressions() {
		return expressions;
	}
}
