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

package org.ayound.js.debug.ui.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ayound.js.debug.util.PathUtil;

public class ConfigUtil {


	private static String getFilePath(){
		String jsdtHome = PathUtil.getJsdtHome();
		return jsdtHome +  "/conf/config.properties";
	}

	public static String getPropertie(String key){
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(getFilePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props.getProperty(key);
	}


	public static void writeProppertie(String key,String value){
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(getFilePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		props.setProperty(key, value);
		try {
//			System.out.println(CONFIG_PATH);
			props.store(new FileOutputStream(getFilePath()), "config of jsdt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//props.save(JsResourcUtil.class.getResource("conf/config.properties"), "config of jsdt");
	}

	public static void main(String[] args){
		writeProppertie("url", "aaa");
//		System.out.println(getPropertie("a"));
	}
}
