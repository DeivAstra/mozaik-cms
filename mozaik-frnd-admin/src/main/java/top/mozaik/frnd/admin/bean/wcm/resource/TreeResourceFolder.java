/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.resource;

import top.mozaik.bknd.api.model.WcmResourceFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeResourceFolder extends A_TreeNode<A_TreeNode, 
	A_TreeElement<A_TreeNode, Object>, WcmResourceFolder> {

	public TreeResourceFolder(WcmResourceFolder value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
