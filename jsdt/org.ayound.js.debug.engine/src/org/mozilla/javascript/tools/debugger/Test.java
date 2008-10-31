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
 * Created on 2008-10-24
 *******************************************************************************/

package org.mozilla.javascript.tools.debugger;

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.Dim.SourceInfo;
import org.mozilla.javascript.tools.debugger.Dim.StackFrame;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Dim dim = new Dim();
		ContextFactory factory = new ContextFactory();
		dim.attachTo(factory);
		dim.setGuiCallback(new GuiCallback() {

			public void dispatchNextGuiEvent() throws InterruptedException {
				// TODO Auto-generated method stub
			}

			public void enterInterrupt(StackFrame lastFrame,
					String threadTitle, String alertMessage) {
			}

			public boolean isGuiEventThread() {
				return false;
			}

			public void updateSourceText(SourceInfo sourceInfo) {

			}
		});
//		String str = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
//		str = str + "<HTML>\n";
//		str = str + " <BODY>\n";
//		str = str + "  <script>\n";
//		str = str
				String str =  "test('a',\n'c',a(b));/**aaaaaaaaaa*/var c =\n 'd';\nfunction test(a,\nb)\n{\nalert(a);\na=a+'a';\n/*abc*/\nreturn a+b;\n}var a = {'a':'a',\n'b':'b'}\n";
//		str = str + "  </script>\n";
//		str = str + "  <input type=\"button\" onclick=\"alert(test(1,2))\"/>\n";
//		str = str + " </BODY>\n";
//		str = str + "</HTML>\n";

//		IJsEngine engine = new JsEngineImpl();
//		engine.compileHtml("test.js", str);
		dim.compileScript("test.js", str);
		//dim.sourceInfo("test.js").
		System.out.println(dim.functionSourceByName("test").toString());
	}
}
