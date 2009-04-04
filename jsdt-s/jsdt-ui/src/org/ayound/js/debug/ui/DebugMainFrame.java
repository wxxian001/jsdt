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
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.ayound.ext.BookMarkListenerManager;
import org.ayound.ext.IBookMarkListener;
import org.ayound.js.debug.core.IResourceListener;
import org.ayound.js.debug.core.ResourceListenerManager;
import org.ayound.js.debug.listener.IDebugListener;
import org.ayound.js.debug.listener.IFrameSelectionListener;
import org.ayound.js.debug.model.DebugInfo;
import org.ayound.js.debug.model.DebugStackFrame;
import org.ayound.js.debug.ui.editor.AbstractEditor;
import org.ayound.js.debug.ui.editor.HtmlEditor;
import org.ayound.js.debug.ui.editor.JsEditor;
import org.ayound.js.debug.ui.util.ConfigUtil;
import org.ayound.js.debug.ui.util.DebugUIUtil;
import org.ayound.js.debug.ui.widget.CloseableTabbedPane;
import org.ayound.js.debug.util.BreakPointManager;
import org.ayound.js.debug.util.DebugManager;
import org.ayound.js.debug.util.JsDebugUtil;
import org.ayound.js.debug.util.PathUtil;

import sun.awt.VerticalBagLayout;

public class DebugMainFrame extends JFrame {

	protected JMenuItem[] fontMenus;

	protected JToolBar toolBar;

	protected CloseableTabbedPane mainPane;

	protected Action actionOpen, actionClose, actionExit, actionDebugStart,
			actionDebugEnd, actionHelp, actionAbout, actionLanguageChinese,
			actionLanguageEnglish;

	private JTextField urlText;

	private JButton urlButton;

	private JTextField portText;

	private JTextField browserText;

	private JButton browserButton;

	public DebugMainFrame() throws HeadlessException {
		super(Messages.getString("DebugMainFrame.ApplicationName")); //$NON-NLS-1$
		initAction();
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
		initToolBar();
		setSize(800, 600);
		ImageIcon jsIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/js.png")); //$NON-NLS-1$
		setIconImage(jsIcon.getImage());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		initMainLayout();
		setVisible(true);
		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(wndCloser);
		initListener();
		initReadMe();
	}

