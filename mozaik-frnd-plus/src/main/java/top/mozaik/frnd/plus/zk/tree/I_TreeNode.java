/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.tree;

public interface I_TreeNode<P extends I_TreeNode, C extends I_TreeElement, V> extends I_TreeElement<P, V> {
	/*
	List<ITreeEntity<P, C, V>> getChilds();
	void setChilds(List<ITreeEntity<P, C, V>> list);
	*/
	void addChild(C child);
	boolean removeChild(C child);
	boolean childsIsNull();
	C get(int index);
	/*int indexOfChild(C child);*/
	int size();
}
