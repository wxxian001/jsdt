package org.ayound.js.debug.ui.editor;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class HtmlJsTagScanner extends RuleBasedScanner {

	public HtmlJsTagScanner(ColorManager manager) {
		IToken string =
			new Token(
				new TextAttribute(manager.getColor(IHtmlJsColorConstants.STRING)));

		IRule[] rules = new IRule[3];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[1] = new SingleLineRule("'", "'", string, '\\');
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new HtmlJsWhitespaceDetector());

		setRules(rules);
	}
}
