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

package org.ayound.js.debug.core;

import java.util.ArrayList;
import java.util.List;

public class ResourceListenerManager {
	private static List<IResourceListener> listeners = new ArrayList<IResourceListener>();

	public static void addListener(IResourceListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(IResourceListener listener) {
		listeners.remove(listener);
	}

	public static void addResource(String resource) {
		for(IResourceListener listener:listeners){
			listener.addResource(resource);
		}
	}

	public static void removeResource(String resource) {
		for(IResourceListener listener:listeners){
			listener.removeResource(resource);
		}
	}
}
