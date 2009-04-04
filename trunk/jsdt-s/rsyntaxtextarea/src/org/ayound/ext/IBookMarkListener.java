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

public interface IBookMarkListener {

	public boolean beforeAddBookmark(int line);
	public boolean beforeRemoveBookmark(int line);
}
