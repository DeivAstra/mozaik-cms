/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.component;

import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeComponent extends A_TreeElement<A_TreeNode<?,?,?>, WcmComponent> {

	public TreeComponent(WcmComponent value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
