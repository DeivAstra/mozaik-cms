/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.bean.tree;

import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeResourceSetFolder extends A_TreeNode<TreeResourceRootFolder, TreeResourceFolder, _ResourceSet> {

	private final String resourcePackAlias;
	
	public TreeResourceSetFolder(String resourcePackAlias, _ResourceSet value) {
		super(value);
		this.resourcePackAlias = resourcePackAlias;
	}
	
	public String getResourcePackAlias() {
		return resourcePackAlias;
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
