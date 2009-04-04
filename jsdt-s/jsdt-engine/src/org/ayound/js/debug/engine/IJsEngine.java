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
package org.ayound.js.debug.engine;

import java.io.File;

public interface IJsEngine {

	public void compileFile(File file);

	/**
	 * 
	 * @param url
	 * @param text
	 */
	public void compileJs(String url, String text);

	/**
	 * 
	 * @param url
	 * @param text
	 */
	public void compileHtml(String url, String text);

	/**
	 * find can toggle breakpoint to this line
	 * 
	 * @param url
	 * @param line
	 * @return
	 */
	public boolean canBreakLine(String url, int line);

	/**
	 * get script lines array .the engine handle the source script
	 * 
	 * @param url
	 * @return
	 */
	public String[] getScriptLines(String url);

}
