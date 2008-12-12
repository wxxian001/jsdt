package org.ayound.js.debug.ui.editor;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;


public class HtmlJsPartitionScanner extends RuleBasedPartitionScanner {
	public final static String HTMLJS_COMMENT = "__html_js_comment";
	public final static String HTML_TAG = "__html_js_tag";

	public HtmlJsPartitionScanner() {

		IToken htmlJsComment = new Token(HTMLJS_COMMENT);
		IToken tag = new Token(HTML_TAG);

		IPredicateRule[] rules = new IPredicateRule[3];

		rules[0] = new MultiLineRule("<!--", "-->", htmlJsComment);
//		rules[1] = new WordPatternRule(new IWordDetector(){
//
//			public boolean isWordPart(char c) {
//				return Character.isLetterOrDigit(c)||c==':'||c=='/'||c=='<';
//			}
//
//			public boolean isWordStart(char c) {
//				return true;
//			}
//			
//		},"<","",tag);
		rules[1] = new MultiLineRule("/*","*/",htmlJsComment);
		rules[2] = new SingleLineRule("//","",htmlJsComment);
		setPredicateRules(rules);
	}
}
