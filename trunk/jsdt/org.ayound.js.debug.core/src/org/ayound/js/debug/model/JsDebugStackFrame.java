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
import java.util.Stack;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.server.JsDebugResponse;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

/**
 * 
 * 
 */

public class JsDebugStackFrame extends JsDebugElement implements IStackFrame {

	private JsDebugResponse response;

	private IVariable[] variables;

	private boolean executed = false;

	private boolean terminated = false;

	private String resource;

	private int lineNum;

	private IThread thread;

	private int charStart = -1;

	private int charEnd = -1;

	private Stack<String> expressionStack = new Stack<String>();

	private boolean isExpression = false;

	private List<ExpressionModel> expressionResult = new ArrayList<ExpressionModel>();

	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}

	public void setCharStart(int charStart) {
		this.charStart = charStart;
	}

	public void setResponse(JsDebugResponse response) {
		this.response = response;
		if (!this.expressionStack.empty()) {
			runExpression();
		}
	}

	public JsDebugStackFrame(IThread thread, IDebugTarget target, ILaunch launch) {
		super(target, launch);
		this.thread = thread;
		this.expressionStack.addAll(((JsDebugThread) thread)
				.getDebugExpressions());
	}

	public int getCharEnd() throws DebugException {
		return this.charEnd;
	}

	public int getCharStart() throws DebugException {
		return this.charStart;
	}

	public int getLineNumber() throws DebugException {
		return this.lineNum;
	}

	public String getName() throws DebugException {
		if (this.executed) {
			return "<executed>" + this.resource + "[" + this.lineNum + "]";
		} else {
			return this.resource + "[" + this.lineNum + "]";
		}
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	public IThread getThread() {
		return this.thread;
	}

	public IVariable[] getVariables() throws DebugException {
		return this.variables;
	}

	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	public boolean hasVariables() throws DebugException {
		return (this.variables != null) && (this.variables.length > 0);
	}

	public boolean canStepInto() {
		return !this.executed && !this.terminated;
	}

	public boolean canStepOver() {
		return !this.executed && !this.terminated;
	}

	public boolean canStepReturn() {
		return !this.executed && !this.terminated;
	}

	public boolean isStepping() {
		return false;
	}

	public void stepInto() throws DebugException {
		this.executed = true;
		this.response.writeStepInTo();
		this.response.close();

	}

	public void stepOver() throws DebugException {
		this.executed = true;
		this.response.writeStepOver();
		this.response.close();
		fireResumeEvent(DebugEvent.STEP_OVER);

	}

	public void stepReturn() throws DebugException {
		this.executed = true;
		this.response.writeStepReturn();
		this.response.close();
	}

	public boolean canResume() {
		return !this.executed && !this.terminated;
	}

	public boolean canSuspend() {
		return false;
	}

	public boolean isSuspended() {
		return false;
	}

	public void resume() throws DebugException {
		this.executed = true;
		this.response.writeResume();
		this.response.close();
		fireResumeEvent(DebugEvent.RESUME);
	}

	public void suspend() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean canTerminate() {
		return !this.executed && !this.terminated;
	}

	public void terminate() throws DebugException {
		this.terminated = true;
		this.response.writeTerminate();
		this.response.close();
		this.getDebugTarget().terminate();
		this.getLaunch().terminate();
		fireTerminateEvent();
	}

	public void setExecuted(boolean b) {
		this.executed = b;

	}

	public String getResource() {
		return this.resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	@Override
	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	public void setVariables(IVariable[] variables) {
		this.variables = variables;
	}

	public void addExpression(String expression) {
		((JsDebugThread) thread).addExpression(expression);
		if (this.executed || this.terminated || this.response.isClosed()) {
			return;
		}
		if (!this.expressionStack.contains(expression)) {
			this.expressionStack.add(expression);
		}
		if (!this.isExpression) {
			runExpression();
		}
	}

	public void runExpression() {
		synchronized (this) {
			if (this.expressionStack.empty()) {
				this.isExpression = false;
			} else {
				this.isExpression = true;
				this.response.writeExpression(this.expressionStack.pop());
				this.response.close();
			}
		}
	}

	public void finishExpression(final String expression, final String result,
			final String error) {

		for (ExpressionModel model : expressionResult) {
			if (expression.equals(model.getExpression())) {
				expressionResult.remove(model);
				break;
			}
		}
		expressionResult.add(new ExpressionModel(expression, result, error));
		JsDebugCorePlugin.getDefault().updateEval(this);
		if (this.isExpression) {
			runExpression();
		}
	}

	public List<ExpressionModel> getExpressionResult() {
		return expressionResult;
	}

}
