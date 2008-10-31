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
package org.ayound.js.debug.ui;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JsLaunchConfigurationMainTab extends
		AbstractLaunchConfigurationTab {

	private Text urlText;

	private Text portText;

	private Text browserText;

	private static final String DEFAULT_URL = "D:\\eos61_bak\\dev\\jsdebug\\org.ayound.js.debug.ui\\test\\test.html";

	private static final String DEFAULT_BROWSER = "iexplore.exe";

	private static final String DEFAULT_PORT = "8088";

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText("url:");
		this.urlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData urlData = new GridData(GridData.FILL_HORIZONTAL);
		this.urlText.setLayoutData(urlData);
		Button urlButton = new Button(composite,SWT.BUTTON1);
		urlButton.setText("Browser...");
		urlButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				FileDialog dialog = new FileDialog(shell);
				dialog.setFilterExtensions(new String[]{"*.*","*.htm","*.html"});
				dialog.setFileName(urlText.getText());
				String fileName = dialog.open();
				if(fileName!=null){					
					urlText.setText(fileName);
					updateLaunchConfigurationDialog();
				}
			}});
		Label portLabel = new Label(composite, SWT.NONE);
		portLabel.setText("8088");
		this.portText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData portData = new GridData();
		portData.horizontalSpan = 2;
		portData.minimumWidth = 50;
		this.portText.setLayoutData(portData);
		Label browserLabel = new Label(composite, SWT.NONE);
		browserLabel.setText("browser:");
		this.browserText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.browserText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button browserButton = new Button(composite,SWT.BUTTON1);
		browserButton.setText("Browser...");
		browserButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				FileDialog dialog = new FileDialog(shell);
				dialog.setFilterExtensions(new String[]{"*.exe"});
				dialog.setFileName(browserText.getText());
				String fileName = dialog.open();
				if(fileName!=null){					
					browserText.setText(fileName);
					updateLaunchConfigurationDialog();
				}
			}});
		this.setControl(composite);
		this.urlText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		this.portText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		this.browserText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

	}

	public String getName() {
		return "Js Debug";
	}

	private int getDefaultPort(){
		int defaultPort = Integer.parseInt(DEFAULT_PORT);
		while(defaultPort<65535){
			if(!JsDebugCorePlugin.getDefault().hasPort(defaultPort)){
				return defaultPort;
			}
			defaultPort = defaultPort + 1;
		}
		return -1;
		
	}
	
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			this.urlText
					.setText(configuration.getAttribute("url", DEFAULT_URL));
			this.portText.setText(configuration.getAttribute("port",
					String.valueOf(getDefaultPort())));
			this.browserText.setText(configuration.getAttribute("browser",
					DEFAULT_BROWSER));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("url", this.urlText.getText());
		configuration.setAttribute("port", this.portText.getText());
		configuration.setAttribute("browser", this.browserText.getText());
		setDirty(false);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("url", DEFAULT_URL);
		configuration.setAttribute("port", String.valueOf(getDefaultPort()));
		configuration.setAttribute("browser", DEFAULT_BROWSER);
	}

}
