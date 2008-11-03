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
package org.ayound.js.debug.script;

/**
 * 
 * the class is used to compile js line to debug line
 * by add $jsd line before the javascript line.
 *
 */
public class ScriptCompileUtil {

	/**
	 * 
	 * @param lines
	 * @param resourcePath
	 * @param index
	 * @return
	 */
	public static String compileJsLine(String lines[], String resourcePath,
			int index) {
		
		String jsLine = lines[index];
		return compileOneLine(jsLine,lines,resourcePath,index);
		
	}
	/**
	 * compile one javascript line,
	 * if the line has ":" it maybe a json Object line or case,default
	 * 
	 * @param jsLine
	 * @param lines
	 * @param resourcePath
	 * @param index
	 * @return
	 */
	private static String compileOneLine(String jsLine,String lines[],String resourcePath,int index){
		int firstColon = jsLine.indexOf(':');
		if (firstColon > 0 && firstColon < jsLine.length() - 2) {
			if (jsLine.trim().startsWith("case ")) {
				int caseOffset = jsLine.indexOf(':');
				if (caseOffset > 0) {
					String caseLine = jsLine.substring(0, caseOffset + 1)
							+ getdebugString(lines, resourcePath, index) + ";";
					if (caseOffset < jsLine.length()) {
						caseLine = caseLine + jsLine.substring(caseOffset + 1);
					}
					return caseLine;
				}
				return jsLine;
			}
			String beforeColon = jsLine.substring(0,firstColon).trim();
			boolean isColonLine = true;
			if(beforeColon.startsWith("'")&&beforeColon.endsWith("'")){
				isColonLine = true;
			}else if(beforeColon.startsWith("\"")&&beforeColon.endsWith("\"")){
				isColonLine = true;
			}else if(beforeColon.equals("default")){
				String defaultLine = jsLine.substring(0, firstColon + 1)
				+ getdebugString(lines, resourcePath, index) + ";";
				return defaultLine;
			}else{
				for(int i=0;i<beforeColon.length();i++){
					char ch = beforeColon.charAt(i);
					if(Character.isLetterOrDigit(ch)||ch=='_'||ch=='$'){
						continue;
					}else{
						isColonLine = false; 
					}
				}
			}
			if(isColonLine){
				return "$jsd:" + getdebugString(lines, resourcePath, index)
				+ "," + jsLine;
			}
		}
		return getdebugString(lines, resourcePath, index) + ";" + jsLine;
	}
	
	/**
	 * get debug string insert before every javascript line.
	 * @param lines
	 * @param resourcePath
	 * @param index
	 * @return
	 */
	private static String getdebugString(String lines[], String resourcePath,
			int index) {
		String debugStr = "$jsd('" + resourcePath + "'," + (index + 1)
				+ ",this,((typeof(arguments)!=\"undefined\"?arguments:null)),function(__text){try{return eval(__text);}catch(e){}})";
		return debugStr;
	}
	/**
	 * insert $jsd after <script> tag
	 * @param lines
	 * @param resourcePath
	 * @param index
	 * @return
	 */
	public static String compileHtmlLine(String[] lines, String resourcePath,
			int index) {
		String htmlLine = lines[index];
		int offset = htmlLine.indexOf('>');
		if (htmlLine.indexOf('>') >= 0) {
			StringBuffer buffer = new StringBuffer(htmlLine
					.substring(0, offset));
			if (!buffer.toString().toLowerCase().contains("<script")) {
				for (int i = index - 1; i >= 0; i--) {
					String line = lines[i];
					int offsetScript = line.toLowerCase().indexOf("<script");
					if (offsetScript > -1) {
						buffer.append(line.substring(offsetScript));
						break;
					} else {
						buffer.append(line);
					}
				}
			}
			if (buffer.toString().contains(">")) {
				htmlLine = getdebugString(lines, resourcePath, index) + ";"
						+ htmlLine;
			} else {
				htmlLine = htmlLine.substring(0, offset + 1)
						+ getdebugString(lines, resourcePath, index) + ";"
						+ htmlLine.substring(offset + 1);
			}

		} else {
			htmlLine = getdebugString(lines, resourcePath, index) + ";"
					+ htmlLine;
		}
		return htmlLine;
	}
}
