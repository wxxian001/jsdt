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
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ayound.js.debug.listener.IDebugListener;
import org.ayound.js.debug.listener.IFrameSelectionListener;
import org.ayound.js.debug.listener.IStackFrameListener;
import org.ayound.js.debug.model.DebugInfo;
import org.ayound.js.debug.model.DebugStackFrame;
import org.ayound.js.debug.model.ErrorStackFrame;
import org.ayound.js.debug.server.JsDebugResponse;
import org.ayound.js.debug.util.DebugManager;
import org.ayound.js.debug.util.JsDebugUtil;

public class DebugStackPanel extends JPanel {

	private Action actionResume, actionStepInto, actionStepOver,
			actionStepReturn;

	private JTree stackTree;

	private DefaultMutableTreeNode treeRoot;

	public DebugStackPanel() {
		super();
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		initAction();
		initToolBar();
		initStackTree();
		addDebugListener();
	}

	private void initStackTree() {
		treeRoot = new DefaultMutableTreeNode(Messages
				.getString("DebugStackPanel.DebugStackFrames")); //$NON-NLS-1$
		stackTree = new JTree(treeRoot);
		final ImageIcon jsIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/js.png")); //$NON-NLS-1$
		final ImageIcon frameIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/frame.gif")); //$NON-NLS-1$
		stackTree.setCellRenderer(new DefaultTreeCellRenderer() {

			@Override
			public Icon getClosedIcon() {
				return jsIcon;
			}

			@Override
			public Icon getLeafIcon() {
				if (stackTree.getRowCount() == 1) {
					return jsIcon;
				} else {
					return frameIcon;
				}
			}

			@Override
			public Icon getOpenIcon() {
				return jsIcon;
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 16);// super.getPreferredSize();
			}

		});
		stackTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// stackTree.setAutoscrolls(true);
		// JScrollPane scrollPane = new JScrollPane(stackTree);
		add(stackTree, BorderLayout.CENTER);
		stackTree.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent event) {

				Object obj = stackTree.getLastSelectedPathComponent();
				if (obj instanceof DefaultMutableTreeNode) {
					Object userObject = ((DefaultMutableTreeNode) obj)
							.getUserObject();
					if (userObject instanceof DebugStackFrame) {
						DebugManager.selectFrame((DebugStackFrame) userObject);
					}
				}

			}
		});

	}

	private void addDebugListener() {
		JsDebugUtil.addDebugListener(new IDebugListener() {

			public void endDebug() {
				for (int i = 0; i < treeRoot.getChildCount(); i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeRoot
							.getChildAt(i);
					Object userObj = child.getUserObject();
					if (userObj instanceof DebugStackFrame) {
						DebugStackFrame childFrame = (DebugStackFrame) userObj;
						if (!childFrame.isTerminated()) {
							childFrame.getResponse().writeTerminate();
						}
						childFrame.setTerminated(true);
					}
				}
				treeRoot.setUserObject("data stack frames");
				treeRoot.removeAllChildren();
				stackTree.updateUI();
			}

			public void startDebug(DebugInfo info) {
				treeRoot.setUserObject("[debug:" + info.getPort() + "]" //$NON-NLS-1$ //$NON-NLS-2$
						+ info.getUrl());
				stackTree.updateUI();

			}
		});
		DebugManager.addStackFrameListener(new IStackFrameListener() {

			public void addErrorFrame(ErrorStackFrame frame) {
				addNode(frame);
			}

			public void addStackFrame(DebugStackFrame frame) {
				addNode(frame);
			}
		});
		DebugManager
				.addStackFrameSelectionListener(new IFrameSelectionListener() {

					public void selectFrame(DebugStackFrame frame) {
						if ((frame.getResponse() == null || frame
								.isTerminated())) {
							updateActions(false);
						} else {
							updateActions(true);
						}

					}
				});
	}

	private void updateActions(boolean status) {
		actionResume.setEnabled(status);
		actionStepInto.setEnabled(status);
		actionStepOver.setEnabled(status);
		actionStepReturn.setEnabled(status);
	}

	private void addNode(DebugStackFrame frame) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(frame);

		treeRoot.insert(node, 0);
		stackTree.updateUI();
		TreePath visiblePath = new TreePath(((DefaultTreeModel) stackTree
				.getModel()).getPathToRoot(node));
		stackTree.setSelectionPath(visiblePath);
	}

	private void initToolBar() {
		JToolBar toolBar = new JToolBar();
		JButton resumeBtn = toolBar.add(actionResume);
		resumeBtn.setToolTipText(Messages.getString("DebugStackPanel.Resume")); //$NON-NLS-1$

		JButton stepIntoBtn = toolBar.add(actionStepInto);
		stepIntoBtn.setToolTipText(Messages
				.getString("DebugStackPanel.StepInto")); //$NON-NLS-1$

		JButton stepOverBtn = toolBar.add(actionStepOver);
		stepOverBtn.setToolTipText(Messages
				.getString("DebugStackPanel.StepOver")); //$NON-NLS-1$

		JButton stepReturnBtn = toolBar.add(actionStepReturn);
		stepReturnBtn.setToolTipText(Messages
				.getString("DebugStackPanel.StepReturn")); //$NON-NLS-1$
		add(toolBar, BorderLayout.NORTH);

	}

	private void executedCurrentFrame() {
		DebugManager.getCurrentFrame().setTerminated(true);
		DebugManager.getCurrentFrame().setResponse(null);
		updateActions(false);
		stackTree.updateUI();
	}

	private void initAction() {
		// TODO Auto-generated method stub
		ImageIcon resumeIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/resume_co.gif")); //$NON-NLS-1$
		actionResume = new AbstractAction(Messages
				.getString("DebugStackPanel.Resume"), resumeIcon) {
			public void actionPerformed(ActionEvent e) {
				JsDebugResponse response = DebugManager.getCurrentResponse();
				if (response != null) {
					response.writeResume();
					response.close();
					executedCurrentFrame();
				}
			}
		};
		ImageIcon stepIntoIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/stepinto_co.gif")); //$NON-NLS-1$
		actionStepInto = new AbstractAction(Messages
				.getString("DebugStackPanel.StepInto"), stepIntoIcon) {
			public void actionPerformed(ActionEvent e) {
				JsDebugResponse response = DebugManager.getCurrentResponse();
				if (response != null) {
					response.writeStepInTo();
					response.close();
					executedCurrentFrame();
				}
			}
		};

		ImageIcon stepOverIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/stepover_co.gif")); //$NON-NLS-1$
		actionStepOver = new AbstractAction(Messages
				.getString("DebugStackPanel.StepOver"), stepOverIcon) {
			public void actionPerformed(ActionEvent e) {
				JsDebugResponse response = DebugManager.getCurrentResponse();
				if (response != null) {
					response.writeStepOver();
					response.close();
					executedCurrentFrame();
				}
			}
		};

		ImageIcon stepReturnIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/stepreturn_co.gif")); //$NON-NLS-1$
		actionStepReturn = new AbstractAction(Messages
				.getString("DebugStackPanel.StepReturn"), stepReturnIcon) {
			public void actionPerformed(ActionEvent e) {
				JsDebugResponse response = DebugManager.getCurrentResponse();
				if (response != null) {
					response.writeStepReturn();
					response.close();
					executedCurrentFrame();
				}
			}
		};
		updateActions(false);
	}

}
