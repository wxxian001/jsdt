/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com
 * All rights reserved.
 * 
 * Created on 2008-10-26
 *******************************************************************************/
package org.ayound.js.debug.ui.breakpoint;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

public class ManageBreakpointRulerActionDelegate extends
		AbstractRulerActionDelegate {

	@Override
	protected IAction createAction(ITextEditor editor,
			IVerticalRulerInfo rulerInfo) {
		return new ManageBreakpointRulerAction(rulerInfo, editor);
	}

}
