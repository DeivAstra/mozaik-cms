/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.template;

import top.mozaik.bknd.api.model.WcmTemplate;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeTemplate extends A_TreeElement<A_TreeNode, WcmTemplate> {

	public TreeTemplate(WcmTemplate value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
