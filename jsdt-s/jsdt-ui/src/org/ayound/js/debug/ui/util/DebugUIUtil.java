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


package org.ayound.js.debug.ui.util;

import java.awt.Toolkit;
import java.io.File;
import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.ayound.js.debug.server.JsDebugServer;
import org.ayound.js.debug.ui.DebugMainFrame;
import org.ayound.js.debug.util.JsDebugUtil;
import org.ayound.js.debug.util.PathUtil;

public class DebugUIUtil {
	private static DebugMainFrame mainFrame;

	private static String currentFile;

	private static Locale currentLocale;

	public static void openHtmlFile(String filePath){
		mainFrame.openHtmlFile(new File(filePath));
	}

	public static void openJsFile(String filePath){
		mainFrame.openJsFile(new File(filePath));
	}

	public void closeFile(String filePath){

	}
	public static void updateUI(Locale locale){
		updataLocale(locale);
		ConfigUtil.writeProppertie("locale", locale.toString());
		JsDebugUtil.endDebug();
		mainFrame.setVisible(false);
		mainFrame.removeAll();
		mainFrame = new DebugMainFrame();
		mainFrame.setLocale(locale);
		mainFrame.setVisible(true);
	}
	private static void updataLocale(Locale locale){
		org.ayound.js.debug.ui.Messages.setLocole(locale);
		org.ayound.js.debug.util.Messages.setLocole(locale);
		setCurrentLocale(locale);
	}

	public static void runDebugUI(){
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		String language = ConfigUtil.getPropertie("locale");
		if(language!=null){
			currentLocale = new Locale(language);
		}else{
			currentLocale = Locale.getDefault();
		}
		updataLocale(currentLocale);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.
							getCrossPlatformLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace(); // Never happens
				}
				mainFrame = new DebugMainFrame();
				mainFrame.setVisible(true);
			}
		});
	}

	public static String getCurrentFile() {
		return currentFile;
	}

	public static void setCurrentFile(String currentFile) {
		DebugUIUtil.currentFile = currentFile;
	}

	public static void openFile(String resource) {
		// TODO Auto-generated method stub
		if(JsDebugServer.isHtmlPage(resource)){
			DebugUIUtil.openHtmlFile(resource);
		}else{
			if(resource.toLowerCase().endsWith(".htm")||resource.toLowerCase().endsWith(".html")){
				DebugUIUtil.openHtmlFile(resource);
			}else{
				DebugUIUtil.openJsFile(resource);
			}
		}
	}

	public static Locale getCurrentLocale() {
		return currentLocale;
	}

	public static void setCurrentLocale(Locale currentLocale) {
		DebugUIUtil.currentLocale = currentLocale;
	}
	public static String getReadMePath(){
		Locale locale = DebugUIUtil.getCurrentLocale();
		String language = "";
		if(locale!=null){
			language = "_" + locale;
		}
		String readmePath = PathUtil.getJsdtHome() + "/readme" + language + ".txt";
		if(new File(readmePath).exists()){
			return readmePath;
		}else{
			return PathUtil.getJsdtHome() + "/readme.txt";
		}
	}

}
