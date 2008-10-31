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

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

public class JsBreakPoint extends LineBreakpoint {

	public String getModelIdentifier() {
		return JsDebugCorePlugin.MODEL_ID;
	}

	public JsBreakPoint(final IResource resource,final int lineNumber){
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker("org.ayound.js.debug.core.jsMarker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: " + resource.getName() + " [line: " + lineNumber + "]");
				setRegistered(false);
				setPersisted(false);
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
