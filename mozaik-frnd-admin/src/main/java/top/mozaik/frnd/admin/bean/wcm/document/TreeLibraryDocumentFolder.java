/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.document;

import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeLibraryDocumentFolder extends A_TreeNode<TreeDocumentFolder, TreeDocumentFolder, WcmLibrary> {

	public TreeLibraryDocumentFolder(WcmLibrary value) {
		super(value);
	}
}
