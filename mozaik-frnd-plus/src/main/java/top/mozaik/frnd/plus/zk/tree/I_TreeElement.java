/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.tree;

public interface I_TreeElement<P extends I_TreeNode, V> {
	void setParent(P parent);
	P getParent();
	void setValue(V value);
	V getValue();
}
