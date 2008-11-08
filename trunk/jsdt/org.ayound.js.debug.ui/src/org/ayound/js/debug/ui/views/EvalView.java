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

package org.ayound.js.debug.ui.views;

import java.util.List;

import org.ayound.js.debug.core.IEvalListener;
import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.model.ExpressionModel;
import org.ayound.js.debug.model.JsDebugStackFrame;
import org.ayound.js.debug.ui.expression.JsExpressionAddAction;
import org.ayound.js.debug.ui.expression.JsExpressionRemoveAction;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;

public class EvalView extends AbstractDebugView implements ISelectionListener,
		IEvalListener {

	private Text valueText;

	@Override
	protected Viewer createViewer(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.makeColumnsEqualWidth = true;
		parent.setLayout(layout);
		Table table = new Table(parent, SWT.NONE);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setWidth(200);
		column1.setResizable(true);
		table.setHeaderVisible(false);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableViewer viewer = new TableViewer(table);
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, this);
		JsDebugCorePlugin.getDefault().addEvalListener(this);
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof List) {
					return ((List) inputElement).toArray();
				}
				return null;
			}

			public void dispose() {
				// TODO Auto-generated method stub

			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub

			}
		});
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof ExpressionModel) {
					ExpressionModel model = (ExpressionModel) element;
					if (columnIndex == 0) {
						if (model.getError() != null) {
							return model.getExpression() + " = error : "
									+ model.getError();
						} else {
							return model.getExpression() + " = "
									+ model.getResult();
						}
					}
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			public void dispose() {
				// TODO Auto-generated method stub

			}

			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				ExpressionModel model = (ExpressionModel) selection
						.getFirstElement();
				if (model != null) {
					if (model.getError() != null) {
						valueText.setText(model.getError());
					} else {
						valueText.setText(model.getResult());
					}
				} else {
					valueText.setText("");
				}

			}
		});
		valueText = new Text(parent, SWT.MULTI);
		valueText.setLayoutData(new GridData(GridData.FILL_BOTH));
		return viewer;
	}

	@Override
	protected void fillContextMenu(IMenuManager menu) {
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new JsExpressionAddAction(this, "add watch experssion", null));
		menu.add(new JsExpressionRemoveAction(this, "remove watch expression",
				null));
	}

	@Override
	protected String getHelpContextId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IAdaptable adaptable = DebugUITools.getDebugContext();
		if (adaptable != null) {
			if (adaptable instanceof IDebugElement) {
				JsDebugStackFrame frame = (JsDebugStackFrame) adaptable
						.getAdapter(JsDebugStackFrame.class);
				if (frame != null) {
					if (this.getViewer() != null) {
						this.getViewer().setInput(frame.getExpressionResult());
						this.getViewer().refresh();
					}
				}
			}
		}

	}

	public void update(JsDebugStackFrame frame) {
		if (frame != null) {
			if (this.getViewer() != null) {
				this.getViewer().setInput(frame.getExpressionResult());
				this.getViewer().refresh();
			}
		}
	}

	@Override
	protected void configureToolBar(IToolBarManager tbm) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub

	}

}
