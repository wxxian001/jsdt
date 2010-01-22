
package org.mozilla.javascript;

import java.util.HashMap;
import java.util.Map;

public class JsDebugCompileEngine {

	private String sourceString;

	private String sourceName;

	private int lineno;

	private Map breakPoints = new HashMap();

	private int offsetLine = 0;

	public int getLineno() {
		return lineno;
	}

	public void setLineno(int lineno) {
		this.lineno = lineno;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceString() {
		return sourceString;
	}

	public void setSourceString(String sourceString) {
		this.sourceString = sourceString;
	}

	public String compile() {
		if(sourceString.trim().length()==0){
			return sourceString;
		}
		Context ctx = ContextFactory.getGlobal().enterContext();
		CompilerEnvirons compilerEnv = new CompilerEnvirons();
		compilerEnv.initFromContext(ctx);
		DefaultErrorReporter compilationErrorReporter = DefaultErrorReporter.instance;
		Parser parser = new Parser(compilerEnv, compilationErrorReporter);
		parser.parse(sourceString, sourceName, lineno);
		Map lineInfo = parser.getLines();
		String encodedSource = parser.getEncodedSource();
		UintMap properties = new UintMap(1);
		properties.put(Decompiler.LINE_INFO, lineInfo);
		properties.put(Decompiler.JS_DEBUG, true);
		properties.put(Decompiler.BREAKPOINT_INFO, breakPoints);
		properties.put(Decompiler.RESOURCE_PATH, sourceName);
		properties.put(Decompiler.OFFSET_LINE, offsetLine);
		return Decompiler.decompile(encodedSource, 0, properties);

	}

	public Map getBreakPoints() {
		return breakPoints;
	}

	public void setBreakPoints(Map breakPoints) {
		this.breakPoints = breakPoints;
	}

	public int getOffsetLine() {
		return offsetLine;
	}

	public void setOffsetLine(int offsetLine) {
		this.offsetLine = offsetLine;
	}
}
