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


package org.ayound.js.debug.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ayound.js.debug.listener.IBreakPointListener;
import org.ayound.js.debug.model.BreakPointModel;

public class BreakPointManager {

	private static boolean breakPointEnabled = true;

	private static List<IBreakPointListener> listeners = new ArrayList<IBreakPointListener>();

	private static Vector<BreakPointModel> breakpoints = new Vector<BreakPointModel>();

	public static boolean isBreakPointEnabled(){
		return breakPointEnabled;
	}

	synchronized public static void addBreakPoint(String resource,int lineNumber){
		for(BreakPointModel model:breakpoints){
			if(model.getLineNumber()==lineNumber&&resource.equals(model.getResourcePath())){
				return;
			}
		}
		BreakPointModel model = new BreakPointModel(resource,lineNumber,breakPointEnabled);
		breakpoints.add(model);

		for(IBreakPointListener listener:listeners){
			listener.addBreakPoint(model);
		}
	}

	synchronized public static void removeBreakPoint(String resource,int lineNumber,boolean fireRemoveEvent){
		for(BreakPointModel model:breakpoints){
			if(resource.equals(model.getResourcePath())&&lineNumber==model.getLineNumber()){
				breakpoints.remove(model);
				for(IBreakPointListener listener:listeners){
					if(fireRemoveEvent){
						listener.removeBreakPoint(model);
					}else{
						listener.updateBreakPoints();
					}
				}
				return;
			}
		}
	}

	public static Vector<BreakPointModel> getAllBreakPoints(){
		return breakpoints;
	}

	public static void enableAllBreakPoint(){
		breakPointEnabled = true;
		for(BreakPointModel model:breakpoints){
			model.setEnabled(true);
		}
		for(IBreakPointListener listener:listeners){
			listener.updateBreakPoints();
		}
	}
	public static void disableAllBreakPoint(){
		breakPointEnabled = false;
		for(BreakPointModel model:breakpoints){
			model.setEnabled(false);
		}
		for(IBreakPointListener listener:listeners){
			listener.updateBreakPoints();
		}
	}

	public static void clearAllBreakPoint(){
		breakpoints.clear();
		for(IBreakPointListener listener:listeners){
			listener.updateBreakPoints();
		}
	}

	public static void addBreakPointListener(IBreakPointListener listener){
		listeners.add(listener);
	}

	public static void removeBreakPointListener(IBreakPointListener listener){
		listeners.remove(listener);
	}
}
