/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class A_TreeNode
	<P extends A_TreeNode, C extends A_TreeElement, V extends Object> 
		extends A_TreeElement<P,V>
		implements I_TreeNode<P,C,V> {

	private List<C> childs;
	
	public A_TreeNode(V value) {
		super(value);
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
	
	public int indexOfChild(C child) {
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
