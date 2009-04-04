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

package org.ayound.js.debug.ui.widget;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import org.ayound.js.debug.model.BreakPointModel;
import org.ayound.js.debug.ui.DebugMainFrame;

public class BreakPointListRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		// Get the renderer component from parent class

		JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);

		// Get icon to use for the list item value
		if (value instanceof BreakPointModel) {
			Icon icon = getIcon((BreakPointModel) value);
			label.setIcon(icon);
		}

		// Set icon to display for value

		return label;
	}

	private Icon getIcon(BreakPointModel model) {
		if (model.isEnabled()) {
			return new ImageIcon(DebugMainFrame.class
					.getResource("icons/breakpoint.gif"));
		} else {

			return new ImageIcon(DebugMainFrame.class
					.getResource("icons/breakpointDisable.gif"));
		}
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame("Icon List");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// create a list with some test data
		BreakPointModel model = new BreakPointModel(
				"E:\\资源\\full\\dlcl16\\test1.txt", 10, true);
		BreakPointModel model2 = new BreakPointModel(
				"E:\\资源\\full\\dlcl16\\test2.txt", 1, true);

		JList list = new JList(new Object[] { model, model2 });

		// create a cell renderer to add the appropriate icon

		list.setCellRenderer(new BreakPointListRenderer());
		frame.add(list);
		frame.pack();
		frame.setVisible(true);
	}

}
