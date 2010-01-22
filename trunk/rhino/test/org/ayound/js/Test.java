/*******************************************************************************
 * $Header$
 * $Revision$
 * $Date$
 *
 *==============================================================================
 *
 * Copyright (c) 2001-2006 Primeton Technologies, Ltd.
 * All rights reserved.
 *
 * Created on 2010-1-13
 *******************************************************************************/


package org.ayound.js;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.JsDebugCompileEngine;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text = null;
		try {
//			File source = new File("E:\\eclipse\\life-insurance\\scripts\\jquery-1.3.2.js");
			File source = new File("d:\\ext-all-debug.js");
			text = FileUtils.readFileToString(source);
			JsDebugCompileEngine c = new JsDebugCompileEngine();
			c.setLineno(0);
			c.setSourceName("test");
			c.setSourceString("/*abcdef\n\nddd\n*///abc\nvar a = '' \n\nfunction test()\n{\nreturn 'ddd';\n}\ntest();if(2>1)a=b");
//			c.setSourceString(text);
			System.out.println(c.compile());
			FileUtils.writeStringToFile(new File("d:\\result.js"), c.compile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
