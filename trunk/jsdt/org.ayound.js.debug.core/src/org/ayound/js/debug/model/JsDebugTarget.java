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

import java.util.ArrayList;
import java.util.List;

import org.ayound.js.debug.server.JsDebugServer;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class JsDebugTarget extends JsDebugElement implements IDebugTarget {

	private JsDebugServer server;

	IThread[] threads = new IThread[] {};

	public JsDebugTarget(JsDebugServer server, ILaunch lanuch) {
		super(null, lanuch);
		this.server = server;

	}

	public String getName() throws DebugException {
		return "JavaScript Debug [" + server.getPort() + "]";
	}

	public IProcess getProcess() {
		return null;
	}

	public IThread[] getThreads() throws DebugException {
		return this.threads;
	}

	public boolean hasThreads() throws DebugException {
		return this.threads != null && this.threads.length > 0;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canTerminate() {
		return !this.isTerminated();
	}

	public void terminate() throws DebugException {
		this.setTerminated(true);
		this.server.stop();
		this.fireTerminateEvent();

	}

	public boolean canResume() {
		return false;
	}

	public boolean canSuspend() {
		return false;
	}

	public boolean isSuspended() {
		return false;
	}

	public void resume() throws DebugException {
		// TODO Auto-generated method stub

	}

	public void suspend() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean canDisconnect() {
		return false;
	}

	public void disconnect() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean isDisconnected() {
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	public boolean supportsStorageRetrieval() {
		// TODO Auto-generated method stub
		return false;
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		return;
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		return;
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		return;
	}

	/**
	 * add thread to debug target and fire create,state,breakpoint event
	 * @param jsThread
	 */
	public void addThread(JsDebugThread jsThread) {
		List<IThread> list = new ArrayList<IThread>();
		for (IThread thread : this.threads) {
			list.add(thread);
		}
		list.add(jsThread);
		this.threads = list.toArray(new IThread[list.size()]);
		fireCreationEvent();
		fireChangeEvent(DebugEvent.STATE);
		fireSuspendEvent(DebugEvent.BREAKPOINT);
	}

	public JsDebugServer getServer() {
		return server;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

}
