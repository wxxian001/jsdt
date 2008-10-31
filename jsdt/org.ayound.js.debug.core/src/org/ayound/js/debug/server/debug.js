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
window.onerror = function(e) {
	if (e == "exit") {
		return true;
	} else {
		alert(e + window.onerror.caller);
	}
}
var arguments = [];
function createXMLHttp() {
	if (typeof XMLHttpRequest != "undefined") {
		return new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		var aVersions = ["MSXML2.XMLHttp.5.0", "MSXML2.XMLHttp.4.0",
				"MSXML2.XMLHttp.3.0", "MSXML2.XMLHttp", "Microsoft.XMLHttp"];
		for (var i = 0; i < aVersions.length; i++) {
			try {
				var oXmlHttp = new ActiveXObject(aVersions[i]);
				return oXmlHttp;
			} catch (oError) {

			}
		}
	}
}
function jsDebug() {

}
jsDebug.xmlHttp = createXMLHttp();
jsDebug.debugCommand = null;
jsDebug.currResource = null;
jsDebug.breakpoints = null;
jsDebug.functionStack = [];

jsDebug.getFuncData = function(args, evalFunc) {
	if (!evalFunc) {
		return {};
	}
	if (args == null) {
		return {
			"window" : window
		};
	} else {
		var vars = [];
		var func = args.callee;
		if (func) {
			var funcStr = func.toString().replace(/\n/g, "");
			var argStart = funcStr.indexOf("(");
			var argEnd = funcStr.indexOf(")");
			if (argStart > 0 && argEnd > 0) {
				var argStr = funcStr.substring(argStart + 1, argEnd);
				vars = argStr.split(",");
			}
			var nameArr = funcStr.split("var ");
			for (var i = 1; i < nameArr.length; i++) {
				var line = nameArr[i];
				vars = vars.concat(jsDebug.parseVars(line));
			}
		}
		var data = {};
		for (var i = 0; i < vars.length; i++) {
			var key = vars[i];
			if (key && key.length > 0) {
				key = key.replace(/\n|\r|\t| /g, "");
				if(/^[A-Za-z0-9_\$]*$/.test(key)){
					var result = evalFunc(key);
					if(result==undefined){
						data[key] = "undefined";
					}else if(result==null){
						data[key] = "null";
					}else {				
						data[key] = evalFunc(key);
					}
				}
			}
		}
		return data;
	}
}
jsDebug.parseVars = function(line) {
	var varNames = [];
	if (line && line.length > 0) {
		var endOffset = line.indexOf(";");
		if (endOffset > 0) {
			line = line.substring(0, endOffset);
		}
		var lineArr = line.split(",");
		for (var i = 0; i < lineArr.length; i++) {
			var varStr = lineArr[i];
			var varEndOffset = varStr.indexOf("=");
			if (varEndOffset > 0) {
				varNames.push(varStr.substring(0, varEndOffset));
			} else {
				varNames.push(varStr);
			}
		}
	}
	return varNames;
}
jsDebug.isStepReturn = function(args) {
	if(args){
		var func = args.callee;
		for (var i = jsDebug.functionStack.length - 2;i>-1;i--){
			if(func==jsDebug.functionStack[i]){
				return true;
			}
		}
		return false;
	}else{
		return true;
	}
}
jsDebug.isStepOver = function(args) {
	if(args){
		var func = args.callee;
		for (var i = jsDebug.functionStack.length - 1;i>-1;i--){
			if(func==jsDebug.functionStack[i]){
				return true;
			}
		}
		return false;
	}else{
		return true;
	}	
}
jsDebug.getBreakPoint = function() {
	try {
		var postData = {
			"COMMAND" : "RESUME"
		}
		var xmlHttp = createXMLHttp();
		xmlHttp.open("POST", "/jsdebug.debug?" + new Date(), false);
		xmlHttp.send(json2string(postData));
		eval("var retObj = " + xmlHttp.responseText);
		jsDebug.breakpoints = retObj["BREAKPOINTS"];
		setTimeout(jsDebug.getBreakPoint, 500);
	} catch (e) {
	}
}
jsDebug.updateStack = function(args){
	if(args){
		var func = args.callee;
		for(var i=jsDebug.functionStack.length-1;i>-1;i--){
			if(func==jsDebug.functionStack[i]){
				jsDebug.functionStack = jsDebug.functionStack.slice(0,i+1);
				return ;
			}
		}
		jsDebug.functionStack.push(func);
	}else{	
		jsDebug.functionStack = [];
	}

}
jsDebug.debug = function(resource, line, scope, args, evalFunc) {
	try {
		if (jsDebug.debugCommand == null) {
			jsDebug.debugCommand = "START";
			jsDebug.getBreakPoint();
		}
		if (jsDebug.debugCommand == "TERMINATE") {
			throw "exit";
		}
		
		if (!(jsDebug.breakpoints && jsDebug.breakpoints[resource + line])) {
			if (jsDebug.debugCommand == "STEPRETURN") {
				if (!jsDebug.isStepReturn(args)) {
					return;
				}
			} else if (jsDebug.debugCommand == "STEPOVER") {
				if (!jsDebug.isStepOver(args)) {
					return;
				}
			} else if(jsDebug.debugCommand == "RESUME"){
				return;
			}else if(jsDebug.debugCommand == "STEPINTO"){
				
			}else {
				return;
			}
		}else{
			jsDebug.debugCommand = "BREAKPOINT";
		}
		var data = jsDebug.getFuncData(args, evalFunc);
		jsDebug.currResource = resource;
		jsDebug.updateStack(args);
		jsDebug.xmlHttp.open("POST", "/jsdebug.debug?" + new Date(), false);
		if (scope != window) {
			data["this"] = scope;
		}
		var postData = {
			"STACK" : data,
			"COMMAND" : jsDebug.debugCommand,
			"RESOURCE" : resource,
			"LINE" : line
		}
		jsDebug.xmlHttp.send(json2string(postData));
		jsDebug.parseResult(jsDebug.xmlHttp.responseText);
	} catch (e) {
		alert(e)
	}
}
jsDebug.parseResult = function(result) {
	if (result) {
		if (result.indexOf("{") == 0) {
			try {
				eval("var retObj = " + result);
				jsDebug.debugCommand = retObj["COMMAND"];
				if (jsDebug.debugCommand == "BREAKPOINT") {
					jsDebug.breakpoints = retObj["BREAKPOINTS"];
				}
			} catch (e) {
			}
		} else {
			// alert("Debug error:" + result);
		}
	} else {

	}
}
var $jsd = jsDebug.debug;

