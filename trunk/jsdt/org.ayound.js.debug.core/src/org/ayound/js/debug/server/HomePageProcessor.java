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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.ayound.js.debug.core.JsDebugCorePlugin;
import org.ayound.js.debug.resource.JsResourceManager;
import org.ayound.js.debug.script.ScriptCompileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IThread;
/**
 * 
 * the processor to resolver home page.
 * the home page have some html code and javascript code
 */
public class HomePageProcessor extends AbstractProcessor {

	public HomePageProcessor(String requestUrl, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server,Map<String, String> requestHeader) {
		super(requestUrl, postData, response, thread, server,requestHeader);
	}

	@Override
	public void process() {
		try {
			URL url = this.getServer().getRemoteBaseUrl();
			String resourcePath = url.getPath();
			JsResourceManager manager = getServer().getJsResourceManager();
			manager.createFile(resourcePath, ProcesserUtil
					.getInputStream(url, null, getPostData(),this.getRequestHeader()));
			JsDebugCorePlugin.getDefault().addResource(resourcePath, getServer());
			getServer().addResource(resourcePath);
			IFile homeFile = manager.getFileByResource(resourcePath);
			getResponse().writeHTMLHeader(homeFile.getCharset());
			//write debug javascript file before any one
			getResponse().writeln("<script type=\"text/javascript\">");
			InputStream inputStream = HomePageProcessor.class
					.getResourceAsStream("debug.js");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					getResponse().writeln(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				inputStream.close();
				reader.close();
			}
			getResponse().writeln("</script>");
			StringBuffer buffer = new StringBuffer();
			BufferedReader homeInputStream = new BufferedReader(
					new InputStreamReader(homeFile.getContents(), homeFile
							.getCharset()));
			line = null;
			while ((line = homeInputStream.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			String scriptContent = buffer.toString();

			//compile html file by javascript engine
			getServer().getJsEngine().compileHtml(resourcePath, scriptContent);
			String[] lines = scriptContent.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String htmlLine = lines[i];
				String jsLine = htmlLine;
				if (getServer().getJsEngine().canBreakLine(resourcePath, i + 1)) {
					jsLine = ScriptCompileUtil.compileHtmlLine(lines,
							resourcePath, i);//
				}
				getResponse().writeln(jsLine);
			}
			lines = null;
			scriptContent = null;
			buffer = null;
			try {
				homeInputStream.close();
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
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getResponse().close();
		}
	}

}
