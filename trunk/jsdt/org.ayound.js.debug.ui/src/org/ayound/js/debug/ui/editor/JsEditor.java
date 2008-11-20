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

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

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
}
