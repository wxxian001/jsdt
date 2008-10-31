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


public class ScriptCompileUtil {

//	private static final String KEY_WORD = " break delete function return typeof case do if switch var catch else in this void continue false instanceof throw while debugger finally new true with default for null try ";

	public static String compileJsLine(String lines[], String resourcePath,
			int index) {
		
		String jsLine = lines[index];
		return compileOneLine(jsLine,lines,resourcePath,index);
		
	}

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
	
	
	private static String getdebugString(String lines[], String resourcePath,
			int index) {
		String debugStr = "$jsd('" + resourcePath + "'," + (index + 1)
				+ ",this,((typeof(arguments)!=\"undefined\"?arguments:null)),function(__text){try{return eval(__text);}catch(e){}})";
//		debugStr = debugStr + "'arguments':arguments})";
//		Set<String> varNames = getContextVars(lines, index);
//		for (String varName : varNames) {
//			varName = varName.replace("var ", "");
//			if (varName.length() > 0 && !KEY_WORD.contains(" " + varName + " ")
//					&& isVar(varName)) {
//				debugStr = debugStr + "'" + varName + "':((typeof " + varName
//						+ " !=\"undefined\")?" + varName + ":undefined),";
//			}
//		}
		return debugStr;
	}

//	private static boolean isVar(String varName) {
//		for (int i = 0; i < varName.length(); i++) {
//			char ch = varName.charAt(i);
//			if (!(Character.isLetterOrDigit(ch) || ch == '$' || ch == '_')) {
//				return false;
//			}
//		}
//		return true;
//	}

//	private static Set<String> getContextVars(String lines[], int index) {
//		Set<String> varNames = new HashSet<String>();
//		String line = lines[index];
//		if (line.indexOf("function") >= 0) {
//			return varNames;
//		}
//		for (int i = index - 1; i > -1; i--) {
//			String lastLine = lines[i];
//			String trimLine = lastLine.replace(" ", "");
//			if (trimLine.indexOf("function") > -1) {
//				String trimLineEnd = trimLine.substring(trimLine
//						.indexOf("function"));
//				int startOffset = trimLine.indexOf("(");
//				int endOffset = trimLine.indexOf(')');
//				if (endOffset >= 0) {
//					trimLineEnd = trimLine
//							.substring(startOffset + 1, endOffset);
//				}
//				if (trimLineEnd.length() > 0) {
//					String[] vars = trimLineEnd.split(",");
//					for (String var : vars) {
//						varNames.add(var.trim());
//					}
//				}
//				break;
//			} else {
//				varNames.addAll(getVarNames(lastLine));
//			}
//		}
//		return varNames;
//	}
//
//	private static List<String> getVarNames(String line) {
//		List<String> varNames = new ArrayList<String>();
//		line = line.trim();
//		if (line.startsWith("var ")) {
//			int endOffset = line.indexOf(';');
//			if (endOffset > -1) {
//				line = line.substring(4, endOffset).trim();
//			}
//			String[] vars = line.split(",");
//			for (String var : vars) {
//				int varEnd = var.indexOf("=");
//				if (varEnd > 0) {
//					varNames.add(var.substring(0, varEnd).trim());
//				}
//			}
//		} else {
//			int endOffset = line.indexOf('=');
//			if (endOffset >= 0) {
//				String varLine = line.substring(0, endOffset).trim();
//				StringBuffer varBuffer = new StringBuffer();
//				for (int i = 0; i < varLine.length(); i++) {
//					char ch = varLine.charAt(i);
//					if (Character.isLetterOrDigit(ch) || ch == '$' || ch == '_') {
//						varBuffer.append(ch);
//					} else {
//						break;
//					}
//				}
//				varNames.add(varBuffer.toString());
//			}
//		}
//		return varNames;
//	}

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
