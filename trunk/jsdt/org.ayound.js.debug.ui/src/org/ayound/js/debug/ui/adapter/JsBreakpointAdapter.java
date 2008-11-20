package org.ayound.js.debug.ui.adapter;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.engine.EngineManager;
import org.ayound.js.debug.engine.IJsEngine;
import org.ayound.js.debug.model.JsBreakPoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class JsBreakpointAdapter implements IToggleBreakpointsTargetExtension {

	public boolean canToggleBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return canToggleLineBreakpoints(part, selection);
	}

	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		toggleLineBreakpoints(part, selection);

	}

	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return getEditor(part) != null;
	}

	public boolean canToggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	public boolean canToggleWatchpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor != null) {
			IFileEditorInput editorInput = (IFileEditorInput) textEditor
					.getEditorInput();
			IFile resource = editorInput.getFile();
			ITextSelection textSelection = (ITextSelection) selection;
			int rulerLine = textSelection.getStartLine() + 1;
			int breakLine = getBreakPointLine(textEditor, rulerLine);
			if (breakLine < 1) {
				return;
			}
			if (rulerLine == breakLine) {
				try {
					IBreakpoint breakPoints[] = DebugPlugin.getDefault()
							.getBreakpointManager().getBreakpoints(
									JsDebugCorePlugin.MODEL_ID);
					boolean hasBreakPoint = false;
					for (IBreakpoint point : breakPoints) {
						if (point instanceof JsBreakPoint) {
							if (point.getMarker().getResource()
									.equals(resource)
									&& point.getMarker().getAttribute(
											IMarker.LINE_NUMBER, 0) == breakLine) {
								point.delete();
								hasBreakPoint = true;
								break;
							}
						}
					}
					if (!hasBreakPoint) {
						DebugPlugin.getDefault().getBreakpointManager()
								.addBreakpoint(
										new JsBreakPoint(editorInput.getFile(),
												breakLine));
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void toggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) throws CoreException {
		// TODO Auto-generated method stub

	}

	private int getBreakPointLine(ITextEditor textEditor, int lineNum) {
		IFileEditorInput editorInput = (IFileEditorInput) textEditor
				.getEditorInput();
		IFile resource = editorInput.getFile();
		int rulerLine = lineNum;
		IJsEngine engine = EngineManager.getEngine();
		engine.compileFile(resource);
		String jsResource = resource.getFullPath().toString();
		if (engine.canBreakLine(jsResource, rulerLine)) {
			return rulerLine;
		} else {
			for (int i = rulerLine; i > 0; i--) {
				if (engine.canBreakLine(jsResource, i)) {
					return i;
				}
			}
		}
		return 0;
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	private ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) part;
			IResource resource = (IResource) editorPart.getEditorInput()
					.getAdapter(IResource.class);
			if (resource != null) {
				return editorPart;
			}
		}
		return null;
	}
}
