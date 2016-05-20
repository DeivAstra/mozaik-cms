/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.site.tree;

import top.mozaik.bknd.api.model.SitePage;

public class TreeSitePage extends A_TreeSiteElement<A_TreeSiteElement, TreeSitePage, SitePage> {
		
	public TreeSitePage(SitePage value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
