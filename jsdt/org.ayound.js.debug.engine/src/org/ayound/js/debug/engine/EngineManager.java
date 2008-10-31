/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * All rights reserved.
 * 
 * Created on 2008-10-26
 *******************************************************************************/
package org.ayound.js.debug.engine;

public class EngineManager {

	private static IJsEngine ENGINE = new JsEngineImpl();

	public static IJsEngine getEngine() {
		return ENGINE;
	}
}
