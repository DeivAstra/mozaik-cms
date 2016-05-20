/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.site.tree;

import top.mozaik.bknd.api.model.Site;

public class TreeSite extends A_TreeSiteElement<A_TreeSiteElement<?,?,?>, TreeSitePage, Site> {

	public TreeSite(Site value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return getValue().getTitle();
	}
}
