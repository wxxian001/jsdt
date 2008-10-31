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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class JsResourceManager {

	public static final String PROJECT_NAME = "jsdebug";

	public static final String FOLDER_NAME = "tempdir";

	private String port;

	public JsResourceManager(String port) {
		super();
		this.port = port;
	}

	public static void removeDebugProject() {
		try {
			getProject().delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	public static IFolder getTempRoot() {
		IProject project = getProject();
		IFolder tempFolder = project.getFolder(FOLDER_NAME);
		if (!tempFolder.exists()) {
			createFolder(tempFolder);
		}
		return tempFolder;
	}

	public IFolder getTempDir() {
		IProject project = getProject();
		IFolder tempFolder = project.getFolder(FOLDER_NAME + "/" + this.port);
		if (!tempFolder.exists()) {
			createFolder(tempFolder);
		}
		return tempFolder;
	}

	public void clear() {
		IFolder folder = getTempDir();
		try {
			folder.delete(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getResourceByFile(IFile file) {
		IFolder folder = getTempDir();
		return file.getFullPath().toString().replace(
				folder.getFullPath().toString(), "");
	}

	public IFile getFileByResource(String resource) {
		IFolder folder = getTempDir();
		return folder.getFile(resource);
	}

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

	public void createFile(String resourcePath, InputStream isResult) {
		IFile file = getFileByResource(resourcePath);
		if (!file.exists()) {
			IFolder parent = (IFolder) file.getParent();
			if (!parent.exists()) {
				createFolder(parent);
			}
			try {
				file.create(isResult, true, null);

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			isResult.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
