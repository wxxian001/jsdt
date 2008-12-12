package org.ayound.js.debug.ui.editor;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;

public class HtmlJsScanner extends RuleBasedScanner {
	public static final String[] KEYWORDS = new String[] { "break", "delete",
			"function", "return", "typeof", "case", "do", "if", "switch",
			"var", "catch", "else", "in", "this", "void", "continue", "false",
			"instanceof", "throw", "while", "debugger", "finally", "new",
			"true", "with", "default", "for", "null", "try" };

	class JsWordDetector implements IWordDetector {

		public boolean isWordPart(char c) {
			return Character.isLetter(c);
		}

		public boolean isWordStart(char c) {
			return Character.isLetter(c);
		}

	}

	public HtmlJsScanner(ColorManager manager) {
		IToken procInstr = new Token(new TextAttribute(manager
				.getColor(IHtmlJsColorConstants.PROC_INSTR)));

		IRule[] rules = new IRule[6];
		// Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", procInstr);
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new HtmlJsWhitespaceDetector());

		IToken keywordToken = new Token(new TextAttribute(manager
				.getColor(IHtmlJsColorConstants.KEY_WORD), null, SWT.BOLD));
		WordRule keywordRule = new WordRule(new JsWordDetector());
		for (String keyword : KEYWORDS) {
			keywordRule.addWord(keyword, keywordToken);
		}
		rules[2] = keywordRule;
		IToken stringToken = new Token(new TextAttribute(manager.getColor(IHtmlJsColorConstants.STRING)));
		rules[3] = new SingleLineRule("/","/",stringToken);
		rules[4] = new SingleLineRule("\"","\"",stringToken);
		rules[5] = new SingleLineRule("'","'",stringToken);
		setRules(rules);
	}
}
