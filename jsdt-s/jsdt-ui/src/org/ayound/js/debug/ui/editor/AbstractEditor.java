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

package org.ayound.js.debug.ui.editor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JRootPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;
import org.ayound.js.debug.engine.CharsetDetector;
import org.ayound.js.debug.engine.EngineManager;
import org.ayound.js.debug.listener.IBreakPointListener;
import org.ayound.js.debug.model.BreakPointModel;
import org.ayound.js.debug.ui.DebugMainFrame;
import org.ayound.js.debug.util.BreakPointManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

public class AbstractEditor extends JRootPane implements SyntaxConstants {

	public AbstractEditor(String contextTpye) {
		this.contextType = contextTpye;
		init();
	}

	private RTextScrollPane scrollPane;

	private RSyntaxTextArea textArea;

	private String contextType;

	private String filePath;

	private Gutter gutter;

	public void init() {
		textArea = createTextArea();
		scrollPane = new RTextScrollPane(textArea);
		gutter = scrollPane.getGutter();
		gutter.setBookmarkingEnabled(true);
		URL url = DebugMainFrame.class.getResource("icons/breakpoint.gif");
		gutter.setBookmarkIcon(new ImageIcon(url));
		getContentPane().add(scrollPane);
		addBreakPointListener();
	}

	private RSyntaxTextArea createTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(this.getContextType());
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setWrapStyleWord(false);
		return textArea;
	}

	private void addBreakPointListener() {
		BreakPointManager.addBreakPointListener(new IBreakPointListener() {

			public void addBreakPoint(BreakPointModel model) {

			}

			public void removeBreakPoint(BreakPointModel model) {
			}

			public void updateBreakPoints() {
				updateBookMark();
			}

		});
	}

	private void updateBookMark() {
		gutter.removeAllTrackingIcons();
		for (BreakPointModel model : BreakPointManager.getAllBreakPoints()) {
			if (filePath!=null && filePath.equals(model.getResourcePath())) {
				try {
					gutter.addLineTrackingIcon(model.getLineNumber(), gutter
							.getBookmarkIcon());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String getText() {
		return textArea.getText();
	}

	public void setText(String text) {
		textArea.setText(text);
	}

	public void openFile(File file) {
		filePath = file.getAbsolutePath();
		String fileContent;
		try {
			CharsetDetector detector = new CharsetDetector();
			detector.detect(file);
			String encoding = detector.getCharset();
			fileContent = FileUtils.readFileToString(file, encoding);
			setText(fileContent);
			EngineManager.getEngine().compileFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateBookMark();
	}

	public String getFilePath() {
		return filePath;
	}

	public void focus(int line) {
		int offset;
		try {
			offset = textArea.getLineEndOffset(line - 2);
			if (offset > 0) {
				textArea.setCaretPosition(offset);
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

}
