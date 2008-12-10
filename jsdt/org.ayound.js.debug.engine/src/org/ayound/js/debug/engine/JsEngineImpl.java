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
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.GuiCallback;
import org.mozilla.javascript.tools.debugger.Dim.SourceInfo;
import org.mozilla.javascript.tools.debugger.Dim.StackFrame;

/**
 * js engine use rhino to compile javascript files the first page is html
 * file,it remove all the html tag and convert html file to js file
 * 
 */
public class JsEngineImpl implements IJsEngine {

	private Dim dim;

	private Map<String, String[]> lineMap = new HashMap<String, String[]>();

	private static final String LAST_CHAR = ",(|&?+=*";

	private static final String FIRST_CHAR = ",:)]}&+=|";

	public JsEngineImpl() {
		dim = new Dim();
		ContextFactory factory = new ContextFactory();
		dim.attachTo(factory);
		dim.setGuiCallback(new GuiCallback() {

			public void dispatchNextGuiEvent() throws InterruptedException {
				// TODO Auto-generated method stub
			}

			public void enterInterrupt(StackFrame lastFrame,
					String threadTitle, String alertMessage) {
			}

			public boolean isGuiEventThread() {
				return false;
			}

			public void updateSourceText(SourceInfo sourceInfo) {

			}
		});
	}

	public boolean canBreakLine(String url, int line) {
		SourceInfo info = dim.sourceInfo(url);
		if (info != null) {
			if (info.breakableLine(line)) {
				String[] lines = lineMap.get(url);
				if (lines != null) {
					if (line < 1 || line > lines.length) {
						return false;
					} else {
						String jsLine = lines[line - 1];
						int firstStartBrakets = jsLine.indexOf('(');
						int firstEndBrakets = jsLine.indexOf(')');
						if ((firstEndBrakets>0 && firstEndBrakets < firstStartBrakets)
								|| (firstEndBrakets > 0 && firstStartBrakets < 0)) {
							return false;
						}
						String trimLine = jsLine.trim();
						if (trimLine.startsWith("else")
								|| trimLine.startsWith("}")
								|| trimLine.startsWith("{")
								|| trimLine.startsWith("function")
								|| trimLine.startsWith("catch")
								|| trimLine.startsWith("final")) {
							return false;
						} else {
							if (isHalfLine(lines, line, jsLine)) {
								return false;
							} else {
								return true;
							}
						}
					}
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * find out is it a half line
	 * 
	 * @param lines
	 * @param line
	 * @param jsLine
	 * @return
	 */
	private boolean isHalfLine(String[] lines, int line, String jsLine) {
		String trimLine = jsLine.trim();
		char firstChar = trimLine.charAt(0);
		if (FIRST_CHAR.indexOf(firstChar) > -1) {
			return true;
		}
		String lastLine = null;
		int lastIndex = line - 2;
		while (true) {
			if (lastIndex > -1) {
				lastLine = lines[lastIndex].trim();
				if (lastLine.length() > 0) {
					break;
				}
			} else {
				break;
			}
			lastIndex = lastIndex - 1;
		}
		if (lastLine == null || lastLine.length() == 0) {
			return false;
		} else {
			char lastCh = lastLine.charAt(lastLine.length() - 1);
			if (LAST_CHAR.indexOf(lastCh) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * compile javascript files use rhino dim engine.
	 */
	public void compileJs(String url, String text) {
		lineMap.put(url, text.split("\n"));
		dim.compileScript(url, text);
	}

	/**
	 * remove all the html tag and convert html file to javascript file then
	 * compile html file all the \n is keeped to set relation to breakpoint
	 */
	public void compileHtml(String url, String text) {
		StringBuffer buffer = new StringBuffer();
		boolean scriptStart = false;
		boolean scriptChar = false;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\n') {
				buffer.append(' ').append(ch);
			} else {
				if (ch == '<') {
					if (text.length() > i + 8) {
						String targetTag = text.substring(i, i + 8)
								.toLowerCase();
						if (targetTag.startsWith("<script")) {
							scriptStart = true;
							scriptChar = false;
						} else if (targetTag.startsWith("</script")) {
							scriptStart = false;
							scriptChar = false;
						}
					}
				} else if (ch == '>') {
					if (scriptStart == true && scriptChar == false) {
						scriptChar = true;
						continue;
					}
				}
				if (scriptChar) {
					buffer.append(ch);
				}
			}
		}
		String scriptStr = buffer.toString();
		lineMap.put(url, scriptStr.split("\n"));
		if (scriptStr.trim().length() > 0) {
			dim.compileScript(url, scriptStr);
		}

	}

	/**
	 * get lines by url
	 */
	public String[] getScriptLines(String url) {
		return lineMap.get(url);
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
