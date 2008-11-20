package org.ayound.js.debug.ui.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class HtmlJsWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
