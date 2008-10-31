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



public interface IJsEngine {
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
	public void compileHtml(String url,String text);
	

	public boolean canBreakLine(String url, int line);
	
	public String[] getScriptLines(String url);
	
}
