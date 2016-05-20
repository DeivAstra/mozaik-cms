/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.wcm.resource;

import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TreeLibraryResourceFolder extends A_TreeNode<TreeResourceFolder, TreeResourceFolder, WcmLibrary> {

	public TreeLibraryResourceFolder(WcmLibrary value) {
		super(value);
	}
}
