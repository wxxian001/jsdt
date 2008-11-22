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

	private static final String ERROR_MARKER_ID = "org.ayound.js.debug.errorMarker";

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
			IMarker[] markers = file.findMarkers(ERROR_MARKER_ID, true,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				marker.delete();
			}
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		String text = document.get();
		if (text.trim().startsWith("<")) {
			try {
				EngineManager.getEngine().compileHtml("_temp.js", text);
			} catch (EvaluatorException e) {
				handleError(e, true);
			}
		} else {
			try {
				EngineManager.getEngine().compileJs("_temp.js", text);
			} catch (EvaluatorException e) {
				handleError(e, true);
			}
		}

	}

	protected void handleError(EvaluatorException e, boolean isFatal) {

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		;
		try {
			IMarker marker = file.createMarker(ERROR_MARKER_ID);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker
					.setAttribute(IMarker.LOCATION, file.getFullPath()
							.toString());
			marker.setAttribute(IMarker.MESSAGE, "[Error]: " + e.details());
			marker.setAttribute(IMarker.LINE_NUMBER, e.lineNumber());
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

	}

}
