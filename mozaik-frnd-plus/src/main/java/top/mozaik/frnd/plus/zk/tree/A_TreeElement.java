/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.tree;

import top.mozaik.frnd.plus.zk.tree.I_TreeElement;

public abstract class A_TreeElement<P extends A_TreeNode, V extends Object> implements I_TreeElement<P, V> {

	private P parent;

	private V value;
	
	public A_TreeElement(V value) {
		this.value = value;
	}
	
	public A_TreeElement(P parent, V value) {
		this.parent = parent;
		this.value = value;
	}
	
	public void setParent(P parent) {
		this.parent = parent;
	}
	
	@Override
	public P getParent() {
		return parent;
	}
	
	@Override
	public V getValue() {
		return value;
	}
	
	@Override
	public void setValue(V value) {
		this.value = value;
	}
}
