/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.bean.tree;

import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

public class TreeResource extends A_TreeElement<TreeResourceFolder, _Resource> {
	
	public TreeResource(TreeResourceFolder parent, _Resource value) {
		super(parent, value);
	}

	public TreeResource(_Resource value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getName();
	}
}
