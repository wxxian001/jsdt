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
 * Created on 2008-11-8
 *******************************************************************************/

package org.ayound.js.debug.ui.eval;

import org.ayound.js.debug.model.JsDebugStackFrame;
import org.ayound.js.debug.ui.editor.JsEditor;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class JsEditorEvalAction implements IEditorActionDelegate {
	IEditorPart editPart;

	public JsEditorEvalAction() {
		// TODO Auto-generated constructor stub
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editPart = targetEditor;
	}

	public void run(IAction action) {
		if (editPart instanceof JsEditor) {
			ITextEditor textEditor = (ITextEditor) editPart;

			ISelection selection = textEditor.getSelectionProvider()
					.getSelection();
			IDocument document = textEditor.getDocumentProvider().getDocument(
					textEditor.getEditorInput());
			if (selection instanceof ITextSelection && document != null) {
				ITextSelection textSelection = (ITextSelection) selection;
				if (textSelection.getLength() < document.getLength()) {
					JsDebugStackFrame frame = (JsDebugStackFrame) DebugUITools
							.getDebugContext().getAdapter(
									JsDebugStackFrame.class);
					if (frame != null) {
						frame.addExpression(textSelection.getText());
					}
				}
			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
