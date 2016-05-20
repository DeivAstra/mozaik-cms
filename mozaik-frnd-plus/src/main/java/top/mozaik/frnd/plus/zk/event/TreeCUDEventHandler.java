/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.event;

import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.event.TreeDataEvent;

import top.mozaik.frnd.plus.zk.tree.I_TreeElement;
import top.mozaik.frnd.plus.zk.tree.I_TreeNode;

public class TreeCUDEventHandler<T extends I_TreeElement> implements I_CUDEventHandler<T> {
	
	private final Tree tree;
	public TreeCUDEventHandler(Tree tree) {
		if(tree == null)
			throw new NullPointerException("Tree is null");
		this.tree = tree;
	}

	public void onCreate(T v) {
		final AbstractTreeModel<T> model = (AbstractTreeModel) tree.getModel();
		
		if(v.getParent() == null) { // if null then append to root
			v.setParent((I_TreeNode)model.getRoot());
			v.getParent().addChild(v);
			model.fireEvent(TreeDataEvent.STRUCTURE_CHANGED, model.getPath(model.getRoot()), 0, 1);
			return;
		}
		
		v.getParent().addChild(v);
		model.fireEvent(TreeDataEvent.STRUCTURE_CHANGED, model.getPath(v), 0, 1);
	}

	public void onUpdate(T v) {
		final AbstractTreeModel<T> model = (AbstractTreeModel) tree.getModel();
		model.fireEvent(TreeDataEvent.STRUCTURE_CHANGED, model.getPath(v), 0, 1);
	}

	public void onDelete(T v) {
		final AbstractTreeModel<T> model = (AbstractTreeModel) tree.getModel();
		
		if(v.getParent() != null)
			v.getParent().removeChild(v);
		
		model.clearSelection();
		model.fireEvent(TreeDataEvent.STRUCTURE_CHANGED, model.getPath((T)v.getParent()), 0, 1);
	}
}
