/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.bean.tree;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeResourceFolder extends A_TreeNode<A_TreeNode, TreeResource, _Resource> {
	
	private final E_ResourceType type;
	
	public TreeResourceFolder(_Resource value) {
		super(value);
		type = getType(value);
	}
	
	public TreeResourceFolder(A_TreeNode parent, _Resource value) {
		super(value);
		setParent(parent);
		
		type = getType(value);
	}
	
	/// WE HAVE RESERVED FOLDER NAMES. SEE E_ResourceType
	private E_ResourceType getType(_Resource value) {
		if(!(value instanceof _Resource))  return null;
			
		final _Resource bean = (_Resource) value;
		try {
			return E_ResourceType.getTypeByString(bean.getName());
		} catch (Exception e) {}
		return null;
	}
	
	public E_ResourceType getType() {
		return type;
	}
	
	public boolean isType(E_ResourceType type) {
		return type == this.type;
	}
	
	public boolean hasType() {
		return type != null;
	}
	
	@Override
	public String toString() {
		return getValue().getName();
	}
}