	private void initListener() {
		JsDebugUtil.addDebugListener(new IDebugListener() {

			public void endDebug() {
				updateButtons(true);
			}

			public void startDebug(DebugInfo info) {
				updateButtons(false);
				ConfigUtil.writeProppertie("url", urlText.getText());
				ConfigUtil.writeProppertie("browser", browserText.getText());
			}
		});
		ResourceListenerManager.addListener(new IResourceListener() {

			public void addResource(String resource) {
				DebugUIUtil.openFile(resource);

			}

			public void removeResource(String resource) {
			}
		});
		DebugManager
				.addStackFrameSelectionListener(new IFrameSelectionListener() {

					public void selectFrame(DebugStackFrame frame) {
						DebugUIUtil.openFile(frame.getResource());
						Component component = mainPane.getSelectedComponent();
						if (component instanceof AbstractEditor) {
							final AbstractEditor editor = (AbstractEditor) component;
							if (editor.getFilePath().replace('\\', '/').equals(
									frame.getResource())) {
								editor.focus(frame.getLineNumber());
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										DebugMainFrame.this.setVisible(true);
										editor.grabFocus();
									}
								});
							}
						}

					}
				});
	}

	private void initMainLayout() {
		final JSplitPane bottomSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, createMainPane(), createDebugPane());
		bottomSplit.setDividerLocation(0.7);
		final JSplitPane totalSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, createDebugContextPane(),
				bottomSplit);
		totalSplit.setDividerLocation(0.25);
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				bottomSplit.setDividerLocation(0.7);
				totalSplit.setDividerLocation(0.25);
			}

		});

		add(totalSplit);
	}

	private Component createDebugPane() {
		JTabbedPane debugPane = new JTabbedPane();
		debugPane
				.addTab(
						Messages.getString("DebugMainFrame.Varibles"), new DebugContextPanel()); //$NON-NLS-1$
		debugPane
				.addTab(
						Messages.getString("DebugMainFrame.BreakPoints"), new BreakPointManagerPanel()); //$NON-NLS-1$
		debugPane
				.addTab(
						Messages.getString("DebugMainFrame.Expressions"), new ExpressionManagerPanel()); //$NON-NLS-1$
		return debugPane;

	}

	private Component createMainPane() {
		mainPane = new CloseableTabbedPane();
		mainPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				Object source = e.getSource();
				if (source instanceof JTabbedPane) {
					JTabbedPane pane = (JTabbedPane) source;
					int index = pane.getSelectedIndex();
					if (index > -1) {
						String tips = pane.getToolTipTextAt(index);
						DebugUIUtil.setCurrentFile(tips);
					}
				}
			}
		});
		BookMarkListenerManager.setListener(new IBookMarkListener() {

			public boolean beforeAddBookmark(int line) {
//				if (EngineManager.getEngine().canBreakLine(
//						DebugUIUtil.getCurrentFile(), line + 1)) {
//					BreakPointManager.addBreakPoint(DebugUIUtil
//							.getCurrentFile(), line);
//					return true;
//				} else {
//					return false;
//				}
				BreakPointManager.addBreakPoint(DebugUIUtil
						.getCurrentFile(), line);
				return true;
			}

			public boolean beforeRemoveBookmark(int line) {
				BreakPointManager.removeBreakPoint(
						DebugUIUtil.getCurrentFile(), line,true);
				return true;
			}
		});

		return mainPane;

	}

	private void initReadMe(){

		HtmlEditor editor = new HtmlEditor();
		File file = new File(DebugUIUtil.getReadMePath());
		try {
			editor.setText(FileUtils.readFileToString(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainPane.addTab(file.getName(), null, editor, file.getAbsolutePath());
		editor.firePropertyChange("isClosable", true, false);
		mainPane.setSelectedComponent(editor);
	}

	private Component createDebugContextPane() {
		JPanel debugContextPanel = new JPanel();
		debugContextPanel.setLayout(new VerticalBagLayout());
		JTabbedPane debugTreePane = new JTabbedPane();
		DebugStackPanel treePanel = new DebugStackPanel();
		debugTreePane.addTab(
				Messages.getString("DebugMainFrame.Debug"), treePanel); //$NON-NLS-1$
		JsResourcePanel resourcePanel = new JsResourcePanel();
		debugTreePane.addTab(
				Messages.getString("DebugMainFrame.Resources"), resourcePanel); //$NON-NLS-1$
		debugContextPanel.add(debugTreePane);
		return debugContextPanel;
	}

	public void openHtmlFile(File file) {

		for (int i = 0; i < mainPane.countComponents(); i++) {
			String tips = mainPane.getToolTipTextAt(i);
			if (file.equals(new File(tips))) {
				mainPane.setSelectedIndex(i);
				return;
			}
		}
		HtmlEditor editor = new HtmlEditor();
		editor.openFile(file);
		mainPane.addTab(file.getName(), null, editor, file.getAbsolutePath());
		mainPane.setSelectedComponent(editor);

	}

	public void openJsFile(File file) {

		for (int i = 0; i < mainPane.countComponents(); i++) {
			String tips = mainPane.getToolTipTextAt(i);
			if (file.equals(new File(tips))) {
				mainPane.setSelectedIndex(i);
				return;
			}
		}
		JsEditor editor = new JsEditor();
		editor.openFile(file);
		mainPane.addTab(file.getName(), null, editor, file.getAbsolutePath());
		mainPane.setSelectedComponent(editor);

	}

	private void initAction() {
		actionOpen = new AbstractAction(Messages
				.getString("DebugMainFrame.Open")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileDialog = new JFileChooser(); // 文件选择器
				fileDialog.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						String fileName = f.getName().toLowerCase();
						if (fileName.endsWith(".htm") //$NON-NLS-1$
								|| fileName.endsWith(".html") //$NON-NLS-1$
								|| fileName.endsWith(".js") || f.isDirectory()) { //$NON-NLS-1$
							return true;
						} else {
							return false;
						}

					}

					@Override
					public String getDescription() {
						return ".htm,.html,.js"; //$NON-NLS-1$
					}
				});
				int result = fileDialog.showOpenDialog(DebugMainFrame.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					openHtmlFile(fileDialog.getSelectedFile());
				}

			}
		};
		actionClose = new AbstractAction(Messages
				.getString("DebugMainFrame.Close")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				int index = mainPane.getSelectedIndex();
				if (index > -1) {
					mainPane.remove(index);
				}
			}
		};
		actionExit = new AbstractAction(Messages
				.getString("DebugMainFrame.Exit")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		ImageIcon startIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/launch_run.gif")); //$NON-NLS-1$
		actionDebugStart = new AbstractAction(Messages
				.getString("DebugMainFrame.Start"), startIcon) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				startDebug();
			}
		};
		ImageIcon endIcon = new ImageIcon(DebugMainFrame.class
				.getResource("icons/terminate_co.gif")); //$NON-NLS-1$
		actionDebugEnd = new AbstractAction(Messages
				.getString("DebugMainFrame.End"), endIcon) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				endDebug();
			}
		};
		actionDebugEnd.setEnabled(false);
		actionHelp = new AbstractAction(Messages
				.getString("DebugMainFrame.Content")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				String locale = DebugMainFrame.this.getLocale().toString();
				String helpPath = "help/index_" + locale + ".html";
				File testFile = new File(
						new File(getBaseDir()),
						helpPath); //$NON-NLS-1$
				final String url = "file:///" + testFile.getAbsolutePath().replace('\\', '/'); //$NON-NLS-1$

				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						try {
							// TODO Auto-generated method stub
							JFrame someWindow = new JFrame();
							JEditorPane htmlPane = new JEditorPane(url);
							htmlPane.setEditable(false);
							someWindow.setSize(800, 600);
							someWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
							someWindow.setVisible(true);
							someWindow.add(new JScrollPane(htmlPane));
						} catch (IOException ioe) {
							System.err
									.println(Messages
											.getString("DebugMainFrame.ErrorDisplay") + url); //$NON-NLS-1$
						}
					}
				});

			}
		};
		actionAbout = new AbstractAction(Messages
				.getString("DebugMainFrame.About")) { //$NON-NLS-1$
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								DebugMainFrame.this,
								Messages
										.getString("DebugMainFrame.AboutContent"), //$NON-NLS-1$
								Messages
										.getString("DebugMainFrame.ApplicationName"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			}
		};
		actionLanguageChinese = new AbstractAction("Chinese") {

			public void actionPerformed(ActionEvent e) {
				DebugUIUtil.updateUI(Locale.SIMPLIFIED_CHINESE);
			}
		};
		actionLanguageEnglish = new AbstractAction("English") {

			public void actionPerformed(ActionEvent e) {
				DebugUIUtil.updateUI(Locale.ENGLISH);
			}
		};
	}

	private void updateButtons(boolean enable) {
		urlText.setEditable(enable);
		urlButton.setEnabled(enable);
		portText.setEditable(enable);
		browserText.setEditable(enable);
		browserButton.setEnabled(enable);
		actionDebugStart.setEnabled(enable);
		actionDebugEnd.setEnabled(!enable);
	}

	private void startDebug() {
		DebugInfo info = new DebugInfo();
		info.setBrowser(browserText.getText());
		info.setPort(Integer.parseInt(portText.getText()));
		info.setUrl(urlText.getText());
		JsDebugUtil.startDebug(info);
	}

	private void endDebug() {
		JsDebugUtil.endDebug();
	}

	protected JMenuBar createMenuBar() {
		final JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(Messages.getString("DebugMainFrame.File")); //$NON-NLS-1$
		fileMenu.setMnemonic('f');
		JMenuItem item = null;

		item = fileMenu.add(actionOpen);
		fileMenu.add(item);

		item = fileMenu.add(actionClose);
		fileMenu.add(item);

		fileMenu.addSeparator();

		item = fileMenu.add(actionExit);
		item.setMnemonic('x');
		menuBar.add(fileMenu);

		JMenu debugMenu = new JMenu(Messages.getString("DebugMainFrame.Debug")); //$NON-NLS-1$
		debugMenu.setMnemonic('d');

		item = debugMenu.add(actionDebugStart);
		debugMenu.add(item);

		menuBar.add(debugMenu);

		item = debugMenu.add(actionDebugEnd);
		debugMenu.add(item);

		JMenu helpMenu = new JMenu(Messages.getString("DebugMainFrame.Help")); //$NON-NLS-1$
		helpMenu.setMnemonic('d');

		item = helpMenu.add(actionHelp);
		helpMenu.add(item);

		item = helpMenu.add(actionAbout);
		helpMenu.add(item);

		JMenu languageMenu = new JMenu("lauguage");

		item = languageMenu.add(actionLanguageChinese);
		languageMenu.add(item);

		item = languageMenu.add(actionLanguageEnglish);
		languageMenu.add(item);

		menuBar.add(languageMenu);

		menuBar.add(helpMenu);

		return menuBar;
	}

	private void initToolBar() {
		toolBar = new JToolBar();

		Container debugInfoContainer = new Box(BoxLayout.LINE_AXIS);
		toolBar.add(debugInfoContainer);

		JLabel urlLabel = new JLabel(Messages
				.getString("DebugMainFrame.UrlLabel")); //$NON-NLS-1$
		debugInfoContainer.add(urlLabel);

		urlText = new JTextField(20);
		String historyUrl = ConfigUtil.getPropertie("url");
		if (historyUrl == null) {
			File testFile = new File(new File(getBaseDir()),
					"test/test.htm"); //$NON-NLS-1$
			urlText.setText(testFile.getAbsolutePath().replace('\\', '/'));
		} else {
			urlText.setText(historyUrl);
		}
		debugInfoContainer.add(urlText);

		urlButton = new JButton(Messages.getString("DebugMainFrame.Select")); //$NON-NLS-1$
		urlButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileDialog = new JFileChooser(); // 文件选择器
				fileDialog.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						String fileName = f.getName().toLowerCase();
						if (fileName.endsWith(".htm") //$NON-NLS-1$
								|| fileName.endsWith(".html") //$NON-NLS-1$
								|| f.isDirectory()) {
							return true;
						} else {
							return false;
						}

					}

					@Override
					public String getDescription() {
						return ".htm,.html"; //$NON-NLS-1$
					}
				});
				int result = fileDialog.showOpenDialog(DebugMainFrame.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					urlText.setText(fileDialog.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});
		debugInfoContainer.add(urlButton);

		JLabel portLabel = new JLabel(Messages
				.getString("DebugMainFrame.PortLabel")); //$NON-NLS-1$
		debugInfoContainer.add(portLabel);

		portText = new JTextField(2);
		portText.setText("8088"); //$NON-NLS-1$
		debugInfoContainer.add(portText);

		JLabel browserLabel = new JLabel(Messages
				.getString("DebugMainFrame.BrowserLabel")); //$NON-NLS-1$
		debugInfoContainer.add(browserLabel);

		browserText = new JTextField(20);
		debugInfoContainer.add(browserText);
		String historyBrowser = ConfigUtil.getPropertie("browser");
		if (historyBrowser != null) {
			browserText.setText(historyBrowser);
			// .setText("C:\\Program Files\\Internet Explorer\\iexplore.exe");
			// //$NON-NLS-1$
		}

		browserButton = new JButton(Messages.getString("DebugMainFrame.Select")); //$NON-NLS-1$
		browserButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileDialog = new JFileChooser(); // 文件选择器
				fileDialog.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						String fileName = f.getName().toLowerCase();
						if (fileName.endsWith(".exe")|| f.isDirectory()) { //$NON-NLS-1$
							return true;
						} else {
							return false;
						}

					}

					@Override
					public String getDescription() {
						return ".exe(windows)"; //$NON-NLS-1$
					}
				});
				int result = fileDialog.showOpenDialog(DebugMainFrame.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					browserText.setText(fileDialog.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});
		debugInfoContainer.add(browserButton);

		toolBar.addSeparator();

		JButton startBtn = toolBar.add(actionDebugStart);
		startBtn
				.setToolTipText(Messages.getString("DebugMainFrame.StartDebug")); //$NON-NLS-1$

		JButton endBtn = toolBar.add(actionDebugEnd);
		endBtn.setToolTipText(Messages.getString("DebugMainFrame.EndDebug")); //$NON-NLS-1$

		getContentPane().add(toolBar, BorderLayout.BEFORE_FIRST_LINE);
	}

	private static String getBaseDir() {
		return PathUtil.getJsdtHome();
	}

}
