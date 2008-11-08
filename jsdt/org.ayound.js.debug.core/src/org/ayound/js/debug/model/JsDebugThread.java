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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
/**
 * 
 *
 */

public class JsDebugThread extends JsDebugElement implements IThread {

	private IStackFrame[] stackFrames = new IStackFrame[] {};

	private Set<String> debugExpressions = new HashSet<String>();
	
	public JsDebugThread(IDebugTarget target, ILaunch launch) {
		super(target, launch);
	}

	public String getName() throws DebugException {
		IDebugTarget target =  getDebugTarget();
		if(target instanceof JsDebugTarget){			
			return "Thread [" + ((JsDebugTarget)target).getServer().getRemoteBaseUrl() + "]";
		}else{
			return "Thread ";
		}
	}

	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		return this.stackFrames;
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		if (this.hasStackFrames()) {
			return this.stackFrames[0];
		} else {
			return null;
		}
	}

	public void addExpression(String expression){
		debugExpressions.add(expression);
	}
	
	public void removeExpression(String expression){
		debugExpressions.remove(expression);
	}
	
	public boolean hasStackFrames() throws DebugException {
		return this.stackFrames != null && this.stackFrames.length > 0;
	}

	public boolean canResume() {
		try {
			if(this.getTopStackFrame()!=null){			
				return this.getTopStackFrame().canResume();
			}
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean canSuspend() {
		return false;
	}

	public boolean isSuspended() {
		return false;
	}

	public void resume() throws DebugException {
		if(this.getTopStackFrame()!=null){			
			this.getTopStackFrame().resume();
		}
	}

	public void suspend() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean canStepInto() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepOver() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepReturn() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStepping() {
		// TODO Auto-generated method stub
		return false;
	}

	public void stepInto() throws DebugException {
		// TODO Auto-generated method stub

	}

	public void stepOver() throws DebugException {
		// TODO Auto-generated method stub

	}

	public void stepReturn() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean canTerminate() {
		try {
			if(this.getTopStackFrame()!=null){			
				return this.getTopStackFrame().canTerminate();
			}
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public void terminate() throws DebugException {
		if(this.getTopStackFrame()!=null){
			this.getTopStackFrame().terminate();
		}

	}
	/**
	 *  add statck frame to thread and fire resume event and breakpoint event
	 * @param frame
	 */
	public void addStackFrame(JsDebugStackFrame frame) {
		List<IStackFrame> list = new ArrayList<IStackFrame>();
		list.add(frame);
		for (IStackFrame stackFrame : this.stackFrames) {
			list.add(stackFrame);
		}
		this.stackFrames = list.toArray(new IStackFrame[list.size()]);
		fireResumeEvent(DebugEvent.STEP_END);
		fireSuspendEvent(DebugEvent.BREAKPOINT);

	}

	public Set<String> getDebugExpressions() {
		return debugExpressions;
	}

}
