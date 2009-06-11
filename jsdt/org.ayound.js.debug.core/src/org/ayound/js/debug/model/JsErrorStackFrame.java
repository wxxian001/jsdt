package org.ayound.js.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class JsErrorStackFrame extends JsDebugStackFrame {

	public JsErrorStackFrame(IThread thread, IDebugTarget target, ILaunch launch) {
		super(thread, target, launch);
		// TODO Auto-generated constructor stub
	}

	public void setErrorMsg(String msg){
		JsValue value = new JsValue(this.getDebugTarget(),this.getLaunch(),this);
		value.setReferenceTypeName("error");
		value.setValueString(msg);
		JsVariable var = new JsVariable("error",this.getDebugTarget(),this.getLaunch());
		try {
			var.setValue(value);
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setVariables(new IVariable[]{var});
	}
	
	public String getName() throws DebugException {
		return "<error>" + this.getResource() + "[" + this.getLineNumber()
				+ "]";
	}

	@Override
	public boolean canResume() {
		return false;
	}

	@Override
	public boolean canStepInto() {
		return false;
	}

	@Override
	public boolean canStepOver() {
		return false;
	}

	@Override
	public boolean canStepReturn() {
		return false;
	}

	@Override
	public boolean canSuspend() {
		return false;
	}

	@Override
	public boolean canTerminate() {
		return false;
	}
	
}
