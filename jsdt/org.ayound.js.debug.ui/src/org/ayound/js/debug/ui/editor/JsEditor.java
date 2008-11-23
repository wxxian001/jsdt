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
package org.ayound.js.debug.ui.editor;

import org.ayound.js.debug.engine.EngineManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.mozilla.javascript.EvaluatorException;

public class JsEditor extends AbstractDecoratedTextEditor {

	private ColorManager colorManager;

	/**
	 * Constructor for SampleEditor.
	 */
	public JsEditor() {
		super();
		setRulerContextMenuId("org.ayound.js.debug.rulerMenu");
		setEditorContextMenuId("org.ayound.js.debug.ui.jsEvalMenu");
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new HtmlJsconfiguration(colorManager));
		setDocumentProvider(new HtmlJsDocumentProvider());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();

	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		validateAndMark();
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		validateAndMark();
	}

	protected void validateAndMark() {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		try {
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				marker.delete();
			}
		} catch (CoreException e1) {
		}
		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		String text = document.get();
		if (text.trim().startsWith("<")) {
			try {
				EngineManager.getEngine().compileHtml("_temp.js", text);
			} catch (EvaluatorException e) {
				String[] lines = EngineManager.getEngine().getScriptLines(
						"_temp.js");
				int lineNum = e.lineNumber();
				if(lineNum>lines.length){
					lineNum = lines.length;
				}
				int errLine = lineNum;
				
				for (int i = lineNum - 1; i > -1; i--) {
					String line = lines[i];
					if (line.trim().length() > 0) {
						errLine = i + 1;
						break;
					}
				}
				handleError(e, errLine, true);
			}
		} else {
			try {
				EngineManager.getEngine().compileJs("_temp.js", text);
			} catch (EvaluatorException e) {
				handleError(e, true);
			}
		}

	}

	protected void handleError(EvaluatorException e, int lineNum,
			boolean isFatal) {

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		try {
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);

			marker.setAttribute(IMarker.MESSAGE, "[Error]: " + e.details());
			marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
			int startChar = getCharStart(lineNum, e.columnNumber());
			int endChar = getCharEnd(lineNum, e.columnNumber());
			marker.setAttribute(IMarker.CHAR_START, startChar);
			marker.setAttribute(IMarker.CHAR_END, endChar);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

	}

	protected void handleError(EvaluatorException e, boolean isFatal) {

		handleError(e, e.lineNumber(), isFatal);
	}

	private int getCharStart(int lineNum, int columnNum) {
		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		String text = document.get();
		int offset = 0;
		int lineIndex = 0;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			offset++;
			if (ch == '\n') {
				lineIndex++;
			}
			if (lineIndex >= lineNum - 1) {
				break;
			}
		}
		return offset;
	}

	private int getCharEnd(int lineNum, int columnNum) {
		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		String text = document.get();
		int offset = 0;
		int lineIndex = 0;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			offset++;
			if (ch == '\n') {
				lineIndex++;
			}
			if (lineIndex >= lineNum) {
				break;
			}
		}
		return offset - 1;
	}
}
