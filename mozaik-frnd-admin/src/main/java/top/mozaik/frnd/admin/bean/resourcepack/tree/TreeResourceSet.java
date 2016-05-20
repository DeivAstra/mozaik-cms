/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.resourcepack.tree;

import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.frnd.plus.zk.tree.I_TreeElement;

public class TreeResourceSet<P extends TreeResourceSetTypeFolder, V extends _ResourceSet> implements I_TreeElement<P, V>{
	
	private P parent;
	
	private V value;
	
	public TreeResourceSet(V value) {
		this.value = value;
	}
	
	public TreeResourceSet(P parent, V value) {
		this.parent = parent;
		this.value = value;
	}
		
	public void setParent(P parent) {
		this.parent = parent;
	}

	public P getParent() {
		return parent;
	}

	public V getValue() {
		return value;
	}
	
	public void setValue(V v) {
		value = v;
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
