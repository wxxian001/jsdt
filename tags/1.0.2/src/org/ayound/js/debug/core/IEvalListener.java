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
 * Created on 2008-11-8
 *******************************************************************************/

package org.ayound.js.debug.core;

import org.ayound.js.debug.model.JsDebugStackFrame;

public interface IEvalListener {

	public void update(JsDebugStackFrame frame);
}
