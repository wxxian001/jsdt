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

public class ResponseInfo {
	private String encoding;

	private InputStream inputStream;

	public ResponseInfo(String encoding, InputStream inputStream) {
		super();
		this.encoding = encoding;
		this.inputStream = inputStream;
	}

	public String getEncoding() {
		return encoding;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}
