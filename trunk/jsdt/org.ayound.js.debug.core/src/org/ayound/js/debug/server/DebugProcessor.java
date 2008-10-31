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
package org.ayound.js.debug.server;

import org.ayound.js.debug.model.JsDebugStackFrame;
import org.ayound.js.debug.model.JsDebugThread;
import org.ayound.js.debug.model.VariableUtil;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;

public class DebugProcessor extends AbstractProcessor {

	public DebugProcessor(String requestUrl, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server) {
		super(requestUrl, postData, response, thread, server);
	}

	public void process() {
		getResponse().writeHTMLHeader("UTF-8");
		JsDebugParam param = new JsDebugParam(getPostData());
		IThread thread = getThread();
		if (thread instanceof JsDebugThread) {
			JsDebugThread jsThread = (JsDebugThread) thread;
			if ("START".equalsIgnoreCase(param.getCommand())||"RESUME".equalsIgnoreCase(param.getCommand())) {
				getResponse().writeResume();
				getResponse().close();
			} else if ("STEPOVER".equalsIgnoreCase(param.getCommand())
					|| "STEPINTO".equalsIgnoreCase(param.getCommand())
					|| "STEPRETURN".equalsIgnoreCase(param.getCommand())) {
				
				JsDebugStackFrame frame = new JsDebugStackFrame(getThread(),
						getThread().getDebugTarget(), getThread().getLaunch());
				frame.setResponse(getResponse());
//				try {
//					IMarker [] markers = getServer().getJsResourceManager().getFileByResource(param.getJsResource()).findMarkers(null, true, IResource.DEPTH_ZERO);
//					for(IMarker marker:markers){
//						System.out.println(marker.getAttribute(IBreakpoint.ID));
//						if(marker.getAttribute(IBreakpoint.ID)==null){
//							marker.delete();
//						}
//					}
//					System.out.println(markers.length);
//				} catch (CoreException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				frame.setResource(param.getJsResource());
				frame.setLineNum(param.getLine());
				frame.setVariables(VariableUtil.createVarsByObject(param
						.getJsonStack(), thread.getDebugTarget(), thread
						.getLaunch()));
				jsThread.addStackFrame(frame);
			} else if ("ERROR".equalsIgnoreCase(param.getCommand())) {
				try {
					thread.terminate();
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getResponse().close();
			} else if("BREAKPOINT".equalsIgnoreCase(param.getCommand())){
				JsDebugStackFrame frame = new JsDebugStackFrame(getThread(),
						getThread().getDebugTarget(), getThread().getLaunch());
				frame.setResponse(getResponse());
				frame.setResource(param.getJsResource());
				frame.setLineNum(param.getLine());
				frame.setVariables(VariableUtil.createVarsByObject(param
						.getJsonStack(), thread.getDebugTarget(), thread
						.getLaunch()));
				jsThread.addStackFrame(frame);
			}else{
				 getResponse().writeResume();
				 getResponse().close();
			}
		} else {
			getResponse().close();
		}
	}

}
