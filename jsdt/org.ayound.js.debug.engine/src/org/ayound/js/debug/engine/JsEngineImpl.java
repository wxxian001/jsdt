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

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.GuiCallback;
import org.mozilla.javascript.tools.debugger.Dim.SourceInfo;
import org.mozilla.javascript.tools.debugger.Dim.StackFrame;

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
						String trimLine = jsLine.trim();
						if (trimLine.startsWith("else")
								|| trimLine.startsWith("}")
								|| trimLine.startsWith("{")
								|| trimLine.startsWith("function")
								|| trimLine.startsWith("catch")
								|| trimLine.startsWith("final")) {
							return false;
						} else {
							if (isHaflLine(lines, line, jsLine)) {
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

	private boolean isHaflLine(String[] lines, int line, String jsLine) {
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

	public void compileJs(String url, String text) {
		lineMap.put(url, text.split("\n"));
		dim.compileScript(url, text);
	}

	public void compileHtml(String url, String text) {
		StringBuffer buffer = new StringBuffer();
		boolean scriptStart = false;
		boolean scriptChar = false;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\n') {
				buffer.append(ch);
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
		if (scriptStr.trim().length() > 0) {
			lineMap.put(url, scriptStr.split("\n"));
			dim.compileScript(url, scriptStr);
		}

	}

	public String[] getScriptLines(String url) {
		return lineMap.get(url);
	}

}
