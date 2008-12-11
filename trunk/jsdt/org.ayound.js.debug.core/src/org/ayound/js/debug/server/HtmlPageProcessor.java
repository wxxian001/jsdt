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

import org.ayound.js.debug.resource.JsResourceManager;
import org.ayound.js.debug.script.ScriptCompileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IThread;
import org.mozilla.javascript.EvaluatorException;

/**
 * 
 * the processor to resolver home page. the home page have some html code and
 * javascript code
 */
public class HtmlPageProcessor extends AbstractProcessor {

	public HtmlPageProcessor(String requestUrl, String postData,
			JsDebugResponse response, IThread thread, IDebugServer server,
			Map<String, String> requestHeader,ResponseInfo info) {
		super(requestUrl, postData, response, thread, server, requestHeader,info);
	}

	@Override
	public void process() {
		try {
			URL url = this.computeRemoteURL();
			String resourcePath = url.getPath();
			JsResourceManager manager = getServer().getJsResourceManager();
			getServer().setDefaultEncoding(getInfo().getEncoding());
			manager.createFile(resourcePath, getInfo().getInputStream(),true);
			IFile htmlFile = manager.getFileByResource(resourcePath);
			String encoding = getInfo().getEncoding();
			if(encoding==null){				
				CharsetDetector detector = new CharsetDetector();
				detector.detect(htmlFile);
				encoding = detector.getCharset();
			}
			htmlFile.setCharset(encoding, null);
			getResponse().writeHTMLHeader(encoding,getInfo().getResponseHeader());
			// write debug javascript file before any one
			getResponse().writeln("<script type=\"text/javascript\">",encoding);
			InputStream inputStream = HtmlPageProcessor.class
					.getResourceAsStream("debug.js");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null;
			int debugLine = 2;
			try {
				while ((line = reader.readLine()) != null) {
					getResponse().writeln(line,encoding);
					debugLine ++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				inputStream.close();
				reader.close();
			}
			getServer().setDebugLine(debugLine);
			
			getResponse().writeln("</script>",encoding);
			StringBuffer buffer = new StringBuffer();
			BufferedReader homeInputStream = new BufferedReader(
					new InputStreamReader(htmlFile.getContents(), htmlFile
							.getCharset()));
			line = null;
			while ((line = homeInputStream.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			String scriptContent = buffer.toString();
			String scriptPath = htmlFile.getFullPath().toString();
			getServer().addHtmlPage(scriptPath);
			getServer().addResource(scriptPath);
			// compile html file by javascript engine
			try{
				getServer().getJsEngine().compileHtml(scriptPath, scriptContent);
			}catch(EvaluatorException e){
				getServer().compileError(e.getMessage(), scriptPath, e.getLineNumber());
			}
			String[] lines = scriptContent.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String htmlLine = lines[i];
				String jsLine = htmlLine;
				if (getServer().getJsEngine().canBreakLine(scriptPath, i + 1)) {
					jsLine = ScriptCompileUtil.compileHtmlLine(lines,
							scriptPath, i);//
				}
				getResponse().writeln(jsLine,encoding);
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
