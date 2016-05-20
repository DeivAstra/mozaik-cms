/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.resourcepack.tree;

import top.mozaik.bknd.api.model.ResourcePack;

public class TreeResourcePackFolder extends A_TreeResourcePackFolder<TreeResourcePackRootFolder, TreeResourceSetTypeFolder, ResourcePack> {

	public TreeResourcePackFolder(ResourcePack value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle() + " (" + getValue().getAlias() +")";
	}
}
