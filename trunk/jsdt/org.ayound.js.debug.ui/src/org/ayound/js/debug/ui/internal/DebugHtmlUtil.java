package org.ayound.js.debug.ui.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.ayound.js.debug.ui.JsDebugUIPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

public class DebugHtmlUtil {
	public static String getDefaultDebugPath(){
		try {
			String rootPath = JsDebugUIPlugin.getDefault().getStateLocation().makeAbsolute().toFile().getAbsolutePath();
			String fileName = "debug.htm";
			File debugFile = new File(rootPath,fileName);
			if(debugFile.exists()){
				return debugFile.getAbsolutePath();
			}else{				
				InputStream input = DebugHtmlUtil.class.getResourceAsStream(fileName);
				debugFile.createNewFile();
				FileOutputStream out = new FileOutputStream(debugFile);
				byte[] bytes = new byte[input.available()];
				input.read(bytes);
				out.write(bytes);
				input.close();
				out.close();
				return debugFile.getAbsolutePath();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
