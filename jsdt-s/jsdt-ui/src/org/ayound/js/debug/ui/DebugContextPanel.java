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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.ayound.js.debug.listener.IDebugListener;
import org.ayound.js.debug.listener.IFrameSelectionListener;
import org.ayound.js.debug.model.DebugInfo;
import org.ayound.js.debug.model.DebugStackFrame;
import org.ayound.js.debug.ui.widget.treetable.DebugDataModel;
import org.ayound.js.debug.ui.widget.treetable.JTreeTable;
import org.ayound.js.debug.ui.widget.treetable.TreeTableModel;
import org.ayound.js.debug.util.DebugManager;
import org.ayound.js.debug.util.JsDebugUtil;

public class DebugContextPanel extends JPanel {

	private JTreeTable treeTable;

	private DebugDataModel dataModel;

	public DebugContextPanel() {
		super();
		setLayout(new BorderLayout());
		dataModel = new DebugDataModel(new String[]{Messages.getString("DebugContextPanel.Variable"),Messages.getString("DebugContextPanel.Value")}); //$NON-NLS-1$ //$NON-NLS-2$
		treeTable = new JTreeTable(dataModel);
		TableCellRenderer render = treeTable.getDefaultRenderer(TreeTableModel.class);
		if(render!=null && render instanceof JTree){
			final ImageIcon varIcon = new ImageIcon(DebugMainFrame.class
					.getResource("icons/variable.gif")); //$NON-NLS-1$
			((JTree)render).setCellRenderer(new DefaultTreeCellRenderer(){
				@Override
				public Icon getClosedIcon() {
					return varIcon;
				}

				@Override
				public Icon getLeafIcon() {
					return varIcon;
				}

				@Override
				public Icon getOpenIcon() {
					return varIcon;
				}

			});
		}
		JScrollPane scrollPane = new JScrollPane(treeTable);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		add(panel,BorderLayout.CENTER);
		DebugManager.addStackFrameSelectionListener(new IFrameSelectionListener(){

			public void selectFrame(DebugStackFrame frame) {
				dataModel.setVars(frame.getVars());
				treeTable.updateUI();
			}});
		JsDebugUtil.addDebugListener(new IDebugListener(){

			public void endDebug() {
				dataModel.setVars(null);
				treeTable.updateUI();

			}

			public void startDebug(DebugInfo info) {
				// TODO Auto-generated method stub

			}});
	}

}
