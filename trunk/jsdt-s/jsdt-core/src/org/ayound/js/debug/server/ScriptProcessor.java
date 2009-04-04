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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.ayound.js.debug.engine.CharsetDetector;
import org.ayound.js.debug.resource.JsResourceManager;
import org.ayound.js.debug.script.ScriptCompileUtil;
import org.mozilla.javascript.EvaluatorException;

public class ScriptProcessor extends AbstractProcessor {

	public ScriptProcessor(String requestUrl, String postData,
			JsDebugResponse response,
			Map<String, String> requestHeader,ResponseInfo info) {
		super(requestUrl, postData, response, requestHeader,info);
	}

	@Override
	public void process() {
		try {
			URL url = this.computeRemoteURL();
			String resourcePath = url.getPath();
			JsResourceManager.createFile(resourcePath, getInfo());
			String realPath = JsResourceManager.getResourceRealPath(resourcePath);
			String encoding = getInfo().getEncoding();
			if(encoding==null||"gzip".equals(encoding)){
				CharsetDetector detector = new CharsetDetector();
				detector.detect(realPath);
				encoding = detector.getCharset();
			}
			if (encoding == null) {
				encoding = JsDebugServer.getDefaultEncoding();
			}
			getResponse().writeJsHeader(encoding);
			InputStream inputStream =new FileInputStream(realPath);
//			if(getInfo().getResponseHeader().get("Content-Encoding").contains("gzip")){
//				inputStream = new GZIPInputStream(inputStream);
//			}
			BufferedReader scriptStream = new BufferedReader(
					new InputStreamReader(inputStream, encoding));
			String line = null;
			StringBuffer buffer = new StringBuffer();
			while ((line = scriptStream.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			String scriptContent = buffer.toString();
			JsDebugServer.addResource(realPath);
			try{
				JsDebugServer.getJsEngine().compileJs(realPath, scriptContent);
			}catch(EvaluatorException e){
				JsDebugServer.compileError(e.getMessage(), realPath, e.getLineNumber());
			}
			String[] lines = JsDebugServer.getJsEngine().getScriptLines(
					realPath);
			for (int i = 0; i < lines.length; i++) {
				String jsLine = lines[i];
				if (i == 0 && "UTF-8".equalsIgnoreCase(encoding)) {
					try {
						if(jsLine.length()>0){
							char ch = jsLine.charAt(0);
							if (!(Character.isLetter(ch) || ch == '/')) {
								jsLine = jsLine.substring(1);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (JsDebugServer.getJsEngine().canBreakLine(realPath, i + 1)) {
					jsLine = ScriptCompileUtil.compileJsLine(lines,
							realPath, i);//
				}
				getResponse().writeln(jsLine,encoding);
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
