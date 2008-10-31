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
package org.ayound.js.debug.ui;

import org.ayound.js.debug.model.JsBreakPoint;
import org.ayound.js.debug.model.JsValue;
import org.ayound.js.debug.model.JsVariable;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;

public class JsDebugModelPresentation extends LabelProvider implements IDebugModelPresentation {

	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
			e.printStackTrace();
		}
		listener.detailComputed(value, detail);
	}

	public String getEditorId(IEditorInput input, Object element) {
		return "org.ayound.js.debug.ui.editor.JsEditor";
	}

	@Override
	public String getText(Object element) {
		try {
			if (element instanceof IStackFrame) {
				return ((IStackFrame) element).getName();

			} else if (element instanceof JsVariable) {
				try {
					return ((JsVariable) element).getName();
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (element instanceof JsValue) {
				try {
					return ((JsValue) element).getReferenceTypeName();
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (element instanceof IDebugTarget) {
				return ((IDebugTarget) element).getName();
			} else if (element instanceof IThread) {
				return ((IThread) element).getName();
			}else if(element instanceof JsBreakPoint){
				return ((JsBreakPoint)element).getText();
			}
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.getText(element);
	}

	public IEditorInput getEditorInput(Object element) {
		IEditorInput editorInput = null;
		if (element instanceof IFile) {
			editorInput = new JsFileEditorInput((IFile) element);
		}
		return editorInput;
	}

	public void setAttribute(String attribute, Object value) {
		// TODO Auto-generated method stub

	}

}
