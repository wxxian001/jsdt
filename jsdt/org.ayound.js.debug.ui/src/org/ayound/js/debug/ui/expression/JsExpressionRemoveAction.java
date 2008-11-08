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

package org.ayound.js.debug.ui.expression;

import org.ayound.js.debug.model.ExpressionModel;
import org.ayound.js.debug.model.JsDebugStackFrame;
import org.ayound.js.debug.ui.views.EvalView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;

public class JsExpressionRemoveAction extends Action {
	private EvalView view;

	public JsExpressionRemoveAction(EvalView view,String text,ImageDescriptor imageDescriptor) {
		super(text,imageDescriptor);
		this.view = view;
	}
	public void run() {
		JsDebugStackFrame frame = (JsDebugStackFrame) DebugUITools
		.getDebugContext().getAdapter(JsDebugStackFrame.class);
		if (frame != null) {
			IStructuredSelection selection = (IStructuredSelection)view.getViewer().getSelection();
			Object selectObject = selection.getFirstElement();
			if(selectObject!=null && selectObject instanceof ExpressionModel){					
				ExpressionModel model = (ExpressionModel)selectObject;
				frame.removeExpression(model);
			}
		}

	}

}
