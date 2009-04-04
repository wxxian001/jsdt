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
 * Created on 2008-11-03
 *******************************************************************************/

package org.ayound.js.debug.server;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ResponseInfo {
	private String encoding;

	private InputStream inputStream;

	private String contentType;

	private Map<String ,List<String>> responseHeader;
	
	public ResponseInfo(String encoding, InputStream inputStream,
			String contentType,Map<String ,List<String>> responseHeader) {
		super();
		this.encoding = encoding;
		this.inputStream = inputStream;
		this.contentType = contentType;
		this.responseHeader = responseHeader;
	}

	public String getEncoding() {
		return encoding;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getContentType() {
		return contentType;
	}

	public Map<String, List<String>> getResponseHeader() {
		return responseHeader;
	}
	
}
