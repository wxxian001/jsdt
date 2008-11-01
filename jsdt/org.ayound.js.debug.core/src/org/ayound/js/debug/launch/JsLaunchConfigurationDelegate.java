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
 * Created on 2008-10-26
 *******************************************************************************/

package org.ayound.js.debug.launch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.ayound.js.debug.core.IJsDebugConstants;
import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.resource.JsResourceManager;
import org.ayound.js.debug.server.JsDebugServer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class JsLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		final String port = configuration.getAttribute(IJsDebugConstants.PORT,
				String.valueOf(IJsDebugConstants.DEFAULT_PORT));
		String url = null;
		try {
			url = URLDecoder.decode(configuration.getAttribute(
					IJsDebugConstants.URL, ""), IJsDebugConstants.UTF8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		final String browser = configuration.getAttribute(
				IJsDebugConstants.BROWSER, "");
		URL remoteUrl = null;
		try {
			if (url.startsWith("http") || url.startsWith("file")) {
				remoteUrl = new URL(url.replace(":///", "://"));
			} else {
				if (url.charAt(1) == ':') {
					remoteUrl = new URL("file://" + url);
				} else {
					final String errorUrl = url;
					Display.getDefault().syncExec(new Runnable() {

						public void run() {
							MessageDialog.openError(new Shell(),
									"Java Script Debug Failed", "invalid url :"
											+ errorUrl);
						}
					});
				}
			}
			final JsDebugServer server = new JsDebugServer(launch, Integer
					.parseInt(port), remoteUrl, new JsResourceManager(port));
			if (server.start()) {
				JsDebugCorePlugin.getDefault().usePort(server.getPort());
			} else {
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						MessageDialog.openError(new Shell(),
								"Java Script Debug Failed",
								"Js Debug Server start failed, the port of "
										+ server.getPort() + " is used.");
					}
				});
			}
			final String startUrl = server.getLocalBaseUrl()
					+ remoteUrl.getPath().replace(" ", "+");
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					try {
						Runtime.getRuntime().exec(browser + " " + startUrl);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
