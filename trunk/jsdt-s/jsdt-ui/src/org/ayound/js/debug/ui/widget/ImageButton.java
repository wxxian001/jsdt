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

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class ImageButton extends JButton implements MouseListener {
	protected Border m_raised;

	protected Border m_lowered;

	protected Border m_inactive;

	public ImageButton(Action act, String tip) {
		super((Icon) act.getValue(Action.SMALL_ICON));
		m_raised = new BevelBorder(BevelBorder.RAISED);
		m_lowered = new BevelBorder(BevelBorder.LOWERED);
		m_inactive = new EmptyBorder(2, 2, 2, 2);
		setBorder(m_inactive);
		setMargin(new Insets(1, 1, 1, 1));
		setToolTipText(tip);
		addActionListener(act);
		addMouseListener(this);
		setRequestFocusEnabled(false);
	}

	public float getAlignmentY() {
		return 0.5f;
	}

	public void mousePressed(MouseEvent e) {
		setBorder(m_lowered);
	}

	public void mouseReleased(MouseEvent e) {
		setBorder(m_inactive);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		setBorder(m_raised);
	}

	public void mouseExited(MouseEvent e) {
		setBorder(m_inactive);
	}

}