function json2string(obj, depth) {
	depth = depth || 0;
	if (typeof obj == "function") {
		return "\"function\"";
	} else if (typeof obj == "object") {
		return obj2string(obj, depth + 1);
	} else if (typeof obj == "array") {
		return array2string(obj, depth + 1);
	} else {
		if (typeof obj == "string") {
			return "\"" + obj.replace(/"/gm, "\\\"").replace(/\n|\r/, "")
					+ "\"";
		} else if (typeof obj == "number") {
			if (isFinite(obj)) {
				return obj;
			} else {
				return "\"out of number\"";
			}
		} else {
			return obj.toString().replace(/"/gm, "\\\"").replace(/\n|\r/, "");
		}
	}
}
function obj2string(obj, depth) {
	depth = depth || 0;
	if (obj == window) {
		return "window";
	} else if (obj == document) {
		return "document";
	} else if (obj == document.body) {
		return "document.body";
	} else {
		var arr = [];
		for (var prop in obj) {
			try {
				if (obj.hasOwnProperty(prop) && typeof(obj[prop]) != "function") {
					if (depth < 9) {
						arr.push("\"" + prop + "\":"
								+ json2string(obj[prop], depth + 1));
					} else {
						arr.push("\"" + prop + "\":\"...\"")
					}
				}
			} catch (e) {

			}
		}
		return "{" + arr.join(",") + "}";
	}
}
function array2string(array, depth) {
	depth = depth || 0;
	var arr = [];
	for (var i = 0; i < array.length; i++) {
		arr.push(json2string(array[i]), depth + 1);
	}
	return "[" + arr.join(",") + "]";
}
