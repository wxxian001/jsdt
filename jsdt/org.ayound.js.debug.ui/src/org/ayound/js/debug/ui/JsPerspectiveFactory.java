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
 * Created on 2008-11-7
 *******************************************************************************/

package org.ayound.js.debug.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JsPerspectiveFactory implements IPerspectiveFactory {

	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		IFolderLayout consoleFolder = layout.createFolder("bottom",
				IPageLayout.BOTTOM, (float) 0.75, layout.getEditorArea());
		consoleFolder.addView(IPageLayout.ID_TASK_LIST);

		IFolderLayout navFolder = layout.createFolder("top", IPageLayout.TOP,
				(float) 0.45, layout.getEditorArea());
		navFolder.addView(IDebugUIConstants.ID_DEBUG_VIEW);
		navFolder.addPlaceholder(IPageLayout.ID_RES_NAV);

		IFolderLayout toolsFolder = layout.createFolder("right",
				IPageLayout.RIGHT, (float) 0.40, "top");
		toolsFolder.addView(IDebugUIConstants.ID_VARIABLE_VIEW);
		toolsFolder.addView(IDebugUIConstants.ID_BREAKPOINT_VIEW);
		IFolderLayout expressionFolder = layout.createFolder("expression",
				IPageLayout.RIGHT, (float) 0.50, "right");
		expressionFolder.addView("org.ayound.js.debug.ui.ExpressionView");
		IFolderLayout resourceFolder = layout.createFolder("resource",
				IPageLayout.RIGHT, (float) 0.75, layout.getEditorArea());
		resourceFolder.addView("org.ayound.js.debug.ui.resourceView");
	}
}
