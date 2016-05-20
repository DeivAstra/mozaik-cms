/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.document;

import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeDocument extends A_TreeElement<A_TreeNode, WcmDocument> {

	public TreeDocument(WcmDocument value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
