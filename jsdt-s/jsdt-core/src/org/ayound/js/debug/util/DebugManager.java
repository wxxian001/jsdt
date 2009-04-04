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

import org.ayound.js.debug.listener.IFrameSelectionListener;
import org.ayound.js.debug.listener.IStackFrameListener;
import org.ayound.js.debug.model.DebugStackFrame;
import org.ayound.js.debug.model.ErrorStackFrame;
import org.ayound.js.debug.server.JsDebugResponse;

public class DebugManager {

	private static List<IStackFrameListener> stackFrameListener = new ArrayList<IStackFrameListener>();


	private static List<IFrameSelectionListener> stackFrameSelectionListener = new ArrayList<IFrameSelectionListener>();

	private static DebugStackFrame currentFrame;

	public static void addStackFrame(DebugStackFrame frame){
		for(IStackFrameListener listener:stackFrameListener){
			listener.addStackFrame(frame);
		}
	}
	public static void addErrorStackFrame(ErrorStackFrame frame){
		for(IStackFrameListener listener:stackFrameListener){
			listener.addErrorFrame(frame);
		}
	}

	public static void selectFrame(DebugStackFrame frame){
		currentFrame = frame;
		for(IFrameSelectionListener listener:stackFrameSelectionListener){
			listener.selectFrame(frame);
		}
	}
	public static void addStackFrameListener(IStackFrameListener listener){
		stackFrameListener.add(listener);
	}

	public static void removeStackFrameListener(IStackFrameListener listener){
		stackFrameListener.remove(listener);
	}

	public static void addStackFrameSelectionListener(IFrameSelectionListener listener){
		stackFrameSelectionListener.add(listener);
	}

	public static void removeStackFrameSelectionListener(IFrameSelectionListener listener){
		stackFrameSelectionListener.remove(listener);
	}
	public static DebugStackFrame getCurrentFrame() {
		return currentFrame;
	}
	public static JsDebugResponse getCurrentResponse() {
		if(currentFrame!=null){
			return currentFrame.getResponse();
		}
		return null;
	}
}
