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

package org.ayound.js.debug.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.ayound.js.debug.listener.IDebugListener;
import org.ayound.js.debug.model.DebugInfo;
import org.ayound.js.debug.server.JsDebugServer;
import org.ayound.js.debug.server.SocketServerUtil;

/**
 *
 * @author ayound
 */
public class JsDebugUtil {

	private static List<IDebugListener> listeners = new ArrayList<IDebugListener>();

	private static DebugInfo debugInfo = null;

	public static void startDebug(final DebugInfo info) {
		debugInfo = info;
		if (info.getUrl() == null || info.getUrl().length() < 1) {
			JOptionPane.showMessageDialog(new JFrame(), Messages.getString("JsDebugUtil.InputUrl")); //$NON-NLS-1$
			endDebug();
			return;
		}
		if (info.getBrowser() == null || info.getBrowser().length() < 1) {
			JOptionPane
					.showMessageDialog(new JFrame(), Messages.getString("JsDebugUtil.InputBrowser")); //$NON-NLS-1$
			endDebug();
			return;
		}
		ServerSocket socketServer = null;
		try {
			socketServer = SocketServerUtil.createSocketServer(info.getPort());
		} catch (IOException e) {

		}
		if (socketServer == null) {
			return;
		}
		JsDebugServer.start(info, socketServer);
		final String startUrl = JsDebugServer.getLocalBaseUrl()
				+ JsDebugServer.getRemoteBaseUrl().getPath().replace(" ", "+"); //$NON-NLS-1$ //$NON-NLS-2$
		SwingUtilities.invokeLater(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				try {
					Runtime.getRuntime().exec(info.getBrowser() + " " + startUrl); //$NON-NLS-1$
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		for (IDebugListener listener : listeners) {
			listener.startDebug(info);
		}
	}

	public static void endDebug() {
		JsDebugServer.stop();
		for (IDebugListener listener : listeners) {
			listener.endDebug();
		}
	}

	public static void addDebugListener(IDebugListener listener) {
		listeners.add(listener);
	}

	public static void removeDebugListener(IDebugListener listener) {
		listeners.remove(listener);
	}

	public static DebugInfo getDebugInfo() {
		return debugInfo;
	}
}
