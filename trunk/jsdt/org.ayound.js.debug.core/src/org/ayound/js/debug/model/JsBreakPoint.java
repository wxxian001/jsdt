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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
/**
 * 
 * JsBreakPoint is a breakpoint model of jsdt,
 * it used to toggle breakpoint of javascript files 
 */

public class JsBreakPoint extends LineBreakpoint {

	public String getModelIdentifier() {
		return JsDebugCorePlugin.MODEL_ID;
	}
	/**
	 * the cunstructor of jsbreakpoint
	 * it create the marker and enabled it
	 * @param resource
	 * @param lineNumber
	 */
	public JsBreakPoint(final IResource resource,final int lineNumber){
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker("org.ayound.js.debug.core.jsMarker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: " + resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		try {
			run(getMarkerRule(resource), runnable);
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getText() {
		return this.getMarker().getAttribute(IMarker.MESSAGE, "");
	}

	@Override
	public boolean equals(Object item) {
		if(item instanceof JsBreakPoint){
			JsBreakPoint point = (JsBreakPoint)item;
			return this.getMarker().getResource().equals(point.getMarker().getResource()) && ( this.getMarker().getAttribute(IMarker.LINE_NUMBER, 0) == point.getMarker().getAttribute(IMarker.LINE_NUMBER, 0));
		}
		return false;
	}
	
}
