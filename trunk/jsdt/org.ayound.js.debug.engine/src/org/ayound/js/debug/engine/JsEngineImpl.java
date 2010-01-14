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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JsDebugCompileEngine;

/**
 * js engine use rhino to compile javascript files the first page is html
 * file,it remove all the html tag and convert html file to js file
 *
 */
public class JsEngineImpl implements IJsEngine {

	private Map<String, JsDebugCompileEngine> engineMap = new HashMap<String, JsDebugCompileEngine>();

	private Map<String, String> compileMap = new HashMap<String, String>();

	public JsEngineImpl() {

	}

	public boolean canBreakLine(String url, int line) {
		if(engineMap.containsKey(url)){
			JsDebugCompileEngine engine = engineMap.get(url);
			if(engine.getBreakPoints().containsKey(line-1)){
				return true;
			}
		}
		return false;
	}


	/**
	 * compile javascript files use rhino dim engine.
	 */
	public void compileJs(String url, String text) {
		JsDebugCompileEngine compile = new JsDebugCompileEngine();
		compile.setLineno(0);
		compile.setSourceName(url);
		compile.setSourceString(text);
		compileMap.put(url, compile.compile());
		engineMap.put(url, compile);
	}

	public String compileJsOffset(String url, String text,int offset) throws EvaluatorException{
		JsDebugCompileEngine compile = null;
		if(engineMap.containsKey(url)){
			compile = engineMap.get(url);
		}else{
			compile = new JsDebugCompileEngine();
			engineMap.put(url, compile);
		}
		compile.setLineno(0);
		compile.setSourceName(url);
		compile.setSourceString(text);
		compile.setOffsetLine(offset);
		String result = null;
		try{
			result = compile.compile();
		}catch(EvaluatorException e){
			int errLine = offset + e.lineNumber();
			e.initLineNumber(errLine);
			throw e;
		}
		return result;
	}


	/**
	 * remove all the html tag and convert html file to javascript file then
	 * compile html file all the \n is keeped to set relation to breakpoint
	 */
	public void compileHtml(String url, String text) {
		StringBuffer htmlBuffer = new StringBuffer();
		StringBuffer scriptBuffer = new StringBuffer();
		boolean scriptStart = false;
		boolean scriptChar = false;
//		text = text.replaceAll("\n\r", "\n").replaceAll("\r", "\n");
		int currLine = 1;
		int scriptOffsetLine = 0;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\n') {
				currLine++;
				if(scriptChar){
					scriptBuffer.append(' ').append(ch);
				}else{
					htmlBuffer.append(ch);
				}
			} else {
				if (ch == '<') {
					if (text.length() > i + 8) {
						String targetTag = text.substring(i, i + 8)
								.toLowerCase();
						if (targetTag.startsWith("<script")) {
							scriptOffsetLine = currLine;
							scriptStart = true;
							scriptChar = false;
						} else if (targetTag.startsWith("</script")) {
							scriptStart = false;
							scriptChar = false;
							String script = scriptBuffer.toString();
							htmlBuffer.append(compileJsOffset(url, script,scriptOffsetLine));
							scriptBuffer = new StringBuffer();
						}
					}
				} else if (ch == '>') {
					if (scriptStart == true && scriptChar == false) {
						htmlBuffer.append(ch);
						scriptChar = true;
						if(text.charAt(i+1)=='\n'){
							currLine++;
							i++;
						}
						continue;
					}
				}
				if (scriptChar) {
					scriptBuffer.append(ch);
				}else{
					htmlBuffer.append(ch);
				}
			}
		}
		String htmlStr = htmlBuffer.toString();
		compileMap.put(url, htmlStr);

	}

	public static void main(String[] args){
		JsEngineImpl js = new JsEngineImpl();
		/*StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n");
		buffer.append("<HTML>\n");
		buffer.append("    \n");
		buffer.append("    <HEAD>\n");
		buffer.append("        <TITLE>\n");
		buffer.append("            New Document\n");
		buffer.append("        </TITLE>\n");
		buffer.append("        <META NAME=\"Generator\" CONTENT=\"EditPlus\">\n");
		buffer.append("        <META NAME=\"Author\" CONTENT=\"\">\n");
		buffer.append("        <META NAME=\"Keywords\" CONTENT=\"\">\n");
		buffer.append("        <META NAME=\"Description\" CONTENT=\"\">\n");
		buffer.append("    </HEAD>\n");
		buffer.append("    \n");
		buffer.append("    <BODY>\n");
		buffer.append("        <script type=\"text/javascript\">\n");
		buffer.append("            function test() {\n");
		buffer.append("                alert(\'a\');\n");
		buffer.append("            }\n");
		buffer.append("        </script>\n");
		buffer.append("        <script type=\"text/javascript\">\n");
		buffer.append("            function test() {\n");
		buffer.append("                alert(\'a\');\n");
		buffer.append("            }\n");
		buffer.append("        </script>\n");
		buffer.append("    </BODY>\n");
		buffer.append("\n");
		buffer.append("</HTML>\n");
		buffer.append("\n");*/
		StringBuffer buffer = new StringBuffer();
		buffer.append("function test(){\n\n/*abc\ndef\nddd*/");
		buffer.append("	alert(\'a\');\n");
		buffer.append("}");

		js.compileJs("test", buffer.toString());
		System.out.println(js.compileMap.get("test"));

	}

	/**
	 * get lines by url
	 */
	public String getCompiledString(String url) {
		if(compileMap.containsKey(url)){
			return compileMap.get(url);
		}
		return null;
	}

	public void compileFile(IFile file) {
		String fileName = file.getFullPath().toString();
		InputStream input = null;
		try {
			input = file.getContents();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String line = null;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			String scriptText = buffer.toString();
			if (scriptText.trim().startsWith("<")) {
				this.compileHtml(fileName, scriptText);
			} else {
				this.compileJs(fileName, scriptText);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
