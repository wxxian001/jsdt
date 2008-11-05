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
package org.ayound.js.debug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.resource.JsResourceManager;
import org.ayound.js.debug.script.ScriptCompileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.IThread;

public class ScriptProcessor extends AbstractProcessor {

	public ScriptProcessor(String requestUrl, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server,
			Map<String, String> requestHeader) {
		super(requestUrl, postData, response, thread, server, requestHeader);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process() {
		try {
			URL url = this.computeRemoteURL();
			String resourcePath = url.getPath();
			JsResourceManager manager = getServer().getJsResourceManager();
			ResponseInfo info = ProcesserUtil.getResponseInfo(url, null,
					getPostData(), this.getRequestHeader());
			manager.createFile(resourcePath, info.getInputStream());
			JsDebugCorePlugin.getDefault().addResource(resourcePath,
					getServer());
			getServer().addResource(resourcePath);
			IFile scriptFile = manager.getFileByResource(resourcePath);
			String encoding = info.getEncoding();
			if(encoding==null){				
				CharsetDetector detector = new CharsetDetector();
				detector.detect(scriptFile);
				encoding = detector.getCharset();
			}
			if (encoding == null) {
				encoding = getServer().getDefaultEncoding();
			}
			scriptFile.setCharset(encoding, null);
			getResponse().writeJsHeader(encoding);
			BufferedReader scriptStream = new BufferedReader(
					new InputStreamReader(scriptFile.getContents(), encoding));
			String line = null;
			StringBuffer buffer = new StringBuffer();
			while ((line = scriptStream.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			String scriptContent = buffer.toString();
			getServer().getJsEngine().compileJs(resourcePath, scriptContent);
			String[] lines = getServer().getJsEngine().getScriptLines(
					resourcePath);
			for (int i = 0; i < lines.length; i++) {
				String jsLine = lines[i];
				if (i == 0 && "UTF-8".equalsIgnoreCase(encoding)) {
					try {
						char ch = jsLine.charAt(0);
						if (!(Character.isLetter(ch) || ch == '/')) {
							jsLine = jsLine.substring(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (getServer().getJsEngine().canBreakLine(resourcePath, i + 1)) {
					jsLine = ScriptCompileUtil.compileJsLine(lines,
							resourcePath, i);//
				}
				getResponse().writeln(jsLine);
			}
			lines = null;
			scriptContent = null;
			buffer = null;
			try {
				scriptStream.close();
			} catch (IOException e) {

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			getResponse().close();
		}
	}

}
