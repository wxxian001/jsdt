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
import java.io.File;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ayound.js.debug.core.IResourceListener;
import org.ayound.js.debug.core.ResourceListenerManager;
import org.ayound.js.debug.ui.model.ResourceModel;
import org.ayound.js.debug.ui.util.DebugUIUtil;

public class JsResourcePanel extends JPanel {

	JList resourceList;

	private Vector<ResourceModel> resourceFiles = new Vector<ResourceModel>();

	public JsResourcePanel() {
		super();
		setLayout(new BorderLayout());
		resourceList = new JList(resourceFiles);
		resourceList.setFixedCellWidth(180);
		resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resourceList.addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				Object value = resourceList.getSelectedValue();
				if(value instanceof ResourceModel){
					DebugUIUtil.openFile(((ResourceModel)value).getFilePath());
				}
			}});
		initReadme();
		this.add(resourceList,BorderLayout.CENTER);
		ResourceListenerManager.addListener(new IResourceListener(){

			public void addResource(String resource) {
				JsResourcePanel.this.addResource(resource);
			}

			public void removeResource(String resource) {
				JsResourcePanel.this.removeResource(resource);
			}});
	}

	private void initReadme(){
		File readmeFile = new File(DebugUIUtil.getReadMePath());
		resourceFiles.add(new ResourceModel(readmeFile.getName(),readmeFile.getAbsolutePath()));
	}

	public void addResource(String resource) {
		for(ResourceModel model:resourceFiles){
			if(resource.equals(model.getFilePath())){
				return;
			}
		}
		File file = new File(resource);
		resourceFiles.add(new ResourceModel(file.getName(),resource));
		resourceList.setListData(resourceFiles);
	}

	public void removeResource(String resource) {
		for(ResourceModel model:resourceFiles){
			if(resource.equals(model.getFilePath())){
				resourceFiles.remove(model);
				break;
			}
		}
		resourceList.setListData(resourceFiles);
	}
	public void clearResource() {
		resourceFiles.clear();
		initReadme();
		resourceList.setListData(resourceFiles);
	}
}
