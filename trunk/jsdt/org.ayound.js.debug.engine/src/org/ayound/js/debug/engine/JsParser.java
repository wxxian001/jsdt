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

public class JsParser {

	private String lastNotNullLine = null;

	public void parse(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (isBlankLine(line)) {
				continue;
			} else {
				if (!isNoCharLine(line)) {
					if(!isCommitLine(line)){						
						if (lastNotNullLine != null) {
							
						} else {
							
						}
					}
				}
				lastNotNullLine = line;
				
			}
		}
	}

	private boolean isCommitLine(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isNoCharLine(String line) {
		for(int i=0;i<line.length();i++){
			if(Character.isLetterOrDigit(line.charAt(i))){
				return false;
			}
		}
		return true;
	}

	private boolean isBlankLine(String line) {
		return line.trim().length() == 0;
	}
}
