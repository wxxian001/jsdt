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

package org.ayound.js.debug.ui.widget.treetable;

import org.ayound.js.debug.model.Variable;


public class DebugDataModel extends AbstractTreeTableModel implements
		TreeTableModel {

	private Variable[] vars;

	private String columnNames[] = new String[]{};

	public DebugDataModel(String[] columns) {
		super(new Variable(Messages.getString("DebugDataModel.Global"), "")); //$NON-NLS-1$ //$NON-NLS-2$
		columnNames = columns;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		if (column <=1) {
			return columnNames[column];
		}
		return null;
	}

	public Class getColumnClass(int column) {
		if(column==0){
			return TreeTableModel.class;
		}else{
			return String.class;
		}
	}

	public Object getValueAt(Object node, int column) {
		if (node instanceof Variable) {
			Variable var = (Variable) node;
			if (column == 0) {
				return var.getName();
			} else if (column == 1) {
				return var.getValue();
			}
		}
		return null;
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof Variable) {
			Variable var = (Variable) parent;
			Variable[] childs = var.getVars();
			if (childs == null) {
				return null;
			} else {
				return childs[index];
			}
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent instanceof Variable) {
			Variable var = (Variable) parent;
			Variable[] childs = var.getVars();
			if (childs == null) {
				return 0;
			} else {
				return childs.length;
			}
		}
		return 0;
	}

	// The superclass's implementation would work, but this is more efficient.
	public boolean isLeaf(Object node) {
		return getChildCount(node) < 1;
	}

	public Variable[] getVars() {
		return vars;
	}

	public void setVars(Variable[] vars) {
		if (this.getRoot() instanceof Variable) {
			Variable root = (Variable) this.getRoot();
			this.vars = vars;
			root.setVars(vars);
		}
	}

}
