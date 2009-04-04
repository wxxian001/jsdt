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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.ayound.js.debug.listener.IBreakPointListener;
import org.ayound.js.debug.model.BreakPointModel;
import org.ayound.js.debug.ui.widget.BreakPointListRenderer;
import org.ayound.js.debug.util.BreakPointManager;

public class BreakPointManagerPanel extends JPanel {

	private Action removeBreakPointAction, clearBreakPointAction,
			disableBreakPointAction;

	private JList breakPointsList;

	private Vector<BreakPointModel> breakpoints;

	public BreakPointManagerPanel() {
		super();
		setLayout(new BorderLayout());
		initAction();
		add(createToolBar(),BorderLayout.NORTH);
		add(createBreakPointPane(),BorderLayout.CENTER);
		addBreakPointListener();
	}

	private void addBreakPointListener() {
		BreakPointManager.addBreakPointListener(new IBreakPointListener(){

			public void addBreakPoint(BreakPointModel model) {
				breakPointsList.setListData(BreakPointManager.getAllBreakPoints());
			}

			public void removeBreakPoint(BreakPointModel model) {
				breakPointsList.setListData(BreakPointManager.getAllBreakPoints());

			}

			public void updateBreakPoints() {
				breakPointsList.setListData(BreakPointManager.getAllBreakPoints());

			}});

	}

	private Component createBreakPointPane() {
		BreakPointListRenderer render = new BreakPointListRenderer();
		breakPointsList = new JList(BreakPointManager.getAllBreakPoints());
		breakPointsList.setCellRenderer(render);
		JScrollPane scrollPane = new JScrollPane(breakPointsList);
		return scrollPane;
	}

	private void initAction() {
		ImageIcon removeIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/remove.gif")); //$NON-NLS-1$
		removeBreakPointAction = new AbstractAction(Messages.getString("BreakPointManagerPanel.RemoveSelectBreakPoint"), //$NON-NLS-1$
				removeIcon) {

			public void actionPerformed(ActionEvent e) {
				for(Object value:breakPointsList.getSelectedValues()){
					if(value instanceof BreakPointModel){
						BreakPointModel model = (BreakPointModel)value;
						BreakPointManager.removeBreakPoint(model.getResourcePath(), model.getLineNumber(),false);
					}
				}
			}
		};
		ImageIcon clearIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/clear.gif")); //$NON-NLS-1$
		clearBreakPointAction = new AbstractAction(Messages.getString("BreakPointManagerPanel.RemoveAllBreakPoint"), //$NON-NLS-1$
				clearIcon) {

			public void actionPerformed(ActionEvent e) {
				BreakPointManager.clearAllBreakPoint();
			}
		};
		ImageIcon diableIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/disable.gif")); //$NON-NLS-1$
		disableBreakPointAction = new AbstractAction(Messages.getString("BreakPointManagerPanel.0"), //$NON-NLS-1$
				diableIcon) {

			public void actionPerformed(ActionEvent e) {
				if(BreakPointManager.isBreakPointEnabled()){
					BreakPointManager.disableAllBreakPoint();
				}else{
					BreakPointManager.enableAllBreakPoint();
				}
			}
		};
	}

	private Component createToolBar() {
		JToolBar toolbar = new JToolBar();
		JButton removeBtn = toolbar.add(removeBreakPointAction);
		removeBtn.setToolTipText(Messages.getString("BreakPointManagerPanel.RemoveSelectBreakPoint")); //$NON-NLS-1$
		JButton clearBtn = toolbar.add(clearBreakPointAction);
		clearBtn.setToolTipText(Messages.getString("BreakPointManagerPanel.RemoveAllBreakPoint")); //$NON-NLS-1$
		JButton disableBtn = toolbar.add(disableBreakPointAction);
		disableBtn.setToolTipText(Messages.getString("BreakPointManagerPanel.1")); //$NON-NLS-1$
		return toolbar;
	}

}
