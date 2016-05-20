/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.document;

import top.mozaik.bknd.api.model.WcmDocumentFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeDocumentFolder extends A_TreeNode<A_TreeNode, A_TreeElement<A_TreeNode,?>, WcmDocumentFolder> {

	public TreeDocumentFolder(WcmDocumentFolder value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
