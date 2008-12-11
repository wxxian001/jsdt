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
package org.ayound.js.debug.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.ayound.js.debug.server.IDebugServer;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * JsResourceManager is used to manager javascript files create file ,remove
 * file ,or create temp project and folder
 * 
 */
public class JsResourceManager {

	private IDebugServer server;

	public static final String PROJECT_NAME = "jsdebug";

	public static final String FOLDER_NAME = "tempdir";

	private String port;

	public JsResourceManager(String port) {
		super();
		this.port = port;
	}

	/**
	 * remove temp project
	 * 
	 */
	public static void removeDebugProject() {
		try {
			getProject().delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * get temp project . if the project is not exists, create it
	 * 
	 * @return
	 */
	public static IProject getProject() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		if (!project.exists()) {
			try {
				project.create(null);

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!project.isOpen()) {
			try {
				project.open(null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return project;
	}

	/**
	 * get temp root folder.
	 * 
	 * @return
	 */
	public static IFolder getTempRoot() {
		IProject project = getProject();
		IFolder tempFolder = project.getFolder(FOLDER_NAME);
		if (!tempFolder.exists()) {
			createFolder(tempFolder);
		}
		return tempFolder;
	}

	/**
	 * get temp folder . every server has different folder
	 * 
	 * @return
	 */
	public IFolder getTempDir() {
		IProject project = getProject();
		IFolder tempFolder = project.getFolder(FOLDER_NAME + "/" + this.port);
		if (!tempFolder.exists()) {
			createFolder(tempFolder);
		}
		return tempFolder;
	}

	/**
	 * clear all the resources
	 * 
	 */
	public void clear() {
		IFolder folder = getTempDir();
		try {
			folder.delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * get resource by file
	 * 
	 * @param file
	 * @return
	 */
	public String getResourceByFile(IFile file) {
		return file.getFullPath().toString();
	}

	/**
	 * get file by resource
	 * 
	 * @param resource
	 * @return
	 */
	public IFile getFileByResource(String resource) {
		if(resource.endsWith("/")){
			resource = resource + "_homePage";
		}
		if (server.getRemoteBaseUrl().getProtocol().toLowerCase().startsWith(
				"file")) {
			URL url;
			try {
				url = new URL(server.getRemoteBaseUrl(), resource);
				IFile[] results = ResourcesPlugin.getWorkspace().getRoot()
						.findFilesForLocationURI(url.toURI());
				if (results != null && results.length > 0) {
					return results[0];
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		IFolder folder = getTempDir();
		return folder.getFile(resource);
	}

	/**
	 * the method is used to create folder depth. it will create parent folder
	 * if not present
	 * 
	 * @param folder
	 */
	private static void createFolder(IFolder folder) {
		IContainer parent = folder.getParent();
		if (!parent.exists()) {
			if (parent instanceof IFolder) {
				createFolder((IFolder) parent);
			} else if (parent instanceof IProject) {
				try {
					((IProject) parent).create(null);
					((IProject) parent).open(null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			folder.create(IResource.FORCE, true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * create file by resource path,and write stream to the file
	 * 
	 * @param resourcePath
	 * @param isResult
	 */
	public void createFile(String resourcePath, InputStream isResult,boolean forceUpdate) {
		if(resourcePath.endsWith("/")){
			resourcePath = resourcePath + "_homePage";
		}
		IFile file = getFileByResource(resourcePath);
		if (!file.exists()) {
			IFolder parent = (IFolder) file.getParent();
			if (!parent.exists()) {
				createFolder(parent);
			}
			if (server.getRemoteBaseUrl().getProtocol().toLowerCase()
					.startsWith("file")) {
				try {
					URL fileURL = new URL(this.server.getRemoteBaseUrl(),
							resourcePath);
					file.createLink(fileURL.toURI(),
							IResource.ALLOW_MISSING_LOCAL, null);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					file.create(isResult, true, null);

				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			if(forceUpdate && server.getRemoteBaseUrl().getProtocol().startsWith("http")){
				try {
					file.setContents(isResult, true, true, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {
			isResult.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IDebugServer getServer() {
		return server;
	}

	public void setServer(IDebugServer server) {
		this.server = server;
	}
}
