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
 * Created on 2009-3-27
 *******************************************************************************/


package org.ayound.js.debug.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GZipUtil {
	public static byte[] unzip(InputStream in) throws IOException {
        // Open the compressed stream
        GZIPInputStream gin = new GZIPInputStream(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Transfer bytes from the compressed stream to the output stream
        byte[] buf = new byte[1024];
        int len;
        while ((len = gin.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        // Close the file and stream
        gin.close();
        out.close();
        return out.toByteArray();
    }
}
