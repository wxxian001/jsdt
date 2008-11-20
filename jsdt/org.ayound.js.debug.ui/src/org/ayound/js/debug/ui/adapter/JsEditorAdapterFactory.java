package org.ayound.js.debug.ui.adapter;

import org.ayound.js.debug.ui.editor.JsEditor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.texteditor.ITextEditor;

public class JsEditorAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof JsEditor) {
			ITextEditor editorPart = (ITextEditor) adaptableObject;
			IResource resource = (IResource) editorPart.getEditorInput()
					.getAdapter(IResource.class);
			if (resource != null) {
				if (adapterType.equals(IToggleBreakpointsTarget.class)) {
					return new JsBreakpointAdapter();
				}
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IToggleBreakpointsTarget.class };
	}

}
