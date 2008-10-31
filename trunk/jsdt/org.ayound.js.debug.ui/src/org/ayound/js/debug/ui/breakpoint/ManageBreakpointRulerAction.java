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

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.engine.EngineManager;
import org.ayound.js.debug.engine.IJsEngine;
import org.ayound.js.debug.model.JsBreakPoint;
import org.ayound.js.debug.resource.JsResourceManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

public class ManageBreakpointRulerAction extends Action implements IUpdate {

	private IVerticalRulerInfo rulerInfo;

	private ITextEditor textEditor;

	public ManageBreakpointRulerAction(IVerticalRulerInfo ruler,
			ITextEditor editor) {
		this.rulerInfo = ruler;
		this.textEditor = editor;
		this.setText("Ìí¼Ó/É¾³ý¶Ïµã");
	}

	@Override
	public void run() {
		IFileEditorInput editorInput = (IFileEditorInput) this.getTextEditor()
		.getEditorInput();
		int breakLine = getBreakPointLine();
		if(breakLine<1){
			return;
		}
		try {
			IBreakpoint breakPoints[] = DebugPlugin.getDefault()
					.getBreakpointManager().getBreakpoints(
							JsDebugCorePlugin.MODEL_ID);
			boolean hasBreakPoint = false;
			for (IBreakpoint point : breakPoints) {
				if (point instanceof JsBreakPoint) {
					if (point.getMarker().getResource().equals(
							editorInput.getFile())
							&& point.getMarker().getAttribute(
									IMarker.LINE_NUMBER, 0) == breakLine) {
						point.delete();
						hasBreakPoint = true;
						break;
					}
				}
			}
			if (!hasBreakPoint) {
				DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(
						new JsBreakPoint(editorInput.getFile(), breakLine));
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update() {

	}

	private int getBreakPointLine() {
		IFileEditorInput editorInput = (IFileEditorInput) this.getTextEditor()
				.getEditorInput();
		IFile resource =  editorInput.getFile();
		int rulerLine = this.getRulerInfo().getLineOfLastMouseButtonActivity() + 1;
		IJsEngine engine = EngineManager.getEngine();
		IFolder rootFolder = JsResourceManager.getTempRoot();
		String relativeString = resource.getFullPath().toString().replace(rootFolder.getFullPath().toString() + "/", "");
		String jsResource = relativeString.substring(relativeString.indexOf('/'));
		if(engine.canBreakLine(jsResource, rulerLine)){
			return rulerLine;
		}else{
			for(int i=rulerLine;i>0;i--){
				if(engine.canBreakLine(jsResource, i)){
					return i;
				}
			}
		}
		return 0;
	}

	public IVerticalRulerInfo getRulerInfo() {
		return rulerInfo;
	}

	public ITextEditor getTextEditor() {
		return textEditor;
	}

}
