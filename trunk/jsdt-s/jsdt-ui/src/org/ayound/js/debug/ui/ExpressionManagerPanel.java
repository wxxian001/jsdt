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
 * Created on 2009-3-27
 *******************************************************************************/

package org.ayound.js.debug.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import org.ayound.js.debug.listener.IDebugListener;
import org.ayound.js.debug.listener.IExperssionListener;
import org.ayound.js.debug.listener.IFrameSelectionListener;
import org.ayound.js.debug.model.DebugInfo;
import org.ayound.js.debug.model.DebugStackFrame;
import org.ayound.js.debug.model.ExpressionManager;
import org.ayound.js.debug.model.ExpressionModel;
import org.ayound.js.debug.server.JsDebugResponse;
import org.ayound.js.debug.util.DebugManager;
import org.ayound.js.debug.util.JsDebugUtil;

public class ExpressionManagerPanel extends JPanel {
	private Action removeExpressionAction, clearExpressionAction,
			addExpressionAction;
	private JTable expressionTable;
	Vector<Vector<String>> expressionData = new Vector<Vector<String>>();

	public ExpressionManagerPanel() {
		super();
		setLayout(new BorderLayout());
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Messages.getString("ExpressionManagerPanel.Variable")); //$NON-NLS-1$
		columnNames.add(Messages.getString("ExpressionManagerPanel.Value")); //$NON-NLS-1$
		expressionTable = new JTable(expressionData, columnNames);
		JScrollPane scrollPane = new JScrollPane(expressionTable);
		add(scrollPane, BorderLayout.CENTER);
		initAction();
		initToolBar();
		initListeners();

	}

	private void initListeners() {
		ExpressionManager.addExpressionListener(new IExperssionListener() {

			public void updateExpression() {
				updateExpressionTable();
			}
		});
		DebugManager
				.addStackFrameSelectionListener(new IFrameSelectionListener() {

					public void selectFrame(DebugStackFrame frame) {
						updateExpressionTable();
						if(frame.getResponse()!=null){
							updateActions(true);
						}else{
							updateActions(false);
						}

					}
				});
		JsDebugUtil.addDebugListener(new IDebugListener() {

			public void endDebug() {
				expressionData.clear();
				expressionTable.updateUI();
			}

			public void startDebug(DebugInfo info) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void updateExpressionTable() {
		expressionData.clear();
		DebugStackFrame frame = DebugManager.getCurrentFrame();
		if (frame != null) {
			for (String expression : frame.getExpressions().keySet()) {
				Vector<String> rowData = new Vector<String>();
				rowData.add(expression);
				ExpressionModel model = frame.getExpressionModel(expression);
				rowData.add(model.getResult());
				expressionData.add(rowData);
			}
		}
		expressionTable.updateUI();
	}

	private void initAction() {
		// TODO Auto-generated method stub
		ImageIcon removeIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/remove.gif")); //$NON-NLS-1$
		removeExpressionAction = new AbstractAction(Messages.getString("ExpressionManagerPanel.RemoveSelectExpression"), //$NON-NLS-1$
				removeIcon) {

			public void actionPerformed(ActionEvent e) {
				int index = expressionTable.getSelectedRow();
				if (index > -1) {
					String expression = expressionData.get(index).get(0);
					ExpressionManager.removeExperssion(expression);
				}
			}
		};
		ImageIcon clearIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/clear.gif")); //$NON-NLS-1$
		clearExpressionAction = new AbstractAction(Messages.getString("ExpressionManagerPanel.RemoveAllExpression"), //$NON-NLS-1$
				clearIcon) {

			public void actionPerformed(ActionEvent e) {
				ExpressionManager.clearExpression();
			}
		};
		ImageIcon addIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/add.gif")); //$NON-NLS-1$
		addExpressionAction = new AbstractAction(Messages.getString("ExpressionManagerPanel.AddExpression"), addIcon) { //$NON-NLS-1$

			public void actionPerformed(ActionEvent e) {
				DebugStackFrame frame = DebugManager.getCurrentFrame();
				if (frame != null) {
					JsDebugResponse response = frame.getResponse();
					if (response != null) {
						String expression = JOptionPane
								.showInputDialog(Messages.getString("ExpressionManagerPanel.PleaseInputExpression")); //$NON-NLS-1$
						if(expression!=null){
							response.writeExpression(expression);
							response.close();
							frame.setResponse(null);
						}
					}
				}

			}
		};
		updateActions(false);
	}

	private void updateActions(boolean status){
		addExpressionAction.setEnabled(status);
		clearExpressionAction.setEnabled(status);
		removeExpressionAction.setEnabled(status);
	}

	private void initToolBar() {
		JToolBar toolbar = new JToolBar();
		JButton addBtn = toolbar.add(addExpressionAction);
		addBtn.setToolTipText(Messages.getString("ExpressionManagerPanel.0")); //$NON-NLS-1$
		JButton removeBtn = toolbar.add(removeExpressionAction);
		removeBtn.setToolTipText(Messages.getString("ExpressionManagerPanel.1")); //$NON-NLS-1$
		JButton clearBtn = toolbar.add(clearExpressionAction);
		clearBtn.setToolTipText(Messages.getString("ExpressionManagerPanel.2")); //$NON-NLS-1$
		add(toolbar, BorderLayout.NORTH);
	}

}
