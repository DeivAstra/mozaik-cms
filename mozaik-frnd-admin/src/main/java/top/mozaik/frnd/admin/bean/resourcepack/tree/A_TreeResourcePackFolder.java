/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.resourcepack.tree;

import java.util.ArrayList;
import java.util.List;

import top.mozaik.frnd.plus.zk.tree.I_TreeElement;
import top.mozaik.frnd.plus.zk.tree.I_TreeNode;

public abstract class A_TreeResourcePackFolder<P extends A_TreeResourcePackFolder, 
	C extends I_TreeElement, V extends Object> implements I_TreeNode<P,C,V> {
	
	private P parent;
	
	private V value;
	
	private List<C> childs;
	
	public A_TreeResourcePackFolder(V value) {
		this.value = value;
	}
	
	@Override
	public void setParent(P parent) {
		this.parent = parent;
	}

	@Override
	public P getParent() {
		return parent;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public V getValue() {
		return value;
	}

	public void addChild(C child) {
		if(child == null) throw new IllegalArgumentException("null");
		if(childs == null) childs = new ArrayList<C>();
		childs.add(child);
		child.setParent(this);
	}
	
	public boolean removeChild(C child) {
		if(child == null) throw new IllegalArgumentException("null");
		if(!childs.remove(child)) {
			throw new IllegalArgumentException("Child to remove not found. " + child);
		}
		child.setParent(null);
		return false;
	}
	
	public boolean childsIsNull() {
		return childs == null;
	}
	
	public C get(int index) {
		return childs.get(index);
	}
	
	public int indexOfChild(A_TreeResourcePackFolder child) {
		return childs.indexOf(child);
	};
	
	public int size() {
		if(childs == null) return 0;
		return childs.size();
	}
	
	@Override
	public String toString() {
		if(getValue() == null) return "";
		return getValue().toString();
	}
}
