package org.ayound.js.debug.ui.shortcut;

import java.io.File;

import org.ayound.js.debug.core.IJsDebugConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class JsDebugLaunchShortcut implements ILaunchShortcut {

	private static final String BROWSER_PATH = "C:\\\\Program Files\\\\Internet Explorer\\\\IEXPLORE.EXE";

	public void launch(ISelection selection, String mode) {
		// TODO Auto-generated method stub
		IFile file = (IFile) ((IStructuredSelection) selection)
				.getFirstElement();
		launch(file, mode);
	}

	private void launch(IFile file, String mode) {
		// check for an existing launch config for the js file
		String path = file.getLocation().toOSString();
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType type = launchManager
				.getLaunchConfigurationType("org.ayound.js.debug.core.JsDebug");
		try {
			ILaunchConfiguration[] configurations = launchManager
					.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				String attribute = configuration.getAttribute("url",
						(String) null);
				if (path.equals(attribute)) {
					DebugUITools.launch(configuration, mode);
					return;
				}
			}
		} catch (CoreException e) {
			return;
		}

		try {
			// create a new configuration for the js file
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
					null, file.getName());
			workingCopy.setAttribute(IJsDebugConstants.URL, path);
			workingCopy.setAttribute(IJsDebugConstants.PORT,
					IJsDebugConstants.DEFAULT_PORT + "");
			File browserFile = new File(BROWSER_PATH);
			if (browserFile.exists()) {
				workingCopy.setAttribute(IJsDebugConstants.BROWSER, BROWSER_PATH);
				ILaunchConfiguration configuration = workingCopy.doSave();
				int result = DebugUITools.openLaunchConfigurationPropertiesDialog(
						new Shell(), configuration,
						"org.eclipse.debug.ui.launchGroup.debug");
				if(result==Window.CANCEL){
					configuration.delete();
					workingCopy.delete();
				}else{					
					DebugUITools.launch(configuration, mode);
				}
			} else {
				MessageDialog.openError(new Shell(Display.getCurrent()),
						"Debug Javascript Error!", "The browser " + BROWSER_PATH
								+ " is not exists!");
			}

		} catch (CoreException e1) {
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
		launch(file, mode);

	}

}
