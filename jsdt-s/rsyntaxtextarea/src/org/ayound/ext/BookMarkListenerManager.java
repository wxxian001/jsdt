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
 * Created on 2009-3-27
 *******************************************************************************/


package org.ayound.ext;

public class BookMarkListenerManager {
	private static IBookMarkListener listener;

	public static IBookMarkListener getListener() {
		return listener;
	}

	public static void setListener(IBookMarkListener listener) {
		BookMarkListenerManager.listener = listener;
	}
}
