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
package org.ayound.js.debug.ui.views;

import java.util.HashSet;
import java.util.Set;

import org.ayound.js.debug.core.IResourceListener;
import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.model.JsDebugTarget;
import org.ayound.js.debug.server.IDebugServer;
import org.ayound.js.debug.ui.JsDebugUIPlugin;
import org.ayound.js.debug.ui.JsFileEditorInput;
import org.ayound.js.debug.ui.editor.JsEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

public class ResourceView extends AbstractDebugView implements
		ISelectionListener, IResourceListener {

	public ResourceView() {
		super();
		JsDebugCorePlugin.getDefault().addResourceListener(this);
	}

	@Override
	protected void configureToolBar(IToolBarManager tbm) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Viewer createViewer(Composite parent) {
		ListViewer viewer = new ListViewer(parent);
		getSite().setSelectionProvider(viewer);
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, this);
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
				// TODO Auto-generated method stub

			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub

			}

			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection.size() == 1) {
					String resource = (String) selection.getFirstElement();
					openFile(resource);
				}

			}
		});

		return viewer;
	}

	private void openFile(String resource) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(resource);
		if(res instanceof IFile){			
			JsFileEditorInput input = new JsFileEditorInput((IFile)res);
			try {
				IWorkbenchPage page = JsDebugUIPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
				IEditorPart[] editors = page.getEditors();
				for (IEditorPart editor : editors) {
					if (editor instanceof JsEditor) {
						FileEditorInput jsInput = (FileEditorInput) editor
						.getEditorInput();
						if (jsInput.equals(input)) {
							page.closeEditor(editor, false);
						}
					}
				}
				IDE.openEditor(page, input,
						"org.ayound.js.debug.ui.editor.JsEditor", true);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void closeFile(String resource) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(resource);
		if(res instanceof IFile){			
			JsFileEditorInput input = new JsFileEditorInput((IFile)res);
			IWorkbenchPage page = JsDebugUIPlugin.getDefault().getWorkbench()
			.getActiveWorkbenchWindow().getActivePage();
			IEditorPart[] editors = page.getEditors();
			for (IEditorPart editor : editors) {
				if (editor instanceof JsEditor) {
					FileEditorInput jsInput = (FileEditorInput) editor
					.getEditorInput();
					if (jsInput.equals(input)) {
						page.closeEditor(editor, false);
					}
				}
			}
		}
	}

	@Override
	protected void fillContextMenu(IMenuManager menu) {
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	protected String getHelpContextId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void resourceChange(String[] resources) {
		try {
			getViewer().setInput(resources);
			getViewer().refresh();
		} catch (Exception e) {
		}
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService()
				.removePostSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW,
						this);
		super.dispose();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IAdaptable adaptable = DebugUITools.getDebugContext();
		if (adaptable != null) {
			if (adaptable instanceof IDebugElement) {
				IDebugElement element = (IDebugElement) adaptable
						.getAdapter(IDebugElement.class);
				if (element != null) {
					if (element.getModelIdentifier().equals(
							JsDebugCorePlugin.MODEL_ID)) {
						JsDebugTarget fTarget = (JsDebugTarget) element
								.getDebugTarget();
						IDebugServer server = fTarget.getServer();
						if (server.isRunning()) {
							if (getViewer() != null) {
								for (String resource : server.getResources()) {
									addResource(resource);
								}
							}
						}
					}
				}
			} else if (adaptable instanceof ILaunch) {
				ILaunch launch = (ILaunch) adaptable.getAdapter(ILaunch.class);
				JsDebugTarget fTarget = (JsDebugTarget) launch.getDebugTarget();
				IDebugServer server = fTarget.getServer();
				if (server.isRunning()) {
					if (fTarget != null && getViewer() != null) {
						for (String resource : server.getResources()) {
							addResource(resource);
						}
					}
				}
			}
		}

	}

	private void addResource(String resource) {
		if (getViewer() != null) {
			Object input = getViewer().getInput();
			if (input != null && input instanceof String[]) {
				String[] arr = (String[]) input;
				Set<String> set = new HashSet<String>();
				for (String str : arr) {
					set.add(str);
				}
				set.add(resource);
				getViewer().setInput(set.toArray(new String[set.size()]));
			} else {
				getViewer().setInput(new String[] { resource });
			}
			getViewer().refresh();
		}

	}

	public void addResource(String resource, IDebugServer server) {
		addResource(resource);
		openFile(resource);
	}

	public void removeResource(String resource, IDebugServer server) {
		if (getViewer() != null) {
			Object input = getViewer().getInput();
			if (input != null && input instanceof String[]) {
				String[] arr = (String[]) input;
				Set<String> set = new HashSet<String>();
				for (String str : arr) {
					if (!resource.equals(str)) {
						set.add(str);
					}
				}
				getViewer().setInput(set.toArray(new String[set.size()]));
			}
			getViewer().refresh();
		}

	}
}
