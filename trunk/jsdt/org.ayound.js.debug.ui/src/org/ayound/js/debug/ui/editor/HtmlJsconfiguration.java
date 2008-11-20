package org.ayound.js.debug.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class HtmlJsconfiguration extends SourceViewerConfiguration {
	private HtmlJsTagScanner tagScanner;
	private HtmlJsScanner scanner;
	private ColorManager colorManager;

	public HtmlJsconfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			HtmlJsPartitionScanner.HTMLJS_COMMENT,
			HtmlJsPartitionScanner.HTML_TAG };
	}

	protected HtmlJsScanner getHtmlJsScanner() {
		if (scanner == null) {
			scanner = new HtmlJsScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IHtmlJsColorConstants.DEFAULT))));
		}
		return scanner;
	}
	protected HtmlJsTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new HtmlJsTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IHtmlJsColorConstants.TAG))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, HtmlJsPartitionScanner.HTML_TAG);
		reconciler.setRepairer(dr, HtmlJsPartitionScanner.HTML_TAG);

		dr = new DefaultDamagerRepairer(getHtmlJsScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IHtmlJsColorConstants.COMMIT)));
		reconciler.setDamager(ndr, HtmlJsPartitionScanner.HTMLJS_COMMENT);
		reconciler.setRepairer(ndr, HtmlJsPartitionScanner.HTMLJS_COMMENT);

		return reconciler;
	}

}