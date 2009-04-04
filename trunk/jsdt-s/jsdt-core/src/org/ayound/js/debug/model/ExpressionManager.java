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

package org.ayound.js.debug.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ayound.js.debug.listener.IExperssionListener;

public class ExpressionManager {
	private static Set<String> expressions = new HashSet<String>();

	private static List<IExperssionListener> listeners = new ArrayList<IExperssionListener>();

	public static void addExpressionListener(IExperssionListener listener) {
		listeners.add(listener);
	}

	public static void removeExpressionListener(IExperssionListener listener) {
		listeners.remove(listener);
	}

	public static void addExpression(String expression) {
		if(expressions.contains(expression)){
			removeExperssion(expression);
		}
		expressions.add(expression);
		for(IExperssionListener listener:listeners){
			listener.updateExpression();
		}
	}

	public static void removeExperssion(String expression) {
		expressions.remove(expression);
		for(IExperssionListener listener:listeners){
			listener.updateExpression();
		}
	}

	public static void clearExpression() {
		expressions.clear();
		for(IExperssionListener listener:listeners){
			listener.updateExpression();
		}
	}

}
