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

import org.ayound.js.debug.resource.JsResourceManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

public class JsFileEditorInput extends FileEditorInput {

	public JsFileEditorInput(IFile file) {
		super(file);
	}

	@Override
	public String getName() {
		String rootTemp = JsResourceManager.getTempRoot().getFullPath()
				.toString();
		String filePath = getFile().getFullPath().toString();
		String tempStr = filePath.replace(rootTemp, "").substring(1);
		String port = tempStr.substring(0, tempStr.indexOf('/'));
		return "[" + port + "]" + getFile().getName();
	}

}
